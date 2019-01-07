package vms.vmscurrentstate.dto;

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
public class SensorDTO {
  Integer machineId;
  Integer sensorId;
  int value;

  public SensorDTO(int machineId, Integer sensorId, int value) {
    super();
    this.machineId = machineId;
    this.sensorId = sensorId;
    this.value = value;
  }
}
