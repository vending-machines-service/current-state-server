package vms.vmscurrentstate.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class MachineState {
  Integer machineId;
  Map<Integer, SensorStorage> sensors;

  public MachineState(Integer machineId) {
    this.machineId = machineId;
    this.sensors = new HashMap<Integer, SensorStorage>();
  }

  public void actuateSensorData(SensorDTO sensor) {
    this.sensors.put(sensor.getSensorId(), new SensorStorage(sensor, System.currentTimeMillis()));
  }

  public MachineDTO toMachineDTO() {
    List<SensorDTO> sensors = this.sensors.values().stream().map(sensorStorage -> sensorStorage.getSensor())
        .collect(Collectors.toList());
    return new MachineDTO(this.machineId, sensors);
  }
}
