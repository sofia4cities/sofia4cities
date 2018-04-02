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
package com.indracompany.sofia2.audit.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.Sofia2EventFactory;
import com.indracompany.sofia2.audit.producer.EventProducer;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
public class AuditableAspect extends BaseAspect {

	@Autowired
	private EventProducer eventProducer;

	@Around(value = "@annotation(auditable)")
	public Object processTx(ProceedingJoinPoint joinPoint, Auditable auditable) throws java.lang.Throwable {

		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();
		long executionTime = -1l;

		final long start = System.currentTimeMillis();
		Object proceed = null;
		
		Sofia2AuditEvent event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.GENERAL, "Processing :"+className + "-> "+methodName);

		try {
			proceed = joinPoint.proceed();
		}

		finally {
			updateStats(className, methodName, (System.currentTimeMillis() - start));
			event.setMessage("Action Performed");
			eventProducer.publish(event);
		}

		executionTime = System.currentTimeMillis() - start;

		log.info("EXECUTION Of " + className + "-> "+methodName + "executed in " + executionTime + "ms");

		return proceed;
	}

	@Before("@annotation(auditable)")
	public void beforeExecution(JoinPoint joinPoint, Auditable auditable) {
		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();

		if (joinPoint.getArgs() != null) {
			int size = joinPoint.getArgs().length;
			if (size > 0)
				log.debug("Before -> CALL FOR : " +  className + "-> "+methodName + " WITH ARGS :" + joinPoint.getArgs()[0]);
		}
	}

	@SuppressWarnings("rawtypes")
	@AfterReturning(pointcut = "@annotation(auditable)", returning = "retVal")
	public void afterReturningExecution(JoinPoint joinPoint, Object retVal, Auditable auditable) {
		
		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();

		if (retVal != null) {
			
			Sofia2AuditEvent event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.GENERAL, "Returned Execution of :"+className + "-> "+methodName);
			Map<String, Object> data= new HashMap<String,Object>();
			data.put("data", retVal);
			event.setData(data);
			eventProducer.publish(event);

			if (retVal instanceof ResponseEntity) {
				ResponseEntity response = (ResponseEntity) retVal;
				log.debug("After -> CALL FOR " +  className + "-> "+methodName  + " RETURNED CODE: " + response.getStatusCode());
			}
		}
	}

	@AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Exception ex, Auditable auditable) {

		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();
		
		Sofia2AuditEvent event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, null, null);

		event.setMessage("Exception Detected");
		event.setError(ex.getMessage());
		
		Sofia2EventFactory.setErrorDetails(event, ex);
		eventProducer.publish(event);

		
		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> "+methodName);
		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> "+methodName + " Exception Message: " + ex.getMessage());
		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> "+methodName + " Class: " + ex.getClass().getName());

	}

}