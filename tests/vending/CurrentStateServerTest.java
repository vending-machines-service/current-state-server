package vending;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
		System.out.println("TEST:START");
		List<MachineDTO> machines = TestDataFactory.getMachinesData(2, 2);
		machines.forEach(machine -> machine.getSensors().forEach(this::sendSensorData));
		Map<Integer, MachineState> machinesStates = this.currentStateService.getMachinesState();
		this.sensorActuator.run();
		System.out.println("TEST:END");
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
