/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.flowengine.audit.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.aop.BaseAspect;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
@ConditionalOnExpression("${sofia2.flowengine.audit.enabled:true}")
public class FlowEngineAuditableAspect extends BaseAspect {

	@Autowired
	private FlowEngineAuditProcessor flowEngineAuditProcessor;

	@AfterReturning(returning = "retVal", pointcut = "@annotation(auditable) && args(domain,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.startFlowEngineDomain(..))")
	public void processStartFlowEngineDomain(JoinPoint joinPoint, FlowEngineAuditable auditable,
			FlowEngineDomain domain, String retVal) throws Throwable {

		Method method = getMethod(joinPoint);
		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(method.getName(), retVal, domain,
				OperationType.START);
		eventProducer.publish(event);

	}

	@AfterReturning(returning = "retVal", pointcut = "@annotation(auditable) && args(domain,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.createFlowengineDomain(..))")
	public void processCreateFlowEngineDomain(JoinPoint joinPoint, FlowEngineAuditable auditable,
			FlowEngineDomain domain, String retVal) throws Throwable {

		Method method = getMethod(joinPoint);
		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(method.getName(), retVal, domain,
				OperationType.INSERT);
		eventProducer.publish(event);

	}

	@AfterReturning(pointcut = "@annotation(auditable) && args(domainId,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.stopFlowEngineDomain(..))")
	public void processStopFlowEngine(JoinPoint joinPoint, FlowEngineAuditable auditable, String domainId) {

		Method method = getMethod(joinPoint);
		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(method.getName(), domainId, OperationType.STOP);
		eventProducer.publish(event);

	}

	@AfterReturning(pointcut = "@annotation(auditable) && args(domainId,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.deleteFlowEngineDomain(..))")
	public void processDeleteFlowEngine(JoinPoint joinPoint, FlowEngineAuditable auditable, String domainId) {

		Method method = getMethod(joinPoint);
		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(method.getName(), domainId,
				OperationType.DELETE);
		eventProducer.publish(event);

	}

	@AfterReturning(returning = "retVal", pointcut = "@annotation(auditable) && args(ontologyIdentificator, queryType, query, authentication,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.api.rest.service.impl.FlowEngineNodeServiceImpl.submitQuery(..))")
	public void processSubmitQuery(JoinPoint joinPoint, FlowEngineAuditable auditable, String ontologyIdentificator,
			String queryType, String query, String authentication, String retVal) {

		FlowEngineAuditEvent event = flowEngineAuditProcessor.getQueryEvent(ontologyIdentificator, query, queryType,
				retVal, authentication);

		eventProducer.publish(event);
	}

	@AfterReturning(returning = "retVal", pointcut = "@annotation(auditable) && args(ontology, data, authentication,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.api.rest.service.impl.FlowEngineNodeServiceImpl.submitInsert(..))")
	public void processInsert(JoinPoint joinPoint, FlowEngineAuditable auditable, String ontology, String data,
			String authentication, String retVal) {

		FlowEngineAuditEvent event = flowEngineAuditProcessor.getInsertEvent(ontology, data, retVal, authentication);

		eventProducer.publish(event);
	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(domain,..)"
			+ " && (execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.startFlowEngineDomain(..))"
			+ " || execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.createFlowengineDomain(..)))", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Exception ex, FlowEngineAuditable auditable,
			FlowEngineDomain domain) {

		log.debug("execute aspect flowengineAuditable method doRecoveryActions");

		try {
			Method method = getMethod(joinPoint);
			Sofia2AuditError event = flowEngineAuditProcessor.getErrorEvent(method.getName(), domain, ex);
			eventProducer.publish(event);

		} catch (Exception e) {
			log.error("error auditing apimanager doRecoveryActions", e);
		}
	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(domain,..)"
			+ " && (execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.stopFlowEngineDomain(..))"
			+ " || execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.deleteFlowEngineDomain(..)))", throwing = "ex")
	public void doRecoveryActionsDoamin(JoinPoint joinPoint, Exception ex, FlowEngineAuditable auditable,
			String domain) {

		log.debug("execute aspect flowengineAuditable method doRecoveryActions");

		try {
			Method method = getMethod(joinPoint);
			String message = "Exception Detected while executing " + method.getName() + " for domain : " + domain;
			Sofia2AuditError event = flowEngineAuditProcessor.getErrorEvent(method.getName(), message, domain, ex);
			eventProducer.publish(event);

		} catch (Exception e) {
			log.error("error auditing apimanager doRecoveryActions", e);
		}
	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(ontology,queryType,query,authentication,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.submitQuery(..))", throwing = "ex")
	public void doRecoveryActionsFlowEngineNodeQuery(JoinPoint joinPoint, FlowEngineAuditable auditable, Exception ex,
			String ontology, String queryType, String query, String authentication) {

		log.debug("execute aspect flowengineAuditable method doRecoveryActions");

		try {
			Method method = getMethod(joinPoint);
			String message = "Exception Detected while executing " + method.getName() + ", "
					+ "operation query on ontology " + ontology + " with query " + query;

			Sofia2AuditError event = flowEngineAuditProcessor.getErrorEvent(message, authentication, ex);

			eventProducer.publish(event);

		} catch (Exception e) {
			log.error("error auditing apimanager doRecoveryActions", e);
		}
	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(ontology,data,authentication,..)"
			+ " && execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.submitInsert(..))", throwing = "ex")
	public void doRecoveryActionsFlowEngineNodeInsert(JoinPoint joinPoint, FlowEngineAuditable auditable, Exception ex,
			String ontology, String data, String authentication) {
		log.debug("execute aspect flowengineAuditable method doRecoveryActions");

		try {

			Method method = getMethod(joinPoint);
			String message = "Exception Detected while executing " + method.getName() + ", "
					+ "Operation insert on ontology " + ontology + " with data " + data;

			Sofia2AuditError event = flowEngineAuditProcessor.getErrorEvent(message, authentication, ex);

			eventProducer.publish(event);

		} catch (Exception e) {
			log.error("error auditing apimanager doRecoveryActions", e);
		}
	}

}
