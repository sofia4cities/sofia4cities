package com.indracompany.sofia2.iotbroker.audit.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.listener.Sofia2EventListener;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IotBrokerAuditEventListener extends Sofia2EventListener {
	
	@EventListener
    @Async
    public void handleSofia2AuditErrorEvent(IotBrokerAuditEvent event) {    	
    	log.info("iotbroker audit event: " + event.toString());   	
    	eventRouter.notify(event.toJson());
    }
}
