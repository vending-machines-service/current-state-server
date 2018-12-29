package vending.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vending.dto.MachineState;
import vending.dto.SensorDTO;
import vending.dto.SensorStorage;

@EnableBinding(IDispatcher.class)
public class CurrentStateService implements ISensorStats {
	Map<Integer, MachineState> machinesStates = new HashMap<Integer, MachineState>(); 
	
	ObjectMapper mapper = new ObjectMapper();

	@StreamListener(IDispatcher.INPUT)
	public void handleSensorData(String sensorDataJSON) throws JsonParseException, JsonMappingException, IOException {
		SensorDTO sensor = this.mapper.readValue(sensorDataJSON, SensorDTO.class);
		MachineState machineState = this.machinesStates
				.getOrDefault(sensor.getMachineId(), new MachineState(sensor.getMachineId()));
		machineState.actuateSensorData(sensor);
		this.machinesStates.put(sensor.getMachineId(), machineState);
	}
	
	public Map<Integer, MachineState> getMachinesState() {
		return this.machinesStates;
	}
}























