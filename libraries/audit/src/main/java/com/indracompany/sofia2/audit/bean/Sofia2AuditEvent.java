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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sofia2AuditEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum EventType {
		USER, SECURITY, ERROR, DATA, GENERAL, IOTBROKER, APIMANAGER;
	}

	public static enum Module {
		CONTROLPANEL, APIMANAGER, IOTBROKER, FLOWENGINE, ROUTER
	}

	public static enum OperationType {
		LOGIN, LOGOUT, JOIN, LEAVE, INSERT, UPDATE, UPDATE_BY_ID, DELETE, DELETE_BY_ID, QUERY, SUBSCRIBE, UNSUBSCRIBE, INDICATION, COMMAND
	}

	@Getter
	@Setter
	protected String message;

	@Getter
	@Setter
	protected String id;

	@Getter
	@Setter
	protected EventType type;

	@Getter
	@Setter
	protected Date timeStamp;

	@Getter
	@Setter
	protected String user;

	@Getter
	@Setter
	protected String ontology;

	@Getter
	@Setter
	protected String operationType;

	@Getter
	@Setter
	protected Module module;

	@Getter
	@Setter
	protected Map<String, Object> extraData;

	@Getter
	@Setter
	protected String otherType;

	public Sofia2AuditEvent() {
		super();
	}

	public Sofia2AuditEvent(String message, String id, EventType type, Date timeStamp, String user, String ontology,
			String operationType, Module module, Map<String, Object> extraData, String otherType) {
		super();
		this.message = message;
		this.id = id;
		this.type = type;
		this.timeStamp = timeStamp;
		this.user = user;
		this.ontology = ontology;
		this.operationType = operationType;
		this.module = module;
		this.extraData = extraData;
		this.otherType = otherType;
	}

	@Override
	public String toString() {
		return "Sofia2AuditEvent [message=" + message + ", id=" + id + ", type=" + type + ", timeStamp=" + timeStamp
				+ ", user=" + user + ", ontology=" + ontology + ", operationType=" + operationType + ", module="
				+ module + ", extraData=" + extraData + ", otherType=" + otherType + "]";
	}

	public String toJson() {

		String json = "";
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			log.error("Error parsing audit event ", e);
		}

		return json;
	}

}
