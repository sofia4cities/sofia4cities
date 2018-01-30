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
package com.indracompany.sofia2.iotbroker.processor.impl;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.common.util.SSAP2PersintenceUtil;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.persistence.common.AccessMode;
import com.indracompany.sofia2.persistence.exceptions.NotSupportedStatementException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.DBStatementParser;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.SSAPQueryType;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

@Profile({"dummy-dev"})
@Component
public class InsertProcessor implements MessageTypeProcessor {

	@Autowired
	BasicOpsDBRepository repository;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	SecurityPluginManager securityPluginManager;
	@Autowired
	List<DBStatementParser> dbStatementParsers;
	
	
	
	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message) throws BaseException {
		@SuppressWarnings("unchecked")
		SSAPMessage<SSAPBodyOperationMessage> insertMessage = (SSAPMessage<SSAPBodyOperationMessage>) message;
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		
		//TODO: Dont forget ContextData
		String repositoryResponse = repository.insert(insertMessage.getOntology(), insertMessage.getBody().getData().toString());
		ContextData contextData = new ContextData();
		
		//TODO: Client Connection in contextData
		contextData.setClientConnection("");
		contextData.setClientPatform(insertMessage.getBody().getClientPlatform());
		contextData.setClientPatformInstance(insertMessage.getBody().getClientPlatformInstance());
		contextData.setClientSession(insertMessage.getSessionKey());
		contextData.setTimezoneId(ZoneId.systemDefault().toString());
		contextData.setUser(securityPluginManager.getUserIdFromSessionKey(insertMessage.getSessionKey()));
		
		//TODO: SSAP Copy methods
		responseMessage.setDirection(SSAPMessageDirection.RESPONSE);
		responseMessage.setMessageId(insertMessage.getMessageId());
		responseMessage.setMessageType(insertMessage.getMessageType());
		responseMessage.setOntology(insertMessage.getOntology());
		responseMessage.setSessionKey(insertMessage.getSessionKey());
		responseMessage.setBody(new SSAPBodyReturnMessage());
		responseMessage.getBody().setOk(true);		
		responseMessage.getBody().setClientPlatform(insertMessage.getBody().getClientPlatform());
		responseMessage.getBody().setClientPlatformInstance(insertMessage.getBody().getClientPlatformInstance());
		
		try {
			responseMessage.getBody().setData(objectMapper.readTree(repositoryResponse));
		} catch (IOException e) {
			//TODO: LOG
			throw new SSAPProcessorException("Response from repository on insert is not JSON compliant");
		}
		
		return responseMessage;		
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.INSERT);
	}

	@Override
	public void validateMessage(SSAPMessage<? extends SSAPBodyMessage> message) throws AuthorizationException {
		SSAPMessage<SSAPBodyOperationMessage> operationMessage = (SSAPMessage<SSAPBodyOperationMessage>) message;
		
		List<String> collections = this.getOntologies(message.getMessageType(), operationMessage.getBody().getQueryType(), operationMessage.getBody().getQuery());
	
		for(String col: collections) {
			securityPluginManager.checkAuthorization(message.getMessageType(), col, message.getSessionKey());
		}
		
		
	}
	
	private List<String> getOntologies(SSAPMessageTypes messageType, SSAPQueryType queryType, String query) throws AuthorizationException {
		
		for(DBStatementParser parser : dbStatementParsers) {
			if(queryType.equals(parser.getSSAPQueryTypeSupported())) 
			{
				Optional<AccessMode> accesType = SSAP2PersintenceUtil.formSSAPMessageType2TableAccesMode(messageType);
				List<String> collections = parser.getCollectionList(query, accesType.get());
				return collections;
			}
		}
		return Collections.emptyList();
		
	}

}
