package vending.service;

import java.util.Map;

import vending.dto.MachineState;
import vending.dto.SensorStorage;

public interface ISensorStats {
	public Map<Integer, MachineState> getMachinesState();
}
