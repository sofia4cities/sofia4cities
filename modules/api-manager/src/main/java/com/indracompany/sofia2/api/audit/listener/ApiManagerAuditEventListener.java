package com.indracompany.sofia2.api.audit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.audit.bean.ApiManagerAuditEvent;
import com.indracompany.sofia2.audit.notify.EventRouter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiManagerAuditEventListener {

	@Autowired
	private EventRouter eventRouter;

	@EventListener
	@Async
	public void handleSofia2AuditErrorEvent(ApiManagerAuditEvent event) {
		log.info("api manager audit event: " + event.toString());
		eventRouter.notify(event.toJson());
	}
}
