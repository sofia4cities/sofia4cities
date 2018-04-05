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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
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
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Order
@Component
@Slf4j
public class AuditableAspect extends BaseAspect {

	@Autowired
	private EventProducer eventProducer;

	// @Around(value = "@annotation(auditable)")
	public Object processTx(ProceedingJoinPoint joinPoint, Auditable auditable) throws java.lang.Throwable {

		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();
		long executionTime = -1l;

		final long start = System.currentTimeMillis();
		Object proceed = null;

		Sofia2AuditEvent event = null;

		OperationModel model = (OperationModel) getTheObject(joinPoint, OperationModel.class);
		if (model != null) {
			event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.DATA,
					"Processing :" + className + "-> " + methodName);
			event.setOntology(model.getOntologyName());
			event.setOperationType(model.getOperationType().name());
			event.setUser(model.getUser());
			event.setMessage("Executing operation for Ontology : " + model.getOntologyName() + " Type : "
					+ model.getOperationType().name() + " By User : " + model.getUser() + " With ObjectId: "
					+ model.getObjectId());
		}

		else {
			event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.GENERAL,
					"Processing :" + className + "-> " + methodName);
			event.setMessage("Action Performed");
		}

		try {
			proceed = joinPoint.proceed();
		}

		finally {
			updateStats(className, methodName, (System.currentTimeMillis() - start));

			eventProducer.publish(event);
		}

		executionTime = System.currentTimeMillis() - start;

		log.info("EXECUTION Of " + className + "-> " + methodName + "executed in " + executionTime + "ms");

		return proceed;
	}

	@Before("@annotation(auditable)")
	public void beforeExecution(JoinPoint joinPoint, Auditable auditable) {
		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();

		Sofia2AuditEvent event = null;

		OperationModel model = (OperationModel) getTheObject(joinPoint, OperationModel.class);
		if (model != null) {
			event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.DATA,
					"Processing :" + className + "-> " + methodName);
			event.setOntology(model.getOntologyName());
			event.setOperationType(model.getOperationType().name());
			event.setUser(model.getUser());
			event.setMessage("Before Executing operation for Ontology : " + model.getOntologyName() + " Type : "
					+ model.getOperationType().name() + " By User : " + model.getUser() + " With ObjectId: "
					+ model.getObjectId());
		}

		else {
			event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.GENERAL,
					"Processing :" + className + "-> " + methodName);
			event.setMessage("Action Being Performed");
		}

		eventProducer.publish(event);
	}

	@SuppressWarnings("rawtypes")
	// @AfterReturning(pointcut = "@annotation(auditable)", returning = "retVal")
	public void afterReturningExecution(JoinPoint joinPoint, Object retVal, Auditable auditable) {

		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();

		if (retVal != null) {

			Sofia2AuditEvent event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.DATA,
					"Returned Execution of :" + className + "-> " + methodName);

			if (retVal instanceof ResponseEntity) {
				ResponseEntity response = (ResponseEntity) retVal;
				log.debug("After -> CALL FOR " + className + "-> " + methodName + " RETURNED CODE: "
						+ response.getStatusCode());
			}

			if (retVal instanceof OperationResultModel) {
				OperationModel model = (OperationModel) getTheObject(joinPoint, OperationModel.class);

				OperationResultModel response = (OperationResultModel) retVal;
				event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.DATA,
						"Processing :" + className + "-> " + methodName);
				event.setOntology(model.getOntologyName());
				event.setOperationType(model.getOperationType().name());
				event.setUser(model.getUser());
				event.setMessage(
						"Returned operation : " + model.getOntologyName() + " Type : " + model.getOperationType().name()
								+ " By User : " + model.getUser() + " REsponse Message: " + response.getMessage());
			}

			// not storing returned value at this moment.. Do I need?

			/*
			 * Map<String, Object> data= new HashMap<String,Object>(); data.put("data",
			 * retVal); event.setData(data);
			 */
			eventProducer.publish(event);
		}
	}

	@AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Exception ex, Auditable auditable) {

		String className = getClassName(joinPoint);
		String methodName = getMethod(joinPoint).getName();

		Sofia2AuditEvent event = null;

		OperationModel model = (OperationModel) getTheObject(joinPoint, OperationModel.class);
		if (model != null) {
			event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.ERROR, null);
			event.setOntology(model.getOntologyName());
			event.setOperationType(model.getOperationType().name());
			event.setUser(model.getUser());
			event.setMessage("Exception Detected while operation : " + model.getOntologyName() + " Type : "
					+ model.getOperationType().name() + " By User : " + model.getUser());
		} else {
			event = Sofia2EventFactory.createAuditEvent(joinPoint, auditable, EventType.ERROR, null);
		}

		event.setMessage("Exception Detected");
		event.setError(ex.getMessage());
		event.setType(EventType.ERROR);

		Sofia2EventFactory.setErrorDetails(event, ex);
		eventProducer.publish(event);

		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName);
		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName + " Exception Message: "
				+ ex.getMessage());
		log.debug("INFO Log @@AfterThrowing Call For: " + className + "-> " + methodName + " Class: "
				+ ex.getClass().getName());

	}

	private Object getTheObject(JoinPoint joinPoint, Class T) {
		Object obj = null;
		if (joinPoint.getArgs() != null) {
			int size = joinPoint.getArgs().length;
			if (size > 0) {
				Object[] obs = joinPoint.getArgs();
				for (Object object : obs) {
					if (T.isInstance(object))
						obj = object;
				}
			}
		}
		return obj;
	}

}