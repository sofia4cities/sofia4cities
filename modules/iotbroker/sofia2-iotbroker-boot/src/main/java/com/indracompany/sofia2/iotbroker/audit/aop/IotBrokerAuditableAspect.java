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
package com.indracompany.sofia2.iotbroker.audit.aop;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.aop.BaseAspect;
import com.indracompany.sofia2.audit.bean.AuditConst;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
public class IotBrokerAuditableAspect extends BaseAspect {

	@Autowired
	private SecurityPluginManager securityPluginManager;

	@Autowired
	private List<MessageAuditProcessor> processors;

	@Around(value = "@annotation(auditable) && args(message, info,..)")
	public Object processTx(ProceedingJoinPoint joinPoint, IotBrokerAuditable auditable,
			SSAPMessage<? extends SSAPBodyMessage> message, GatewayInfo info) throws java.lang.Throwable {
		try {

			IotBrokerAuditEvent event = getEvent(message, info);

			@SuppressWarnings("unchecked")
			SSAPMessage<SSAPBodyReturnMessage> returnVal = (SSAPMessage<SSAPBodyReturnMessage>) joinPoint.proceed();

			completeEvent(returnVal, event);

			if (event != null) {
				eventProducer.publish(event);
			}

			return returnVal;

		} catch (Throwable e) {

			throw e;
		}
	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(message, info,..)", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Exception ex, IotBrokerAuditable auditable,
			SSAPMessage<? extends SSAPBodyMessage> message, GatewayInfo info) {

		try {

			final String className = getClassName(joinPoint);
			final String methodName = getMethod(joinPoint).getName();

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

			if (event != null) {
				eventProducer.publish(event);
			}

			log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName);
			log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName + " Exception Message: "
					+ ex.getMessage());
			log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName + " Class: "
					+ ex.getClass().getName());

		} catch (Exception e) {
			log.error("error auditing doRecoveryActions", e);
		}

	}

	public IotBrokerAuditEvent getEvent(SSAPMessage<? extends SSAPBodyMessage> message, GatewayInfo info) {

		IotBrokerAuditEvent event = null;

		try {

			IoTSession session = getSession(message);

			MessageAuditProcessor processor = proxyProcesor(message);

			event = processor.process(message, session, info);

		} catch (SSAPProcessorException e) {
			log.error("audit processor not found", e);
		}

		return event;
	}

	public IotBrokerAuditEvent completeEvent(SSAPMessage<SSAPBodyReturnMessage> message, IotBrokerAuditEvent event) {

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
			throws SSAPProcessorException {

		if (null == message.getMessageType()) {
			throw new SSAPProcessorException(MessageException.ERR_SSAP_MESSAGETYPE_MANDATORY_NOT_NULL);
		}

		final SSAPMessageTypes type = message.getMessageType();

		final List<MessageAuditProcessor> filteredProcessors = processors.stream()
				.filter(p -> p.getMessageTypes().contains(type)).collect(Collectors.toList());

		if (filteredProcessors.isEmpty()) {
			throw new SSAPProcessorException(
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
