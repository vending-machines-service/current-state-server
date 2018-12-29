package vending.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import vending.dto.MachineDTO;
import vending.dto.MachineState;
import vending.dto.SensorDTO;
import vending.dto.SensorStorage;
import vending.repository.StateRepository;

@RefreshScope
@Service
@EnableBinding(IDispatcher.class)
public class SensorActuator extends Thread {
	@Autowired
	ISensorStats statsService;
	@Autowired
	IDispatcher dispatcher;
	@Autowired
	StateRepository stateRepo;
	
	@Value("${actuateFrequency:2000}")
	long actuateFrequency;
	
	@Value("${sensorResponseInterval:600000}")
	long sensorResponseInterval;
	
	@Value("${expirationErrorCode:-1}")
	int expirationErrorCode;

	public SensorActuator() {
		setDaemon(true);
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("ACTUATE:START");
			this.actuateState();
			try {
				sleep(this.actuateFrequency);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * looks for not responding sensors in actual vending machines sensors state;
	 * makes a malfunction report for non-responding sensors, if such sensors exist;
	 * saves actual machine state into DB;
	 */
	private void actuateState() {
		Map<Integer, MachineState> machinesState = this.statsService.getMachinesState();
		List<SensorDTO> expiredSensors = this.getExpiredSensors(machinesState);
		this.reportExpired(expiredSensors, this.expirationErrorCode);
		this.actuateSensorData(machinesState.values());
	}
	/**
	 * saves given collection of vending machines state into DB;
	 * @param machinesState
	 */
	private void actuateSensorData(Collection<MachineState> machinesState) {
		for (MachineState machine : machinesState) {
			this.stateRepo.save(machine.toMachineDTO());
		}
	}

	/**
	 * looks through all sensors of all the vending machines for not responding sensor instances
	 * and returns list of such sensors;
	 * @param machinesState
	 * @return
	 */
	private List<SensorDTO> getExpiredSensors(Map<Integer, MachineState> machinesState) {
		return machinesState.values().stream()
				.flatMap(this::getMachineSensorsStream)
				.filter(sensor -> checkSensorExpired(sensor, this.sensorResponseInterval))
				.map(sensorStorage -> sensorStorage.getSensor())
				.collect(Collectors.toList());
	}
	/**
	 * checks if sensor last report was earlier than given period of time;
	 * @param sensorStorage - sensor container, storing last sensor response time;
	 * @param actuateTime - period of sensor response;
	 * @return
	 */
	private boolean checkSensorExpired(SensorStorage sensorStorage, long sensorResponseInterval) {
		return System.currentTimeMillis() - sensorStorage.getLastUpdateTime() > sensorResponseInterval;
	}
	/**
	 * @param machine;
	 * @return Stream of given vending machine sensors;
	 */
	private Stream<SensorStorage> getMachineSensorsStream(MachineState machineState) {
		return machineState.getSensors().values().stream();
	}
	/**
	 * sends a message into kafka MALFUNCTION channel with expired sensor and expirationError sensor value;
	 * @param expiredSensors
	 */
	private void reportExpired(List<SensorDTO> expiredSensors, int expirationErrorCode) {
		for (SensorDTO s : expiredSensors) {
			s.setValue(expirationErrorCode);
			this.dispatcher.malfunction().send(MessageBuilder.withPayload(s).build());
		}
	}
}
