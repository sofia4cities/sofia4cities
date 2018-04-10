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

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

public class IotBrokerAuditEventFactory {
	
	public static IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyInsertMessage message, String messageText, IoTSession session, GatewayInfo info) {
	
		IotBrokerAuditEvent event = createIotBrokerAuditEvent(SSAPMessageTypes.INSERT, messageText, info);
		
		event.setOntology(message.getOntology());
		event.setSession(session);
		event.setData(message.getData().toString());
		//event.getRemoteAddress();
		event.setUser(session.getUserID());
		
		return event;
	}
	
	public static IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyJoinMessage message, String messageText, GatewayInfo info) {
		
		IotBrokerAuditEvent event = createIotBrokerAuditEvent(SSAPMessageTypes.JOIN,messageText, info);
		event.setClientPlatform(message.getClientPlatform());
		event.setClientPlatformInstance(message.getClientPlatformInstance());
		event.setToken(message.getToken());
		
		return event;
	}
	
	public static IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPBodyUpdateMessage message, String messageText, IoTSession session, GatewayInfo info) {
		
		IotBrokerAuditEvent event = createIotBrokerAuditEvent(message.getOntology(), message.getQuery(),SSAPMessageTypes.UPDATE, messageText, info);

		event.setSession(session);
		//event.getRemoteAddress();
		event.setUser(session.getUserID());
		
		return event;
	}
	
	public static IotBrokerAuditEvent createIotBrokerAuditEvent(String ontology, String query, SSAPMessageTypes operationType, 
																String messageText, GatewayInfo info) {
		
		IotBrokerAuditEvent event = createIotBrokerAuditEvent(operationType, messageText, info);
		
		event.setOntology(ontology);
		event.setQuery(query);
		return event;
	}
	
	public static IotBrokerAuditEvent createIotBrokerAuditEvent(SSAPMessageTypes operationType, String messageText, GatewayInfo info) {
		IotBrokerAuditEvent event = new IotBrokerAuditEvent();
		event.setId(UUID.randomUUID().toString());
		event.setModule(Module.IOTBROKER);	
		event.setType(EventType.IOTBROKER);
		event.setOperationType(operationType.name());
		//event.getRemoteAddress();
		event.setMessage(messageText);
		event.setGatewayInfo(info);
		event.setTimeStamp(new Date());
		return event;
	}

}
