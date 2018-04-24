package com.indracompany.sofia2.api.audit.aop;

import java.util.Map;

import org.apache.camel.Exchange;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.audit.bean.ApiManagerAuditEvent;
import com.indracompany.sofia2.api.service.impl.ApiServiceException;
import com.indracompany.sofia2.audit.aop.BaseAspect;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
@ConditionalOnExpression("${sofia2.apimanager.audit.enabled:true}")
public class ApiManagerAuditableAspect extends BaseAspect {

	@Autowired
	private ApiManagerAuditProcessor apiManagerAuditProcessor;

	@After(value = "@annotation(auditable) && args(exchange,..) && execution (* com.indracompany.sofia2.api.service.impl.ApiServiceImpl.process(..))")
	public void process(JoinPoint joinPoint, ApiManagerAuditable auditable, Exchange exchange) {
		try {

			ApiManagerAuditEvent event = apiManagerAuditProcessor.getStoppedEvent(exchange);

			eventProducer.publish(event);

		} catch (Exception e) {
			log.error("error after process method ", e);
		}
	}

	@Around(value = "@annotation(auditable) && args(data, exchange,..) && execution (* com.indracompany.sofia2.api.service.impl.ApiServiceImpl.processQuery(..))")
	public Object processQuery(ProceedingJoinPoint joinPoint, ApiManagerAuditable auditable, Map<String, Object> data,
			Exchange exchange) throws java.lang.Throwable {

		log.debug("execute api manager aspect method process");

		ApiManagerAuditEvent event = null;
		Map<String, Object> retVal = null;

		try {

			event = apiManagerAuditProcessor.getEvent(data);

			retVal = (Map<String, Object>) joinPoint.proceed();

			event = apiManagerAuditProcessor.completeEvent(retVal, event);

		} catch (ApiServiceException e) {
			log.error("Error process apimanager process ", e);
			if (event != null) {
				event.setResultOperation(ResultOperationType.ERROR);
				event.setMessage(e.getMessage());
			}
		}

		eventProducer.publish(event);

		return retVal;
	}

	@AfterThrowing(pointcut = "@annotation(auditable) && args(data, exchange,..)", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Exception ex, ApiManagerAuditable auditable,
			Map<String, Object> data, Exchange exchange) {

		log.debug("execute aspect iotborkerauditable method doRecoveryActions");

		try {
			Sofia2AuditError event = apiManagerAuditProcessor.getErrorEvent(data, exchange, ex);
			eventProducer.publish(event);

		} catch (Exception e) {
			log.error("error auditing apimanager doRecoveryActions", e);
		}
	}
}
