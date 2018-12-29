package vending;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import vending.service.SensorActuator;

@SpringBootApplication
public class CurrentStateApp {
	private static ConfigurableApplicationContext ctx;
	
	public static void main(String[] args) {
		ctx = SpringApplication.run(CurrentStateApp.class, args);
		SensorActuator actuator = ctx.getBean(SensorActuator.class);
		actuator.start();
	}
}
