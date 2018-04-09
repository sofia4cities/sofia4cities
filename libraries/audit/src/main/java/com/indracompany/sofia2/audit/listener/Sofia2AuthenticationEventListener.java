package com.indracompany.sofia2.audit.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sofia2AuthenticationEventListener extends Sofia2EventListener {
	
	
	@EventListener
    @Async
    public void handleAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
    	
    	
    	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.SECURITY, "Login Success for User : "+event.getAuthentication().getPrincipal().toString());
    	
    	Object source = event.getSource();
    	if (source instanceof UsernamePasswordAuthenticationToken) {
    		
    	}
    	
    	s2event.setOtherType(AuthenticationSuccessEvent.class.getName());
    	s2event.setUser((String)event.getAuthentication().getPrincipal());
    	
    	if (event.getAuthentication().getDetails() != null) {
			Object details = event.getAuthentication().getDetails();
			setAuthValues(details,s2event);
		}
 
    	eventRouter.notify(s2event.toJson());
    }
    
    
    
    @EventListener
    @Async
    public void handleAuthenticationFailureBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent errorEvent) { 
    	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.SECURITY, "Security Failure Event Received: " + errorEvent.getException());
    	
    	Object source = errorEvent.getSource();
    	if (source instanceof FilterInvocation) {
    		//s2event.setRoute(((FilterInvocation) errorEvent.getSource()).getRequestUrl());
    	}
    	
    	s2event.setUser((String)errorEvent.getAuthentication().getPrincipal());
    	s2event.setOtherType(AuthorizationFailureEvent.class.getName());
    	//Sofia2EventFactory.setErrorDetails(s2event, errorEvent.getAccessDeniedException());
		
		if (errorEvent.getAuthentication().getDetails() != null) {
			Object details = errorEvent.getAuthentication().getDetails();
			setAuthValues(details,s2event);
		}
		
		eventRouter.notify(s2event.toJson());	
    }
    
    @EventListener
    @Async
    public void handleAuthorizationFailureEvent(AuthorizationFailureEvent errorEvent) {
    	
    	Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.SECURITY, "Security Failure Event Received: "+errorEvent.getAccessDeniedException().getMessage());
    	
    	Object source = errorEvent.getSource();
    	if (source instanceof FilterInvocation) {
    		//s2event.setRoute(((FilterInvocation) errorEvent.getSource()).getRequestUrl());
    	}
    	
    	s2event.setUser((String)errorEvent.getAuthentication().getPrincipal());
    	s2event.setOtherType(AuthorizationFailureEvent.class.getName());
    	//Sofia2EventFactory.setErrorDetails(s2event, errorEvent.getAccessDeniedException());
		
		if (errorEvent.getAuthentication().getDetails() != null) {
			Object details = errorEvent.getAuthentication().getDetails();
			setAuthValues(details,s2event);
		}
		
		eventRouter.notify(s2event.toJson());	
    }
    
    private static void setAuthValues(Object details, Sofia2AuditEvent s2event) {
    	if (details instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details2 = (WebAuthenticationDetails) details;
			
			//s2event.setRemoteAddress(details2.getRemoteAddress());
			//s2event.setSessionId(details2.getSessionId());
			s2event.setModule(Module.CONTROLPANEL);		
		} else if (details instanceof OAuth2AuthenticationDetails) {
			OAuth2AuthenticationDetails details2 = (OAuth2AuthenticationDetails) details;
			
			//s2event.setRemoteAddress(details2.getRemoteAddress());
			//s2event.setSessionId(details2.getSessionId());
			s2event.setModule(Module.APIMANAGER);	
			Map<String, Object> data= new HashMap<String,Object>();
			data.put("tokenType", details2.getTokenType());
			data.put("tokenValue", details2.getTokenValue());
			s2event.setExtraData(data);

		}
    }
}
