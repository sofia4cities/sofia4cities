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
package com.indracompany.sofia2.iotbroker.audit.processor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.audit.bean.AuditConst;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.iotbroker.audit.aop.MessageAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPAuditProcessorException;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IotBrokerAuditProcessor {

	@Autowired
	private SecurityPluginManager securityPluginManager;

	@Autowired
	private List<MessageAuditProcessor> processors;

	public IotBrokerAuditEvent getEvent(SSAPMessage<? extends SSAPBodyMessage> message, GatewayInfo info)
			throws SSAPAuditProcessorException {

		log.debug("getEvent from message " + message);

		IotBrokerAuditEvent event = null;

		IoTSession session = getSession(message);

		MessageAuditProcessor processor = proxyProcesor(message);

		event = processor.process(message, session, info);

		return event;
	}

	public Sofia2AuditError getErrorEvent(SSAPMessage<? extends SSAPBodyMessage> message, GatewayInfo info,
			Exception ex) throws SSAPAuditProcessorException {

		log.debug("getErrorEvent from message " + message);

		Sofia2AuditError event = null;

		if (message != null && info != null) {

			final IotBrokerAuditEvent iotEvent = getEvent(message, info);

			if (iotEvent != null) {

				IoTSession session = getSession(message);

				if (session != null) {
					String messageOperation = "Exception Detected while operation : " + iotEvent.getOntology()
							+ " Type : " + iotEvent.getOperationType() + " By User : " + session.getUserID();

					event = Sofia2EventFactory.createAuditEventError(session.getUserID(), messageOperation,
							Module.IOTBROKER, ex);

				} else {
					String messageOperation = "Exception Detected while operation : " + iotEvent.getOntology()
							+ " Type : " + iotEvent.getOperationType();

					event = Sofia2EventFactory.createAuditEventError(messageOperation, Module.IOTBROKER, ex);
				}
			}

		} else {

			event = Sofia2EventFactory.createAuditEventError("Exception Detected", Module.IOTBROKER, ex);
		}

		Sofia2EventFactory.setErrorDetails(event, ex);

		return event;
	}

	public IotBrokerAuditEvent completeEventWithResponseMessage(SSAPMessage<SSAPBodyReturnMessage> message,
			IotBrokerAuditEvent event) {

		if (SSAPMessageDirection.ERROR.equals(message.getDirection())) {
			event.setMessage(message.getBody().getError());
			event.setResultOperation(ResultOperationType.ERROR);
		}

		IoTSession session = getSession(message);

		if (session != null) {
			event.setUser(session.getUserID());
			event.setSessionKey(message.getSessionKey());
			event.setClientPlatform(session.getClientPlatform());
			event.setClientPlatformInstance(session.getClientPlatformInstance());
		}

		if (event.getUser() == null || "".equals(event.getUser())) {
			event.setUser(AuditConst.ANONYMOUS_USER);
		}

		return event;
	}

	private MessageAuditProcessor proxyProcesor(SSAPMessage<? extends SSAPBodyMessage> message)
			throws SSAPAuditProcessorException {

		if (null == message.getMessageType()) {
			throw new SSAPAuditProcessorException(MessageException.ERR_SSAP_MESSAGETYPE_MANDATORY_NOT_NULL);
		}

		final SSAPMessageTypes type = message.getMessageType();

		final List<MessageAuditProcessor> filteredProcessors = processors.stream()
				.filter(p -> p.getMessageTypes().contains(type)).collect(Collectors.toList());

		if (filteredProcessors.isEmpty()) {
			throw new SSAPAuditProcessorException(
					String.format(MessageException.ERR_PROCESSOR_NOT_FOUND, message.getMessageType()));
		}

		return filteredProcessors.get(0);

	}

	private IoTSession getSession(SSAPMessage<? extends SSAPBodyMessage> message) {

		IoTSession session = null;

		Optional<IoTSession> sessionPlugin = securityPluginManager.getSession(message.getSessionKey());

		if (sessionPlugin.isPresent()) {
			session = sessionPlugin.get();
		}

		return session;
	}

}
