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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.indracompany.sofia2.audit.bean.Sofia2AuditRemoteEvent;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;

import lombok.Getter;
import lombok.Setter;

public class IotBrokerAuditEvent extends Sofia2AuditRemoteEvent implements Serializable   {

	private static final long serialVersionUID = -4179966151619124358L;

	@Getter
	@Setter
	private IoTSession session;

	@Getter
	@Setter
	private GatewayInfo gatewayInfo;

	@Getter
	@Setter
	private String query;

	@Getter
	@Setter
	private String data;

	@Getter
	@Setter
	private String clientPlatform;

	@Getter
	@Setter
	private String clientPlatformInstance;

	public IotBrokerAuditEvent () {
		super();
	}

	public IotBrokerAuditEvent(String message, String id, EventType type, Date timeStamp, String user, String ontology,
			String operationType, Module module, Map<String, Object> extraData, String otherType,
			String remoteAddress, IoTSession session, GatewayInfo gatewayInfo, String query, String data,
			String clientPlatform, String clientPlatformInstance) {
		super(message, id, type, timeStamp, user, ontology, operationType, module, extraData, otherType, remoteAddress);
		this.session = session;
		this.gatewayInfo = gatewayInfo;
		this.query = query;
		this.data = data;
		this.clientPlatform = clientPlatform;
		this.clientPlatformInstance = clientPlatformInstance;
	}

}

