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
/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.persistence.util.CalendarAdapter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ContextData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter private String user;
	@Getter @Setter private String clientPatform;
	@Getter @Setter private String clientPatformInstance;
	@Getter @Setter private String clientConnection;
	@Getter @Setter private String clientSession;
	@Getter @Setter private String timezoneId;
	
	public ContextData() {}
	
	public ContextData(JsonNode node) {
		try {
			this.user = node.findValue("user").asText();
			this.clientPatform = node.findValue("clientPatform").asText();
			this.clientPatformInstance = node.findValue("clientPatformInstance").asText();
			this.clientConnection = node.findValue("clientConnection").asText();
			this.clientSession = node.findValue("clientSession").asText();
		} catch (Exception e) {
			// We are processing a minimal context data
			this.user = "";
			this.clientPatform= "";
			this.clientPatformInstance= "";
			this.clientConnection = "";
			this.clientSession = "";
		}
		
		JsonNode timezoneId = node.findValue("timezoneId");
		if (timezoneId != null) {
			this.timezoneId = timezoneId.asText();
		} else {
			this.timezoneId = CalendarAdapter.getServerTimezoneId();
		}
	}
	
	public ContextData(ContextData other) {
		this.user = other.user;
		this.clientPatform = other.clientPatform;
		this.clientPatformInstance = other.clientPatformInstance;
		this.clientConnection = other.clientConnection;
		this.clientSession = other.clientSession;
		this.timezoneId = other.timezoneId;
	}

	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof ContextData)) return false;
		ContextData that = (ContextData) other;
		return Objects.equals(this.user, that.user) &&
				Objects.equals(this.clientPatformInstance, that.clientPatformInstance) && 
				Objects.equals(this.clientPatform, that.clientPatform) && 
				Objects.equals(this.clientConnection, that.clientConnection) && 
				Objects.equals(this.clientSession, that.clientSession) && 
				Objects.equals(this.timezoneId, that.timezoneId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(user, clientPatform, clientPatformInstance, clientConnection, clientSession, timezoneId);
	}
}
