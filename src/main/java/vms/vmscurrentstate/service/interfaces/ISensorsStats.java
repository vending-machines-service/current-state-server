package vms.vmscurrentstate.service.interfaces;

import java.util.Map;

import vms.vmscurrentstate.dto.MachineState;

public interface ISensorsStats {
  public Map<Integer, MachineState> getMachinesState();
}
