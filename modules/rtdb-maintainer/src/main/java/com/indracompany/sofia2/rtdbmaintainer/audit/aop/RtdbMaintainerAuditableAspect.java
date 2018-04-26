package com.indracompany.sofia2.rtdbmaintainer.audit.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.aop.BaseAspect;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.config.model.Ontology;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
@ConditionalOnExpression("${sofia2.rtdbmaintainer.audit.enabled:true}")
public class RtdbMaintainerAuditableAspect extends BaseAspect {

	@Autowired
	RtdbMaintainerAuditProcessor auditProcessor;

	@Around(value = "@annotation(auditable) && args(ontology,..)")
	public Object exportToJsonAudit(ProceedingJoinPoint joinPoint, RtdbMaintainerAuditable auditable, Ontology ontology)
			throws Throwable {
		log.debug("execute rtdb maintainer aspect method export to json");
		Sofia2AuditEvent event = auditProcessor.getEvent(ontology);
		try {
			joinPoint.proceed();
		} catch (Throwable e) {
			throw e;
		}
		eventProducer.publish(event);
		return null;
	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(ontology,..)", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, RtdbMaintainerAuditable auditable, Ontology ontology) {

	}

}
