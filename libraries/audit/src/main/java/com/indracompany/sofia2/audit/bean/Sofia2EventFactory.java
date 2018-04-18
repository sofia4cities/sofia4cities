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
package com.indracompany.sofia2.audit.bean;

import java.util.Date;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.indracompany.sofia2.audit.aop.BaseAspect;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;

public class Sofia2EventFactory {

	public static Sofia2AuditError createAuditEventError(JoinPoint joinPoint, String message, Module module,
			Exception e) {
		Sofia2AuditError event = createAuditEventError(joinPoint, message, e);
		event.setModule(module);
		return event;
	}

	public static Sofia2AuditError createAuditEventError(JoinPoint joinPoint, String message, Exception e) {
		Sofia2AuditError event = createAuditEventError(joinPoint, message);
		setErrorDetails(event, e);
		return createAuditEventError(event, message);
	}

	public static Sofia2AuditError createAuditEventError(JoinPoint joinPoint, String message) {
		Sofia2AuditError event = new Sofia2AuditError();
		event.setClassName(BaseAspect.getClassName(joinPoint));
		event.setMethodName(BaseAspect.getMethod(joinPoint).getName());

		return createAuditEventError(event, message);
	}

	public static Sofia2AuditError createAuditEventError(Sofia2AuditError event, String message) {

		Date today = new Date();
		event.setId(UUID.randomUUID().toString());
		event.setTimeStamp(today.getTime());
		event.setFormatedTimeStamp(CalendarUtil.builder().build().convert(today));
		event.setMessage(message);
		event.setType(EventType.ERROR);
		setSecurityData(event);
		return event;
	}

	public static Sofia2AuditEvent createAuditEvent(AuditApplicationEvent actualAuditEvent, EventType type,
			String message) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();

		AuditEvent audit = actualAuditEvent.getAuditEvent();

		setSecurityData(event);

		event.setUser(audit.getPrincipal());
		event.setTimeStamp(audit.getTimestamp().getTime());
		event.setFormatedTimeStamp(CalendarUtil.builder().build().convert(audit.getTimestamp()));

		event.setMessage(message);
		event.setOtherType(audit.getType());
		event.setExtraData(audit.getData());
		event.setType(type);

		if (audit.getData().get("details") instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details = (WebAuthenticationDetails) audit.getData().get("details");

			// event.setRemoteAddress(details.getRemoteAddress());
			// event.setSessionId(details.getSessionId());

		}

		else if (audit.getData().get("details") instanceof OAuth2AuthenticationDetails) {
			OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) audit.getData().get("details");

			// event.setRemoteAddress(details.getRemoteAddress());
			// event.setSessionId(details.getTokenValue());
		}

		// if (audit.getData().get("requestUrl")!=null)
		// event.setRoute((String)audit.getData().get("requestUrl"));

		return event;
	}

	public static Sofia2AuditEvent createAuditEvent(EventType type, String message) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		return createAuditEvent(event, type, message);
	}

	public static Sofia2AuthAuditEvent createAuditAuthEvent(EventType type, String message) {
		Sofia2AuthAuditEvent event = new Sofia2AuthAuditEvent();
		return createAuditAuthEvent(event, type, message);
	}

	public static Sofia2AuthAuditEvent createAuditAuthEvent(Sofia2AuthAuditEvent event, EventType type,
			String message) {

		event.setType(type);
		Date today = new Date();
		event.setTimeStamp(today.getTime());
		event.setFormatedTimeStamp(CalendarUtil.builder().build().convert(today));
		event.setMessage(message);
		event.setId(UUID.randomUUID().toString());
		setSecurityData(event);
		return event;
	}

	public static Sofia2AuditEvent createAuditEvent(Sofia2AuditEvent event, EventType type, String message) {
		event.setType(type);

		Date today = new Date();

		event.setTimeStamp(today.getTime());
		event.setFormatedTimeStamp(CalendarUtil.builder().build().convert(today));
		event.setMessage(message);
		event.setId(UUID.randomUUID().toString());
		setSecurityData(event);
		return event;
	}

	private static void setSecurityData(Sofia2AuditEvent event) {
		if (SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null) {
			event.setUser(SecurityContextHolder.getContext().getAuthentication().getName());

			Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
			if (details instanceof OAuth2AuthenticationDetails) {
				// event.setSessionId(((OAuth2AuthenticationDetails)
				// SecurityContextHolder.getContext().getAuthentication().getDetails()).getTokenValue());
				// event.setRemoteAddress(((OAuth2AuthenticationDetails)
				// details).getRemoteAddress());
			}

			else if (details instanceof WebAuthenticationDetails) {
				// event.setSessionId(((WebAuthenticationDetails)
				// SecurityContextHolder.getContext().getAuthentication().getDetails()).getSessionId());
				// event.setRemoteAddress(((WebAuthenticationDetails)
				// details).getRemoteAddress());
			}
		}
	}

	public static void setErrorDetails(Sofia2AuditError event, final Throwable cause) {
		if (cause != null) {
			Throwable rootCause = cause;
			while (rootCause.getCause() != null && rootCause.getCause() != rootCause)
				rootCause = rootCause.getCause();

			event.setClassName(rootCause.getStackTrace()[0].getClassName());
			event.setMethodName(rootCause.getStackTrace()[0].getMethodName());
			event.setType(EventType.ERROR);
		}

	}

}
