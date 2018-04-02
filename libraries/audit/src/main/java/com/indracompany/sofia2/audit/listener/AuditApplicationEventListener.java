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
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import com.indracompany.sofia2.audit.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.Sofia2EventFactory;
import com.indracompany.sofia2.audit.notify.EventRouter;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class AuditApplicationEventListener {

	@Autowired
	ApplicationEventPublisher publisher;
	
	@Autowired
 	EventRouter eventRouter;
  
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
       
        eventRouter.notify(s2event);
    }

    @EventListener(condition = "#event.auditEvent.type == 'CUSTOM_AUDIT_EVENT'")
    @Async
    public void onCustomAuditEvent(AuditApplicationEvent event) {
    	log.info("Handling custom audit event ...");
    	
    	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(event, EventType.USER, "Audit User Event Received "+event.getAuditEvent().getType()+" Data "+event.getAuditEvent().getData());
    	eventRouter.notify(s2event);
    }
    

    @EventListener
    @Async
    public void handleAllEvents(Object event) {
        System.out.println("event: "+event);
        
        if (event instanceof ServletRequestHandledEvent) {
        	ServletRequestHandledEvent servletEvent = ((ServletRequestHandledEvent) event);
        	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.GENERAL, "Servlet Event Received :"+servletEvent.getRequestUrl());
        	s2event.setOtherType(ServletRequestHandledEvent.class.getName());
        	
        	String client = servletEvent.getClientAddress();
        	s2event.setRemoteAddress(client);
        	
        	Sofia2EventFactory.setErrorDetails(s2event, servletEvent.getFailureCause());
        	
        	String url =servletEvent.getRequestUrl();
        	s2event.setRoute(url);
        	
        	String user =servletEvent.getUserName();
        	s2event.setUser(user);

        	eventRouter.notify(s2event);

		}
        
        else if (event instanceof AuthenticationSuccessEvent) {
        	AuthenticationSuccessEvent servletEvent = ((AuthenticationSuccessEvent) event);
        	
        	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.SECURITY, "Login Success for User : "+servletEvent.getAuthentication().getPrincipal().toString());
        	
        	Object source = servletEvent.getSource();
        	if (source instanceof UsernamePasswordAuthenticationToken) {
        		
        	}
        	
        	s2event.setOtherType(AuthenticationSuccessEvent.class.getName());
        	s2event.setUser((String)servletEvent.getAuthentication().getPrincipal());
        	
        	if (servletEvent.getAuthentication().getDetails() != null) {
    			Object details = servletEvent.getAuthentication().getDetails();
    			setAuthValues(details,s2event);
    		}
     
        	eventRouter.notify(s2event);

		}
        
        else if (event instanceof AuthorizationFailureEvent) {
        	
        	AuthorizationFailureEvent errorEvent = (AuthorizationFailureEvent) event;
        	
        	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.SECURITY, "Security Failure Event Received: "+errorEvent.getAccessDeniedException().getMessage(),errorEvent.getAccessDeniedException() );
        	
        	Object source = errorEvent.getSource();
        	if (source instanceof FilterInvocation) {
        		s2event.setRoute(((FilterInvocation) errorEvent.getSource()).getRequestUrl());
        	}
        	
        	s2event.setUser((String)errorEvent.getAuthentication().getPrincipal());
        	s2event.setOtherType(AuthorizationFailureEvent.class.getName());
        	Sofia2EventFactory.setErrorDetails(s2event, errorEvent.getAccessDeniedException());
    		
    		if (errorEvent.getAuthentication().getDetails() != null) {
    			Object details = errorEvent.getAuthentication().getDetails();
    			setAuthValues(details,s2event);
    		}
    		
    		eventRouter.notify(s2event);	
		}
        
        
    }
    
    private static void setAuthValues(Object details, Sofia2AuditEvent s2event) {
    	if (details instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details2 = (WebAuthenticationDetails) details;
			
			s2event.setRemoteAddress(details2.getRemoteAddress());
			s2event.setSessionId(details2.getSessionId());
					
		} else if (details instanceof OAuth2AuthenticationDetails) {
			OAuth2AuthenticationDetails details2 = (OAuth2AuthenticationDetails) details;
			
			s2event.setRemoteAddress(details2.getRemoteAddress());
			s2event.setSessionId(details2.getSessionId());
			
			Map<String, Object> data= new HashMap<String,Object>();
			data.put("tokenType", details2.getTokenType());
			data.put("tokenValue", details2.getTokenValue());
			s2event.setData(data);

		}
    }

}