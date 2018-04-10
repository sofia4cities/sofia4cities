/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.audit.listener;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.notify.EventRouter;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class AuditApplicationEventListener extends Sofia2EventListener {

	@Autowired
	ApplicationEventPublisher publisher;
	
   /*
    @EventListener(condition = "#event.auditEvent.type != 'CUSTOM_AUDIT_EVENT'")
    @Async
    public void onAuditEvent(AuditApplicationEvent event) {
        AuditEvent actualAuditEvent = event.getAuditEvent();

        log.info("On audit application event: timestamp: {}, principal: {}, type: {}, data: {}",
                actualAuditEvent.getTimestamp(),
                actualAuditEvent.getPrincipal(),
                actualAuditEvent.getType(),
                actualAuditEvent.getData()
        );
        publisher.publishEvent(
                new AuditApplicationEvent(
                        new AuditEvent(actualAuditEvent.getPrincipal(), "CUSTOM_AUDIT_EVENT")
                )
        );
        Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(event, EventType.USER, "Audit User Event Received "+actualAuditEvent.getType()+" Data "+actualAuditEvent.getData());
       
        eventRouter.notify(s2event.toJson());
    }

    @EventListener(condition = "#event.auditEvent.type == 'CUSTOM_AUDIT_EVENT'")
    @Async
    public void onCustomAuditEvent(AuditApplicationEvent event) {
    	log.info("Handling custom audit event ...");
    	
    	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(event, EventType.USER, "Audit User Event Received "+event.getAuditEvent().getType()+" Data "+event.getAuditEvent().getData());
    	eventRouter.notify(s2event.toJson());
    }
    
    */
    @EventListener
    @Async
    public void handleAllEvents(Object event) {
        log.debug("event: "+event.toString());
               
    }
    
    @EventListener
    @Async
    public void handleServletRequestHandledEvent(ServletRequestHandledEvent servletEvent) {
    	
    	/*
    	 Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.GENERAL, "Servlet Event Received :"+servletEvent.getRequestUrl());
    	 
    	s2event.setOtherType(ServletRequestHandledEvent.class.getName());
    	
    	String client = servletEvent.getClientAddress();
    	//s2event.setRemoteAddress(client);
    	
    	//Sofia2EventFactory.setErrorDetails(s2event, servletEvent.getFailureCause());
    	
    	String url =servletEvent.getRequestUrl();
    	//s2event.setRoute(url);
    	
    	String user =servletEvent.getUserName();
    	s2event.setUser(user);

    	eventRouter.notify(s2event.toJson());
    	*/
    }
    
    

}