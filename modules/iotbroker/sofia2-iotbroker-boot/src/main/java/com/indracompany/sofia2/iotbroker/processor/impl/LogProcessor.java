package com.indracompany.sofia2.iotbroker.processor.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.OntologySchemaException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.router.service.app.service.RouterService;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLogMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

public class LogProcessor implements MessageTypeProcessor {

	@Autowired
	private ClientPlatformService clientPlatformService;
	@Autowired
	private SecurityPluginManager securityPluginManager;
	@Autowired
	private RouterService routerService;

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message)
			throws BaseException, Exception {
		final SSAPMessage<SSAPBodyLogMessage> logMessage = (SSAPMessage<SSAPBodyLogMessage>) message;
		SSAPMessage<SSAPBodyReturnMessage> response = new SSAPMessage<>();
		final Optional<IoTSession> session = this.securityPluginManager.getSession(logMessage.getSessionKey());
		ClientPlatform client = null;
		Ontology ontology = null;
		if (session.isPresent()) {
			client = this.clientPlatformService.getByIdentification(session.get().getClientPlatform());
			ontology = this.clientPlatformService.getDeviceLogOntology(client);
			if (client != null && ontology == null)
				ontology = this.clientPlatformService.createDeviceLogOntology(client.getIdentification());
		}
		// TODO generate instance
		return response;
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.LOG);
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
			throws OntologySchemaException, BaseException, Exception {
		final SSAPMessage<SSAPBodyLogMessage> logMessage = (SSAPMessage<SSAPBodyLogMessage>) message;
		if (logMessage.getBody().getMessage().isEmpty() || logMessage.getBody().getLevel() == null
				|| logMessage.getBody().getStatus() == null) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY,
					"message, log level, status", message.getMessageType().name()));
		}
		return true;
	}

}
