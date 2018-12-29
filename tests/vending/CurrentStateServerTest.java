package vending;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vending.dto.MachineDTO;
import vending.dto.MachineState;
import vending.dto.SensorDTO;
import vending.dto.SensorStorage;
import vending.service.CurrentStateService;
import vending.service.SensorActuator;

@SpringBootApplication
class CurrentStateServerTest {
	ConfigurableApplicationContext ctx;
	CurrentStateService currentStateService;
	SensorActuator sensorActuator;
	ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	void setUp() throws Exception {
		ctx = SpringApplication.run(CurrentStateServerTest.class);
		this.currentStateService = this.ctx.getBean(CurrentStateService.class);
		this.sensorActuator = this.ctx.getBean(SensorActuator.class);
	}

	@AfterEach
	void tearDown() {
		ctx.close();
	}

	@Test
	void handleSensorDataTest() {
		List<MachineDTO> machines = TestDataFactory.getMachinesData(2, 2);
		machines.forEach(machine -> machine.getSensors().forEach(this::sendSensorData));
		Map<Integer, MachineState> machinesStates = this.currentStateService.getMachinesState();
//		this.sensorActuator.run();
		assertEquals(machinesStates.keySet().size(), 2);
		assertEquals(machinesStates.get(machines.get(0).getMachineId()).getSensors().size(), 2);
		testMachiesEquals(machines, machinesStates);
	}

	private void testMachiesEquals(List<MachineDTO> machines, Map<Integer, MachineState> machinesStates) {
		machines.forEach(machine -> {
			MachineState expMachine = machinesStates.get(machine.getMachineId());
			assertNotNull(expMachine);
			assertEquals(expMachine.getMachineId(), machine.getMachineId());
			
			testMachineSensors(machine, expMachine);
		});
	}

	private void testMachineSensors(MachineDTO machine, MachineState expMachine) {
		List<SensorDTO> expSensors = expMachine.getSensors().values().stream()
				.map(s -> s.getSensor())
				.collect(Collectors.toList());
		assertNotNull(expSensors);
		List<SensorDTO> sensors = machine.getSensors();
		sensors.forEach(sensor -> {
			assertTrue(expSensors.contains(sensor));
		});
	}

	private void sendSensorData(SensorDTO sensor) {
		try {
			String sensorJSON = this.mapper.writeValueAsString(sensor);
			this.currentStateService.handleSensorData(sensorJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
