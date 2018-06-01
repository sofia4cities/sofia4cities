/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
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
package com.indracompany.sofia2.router.audit.aop;

import org.aspectj.lang.JoinPoint;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2EventFactory;

public class Sofia2RouterEventFactory {

	public static Sofia2AuditEvent createAuditEvent(JoinPoint joinPoint, Auditable auditable, EventType type,
			String message) {
		Sofia2AuditEvent event = new Sofia2AuditEvent();
		return Sofia2EventFactory.builder().build().createAuditEvent(event, type, message);
	}
}
