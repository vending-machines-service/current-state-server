package vms.vmscurrentstate.service.interfaces;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageChannel;

public interface IDispatcher extends Sink {

  final static String MALFUNCTION_CHANNEL = "malfunction";

  @Output(MALFUNCTION_CHANNEL)
  MessageChannel sendMalfunctionChannel();
}
