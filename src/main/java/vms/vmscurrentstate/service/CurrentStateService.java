package vms.vmscurrentstate.service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import lombok.extern.slf4j.Slf4j;
import vms.vmscurrentstate.dto.MachineState;
import vms.vmscurrentstate.dto.SensorDTO;
import vms.vmscurrentstate.service.interfaces.IDispatcher;
import vms.vmscurrentstate.service.interfaces.ISensorsStats;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@EnableBinding(IDispatcher.class)
@Slf4j
public class CurrentStateService implements ISensorsStats {
	Map<Integer, MachineState> machinesStates = new HashMap<Integer, MachineState>(); 
	
	ObjectMapper mapper = new ObjectMapper();

	@StreamListener(IDispatcher.INPUT)
	public void handleSensorData(String sensorDataJSON) throws JsonParseException, JsonMappingException, IOException {
		SensorDTO sensor = this.mapper.readValue(sensorDataJSON, SensorDTO.class);
		log.info("[RECEIVED]: MACHINE: {}; SENSOR: {}; VALUE: {}", sensor.getMachineId(), sensor.getSensorId(), sensor.getValue());
		MachineState machineState = this.machinesStates
				.getOrDefault(sensor.getMachineId(), new MachineState(sensor.getMachineId()));
		machineState.actuateSensorData(sensor);
		this.machinesStates.put(sensor.getMachineId(), machineState);
	}
	
	public Map<Integer, MachineState> getMachinesState() {
		return this.machinesStates;
	}
}























