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

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.audit.bean.AuditConst;
import com.indracompany.sofia2.audit.bean.CalendarUtil;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.flowengine.api.rest.pojo.DecodedAuthentication;
import com.indracompany.sofia2.flowengine.api.rest.service.FlowEngineValidationNodeService;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent.FlowEngineAuditEventBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlowEngineAuditProcessor {

	@Autowired
	private FlowDomainRepository domainRepository;

	@Autowired
	private FlowEngineValidationNodeService flowEngineValidationNodeService;

	public String getUserId(String domainId) {

		String userId = null;

		if (domainId != null) {

			FlowDomain flowDomain = domainRepository.findByIdentification(domainId);

			if (flowDomain != null) {
				userId = flowDomain.getUser().getUserId();
			} else {
				userId = AuditConst.ANONYMOUS_USER;
			}
		}

		return userId;
	}

	public String getUserId(FlowEngineDomain domain) {
		String userId = null;

		if (domain != null) {
			userId = getUserId(domain.getDomain());
		}

		return userId;
	}

	public FlowEngineAuditEvent getEvent(String methodName, String retVal, FlowEngineDomain domain,
			OperationType operation) {

		log.debug("getEvent for operation " + methodName + " and return value " + retVal);
		String userId = getUserId(domain);

		FlowEngineAuditEventBuilder builder = FlowEngineAuditEvent.builder();

		ResultOperationType resultOperation = null;
		String message = null;

		if (!"OK".equals(retVal)) {
			builder.message(retVal);
			message = retVal;
			resultOperation = ResultOperationType.ERROR;
		} else {
			message = "Executed operation " + methodName + " for domain " + domain.getDomain() + " by user " + userId;
			resultOperation = ResultOperationType.SUCCESS;
		}

		FlowEngineAuditEvent event = getEvent(domain.getDomain(), userId, operation, message, resultOperation);

		return event;
	}

	public FlowEngineAuditEvent getEvent(String methodName, String domainId, OperationType operation) {

		log.debug("getEvent for operation " + methodName + " and domain " + domainId);
		String userId = getUserId(domainId);

		String message = "Executed operation " + methodName + " for domain " + domainId + " by user " + userId;

		FlowEngineAuditEvent event = getEvent(domainId, userId, operation, message, ResultOperationType.SUCCESS);

		return event;
	}

	public FlowEngineAuditEvent getQueryEvent(String ontology, String query, String queryType, String retVal,
			String authentication) {
		String message = "Query message on ontology " + ontology;
		return getEvent(ontology, query, queryType, null, message, authentication, OperationType.QUERY);
	}

	public FlowEngineAuditEvent getInsertEvent(String ontology, String data, String retVal, String authentication) {
		String message = "Executed insert on ontology " + ontology;
		return getEvent(ontology, null, null, data, message, authentication, OperationType.INSERT);
	}

	public FlowEngineAuditEvent getEvent(String ontology, String query, String queryType, String data, String message,
			String authentication, OperationType operation) {

		log.debug("getEvent for operation");
		FlowEngineAuditEvent event = null;

		try {

			final DecodedAuthentication decodedAuth = flowEngineValidationNodeService.decodeAuth(authentication);
			final User sofia2User = flowEngineValidationNodeService.validateUserCredentials(decodedAuth.getUserId(),
					decodedAuth.getPassword());

			event = getEvent(null, sofia2User.getUserId(), operation, message, ResultOperationType.SUCCESS);
			event.setData(data);
			event.setOntology(ontology);
			event.setQuery(query);

		} catch (Exception e) {
			log.error("error processing getEvent submit operation ", e);
		}

		return event;
	}

	public FlowEngineAuditEvent getEvent(String domain, String user, OperationType operation, String message,
			ResultOperationType result) {

		Date today = new Date();

		return FlowEngineAuditEvent.builder().id(UUID.randomUUID().toString()).module(Module.FLOWENGINE)
				.type(EventType.FLOWENGINE).operationType(operation.name()).timeStamp(today.getTime()).user(user)
				.message(message).resultOperation(result)
				.formatedTimeStamp(CalendarUtil.builder().build().convert(today)).build();
	}

	public Sofia2AuditError getErrorEvent(String methodName, FlowEngineDomain domain, Exception ex) {

		log.debug("getEventError for operation " + methodName);
		String userId = getUserId(domain);
		String messageOperation = "Exception Detected while executing " + methodName + " for domain : "
				+ domain.getDomain();

		return createErrorEvent(userId, messageOperation, ex);
	}

	public Sofia2AuditError getErrorEvent(String message, String authentication, Exception ex) {

		Sofia2AuditError event = null;

		try {

			final DecodedAuthentication decodedAuth = flowEngineValidationNodeService.decodeAuth(authentication);
			final User sofia2User = flowEngineValidationNodeService.validateUserCredentials(decodedAuth.getUserId(),
					decodedAuth.getPassword());

			event = createErrorEvent(sofia2User.getUserId(), message, ex);
		} catch (Exception e) {
			log.error("error getting error event", e);
		}

		return event;
	}

	public Sofia2AuditError getErrorEvent(String method, String message, String domain, Exception ex) {
		String userId = getUserId(domain);
		return createErrorEvent(userId, message, ex);
	}

	public Sofia2AuditError createErrorEvent(String userId, String message, Exception ex) {

		Sofia2AuditError event = Sofia2EventFactory.builder().build().createAuditEventError(userId, message,
				Module.FLOWENGINE, ex);

		return event;
	}

}
