package com.indracompany.sofia2.api.audit.aop;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.audit.bean.ApiManagerAuditEvent;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.audit.bean.AuditConst;
import com.indracompany.sofia2.audit.bean.CalendarUtil;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiManagerAuditProcessor {

	public ApiManagerAuditEvent getStoppedEvent(Exchange exchange) {

		ApiManagerAuditEvent event = null;
		try {
			if ("STOP".equals(exchange.getIn().getHeader(ApiServiceInterface.STATUS))) {

				String reason = (String) exchange.getIn().getHeader(ApiServiceInterface.REASON);

				String remoteAddress = (String) exchange.getIn().getHeader(ApiServiceInterface.REMOTE_ADDRESS);
				Ontology ontology = (Ontology) exchange.getIn().getHeader(ApiServiceInterface.ONTOLOGY);
				String method = (String) exchange.getIn().getHeader(ApiServiceInterface.METHOD);

				String query = (String) exchange.getIn().getHeader(ApiServiceInterface.QUERY);
				String body = (String) exchange.getIn().getHeader(ApiServiceInterface.BODY);

				OperationType operationType = getAuditOperationFromMethod(method);

				User user = (User) exchange.getIn().getHeader(ApiServiceInterface.USER);

				String userId = (user != null) ? user.getUserId() : AuditConst.ANONYMOUS_USER;
				String ontologyId = (ontology != null) ? ontology.getIdentification() : null;
				Date today = new Date();

				event = ApiManagerAuditEvent.builder().id(UUID.randomUUID().toString()).module(Module.APIMANAGER)
						.type(EventType.APIMANAGER).operationType((operationType != null) ? operationType.name() : null)
						.resultOperation(ResultOperationType.ERROR).remoteAddress(remoteAddress).message(reason)
						.data(body).ontology(ontologyId).query(query).timeStamp(today.getTime()).user(userId)
						.formatedTimeStamp(CalendarUtil.builder().build().convert(today)).build();

			}

		} catch (Exception e) {
			log.error("error processing stopped event ", e);
		}
		return event;
	}

	public ApiManagerAuditEvent getEvent(Map<String, Object> data) {

		String remoteAddress = (String) data.get(ApiServiceInterface.REMOTE_ADDRESS);
		Ontology ontology = (Ontology) data.get(ApiServiceInterface.ONTOLOGY);
		String method = (String) data.get(ApiServiceInterface.METHOD);

		String query = (String) data.get(ApiServiceInterface.QUERY);
		String body = (String) data.get(ApiServiceInterface.BODY);

		User user = (User) data.get(ApiServiceInterface.USER);

		OperationType operationType = getAuditOperationFromMethod(method);

		Date today = new Date();
		String message = "";

		ApiManagerAuditEvent event = ApiManagerAuditEvent.builder().id(UUID.randomUUID().toString())
				.module(Module.APIMANAGER).type(EventType.APIMANAGER).operationType(operationType.name())
				.resultOperation(ResultOperationType.SUCCESS).remoteAddress(remoteAddress).message(message).data(body)
				.ontology(ontology.getIdentification()).query(query).timeStamp(today.getTime()).user(user.getUserId())
				.formatedTimeStamp(CalendarUtil.builder().build().convert(today)).build();

		return event;
	}

	public Sofia2AuditError getErrorEvent(Map<String, Object> data, Exchange exchange, Exception ex) {

		String remoteAddress = (String) data.get(ApiServiceInterface.REMOTE_ADDRESS);
		Ontology ontology = (Ontology) data.get(ApiServiceInterface.ONTOLOGY);
		String method = (String) data.get(ApiServiceInterface.METHOD);
		User user = (User) data.get(ApiServiceInterface.USER);

		OperationType operationType = getAuditOperationFromMethod(method);

		String messageOperation = "Exception Detected while operation : " + ontology.getId() + " Type : "
				+ operationType.name();

		Sofia2AuditError event = Sofia2EventFactory.builder().build().createAuditEventError(user.getUserId(),
				messageOperation, remoteAddress, Module.APIMANAGER, ex);

		return event;
	}

	public ApiManagerAuditEvent completeEvent(Map<String, Object> retVal, ApiManagerAuditEvent event) {

		if (event != null && event.getUser() == null || "".equals(event.getUser())) {
			log.debug("the user is null so set user to anonymous");
			event.setUser(AuditConst.ANONYMOUS_USER);
		}

		return event;
	}

	public OperationType getAuditOperationFromMethod(String method) {

		log.debug("get audit operation from method " + method);
		OperationType operationType = null;

		if (method.equalsIgnoreCase(ApiOperation.Type.GET.name())) {
			operationType = OperationType.QUERY;
		} else if (method.equalsIgnoreCase(ApiOperation.Type.POST.name())) {
			operationType = OperationType.INSERT;
		} else if (method.equalsIgnoreCase(ApiOperation.Type.PUT.name())) {
			operationType = OperationType.UPDATE;
		} else if (method.equalsIgnoreCase(ApiOperation.Type.DELETE.name())) {
			operationType = OperationType.DELETE;
		}

		log.debug("the audit operation is " + operationType);

		return operationType;
	}

}
