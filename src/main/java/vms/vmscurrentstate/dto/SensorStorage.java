package vms.vmscurrentstate.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SensorStorage {
  SensorDTO sensor;
  long lastUpdateTime;

  public SensorStorage(SensorDTO sensor, long lastUpdateTime) {
    this.sensor = sensor;
    this.lastUpdateTime = lastUpdateTime;
  }
}
