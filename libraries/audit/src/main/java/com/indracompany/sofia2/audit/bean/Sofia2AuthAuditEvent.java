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
import java.util.Map;

public class Sofia2AuthAuditEvent extends Sofia2AuditRemoteEvent {

	private static final long serialVersionUID = -146537921734143436L;

	public Sofia2AuthAuditEvent() {
		super();
	}

	public Sofia2AuthAuditEvent(String message, String id, EventType type, Date timeStamp, String user, String ontology,
			String operationType, Module module, Map<String, Object> extraData, String otherType,
			String remoteAddress) {
		super(message, id, type, timeStamp, user, ontology, operationType, module, extraData, otherType, remoteAddress);
	}

}
