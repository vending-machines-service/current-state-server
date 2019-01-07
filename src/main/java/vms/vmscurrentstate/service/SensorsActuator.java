package vms.vmscurrentstate.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vms.vmscurrentstate.configuration.CurrentStateParams;
import vms.vmscurrentstate.dto.MachineState;
import vms.vmscurrentstate.dto.SensorDTO;
import vms.vmscurrentstate.dto.SensorStorage;
import vms.vmscurrentstate.repository.StateRepository;
import vms.vmscurrentstate.service.interfaces.IDispatcher;
import vms.vmscurrentstate.service.interfaces.ISensorsStats;

@RefreshScope
@Service
@Slf4j
@EnableBinding(IDispatcher.class)
public class SensorsActuator extends Thread {
  @Autowired
  ISensorsStats statsService;
  @Autowired
  IDispatcher dispatcher;
  @Autowired
  StateRepository stateRepo;
  @Autowired
  CurrentStateParams params;
  

  // @Value("${actuateFrequency:2000}")
  // long actuateFrequency;

  // @Value("${sensorResponseInterval:600000}")
  // long sensorResponseInterval;

  // @Value("${expirationErrorCode:-1}")
  // int expirationErrorCode;

  public SensorsActuator() {
    setDaemon(true);
  }

  @Override
  public void run() {
    while (true) {
      this.actuateState();
      try {
        sleep(this.params.getActuateFrequency());
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
    this.reportExpired(expiredSensors, this.params.getExpirationErrorCode());
    this.actuateSensorData(machinesState.values());
  }

  /**
   * saves given collection of vending machines state into DB;
   * 
   * @param machinesState
   */
  private void actuateSensorData(Collection<MachineState> machinesState) {
    for (MachineState machine : machinesState) {
      this.stateRepo.save(machine.toMachineDTO());
    }
  }

  /**
   * looks through all sensors of all the vending machines for not responding
   * sensor instances and returns list of such sensors;
   * 
   * @param machinesState
   * @return
   */
  private List<SensorDTO> getExpiredSensors(Map<Integer, MachineState> machinesState) {
    return machinesState.values().stream().flatMap(this::getMachineSensorsStream)
        .filter(sensor -> checkSensorExpired(sensor, this.params.getSensorResponseInterval()))
        .map(sensorStorage -> sensorStorage.getSensor()).collect(Collectors.toList());
  }

  /**
   * checks if sensor last report was earlier than given period of time;
   * 
   * @param sensorStorage - sensor container, storing last sensor response time;
   * @param actuateTime   - period of sensor response;
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
   * sends a message into kafka MALFUNCTION channel with expired sensor and
   * expirationError sensor value;
   * 
   * @param expiredSensors
   */
  private void reportExpired(List<SensorDTO> expiredSensors, int expirationErrorCode) {
    for (SensorDTO s : expiredSensors) {
      s.setValue(expirationErrorCode);
      this.dispatcher.sendMalfunctionChannel().send(MessageBuilder.withPayload(s).build());
    }
  }
}
