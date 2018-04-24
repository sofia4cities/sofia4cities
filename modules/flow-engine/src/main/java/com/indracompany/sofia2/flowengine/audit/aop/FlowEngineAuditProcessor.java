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
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent.FlowEngineAuditEventBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlowEngineAuditProcessor {

	@Autowired
	private FlowDomainRepository domainRepository;

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

	public FlowEngineAuditEvent getEvent(String methodName, String retVal, FlowEngineDomain domain) {

		log.debug("getEvent for operation " + methodName + " and return value " + retVal);
		String userId = getUserId(domain);
		Date today = new Date();

		FlowEngineAuditEventBuilder builder = FlowEngineAuditEvent.builder();

		if (!"OK".equals(retVal)) {
			builder.message(retVal);
			builder.resultOperation(ResultOperationType.ERROR);
		} else {
			String message = "Executed operation " + methodName + " for domain " + domain.getDomain() + " by user "
					+ userId;
			builder.message(message);
			builder.resultOperation(ResultOperationType.SUCCESS);
		}

		FlowEngineAuditEvent event = builder.domain(domain.getDomain()).id(UUID.randomUUID().toString())
				.module(Module.FLOWENGINE).type(EventType.FLOWENGINE).operationType(OperationType.START.name())
				.timeStamp(today.getTime()).user(userId)
				.formatedTimeStamp(CalendarUtil.builder().build().convert(today)).build();

		return event;
	}

	public FlowEngineAuditEvent getEvent(String methodName, String domainId) {

		log.debug("getEvent for operation " + methodName + " and domain " + domainId);
		String userId = getUserId(domainId);
		Date today = new Date();

		String message = "Executed operation " + methodName + " for domain " + domainId + " by user " + userId;

		FlowEngineAuditEvent event = FlowEngineAuditEvent.builder().domain(domainId).id(UUID.randomUUID().toString())
				.module(Module.FLOWENGINE).type(EventType.FLOWENGINE).operationType(OperationType.START.name())
				.timeStamp(today.getTime()).user(userId).message(message).resultOperation(ResultOperationType.SUCCESS)
				.formatedTimeStamp(CalendarUtil.builder().build().convert(today)).build();

		return event;
	}

	public Sofia2AuditError getErrorEvent(String methodName, FlowEngineDomain domain, Exception ex) {

		log.debug("getEventError for operation " + methodName);
		String userId = getUserId(domain);
		String messageOperation = "Exception Detected while executing " + methodName + " for domain : "
				+ domain.getDomain();

		Sofia2AuditError event = Sofia2EventFactory.builder().build().createAuditEventError(userId, messageOperation,
				Module.FLOWENGINE, ex);

		return event;
	}

}
