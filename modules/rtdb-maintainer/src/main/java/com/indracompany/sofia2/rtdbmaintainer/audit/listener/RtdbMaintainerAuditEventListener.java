package com.indracompany.sofia2.rtdbmaintainer.audit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.notify.EventRouter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RtdbMaintainerAuditEventListener {
	@Autowired
	private EventRouter eventRouter;

	@EventListener
	@Async
	public void handleSofia2AuditEvent(Sofia2AuditEvent event) {
		eventRouter.notify(event.toJson());
	}
}
