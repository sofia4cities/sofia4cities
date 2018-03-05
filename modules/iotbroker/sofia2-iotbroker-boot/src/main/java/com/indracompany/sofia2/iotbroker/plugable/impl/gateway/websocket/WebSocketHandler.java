package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

@Component
public class WebSocketHandler {

	@Autowired
	MessageProcessor processor;

	@MessageMapping("/messages")
	@SendTo("/topic/messages")
	public <T extends SSAPBodyMessage>
	SSAPMessage<SSAPBodyReturnMessage> send(SSAPMessage<T> message) {

		return processor.process(message);
	}
}
