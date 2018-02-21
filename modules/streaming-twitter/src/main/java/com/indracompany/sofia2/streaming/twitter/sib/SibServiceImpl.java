/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.streaming.twitter.sib;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPComplianceException;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.SSAPQueryType;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLeaveMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SibServiceImpl implements SibService {

	@Autowired
	private MessageProcessor messageProcessor;

	@Override
	public String getSessionKey(String token) throws SSAPComplianceException, AuthenticationException {

		String sessionKey = null;

		SSAPBodyJoinMessage joinMessage = new SSAPBodyJoinMessage();
		joinMessage.setToken(token);

		SSAPMessage<SSAPBodyJoinMessage> message = new SSAPMessage<SSAPBodyJoinMessage>();
		message.setMessageType(SSAPMessageTypes.JOIN);
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setBody(joinMessage);

		SSAPMessage<SSAPBodyReturnMessage> responseJoin = messageProcessor.process(message);
		
			sessionKey = responseJoin.getSessionKey();
		if (sessionKey != null) {
			log.debug("Connected to SIB with token: " + responseJoin.getSessionKey() + " session key: "
					+ responseJoin.getSessionKey());
		} else
			log.debug("Can't connect to IoT Broker");

		return sessionKey;
	}

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> disconnect(String sessionKey) {
		SSAPMessage<SSAPBodyReturnMessage> response = null;
		SSAPMessage<SSAPBodyLeaveMessage> message = new SSAPMessage<SSAPBodyLeaveMessage>();
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setBody(new SSAPBodyLeaveMessage());
		message.setMessageType(SSAPMessageTypes.LEAVE);
		message.setSessionKey(sessionKey);

		try {
			response = messageProcessor.process(message);
			log.debug("Disconnected from SIB");
		} catch (Exception e) {
			log.debug("Couldn't disconnect from SIB");
		}
		
		return response;
	}

	@Override
	public boolean insertOntologyInstance(String instance, String sessionKey, String ontology, String clientPlatform,
			String clientPlatformInstance) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(instance);

		SSAPMessage<SSAPBodyOperationMessage> message = new SSAPMessage<SSAPBodyOperationMessage>();
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setMessageType(SSAPMessageTypes.INSERT);
		message.setSessionKey(sessionKey);
		message.setOntology(ontology);
		SSAPBodyOperationMessage operationMessage = new SSAPBodyOperationMessage();
		operationMessage.setClientPlatform(clientPlatform);
		operationMessage.setClientPlatformInstance(clientPlatformInstance);
		operationMessage.setData(json);
		operationMessage.setQueryType(SSAPQueryType.NATIVE);
		message.setBody(operationMessage);
		
		SSAPMessage<SSAPBodyReturnMessage> response = messageProcessor.process(message);
		if (response.getBody().isOk())
			log.debug("Ontology instance inserted");
		else
			log.debug("Couldn't insert instance");

		return response.getBody().isOk();
	}

}
