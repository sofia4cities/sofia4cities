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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.aop.BaseAspect;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.audit.processor.IotBrokerAuditProcessor;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
public class IotBrokerAuditableAspect extends BaseAspect {

	@Autowired
	private IotBrokerAuditProcessor auditProcessor;

	@Around(value = "@annotation(auditable) && args(message, info,..)")
	public Object processTx(ProceedingJoinPoint joinPoint, IotBrokerAuditable auditable,
			SSAPMessage<? extends SSAPBodyMessage> message, GatewayInfo info) throws java.lang.Throwable {

		log.debug("execute iotbroker aspect method process");

		try {

			IotBrokerAuditEvent event = auditProcessor.getEvent(message, info);

			@SuppressWarnings("unchecked")
			SSAPMessage<SSAPBodyReturnMessage> returnVal = (SSAPMessage<SSAPBodyReturnMessage>) joinPoint.proceed();

			event = auditProcessor.completeEventWithResponseMessage(returnVal, event);

			eventProducer.publish(event);

			return returnVal;

		} catch (Throwable e) {
			throw e;
		}

	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(message, info,..)", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Exception ex, IotBrokerAuditable auditable,
			SSAPMessage<? extends SSAPBodyMessage> message, GatewayInfo info) {

		log.debug("execute aspect iotborkerauditable method doRecoveryActions");
		try {

			Sofia2AuditError event = auditProcessor.getErrorEvent(message, info, ex);

			eventProducer.publish(event);

		} catch (Exception e) {
			log.error("error auditing doRecoveryActions", e);
		}

	}

}
