package com.indracompany.sofia2.router.service.processor.bean;

import lombok.Getter;
import lombok.Setter;

public class AuditParameters {
	
	@Getter
	@Setter
	private String user;
	
	@Getter
	@Setter
	private String eventType;
	
	@Getter
	@Setter
	private String operationType;

	public AuditParameters(String user, String eventType, String operationType) {
		super();
		this.user = user;
		this.eventType = eventType;
		this.operationType = operationType;
	}

}
