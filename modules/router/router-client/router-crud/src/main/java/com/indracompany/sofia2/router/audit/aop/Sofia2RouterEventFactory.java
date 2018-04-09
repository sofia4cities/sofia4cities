package com.indracompany.sofia2.router.audit.aop;

import org.aspectj.lang.JoinPoint;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;

public class Sofia2RouterEventFactory {

	public static Sofia2AuditEvent createAuditEvent(JoinPoint joinPoint, Auditable auditable, EventType type, String message) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		return Sofia2EventFactory.createAuditEvent(event, type,message);
	}
}
