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
package com.indracompany.sofia2.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


public class Sofia2AuditEvent implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static enum EventType {
		USER, SECURITY, ERROR, DATA, GENERAL;
	}

	@Getter
	@Setter
	private String message;
	
	@Getter
	@Setter
	private String id;
	
	@Getter
	@Setter
	private EventType type;
	
	@Getter
	@Setter
	private Date timeStamp;
	
	
	@Getter
	@Setter
	private String user;
	
	@Getter
	@Setter
	private String ontology;
	
	@Getter
	@Setter
	private String operationType;
	
	@Getter
	@Setter
	private String module;
	
	@Getter
	@Setter
	private String error;
	
	@Getter
	@Setter
	private String className;
	
	@Getter
	@Setter
	private String methodName;
	
	@Getter
	@Setter
	private String sessionId;
	
	@Getter
	@Setter
	private String remoteAddress;
	
	@Getter
	@Setter
	private String route;
	
	@Getter
	@Setter
	private Map<String, Object> data;
	
	@Getter
	@Setter
	private String otherType;

	@Override
	public String toString() {
		return "Sofia2AuditEvent [message=" + message + ", id=" + id + ", type=" + type + ", timeStamp=" + timeStamp
				+ ", user=" + user + ", ontology=" + ontology + ", operationType=" + operationType + ", module="
				+ module + ", error=" + error + ", className=" + className + ", methodName=" + methodName
				+ ", sessionId=" + sessionId + ", remoteAddress=" + remoteAddress + ", route=" + route + ", data="
				+ data + ", otherType=" + otherType + "]";
	}

	


}
