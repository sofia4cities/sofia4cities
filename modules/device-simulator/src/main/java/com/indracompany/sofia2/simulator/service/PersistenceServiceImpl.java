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
package com.indracompany.sofia2.simulator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.token.TokenService;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersistenceServiceImpl implements PersistenceService {


	@Autowired
	private MessageProcessor messageProcessor;
	@Autowired
	private ClientPlatformService clientPlatformService;
	@Autowired
	private TokenService tokenService;
	private String sessionKey;

	public void connectIotBroker(String clientPlatform, String clientPlatformInstance) {
		Token token = this.tokenService.getToken(this.clientPlatformService.getByIdentification(clientPlatform));
		final SSAPMessage<SSAPBodyJoinMessage> join = new SSAPMessage<SSAPBodyJoinMessage>();
		join.setDirection(SSAPMessageDirection.REQUEST);
		join.setMessageType(SSAPMessageTypes.JOIN);
		SSAPBodyJoinMessage body = new SSAPBodyJoinMessage();
		body.setClientPlatform(clientPlatform);
		body.setClientPlatformInstance(clientPlatformInstance);
		body.setToken(token.getToken());
		join.setBody(body);
		SSAPMessage<SSAPBodyReturnMessage> response = messageProcessor.process(join);
		if(response.getSessionKey()!=null)
			this.sessionKey = response.getSessionKey();
	}

	@Override
	public void insertOntologyInstance(String instance, String ontology, String user, String clientPlatform,
			String clientPlatformInstance) throws Exception {

		if (this.sessionKey != null) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(instance);
			
			final SSAPMessage<SSAPBodyInsertMessage> insert = new SSAPMessage<SSAPBodyInsertMessage>();
			final SSAPBodyInsertMessage body = new SSAPBodyInsertMessage();
			insert.setDirection(SSAPMessageDirection.REQUEST);
			insert.setMessageType(SSAPMessageTypes.INSERT);
			insert.setSessionKey(this.sessionKey);
			body.setOntology(ontology);
			body.setData(json);
			insert.setBody(body);
			
			
			SSAPMessage<SSAPBodyReturnMessage> response = messageProcessor.process(insert);
			if(response.getBody().getErrorCode().name()==SSAPErrorCode.AUTENTICATION.name()) {
				this.connectIotBroker(clientPlatform, clientPlatformInstance);
				this.insertOntologyInstance(instance, ontology, user, clientPlatform, clientPlatformInstance);
			
			}

		} else {
			this.connectIotBroker(clientPlatform, clientPlatformInstance);
			this.insertOntologyInstance(instance, ontology, user, clientPlatform, clientPlatformInstance);
		}
	}

}
