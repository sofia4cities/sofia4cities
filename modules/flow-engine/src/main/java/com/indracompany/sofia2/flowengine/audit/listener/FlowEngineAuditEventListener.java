package com.indracompany.sofia2.flowengine.audit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.notify.EventRouter;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FlowEngineAuditEventListener {
	@Autowired
	private EventRouter eventRouter;

	@EventListener
	@Async
	public void handleSofia2AuditErrorEvent(FlowEngineAuditEvent event) {
		log.info("flow engine audit event: " + event.toString());
		eventRouter.notify(event.toJson());
	}
}
