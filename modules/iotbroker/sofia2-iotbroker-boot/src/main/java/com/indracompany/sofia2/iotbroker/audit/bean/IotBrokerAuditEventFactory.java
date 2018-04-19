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
package com.indracompany.sofia2.iotbroker.audit.bean;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.audit.bean.CalendarUtil;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyQueryMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodySubscribeMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUnsubscribeMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateMessage;

import lombok.Builder;

@Builder
public class IotBrokerAuditEventFactory {

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyInsertMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), null, message.getData(),
				OperationType.INSERT, messageText, info, session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyQueryMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), message.getQuery(), null,
				OperationType.QUERY, messageText, info, session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodySubscribeMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), message.getQuery(), null,
				OperationType.SUBSCRIBE, messageText, info, session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyUnsubscribeMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(OperationType.UNSUBSCRIBE, messageText, session, info);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyUpdateByIdMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), null, message.getData(),
				OperationType.UPDATE, messageText, info, session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyUpdateMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), message.getQuery(), null,
				OperationType.UPDATE, messageText, info, session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyDeleteByIdMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), null, null, OperationType.DELETE,
				messageText, info, session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyDeleteMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), message.getQuery(), null,
				OperationType.DELETE, messageText, info, session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyJoinMessage message, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(OperationType.JOIN, messageText, session, info);

		return event;
	}

	// -------------------------------------------------------------------------------------------------//

	public IotBrokerAuditEvent createIotBrokerAuditEvent(OperationType operationType, String messageText,
			IoTSession session, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(null, null, null, operationType, messageText, info,
				session);

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(String ontology, String query, JsonNode data,
			OperationType operationType, String messageText, GatewayInfo info, IoTSession session) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(ontology, query, data, operationType, messageText, info);

		if (session != null) {
			event.setUser(session.getUserID());
			event.setSessionKey(session.getSessionKey());
			event.setClientPlatform(session.getClientPlatform());
			event.setClientPlatformInstance(session.getClientPlatform());
		}

		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(String ontology, String query, JsonNode data,
			OperationType operationType, String messageText, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(ontology, query, operationType, messageText, info);

		event.setData((data != null) ? data.toString() : null);
		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(String ontology, String query, OperationType operationType,
			String messageText, GatewayInfo info) {

		IotBrokerAuditEvent event = createIotBrokerAuditEvent(operationType, messageText, info);

		event.setOntology(ontology);
		event.setQuery(query);
		return event;
	}

	public IotBrokerAuditEvent createIotBrokerAuditEvent(OperationType operationType, String messageText,
			GatewayInfo info) {
		IotBrokerAuditEvent event = new IotBrokerAuditEvent();
		event.setId(UUID.randomUUID().toString());
		event.setModule(Module.IOTBROKER);
		event.setType(EventType.IOTBROKER);
		event.setOperationType(operationType.name());
		event.setResultOperation(ResultOperationType.SUCCESS);
		// event.getRemoteAddress();
		event.setMessage(messageText);
		event.setGatewayInfo(info);
		Date today = new Date();
		event.setTimeStamp(today.getTime());
		event.setFormatedTimeStamp(CalendarUtil.builder().build().convert(today));
		return event;
	}

}
