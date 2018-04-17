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
package com.indracompany.sofia2.controlpanel.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;
import com.indracompany.sofia2.audit.notify.EventRouter;

@Component
public class SofiaLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Autowired
	EventRouter eventRouter;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		super.onLogoutSuccess(request, response, authentication);

		String user = (String) authentication.getPrincipal();

		Sofia2AuditEvent s2event = Sofia2EventFactory.createAuditEvent(EventType.SECURITY,
				"Logout Success for user: " + user);

		s2event.setUser(user);
		s2event.setOperationType(OperationType.LOGOUT.name());
		s2event.setOtherType("LogoutEventSuccess");
		s2event.setResultOperation(ResultOperationType.SUCCESS);
		if (authentication.getDetails() != null) {
			WebAuthenticationDetails details2 = (WebAuthenticationDetails) authentication.getDetails();
			// s2event.setRemoteAddress(details2.getRemoteAddress());
			// s2event.setSessionId(details2.getSessionId());
		}
		s2event.setModule(Module.CONTROLPANEL);
		eventRouter.notify(s2event.toJson());

	}
}
