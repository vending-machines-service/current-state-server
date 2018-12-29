package vending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vending.dto.MachineDTO;
import vending.dto.SensorDTO;


public class TestDataFactory {
	private static final int MIN_BOUND = 0;
	private static final int MAX_BOUND = 100;

	public static List<SensorDTO> getSensorsData(int machineId, int size) {
		List<SensorDTO> res = new ArrayList<SensorDTO>();

		for (int i = 0; i < size; i++) {
			int sensorValue = new Random().ints(1, MIN_BOUND, MAX_BOUND).findFirst().getAsInt();
			SensorDTO sensor = new SensorDTO(machineId, i, sensorValue);
			res.add(sensor);
		}
		return res;
	}
	
	public static List<MachineDTO> getMachinesData(int machineCount, int sensorsCount) {
		List<MachineDTO> machines = new ArrayList<MachineDTO>();
		
		for (int i = 0; i < machineCount; i++) {
			MachineDTO machine = new MachineDTO(i, getSensorsData(i, sensorsCount));
			machines.add(machine);
		}
		return machines;
	}
	
	
}
