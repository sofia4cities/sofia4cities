package com.indracompany.sofia2.audit.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.Sofia2AuditEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sofia2EventProducer implements EventProducer{
	
	@Autowired
	ApplicationEventPublisher publisher;

	public void publish(Sofia2AuditEvent event) {
		log.info("Sofia2EventProducer :: thread '{}' handling '{}' publish Event", Thread.currentThread(), event);
		publisher.publishEvent(event);
	}


}