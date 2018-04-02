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
package com.indracompany.sofia2.audit;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.indracompany.sofia2.audit.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.aop.Auditable;
import com.indracompany.sofia2.audit.aop.BaseAspect;

public class Sofia2EventFactory {
	
	
	public static Sofia2AuditEvent createAuditEvent(JoinPoint joinPoint, Auditable auditable, EventType type, String message) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		event.setClassName(BaseAspect.getClassName(joinPoint));
		event.setMethodName(BaseAspect.getMethod(joinPoint).getName());
		
		return createAuditEvent(event, type,message);
	}
	
	public static Sofia2AuditEvent createAuditEvent(AuditApplicationEvent actualAuditEvent, EventType type, String message) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		
		 AuditEvent audit = actualAuditEvent.getAuditEvent();
		 
		 setSecurityData(event);	
		 
		 event.setUser(audit.getPrincipal());
		 event.setTimeStamp(audit.getTimestamp());
		 event.setMessage(message);
		 event.setOtherType( audit.getType());
		 event.setData( audit.getData());
		 event.setType(type);
		 
		 if (audit.getData().get("details") instanceof WebAuthenticationDetails) {
			 WebAuthenticationDetails details = (WebAuthenticationDetails) audit.getData().get("details");
				
			 event.setRemoteAddress(details.getRemoteAddress());
			 event.setSessionId(details.getSessionId());
				
		 } 
		 
		 else if (audit.getData().get("details") instanceof OAuth2AuthenticationDetails) {
			 OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) audit.getData().get("details");
				
			 event.setRemoteAddress(details.getRemoteAddress());
			 event.setSessionId(details.getTokenValue());
		 }
		 
		 if (audit.getData().get("requestUrl")!=null)
			 event.setRoute((String)audit.getData().get("requestUrl"));
			
		 return event;
	}
	
	
	
	public static Sofia2AuditEvent createAuditEvent(EventType type, String message, Exception error) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		setErrorDetails(event,error);
		return createAuditEvent(event, type, message);
	}
	
	public static Sofia2AuditEvent createAuditEvent(EventType type, String message) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		return createAuditEvent(event, type,message);
	}
	
	public static Sofia2AuditEvent createAuditEvent(Sofia2AuditEvent event, EventType type, String message) {
		event.setType(type);
		
		Date today = new Date();
		
		event.setTimeStamp(today);
		event.setMessage(message);
		
		setSecurityData(event);	
		return event;
	}
	
	private static void setSecurityData(Sofia2AuditEvent event) {
		if (SecurityContextHolder.getContext() != null 	&& SecurityContextHolder.getContext().getAuthentication() != null) {
			event.setUser(SecurityContextHolder.getContext().getAuthentication().getName());

			Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
			if (details instanceof OAuth2AuthenticationDetails) {
				event.setSessionId(((OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getTokenValue());
				event.setRemoteAddress(((WebAuthenticationDetails) details).getRemoteAddress());
			}

			else if (details instanceof WebAuthenticationDetails) {
				event.setSessionId(((WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getSessionId());
				event.setRemoteAddress(((WebAuthenticationDetails) details).getRemoteAddress());
			}
		}
	}
	
	public static void setErrorDetails(Sofia2AuditEvent event, final Throwable cause)
	{
		if (cause!=null) {
			Throwable rootCause = cause;
			while(rootCause.getCause() != null &&  rootCause.getCause() != rootCause)
				rootCause = rootCause.getCause();

			event.setClassName(rootCause.getStackTrace()[0].getClassName()); 
			event.setMethodName(rootCause.getStackTrace()[0].getMethodName()); 
			event.setType(EventType.ERROR);
		}
		
	
	}

}
