package com.indracompany.sofia2.iotbroker.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

@Profile({"dummy-dev"})
@Component
public class InsertProcessor implements MessageTypeProcessor {

	@Autowired
	BasicOpsDBRepository repository;
	
	@Override
	public <T extends SSAPBodyMessage> SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message) {
		@SuppressWarnings("unchecked")
		SSAPMessage<SSAPBodyOperationMessage> insertMessage = (SSAPMessage<SSAPBodyOperationMessage>) message;
		
		//TODO: Dont forget ContextData
		repository.insert(insertMessage.getOntology(), insertMessage.getBody().getData().toString());				
		return null;		
	}

}
