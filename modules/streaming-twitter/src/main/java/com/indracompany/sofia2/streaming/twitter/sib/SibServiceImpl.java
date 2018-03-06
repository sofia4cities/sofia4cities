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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SibServiceImpl implements SibService {

	@Autowired
	private RouterService routerService;
	//
	//	@Override
	//	public String getSessionKey(String token) throws SSAPComplianceException, AuthenticationException {
	//
	//		String sessionKey = null;
	//
	//		SSAPBodyJoinMessage joinMessage = new SSAPBodyJoinMessage();
	//		joinMessage.setToken(token);
	//
	//		SSAPMessage<SSAPBodyJoinMessage> message = new SSAPMessage<SSAPBodyJoinMessage>();
	//		message.setMessageType(SSAPMessageTypes.JOIN);
	//		message.setDirection(SSAPMessageDirection.REQUEST);
	//		message.setBody(joinMessage);
	//
	//		SSAPMessage<SSAPBodyReturnMessage> responseJoin = messageProcessor.process(message);
	//
	//			sessionKey = responseJoin.getSessionKey();
	//		if (sessionKey != null) {
	//			log.debug("Connected to SIB with token: " + responseJoin.getSessionKey() + " session key: "
	//					+ responseJoin.getSessionKey());
	//		} else
	//			log.debug("Can't connect to IoT Broker");
	//
	//		return sessionKey;
	//	}
	//
	//	@Override
	//	public SSAPMessage<SSAPBodyReturnMessage> disconnect(String sessionKey) {
	//		SSAPMessage<SSAPBodyReturnMessage> response = null;
	//		SSAPMessage<SSAPBodyLeaveMessage> message = new SSAPMessage<SSAPBodyLeaveMessage>();
	//		message.setDirection(SSAPMessageDirection.REQUEST);
	//		message.setBody(new SSAPBodyLeaveMessage());
	//		message.setMessageType(SSAPMessageTypes.LEAVE);
	//		message.setSessionKey(sessionKey);
	//
	//		try {
	//			response = messageProcessor.process(message);
	//			log.debug("Disconnected from SIB");
	//		} catch (Exception e) {
	//			log.debug("Couldn't disconnect from SIB");
	//		}
	//
	//		return response;
	//	}
	//
		@Override
		public void insertOntologyInstance(String instance, String ontology, String user) throws Exception {
	
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(instance);
	
			final OperationModel model = new OperationModel();
			model.setBody(json.toString());
			model.setOntologyName(ontology);
			model.setUser(user);
			model.setOperationType(OperationType.POST);
			model.setQueryType(QueryType.NATIVE);
			
			final NotificationModel modelNotification = new NotificationModel();
			modelNotification.setOperationModel(model);
			
			final OperationResultModel response = routerService.insert(modelNotification);
			log.info(response.getResult());
		
//			SSAPBodyInsertMessage insertMessage = new SSAPBodyInsertMessage();
//			insertMessage.setData(json);
//			insertMessage.setOntology(ontology);
			
//		
//			if (response.getBody().isOk())
//				log.debug("Ontology instance inserted");
//			else
//				log.debug("Couldn't insert instance");
//	
//			return response.getBody().isOk();
		}
//
//	@Override
//	public void inserOntologyInstanceToMongo(String instance, String ontology, String clientPlatform, String clientPlatformInstance, String user) throws JsonProcessingException, IOException {
//		if (!springDataMongoTemplate.collectionExists(ontology)) {
//			springDataMongoTemplate.createCollection(ontology);
//			log.debug("Ontology collection created");
//		}
//
//		final ObjectMapper objectMapper = new ObjectMapper();
//		final JsonNode jsonNode = objectMapper.readTree(instance);
//
//		final ContextData contextData = new ContextData();
//
//		contextData.setClientConnection("");
//		contextData.setClientPatform(clientPlatform);
//		contextData.setClientPatformInstance(clientPlatformInstance);
//		contextData.setTimezoneId(ZoneId.systemDefault().toString());
//		contextData.setUser(user);
//
//		((ObjectNode) jsonNode).set("contextData", objectMapper.valueToTree(contextData));
//
//		repository.insert(ontology,
//				jsonNode.toString());
//
//	}

}
