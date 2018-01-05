package com.indracompany.sofia2.iotbroker.ssap.generator;

import java.util.UUID;

import com.github.javafaker.Faker;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;

public final class SSAPMessageGenerator {
	
	private static final Faker faker = new Faker();

	public static SSAPMessage<SSAPBodyJoinMessage> generateJoinMessage() {
		SSAPMessage<SSAPBodyJoinMessage> ssapMessage = new SSAPMessage<>();
		SSAPBodyJoinMessage body = new SSAPBodyJoinMessage();
		ssapMessage.setDirection(SSAPMessageDirection.REQUEST);
		ssapMessage.setMessageId(UUID.randomUUID().toString());
		ssapMessage.setMessageType(SSAPMessageTypes.JOIN);
//		ssapMessage.setOntology(ontology);
		body.setThinKp(faker.name().firstName());
		body.setThinkpInstance(UUID.randomUUID().toString());
		body.setToken(UUID.randomUUID().toString());
		
		ssapMessage.setBody(body);
		
		return ssapMessage;
	}
}
