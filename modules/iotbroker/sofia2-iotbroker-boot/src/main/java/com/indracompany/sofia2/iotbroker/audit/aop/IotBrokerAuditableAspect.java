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

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.aop.BaseAspect;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEventFactory;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyCommandMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyQueryMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodySubscribeMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUnsubscribeMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
public class IotBrokerAuditableAspect extends BaseAspect {

	@Autowired
	SecurityPluginManager securityPluginManager;

	// @Around(value = "@annotation(IotBrokerAuditable)")
	public Object processTx(ProceedingJoinPoint joinPoint, IotBrokerAuditable auditable) throws java.lang.Throwable {
		return joinPoint.proceed();
	}

	@Before("@annotation(auditable)")
	public void beforeExecution(JoinPoint joinPoint, IotBrokerAuditable auditable) {

		final GatewayInfo info = (GatewayInfo) getTheObject(joinPoint, GatewayInfo.class);
		final SSAPMessage message = (SSAPMessage) getTheObject(joinPoint, SSAPMessage.class);

		final IotBrokerAuditEvent event = getEvent(message, info);

		if (event != null) {
			eventProducer.publish(event);
		}
	}

	@SuppressWarnings("rawtypes")
	// @AfterReturning(pointcut = "@annotation(auditable)", returning = "retVal")
	public void afterReturningExecution(JoinPoint joinPoint, Object retVal, IotBrokerAuditable auditable) {

	}

	@AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Exception ex, IotBrokerAuditable auditable) {

		final String className = getClassName(joinPoint);
		final String methodName = getMethod(joinPoint).getName();

		final GatewayInfo info = (GatewayInfo) getTheObject(joinPoint, GatewayInfo.class);
		final SSAPMessage message = (SSAPMessage) getTheObject(joinPoint, SSAPMessage.class);

		Sofia2AuditError event = null;

		if (message != null && info != null) {

			final IotBrokerAuditEvent iotEvent = getEvent(message, info);

			if (iotEvent != null) {

				final String messageOperation = "Exception Detected while operation : " + iotEvent.getOntology() + " Type : "
						+ iotEvent.getOperationType() + " By User : " + iotEvent.getSession().getUserID();

				event = Sofia2EventFactory.createAuditEventError(joinPoint, messageOperation, Module.IOTBROKER, ex);
			}

		} else {

			event = Sofia2EventFactory.createAuditEventError(joinPoint, "", Module.IOTBROKER, ex);
		}

		event.setMessage("Exception Detected");
		event.setEx(ex);

		Sofia2EventFactory.setErrorDetails(event, ex);

		if (event != null) {
			eventProducer.publish(event);
		}

		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName);
		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName + " Exception Message: "
				+ ex.getMessage());
		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName + " Class: "
				+ ex.getClass().getName());

	}

	public IotBrokerAuditEvent getEvent(SSAPMessage message, GatewayInfo info) {

		final IotBrokerAuditEvent event = null;

		final Optional<IoTSession> sessionPlugin = securityPluginManager.getSession(message.getSessionKey());

		if (SSAPMessageTypes.JOIN.equals(message.getMessageType())) {

			final SSAPBodyJoinMessage joinMessage = (SSAPBodyJoinMessage) message.getBody();
			message.getMessageId();
			joinMessage.getClientPlatform();
			joinMessage.getClientPlatformInstance();
			joinMessage.getToken();
			final String messageText = "Join message by clientPlatform  " + joinMessage.getClientPlatform();
			return IotBrokerAuditEventFactory.createIotBrokerAuditEvent(joinMessage, messageText, info);

		} else if (sessionPlugin.isPresent()) {

			final IoTSession session = sessionPlugin.get();

			switch (message.getMessageType()) {
			case NONE:
				break;
			case JOIN:
			case LEAVE:

				break;
			case INSERT:
				final SSAPBodyInsertMessage insertMessage = (SSAPBodyInsertMessage) message.getBody();
				final String insertMessageText = "Insert message on ontology " + insertMessage.getOntology() + " by user "
						+ session.getUserID();
				return IotBrokerAuditEventFactory.createIotBrokerAuditEvent(insertMessage, insertMessageText, session,
						info);

			case UPDATE_BY_ID:
				final SSAPBodyUpdateByIdMessage updateIdMessage = (SSAPBodyUpdateByIdMessage) message.getBody();
				final String updateMessageIdText = "Update ontology " + updateIdMessage.getOntology() + " by user "
						+ session.getUserID();

				break;
				// return IotBrokerAuditEventFactory.createIotBrokerAuditEvent(updateIdMessage,
				// updateMessageText, session, info);
			case UPDATE:
				final SSAPBodyUpdateMessage updateMessage = (SSAPBodyUpdateMessage) message.getBody();
				final String updateMessageText = "Update ontology " + updateMessage.getOntology() + " by user "
						+ session.getUserID();
				return IotBrokerAuditEventFactory.createIotBrokerAuditEvent(updateMessage, updateMessageText, session,
						info);
			case DELETE_BY_ID:

				break;
			case DELETE:

				final SSAPBodyDeleteMessage deletetMessage = (SSAPBodyDeleteMessage) message.getBody();
				deletetMessage.getOntology();
				deletetMessage.getQuery();
				break;
			case QUERY:
				final SSAPBodyQueryMessage queryMessage = (SSAPBodyQueryMessage) message.getBody();
				queryMessage.getOntology();
				queryMessage.getQuery();
				queryMessage.getQueryType();
				queryMessage.getResultFormat();
				break;
			case SUBSCRIBE:
				final SSAPBodySubscribeMessage subscribeMessage = (SSAPBodySubscribeMessage) message.getBody();
				subscribeMessage.getOntology();
				subscribeMessage.getQuery();
				subscribeMessage.getQueryType();
				break;
			case UNSUBSCRIBE:
				final SSAPBodyUnsubscribeMessage unsubscribeMessage = (SSAPBodyUnsubscribeMessage) message.getBody();
				unsubscribeMessage.getSubscriptionId();
				break;
			case INDICATION:
				final SSAPBodyIndicationMessage indicationMessage = (SSAPBodyIndicationMessage) message.getBody();
				indicationMessage.getOntology();
				indicationMessage.getQuery();
				indicationMessage.getData();
				indicationMessage.getSubsciptionId();
				break;
			case COMMAND:
				final SSAPBodyCommandMessage commandMessage = (SSAPBodyCommandMessage) message.getBody();
				commandMessage.getCommand();
				commandMessage.getCommandId();
				break;
			default:
				break;
			}
		}

		return event;
	}

}
