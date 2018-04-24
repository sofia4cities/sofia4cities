package com.indracompany.sofia2.flowengine.audit.bean;

import java.util.Map;

import com.indracompany.sofia2.audit.bean.Sofia2AuditRemoteEvent;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class FlowEngineAuditEvent extends Sofia2AuditRemoteEvent {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String domain;

	@Builder
	public FlowEngineAuditEvent(String message, String id, EventType type, long timeStamp, String formatedTimeStamp,
			String user, String ontology, String operationType, Module module, Map<String, Object> extraData,
			String otherType, String remoteAddress, ResultOperationType resultOperation, String domain) {
		super(message, id, type, timeStamp, formatedTimeStamp, user, ontology, operationType, module, extraData,
				otherType, remoteAddress, resultOperation);
		this.domain = domain;
	}

}
