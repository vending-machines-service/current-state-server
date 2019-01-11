package vms.vmscurrentstate.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@ConfigurationProperties("vms")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CurrentStateParams {
  int actuateFrequency;
  int sensorResponseInterval;
  int expirationErrorCode;
}