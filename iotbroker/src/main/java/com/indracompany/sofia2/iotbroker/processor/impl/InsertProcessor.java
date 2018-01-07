package com.indracompany.sofia2.iotbroker.processor.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
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
	
	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message) throws BaseException {
		@SuppressWarnings("unchecked")
		SSAPMessage<SSAPBodyOperationMessage> insertMessage = (SSAPMessage<SSAPBodyOperationMessage>) message;
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		
		//TODO: Dont forget ContextData
		String repositoryResponse = repository.insert(insertMessage.getOntology(), insertMessage.getBody().getData().toString());
		
		//TODO: SSAP Copy methods
		responseMessage.setDirection(SSAPMessageDirection.RESPONSE);
		responseMessage.setMessageId(insertMessage.getMessageId());
		responseMessage.setMessageType(insertMessage.getMessageType());
		responseMessage.setOntology(insertMessage.getOntology());
		responseMessage.setSessionKey(insertMessage.getSessionKey());
		responseMessage.setBody(new SSAPBodyReturnMessage());
		responseMessage.getBody().setOk(true);		
		responseMessage.getBody().setThinKp(insertMessage.getBody().getThinKp());
		responseMessage.getBody().setThinkpInstance(insertMessage.getBody().getThinkpInstance());
		
		try {
			responseMessage.getBody().setData(objectMapper.readTree(repositoryResponse));
		} catch (IOException e) {
			//TODO: LOG
			throw new SSAPProcessorException("Response from repository on insert is not JSON compliant");
		}
		
		return responseMessage;		
	}

}
