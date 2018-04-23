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
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuthAuditEvent;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.audit.notify.EventRouter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Sofia2EventListener {

	@Autowired
	private EventRouter eventRouter;

	/*
	 * @Async
	 * 
	 * @EventListener void handleAsync(Sofia2AuditEvent event) throws
	 * JsonProcessingException { log.
	 * info("Sofia2EventListener :: Default Event Processing detected : thread '{}' handling '{}' async event"
	 * , event.getType(), event.getMessage()); eventRouter.notify(event.toJson()); }
	 */

	@EventListener
	@Async
	public void handleAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
		log.info("Authentication success event for user " + event.getAuthentication().getPrincipal().toString());

		Sofia2AuthAuditEvent s2event = Sofia2EventFactory.builder().build().createAuditAuthEvent(EventType.SECURITY,
				"Login Success for User : " + event.getAuthentication().getPrincipal().toString());

		Object source = event.getSource();
		if (source instanceof UsernamePasswordAuthenticationToken) {

		}
		s2event.setOperationType(OperationType.LOGIN.name());
		s2event.setOtherType(AuthenticationSuccessEvent.class.getName());
		s2event.setUser((String) event.getAuthentication().getPrincipal());
		s2event.setResultOperation(ResultOperationType.SUCCESS);
		if (event.getAuthentication().getDetails() != null) {
			Object details = event.getAuthentication().getDetails();
			setAuthValues(details, s2event);
		}
		getEventRouter().notify(s2event.toJson());
	}

	@EventListener
	@Async
	public void handleAuthenticationFailureBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event) {
		log.info("authentication failure bad credentials event for user "
				+ event.getAuthentication().getPrincipal().toString());

		String message = "Login Failed (Incorrect Credentials) for User: "
				+ event.getAuthentication().getPrincipal().toString();

		Sofia2AuthAuditEvent s2event = Sofia2EventFactory.builder().build().createAuditAuthEvent(EventType.SECURITY,
				message);

		s2event.setOperationType(OperationType.LOGIN.name());
		s2event.setUser((String) event.getAuthentication().getPrincipal());
		s2event.setOtherType(AuthorizationFailureEvent.class.getName());

		s2event.setResultOperation(ResultOperationType.ERROR);
		// Sofia2EventFactory.setErrorDetails(s2event,
		// errorEvent.getAccessDeniedException());

		if (event.getAuthentication().getDetails() != null) {
			Object details = event.getAuthentication().getDetails();
			setAuthValues(details, s2event);
		}

		getEventRouter().notify(s2event.toJson());
	}

	@EventListener
	@Async
	public void handleAuthorizationFailureEvent(AuthorizationFailureEvent errorEvent) {
		log.info("authorization failure  event for user " + errorEvent.getAuthentication().getPrincipal().toString());

		Sofia2AuthAuditEvent s2event = Sofia2EventFactory.builder().build().createAuditAuthEvent(EventType.SECURITY,
				"Login Failed (AuthorizationFailure) for User: "
						+ errorEvent.getAuthentication().getPrincipal().toString());

		s2event.setOperationType(OperationType.LOGIN.name());
		s2event.setUser((String) errorEvent.getAuthentication().getPrincipal());
		s2event.setOtherType(AuthorizationFailureEvent.class.getName());

		s2event.setResultOperation(ResultOperationType.ERROR);

		if (errorEvent.getAuthentication().getDetails() != null) {
			Object details = errorEvent.getAuthentication().getDetails();
			setAuthValues(details, s2event);
		}

		getEventRouter().notify(s2event.toJson());
	}

	private static void setAuthValues(Object details, Sofia2AuthAuditEvent s2event) {
		if (details instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details2 = (WebAuthenticationDetails) details;
			s2event.setRemoteAddress(details2.getRemoteAddress());
			// s2event.setSessionId(details2.getSessionId());
			s2event.setModule(Module.CONTROLPANEL);
		} else if (details instanceof OAuth2AuthenticationDetails) {
			OAuth2AuthenticationDetails details2 = (OAuth2AuthenticationDetails) details;
			s2event.setRemoteAddress(details2.getRemoteAddress());
			// s2event.setSessionId(details2.getSessionId());
			s2event.setModule(Module.APIMANAGER);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("tokenType", details2.getTokenType());
			data.put("tokenValue", details2.getTokenValue());
			s2event.setExtraData(data);

		}
	}

	public EventRouter getEventRouter() {
		return eventRouter;
	}

	public void setEventRouter(EventRouter eventRouter) {
		this.eventRouter = eventRouter;
	}

}
