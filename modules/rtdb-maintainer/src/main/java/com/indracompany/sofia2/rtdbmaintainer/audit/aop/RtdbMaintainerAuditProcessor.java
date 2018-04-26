package com.indracompany.sofia2.rtdbmaintainer.audit.aop;

import org.springframework.stereotype.Service;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.config.model.Ontology;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RtdbMaintainerAuditProcessor {

	public Sofia2AuditEvent getEvent(Ontology ontology) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		String message = "Exporting for ontology " + ontology.getIdentification() + " started";
		event.setMessage(message);
		event.setUser(ontology.getUser().getUserId());
		event.setOntology(ontology.getIdentification());
		return Sofia2EventFactory.builder().build().createAuditEvent(event, EventType.BATCH, message);

	}
}
