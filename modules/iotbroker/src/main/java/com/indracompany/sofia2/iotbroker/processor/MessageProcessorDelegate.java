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
package com.indracompany.sofia2.iotbroker.processor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.common.util.SSAP2PersintenceUtil;
import com.indracompany.sofia2.iotbroker.processor.impl.InsertProcessor;
import com.indracompany.sofia2.iotbroker.processor.impl.JoinProcessor;
import com.indracompany.sofia2.iotbroker.processor.impl.LeaveProcessor;
import com.indracompany.sofia2.persistence.common.AccessMode;
import com.indracompany.sofia2.persistence.exceptions.NotSupportedStatementException;
import com.indracompany.sofia2.persistence.interfaces.DBStatementParser;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPErrorCode;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.SSAPQueryType;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.util.SSAPMessageGenerator;

@Component
public class MessageProcessorDelegate implements MessageProcessor {
	
	@Autowired
	SecurityPluginManager securityPluginManager;
	@Autowired
	List<MessageTypeProcessor> processors;
	
	@Autowired
	private ApplicationContext context;
	
	public<T extends SSAPBodyMessage> SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message) {
		
		//TODO: PRE-PROCESSORS
		//TODO: PROCESS
			//TODO: CHECK SSAP COMPLIANCE
			//TODO: CHECK CREDENTIALS
			//TODO: CHECK AUTHRIZATIONS & PERMISSIONS
			//TODO: VALIDATE ONTOLOGY SCHEMA IF NECESSARY
			//TODO: GET PROCESSOR AN PROCESS
		
		//TODO: POST-PROCESSORS
		//TODO: RETORNO
		
		SSAPMessage<SSAPBodyReturnMessage> response = null;
		
		try {
			
			
			Optional<SSAPMessage<SSAPBodyReturnMessage>> validation = validateMessage(message);
			if(validation.isPresent()) {
				return validation.get();
			}
			
			MessageTypeProcessor processor = proxyProcesor(message);
			
			processor.validateMessage(message);
			response = processor.process(message);
			
			response.setMessageId(message.getMessageId());
			response.setMessageType(message.getMessageType());
			response.setOntology(message.getOntology());
			
		} catch (SSAPProcessorException e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(e.getMessage(), message.getMessageType().name()));
		} 
		catch (AuthorizationException e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.AUTHORIZATION, 
					String.format(e.getMessage(), message.getMessageType().name()));
		}
		catch (AuthenticationException e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.AUTENTICATION, 
					String.format(e.getMessage(), message.getMessageType().name()));
		}
		catch (BaseException e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(e.getMessage(), message.getMessageType().name()));
		} catch (Exception e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(e.getMessage(), message.getMessageType().name()));
		} 
		
		return response;
	}
	
	public Optional<SSAPMessage<SSAPBodyReturnMessage>> validateMessage(SSAPMessage<? extends SSAPBodyMessage> message) throws AuthorizationException {
		SSAPMessage<SSAPBodyReturnMessage> response = null;
		
		//Check presence of Thinkp
		if(message.getBody().isClientPlatformMandatory() 
				&& (StringUtils.isEmpty(message.getBody().getClientPlatform()) 
						|| StringUtils.isEmpty(message.getBody().getClientPlatformInstance()))) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(MessageException.ERR_THINKP_IS_MANDATORY, message.getMessageType().name()));
			
			return Optional.of(response);
		}
		
		//Check presence of sessionKey and authorization of sessionKey
		if(message.getBody().isSessionKeyMandatory()
				&& StringUtils.isEmpty(message.getSessionKey())) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(MessageException.ERR_SESSIONKEY_IS_MANDATORY, message.getMessageType().name()));
			
			return Optional.of(response);
		}
		
		if(message.getBody().isAutorizationMandatory()) {
			securityPluginManager.checkSessionKeyActive(message.getSessionKey());
		}
		
		//Check if ontology is present and autorization for ontology
		if(message.getBody().isOntologyMandatory()) {
			securityPluginManager.checkAuthorization(message.getMessageType(), message.getOntology(), message.getSessionKey());
		}
		
		return Optional.empty();
		
	}
	
	public MessageTypeProcessor proxyProcesor(SSAPMessage<? extends SSAPBodyMessage> message) throws SSAPProcessorException {
		
		if(null == message.getMessageType()) {
			throw new SSAPProcessorException(MessageException.ERR_SSAP_MESSAGETYPE_MANDATORY_NOT_NULL);
		}
		
		SSAPMessageTypes type = message.getMessageType();
		
		List<MessageTypeProcessor> filteredProcessors = processors.stream().filter(p -> p.getMessageTypes().contains(type)).collect(Collectors.toList());
		
		if(filteredProcessors.isEmpty()) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_PROCESSOR_NOT_FOUND, message.getMessageType()));
		}
		
		return filteredProcessors.get(0);
		
	}
	
	
}
