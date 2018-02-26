package com.indracompany.sofia2.iotbroker.processor.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.OntologySchemaException;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@Component
public class UpdateProcessor implements MessageTypeProcessor {

	@Autowired
	BasicOpsDBRepository repository;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	SecurityPluginManager securityPluginManager;

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message)
			throws BaseException, Exception {

		final SSAPMessage<SSAPBodyInsertMessage> insertMessage = (SSAPMessage<SSAPBodyInsertMessage>) message;
		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();

		repository.updateNative(message.getOntology(), updateStmt);

	}
	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.UPDATE);
	}

	//TODO: Refactor this method is duplicated in insert processor
	@Override
	public void validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
			throws OntologySchemaException, BaseException, Exception {
		final SSAPMessage<SSAPBodyInsertMessage> operationMessage = (SSAPMessage<SSAPBodyInsertMessage>) message;
		securityPluginManager.checkAuthorization(message.getMessageType(), operationMessage.getOntology(), message.getSessionKey());

	}



}
