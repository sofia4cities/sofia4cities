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
			+ " && (execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.startFlowEngineDomain(..))"
			+ " || execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.createFlowengineDomain(..)))")
	public void processStartOrCreateFlowEngineDomain(JoinPoint joinPoint, FlowEngineAuditable auditable,
			FlowEngineDomain domain, String retVal) throws Throwable {

		Method method = getMethod(joinPoint);
		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(method.getName(), retVal, domain);
		eventProducer.publish(event);

	}

	@AfterReturning(pointcut = "@annotation(auditable) && args(domainId,..)"
			+ " && (execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.stopFlowEngineDomain(..))"
			+ " || execution (* com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClientImpl.deleteFlowEngineDomain(..)))")
	public void processStopFlowEngine(JoinPoint joinPoint, FlowEngineAuditable auditable, String domainId) {

		Method method = getMethod(joinPoint);
		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(method.getName(), domainId);
		eventProducer.publish(event);

	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(domain,..)", throwing = "ex")
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

}
