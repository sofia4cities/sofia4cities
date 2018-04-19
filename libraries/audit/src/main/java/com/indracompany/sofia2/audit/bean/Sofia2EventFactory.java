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

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.core.context.SecurityContextHolder;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;

import lombok.Builder;

@Builder
public class Sofia2EventFactory {

	public static Sofia2AuditError createAuditEventError(String userId, String message, Module module, Exception e) {

		Sofia2AuditError event = createAuditEventError(message, module, e);
		event.setUser(userId);

		return event;

	}

	public static Sofia2AuditError createAuditEventError(String message, Module module, Exception e) {

		Sofia2AuditError event = createAuditEventError(message);
		setErrorDetails(event, e);
		event.setModule(module);
		event.setEx(e);
		return createAuditEventError(event, message);
	}

	public static Sofia2AuditError createAuditEventError(String message) {
		Sofia2AuditError event = new Sofia2AuditError();
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
