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
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.persistence.util.CalendarAdapter;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ContextData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter private String clientPatform;
	@Getter private String clientPatformInstance;
	@Getter	private String clientConnection;
	@Getter private String clientSession;
	@Getter	final private String user;
	@Getter final private String timezoneId;
	@Getter final private String timestamp;

	public ContextData(JsonNode node) {
			
		JsonNode clientPlatform = node.findValue("clientPatform");
		if (clientPlatform != null) {
			this.clientPatform = clientPlatform.asText();
		} else {
			this.clientPatform = "";
		}
		
		JsonNode clientPatformInstance = node.findValue("clientPatformInstance");
		if (clientPatformInstance != null) {
			this.clientPatformInstance = clientPatformInstance.asText();
		} else {
			this.clientPatformInstance = "";
		}
		
		JsonNode clientConnection = node.findValue("clientConnection");
		if (clientConnection != null) {
			this.clientConnection = clientConnection.asText();
		} else {
			this.clientConnection = "";
		}
		
		JsonNode clientSession = node.findValue("clientSession");
		if (clientSession != null) {
			this.clientSession = clientSession.asText();
		} else {
			this.clientSession = "";
		}
			
		JsonNode user = node.findValue("user");
		if (user != null) {
			this.user = user.asText();
		} else {
			this.user = "";
		}

		JsonNode timezoneId = node.findValue("timezoneId");
		if (timezoneId != null) {
			this.timezoneId = timezoneId.asText();
		} else {
			this.timezoneId = CalendarAdapter.getServerTimezoneId();
		}

		JsonNode timestamp = node.findValue("timestamp");
		if (timestamp != null) {
			this.timestamp = timestamp.asText();
		} else {
			this.timestamp = Calendar.getInstance(TimeZone.getTimeZone(this.timezoneId)).getTime().toString();
		}
	}

	public ContextData(ContextData other) {
		this.user = other.user;
		this.clientPatform = other.clientPatform;
		this.clientPatformInstance = other.clientPatformInstance;
		this.clientConnection = other.clientConnection;
		this.clientSession = other.clientSession;
		this.timezoneId = other.timezoneId;
		this.timestamp = other.timestamp;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof ContextData))
			return false;
		ContextData that = (ContextData) other;
		return Objects.equals(this.user, that.user)
				&& Objects.equals(this.clientPatformInstance, that.clientPatformInstance)
				&& Objects.equals(this.clientPatform, that.clientPatform)
				&& Objects.equals(this.clientConnection, that.clientConnection)
				&& Objects.equals(this.clientSession, that.clientSession)
				&& Objects.equals(this.timezoneId, that.timezoneId) && Objects.equals(this.timestamp, that.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, clientPatform, clientPatformInstance, clientConnection, clientSession, timezoneId,
				timestamp);
	}
	
	private ContextData(Builder build) {
		this.user = build.user;
		this.timezoneId = build.timezoneId;
		this.timestamp = build.timestamp;
		this.clientConnection = build.clientConnection;
		this.clientPatform = build.clientPatform;
		this.clientPatformInstance = build.clientPatformInstance;
		this.clientSession = build.clientSession;
	}
	
	public static Builder builder(String user, String timezoneId, String timestamp) {
        return new Builder(user, timezoneId, timestamp);
    }

	public static class Builder {
		private String clientPatform;
		private String clientPatformInstance;
		private String clientConnection;
		private String clientSession;
		private String user;
		private String timezoneId;
		private String timestamp;
		
		public Builder(String user, String timezoneId, String timestamp) {
			this.user = user;
			this.timezoneId = timezoneId;
			this.timestamp = timestamp;
		}

		public ContextData build() {
			return new ContextData(this);
		}

		public Builder clientSession(String clientSession) {
			this.clientSession = clientSession;
			return this;
		}

		public Builder clientConnection(String clientConnection) {
			this.clientConnection = clientConnection;
			return this;
		}

		public Builder clientPatformInstance(String clientPatformInstance) {
			this.clientPatformInstance = clientPatformInstance;
			return this;
		}

		public Builder clientPatform(String clientPatform) {
			this.clientPatform = clientPatform;
			return this;
		}
	}
}
