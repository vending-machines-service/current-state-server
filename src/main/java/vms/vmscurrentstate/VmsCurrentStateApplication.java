package vms.vmscurrentstate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import vms.vmscurrentstate.service.SensorsActuator;

@SpringBootApplication
public class VmsCurrentStateApplication {
	private static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(VmsCurrentStateApplication.class, args);
		SensorsActuator actuator = ctx.getBean(SensorsActuator.class);
		actuator.start();
	}

}

