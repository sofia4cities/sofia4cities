package com.indracompany.sofia2.iotbroker.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.processor.impl.InsertProcessor;
import com.indracompany.sofia2.iotbroker.processor.impl.JoinProcessor;
import com.indracompany.sofia2.iotbroker.processor.impl.LeaveProcessor;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPErrorCode;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.util.SSAPMessageGenerator;

@Component
public class MessageProcessorDelegate implements MessageProcessor {
	
	@Autowired
	SecurityPluginManager securityPluginManager;
	
	@Autowired
	JoinProcessor joinProcessor;
	
	@Autowired
	LeaveProcessor leaveProcessor;
	
	@Autowired
	InsertProcessor insertProcessor;
	
	public<T extends SSAPBodyMessage> SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message) {
		
		//TODO: PRE-PROCESSORS
		//TODO: PROCESS
			//TODO: CHECK SSAP COMPLIANCE
			//TODO: CHECK CREDENTIALS
			//TODO: CHECK AUTHRIZATIONS & PERMISSIONS
			//TODO: VALIDATE ONTOLOGY SCHEMA IF NECESSARY
			//TODO: GET PROCESSOR AN PROCESS
		
		//TODO: POST-PROCESSORS
		
		SSAPMessage<SSAPBodyReturnMessage> response = null;
		
		try {
			
			if(message.getBody().isThinKpMandatory() 
					&& (StringUtils.isEmpty(message.getBody().getThinKp()) 
							|| StringUtils.isEmpty(message.getBody().getThinkpInstance()))) {
				response = SSAPMessageGenerator.generateResponseErrorMessage(message,
						SSAPErrorCode.PROCESSOR, 
						String.format(MessageException.ERR_THINKP_IS_MANDATORY, message.getMessageType().name()));
				
				return response;
			}
			
			if(message.getBody().isSessionKeyMandatory()
					&& StringUtils.isEmpty(message.getSessionKey())) {
				response = SSAPMessageGenerator.generateResponseErrorMessage(message,
						SSAPErrorCode.PROCESSOR, 
						String.format(MessageException.ERR_SESSIONKEY_IS_MANDATORY, message.getMessageType().name()));
				
				return response;
			}
			
			if(message.getBody().isAutorizationMandatory()) {
				securityPluginManager.checkSessionKeyActive(message.getSessionKey());
			}
			
			response = proxyProcesor(message).process(message);
			response.setMessageId(message.getMessageId());
			response.setMessageType(message.getMessageType());
			response.setOntology(message.getOntology());
			
		} catch (SSAPProcessorException e) {
			// TODO GENERATE SSAPErrorMessage
			e.printStackTrace();
		} 
		catch (AuthorizationException e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(e.getMessage(), message.getMessageType().name()));
		}
		catch (AuthenticationException e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(e.getMessage(), message.getMessageType().name()));
		}
		catch (BaseException e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(e.getMessage(), message.getMessageType().name()));
		} 
		
		return response;
	}
	
	public MessageTypeProcessor proxyProcesor(SSAPMessage<? extends SSAPBodyMessage> message) throws SSAPProcessorException {
		
		SSAPMessageTypes type = message.getMessageType();
		
		switch(type) {
			case JOIN:
				return joinProcessor;
			case LEAVE:
				return leaveProcessor;
			case INSERT:
				return insertProcessor;
			default:
				throw new SSAPProcessorException(String.format(MessageException.ERR_PROCESSOR_NOT_FOUND, message.getMessageType()));
			
		}
	}
	
	
	
	
	
	
	

}
