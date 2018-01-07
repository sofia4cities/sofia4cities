package com.indracompany.sofia2.iotbroker.processor;

import java.util.List;
import java.util.Optional;

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
	List<DBStatementParser> dbStatementParsers;
	@Autowired
	JoinProcessor joinProcessor;	
	@Autowired
	LeaveProcessor leaveProcessor;	
	@Autowired
	InsertProcessor insertProcessor;
	
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
			
			//Check presence of Thinkp
			if(message.getBody().isThinKpMandatory() 
					&& (StringUtils.isEmpty(message.getBody().getThinKp()) 
							|| StringUtils.isEmpty(message.getBody().getThinkpInstance()))) {
				response = SSAPMessageGenerator.generateResponseErrorMessage(message,
						SSAPErrorCode.PROCESSOR, 
						String.format(MessageException.ERR_THINKP_IS_MANDATORY, message.getMessageType().name()));
				
				return response;
			}
			
			//Check presence of sessionKey and authorization of sessionKey
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
			
			//Check if ontology is present and autorization for ontology
			//Also checks that queries are no referencing not authorized ontologies
			if(message.getBody().isOntologyMandatory()) {
				securityPluginManager.checkAuthorization(message.getMessageType(), message.getOntology(), message.getSessionKey());
				
				SSAPMessage<SSAPBodyOperationMessage> operationMessage = (SSAPMessage<SSAPBodyOperationMessage>) message;
				validateOperation(message.getMessageType(), operationMessage.getBody().getQueryType(), operationMessage.getBody().getQuery(), message.getSessionKey());
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
		} catch (Exception e) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message,
					SSAPErrorCode.PROCESSOR, 
					String.format(e.getMessage(), message.getMessageType().name()));
		} 
		
		return response;
	}
	
	public MessageTypeProcessor proxyProcesor(SSAPMessage<? extends SSAPBodyMessage> message) throws SSAPProcessorException {
		
		if(null == message.getMessageType()) {
			throw new SSAPProcessorException(MessageException.ERR_SSAP_MESSAGETYPE_MANDATORY_NOT_NULL);
		}
		
		SSAPMessageTypes type = message.getMessageType();
		
		switch(type) {
			case JOIN:
				return joinProcessor;
			case LEAVE:
				return leaveProcessor;
			case INSERT:
				return insertProcessor;
			case NONE:
				throw new SSAPProcessorException(MessageException.ERR_SSAP_MESSAGETYPE_MANDATORY_NOT_NULL);	
			default:
				throw new SSAPProcessorException(String.format(MessageException.ERR_PROCESSOR_NOT_FOUND, message.getMessageType()));
			
		}
	}
	
	private void validateOperation(SSAPMessageTypes messageType, SSAPQueryType queryType, String query, String sessionKey) throws AuthorizationException {
		
		for(DBStatementParser parser : dbStatementParsers) {
			if(queryType.equals(parser.getSSAPQueryTypeSupported())) 
			{
				Optional<AccessMode> accesType = SSAP2PersintenceUtil.formSSAPMessageType2TableAccesMode(messageType);
				List<String> collections = parser.getCollectionList(query, accesType.get());
				for(String col : collections) 
				{
					securityPluginManager.checkAuthorization(messageType, col, sessionKey);
				}
				
				return;
			}
			
			throw new NotSupportedStatementException(String.format(MessageException.ERR_BD_QUERY_TYPE_NOT_SUPPORTED, queryType.name()));
			
		}
	}

}
