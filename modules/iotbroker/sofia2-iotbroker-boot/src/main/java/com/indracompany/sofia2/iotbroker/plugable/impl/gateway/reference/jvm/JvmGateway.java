package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.jvm;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

@Component
public class JvmGateway {

	@Autowired
	MessageProcessor processor;

	@Autowired
	GatewayNotifier subscriptor;

	@PostConstruct
	private void init() {
		subscriptor.addSubscriptionListener("jmv",  (s) -> System.out.println("processing") );
	}

	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage request) {
		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request);
		return response;
	}

}
