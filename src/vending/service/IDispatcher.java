package vending.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageChannel;

public interface IDispatcher extends Sink {
	@Value("${MALFUNCTION_CHANNEL:SENSOR_MALFUNCTION}")
	final static String MALFUNCTION_CHANNEL = "SENSOR_MALFUNCTION";
	
	@Output(MALFUNCTION_CHANNEL)
	MessageChannel malfunction();
}
