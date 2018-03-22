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
package com.indracompany.sofia2.security.jwt.ri;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginAttemptsLogger {

	@EventListener
	public void auditEventHappened(AuditApplicationEvent auditApplicationEvent) {
		AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();

		log.info("Begin -> Audit Login Happened -> Principal " + auditEvent.getPrincipal() + " - " + auditEvent.getType());

		if (auditEvent.getData().get("details") instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails details = (WebAuthenticationDetails) auditEvent.getData().get("details");

			log.info("  Remote IP address: " + details.getRemoteAddress());
			log.info("  Session Id: " + details.getSessionId());
			log.info("  Class Id: WebAuthenticationDetails");
			
		} else if (auditEvent.getData().get("details") instanceof OAuth2AuthenticationDetails) {
			OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auditEvent.getData().get("details");

			log.info("  Remote IP address: " + details.getRemoteAddress());
			log.info("  Session Id: " + details.getSessionId());
			log.info("  Class Id: OAuth2AuthenticationDetails");
			log.info("  Token Type: " + details.getTokenType());
			log.info("  Token Value: " + details.getTokenValue());
		}

		log.info("  Request URL: " + auditEvent.getData().get("requestUrl"));
		log.info("End -> Audit Login Happened -> Principal " + auditEvent.getPrincipal() + " - " + auditEvent.getType());
	}
}