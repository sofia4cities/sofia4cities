package com.indracompany.sofia2.audit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.notify.EventRouter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sofia2EventListener {
	
	@Autowired
	EventRouter eventRouter;

	//@EventListener
	void handleSync(Sofia2AuditEvent event) {
		log.info("Sofia2EventListener :: thread '{}' handling '{}' event", Thread.currentThread(), event);
		eventRouter.notify(event);
	}

	@Async
	@EventListener
	void handleAsync(Sofia2AuditEvent event) {
		log.info("Sofia2EventListener :: thread '{}' handling '{}' async event", Thread.currentThread(), event);
		eventRouter.notify(event);
	}

}
