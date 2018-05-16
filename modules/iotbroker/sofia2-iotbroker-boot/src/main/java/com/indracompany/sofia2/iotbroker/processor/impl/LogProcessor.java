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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.IoTSession;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.OntologySchemaException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.processor.DeviceManager;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.Source;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterService;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLogMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@Component
public class LogProcessor implements MessageTypeProcessor {

	@Autowired
	private ClientPlatformService clientPlatformService;
	@Autowired
	private SecurityPluginManager securityPluginManager;
	@Autowired
	private RouterService routerService;
	@Autowired
	private DeviceManager deviceManager;

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
		if (client != null) {
			JsonNode instance = this.deviceManager.createDeviceLog(client, session.get().getDevice(),
					logMessage.getBody());
			final OperationModel model = OperationModel
					.builder(ontology.getIdentification(), OperationType.POST, client.getUser().getUserId(),
							Source.IOTBROKER)
					.body(instance.toString()).queryType(QueryType.NATIVE).clientPlatformId(client.getIdentification())
					.clientPlatformInstance(session.get().getDevice()).clientSession(logMessage.getSessionKey())
					.clientConnection("").build();

			final NotificationModel modelNotification = new NotificationModel();
			modelNotification.setOperationModel(model);
			try {
				final OperationResultModel result = routerService.insert(modelNotification);
				if (!result.getResult().equals("ERROR")) {
					response.setDirection(SSAPMessageDirection.RESPONSE);
					response.setMessageId(logMessage.getMessageId());
					response.setMessageType(logMessage.getMessageType());
					// responseMessage.setOntology(insertMessage.getOntology());
					response.setSessionKey(logMessage.getSessionKey());
					response.setBody(new SSAPBodyReturnMessage());
					response.getBody().setOk(true);
					response.getBody().setData(instance);
				} else {
					throw new SSAPProcessorException(result.getMessage());
				}

			} catch (Exception e) {
				throw new SSAPProcessorException("Could not create log: " + e);
			}
		} else
			throw new SSAPProcessorException("Could not retrieve Device, log failed");

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
