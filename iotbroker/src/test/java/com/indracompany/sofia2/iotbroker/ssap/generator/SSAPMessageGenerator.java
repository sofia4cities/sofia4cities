package com.indracompany.sofia2.iotbroker.ssap.generator;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;

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
	
	public static SSAPMessage<SSAPBodyOperationMessage> generateInsertMessage(String ontology, Object value) throws Exception, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SSAPMessage<SSAPBodyOperationMessage> message = new SSAPMessage<SSAPBodyOperationMessage>();
		message.setSessionKey(UUID.randomUUID().toString());
		
		SSAPBodyOperationMessage body = new SSAPBodyOperationMessage();
		JsonNode jsonValue = mapper.readTree(mapper.writeValueAsBytes(value));
		body.setData(jsonValue);
		message.setBody(body);
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setMessageType(SSAPMessageTypes.INSERT);
		message.setOntology(ontology);
		return message;
	}
}
