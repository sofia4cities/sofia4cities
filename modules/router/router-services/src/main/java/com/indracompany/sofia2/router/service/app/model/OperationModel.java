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
package com.indracompany.sofia2.router.service.app.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class OperationModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum OperationType {
		INSERT, UPDATE, DELETE, QUERY, GET, PUT, POST;
	}

	public static enum QueryType {
		SQLLIKE, NATIVE, NONE;
	}

	public static enum Source {
		IOTBROKER, APIMANAGER, FLOWENGINE, INTERNAL_ROUTER, STREAMING_TWITTER;
	}

	// Mandatory attributes
	@Getter
	@Setter
	private String ontologyName;
	@Getter
	@Setter
	private OperationType operationType;
	@Getter
	@Setter
	private String user;
	@Getter
	@Setter
	private Source source;

	// optional attributes
	@Getter
	@Setter
	private String body;
	@Getter
	@Setter
	private QueryType queryType;
	@Getter
	@Setter
	private String objectId;
	@Getter
	@Setter
	private String clientPlatformId;
	@Getter
	@Setter
	private String clientPlatoformInstance;
	@Getter
	@Setter
	private String clientSession;
	@Getter
	@Setter
	private String clientConnection;
	@Getter
	@Setter
	private boolean cacheable = false;

	public OperationModel() {

	}

	@Override
	public String toString() {
		return new StringBuilder().append("OperationModel [ontologyName=").append(ontologyName)
				.append(", operationType=").append(operationType).append(", user=").append(user).append(", source=")
				.append(source).append(", body=").append(body).append(", queryType=").append(queryType)
				.append(", objectId=").append(objectId).append(", clientPlatformId=").append(clientPlatformId)
				.append(", clientPlatformInstance=").append(clientPlatoformInstance).append(", clientSession=")
				.append(clientSession).append(", clientConnection=").append(clientConnection).append(", cacheable=")
				.append(cacheable).append("]").toString();
	}

	private OperationModel(Builder builder) {
		this.ontologyName = builder.ontologyName;
		this.operationType = builder.operationType;
		this.user = builder.user;
		this.source = builder.source;
		this.body = builder.body;
		this.queryType = builder.queryType;
		this.objectId = builder.objectId;
		this.clientPlatformId = builder.clientPlatformId;
		this.clientPlatoformInstance = builder.clientPlatformInstance;
		this.clientSession = builder.clientSession;
		this.clientConnection = builder.clientConnection;
		this.cacheable = builder.cacheable;
	}

	public static Builder builder(String ontologyName, OperationType operationType, String user, Source source) {
		return new Builder(ontologyName, operationType, user, source);
	}

	public static class Builder {
		private String ontologyName;
		private OperationType operationType;
		private QueryType queryType;
		private String user;
		private Source source;

		private String body;
		private String objectId;
		private String clientPlatformId;
		private String clientPlatformInstance;
		private String clientSession;
		private String clientConnection;
		private boolean cacheable = false;

		public Builder(String ontologyName, OperationType operationType, String user, Source source) {
			this.ontologyName = ontologyName;
			this.operationType = operationType;
			this.user = user;
			this.source = source;
		}

		public OperationModel build() {
			return new OperationModel(this);
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder queryType(QueryType queryType) {
			this.queryType = queryType;
			return this;
		}

		public Builder objectId(String objectId) {
			this.objectId = objectId;
			return this;
		}

		public Builder clientPlatformId(String clientPlatformId) {
			this.clientPlatformId = clientPlatformId;
			return this;
		}

		public Builder clientPlatformInstance(String clientPlatformInstance) {
			this.clientPlatformInstance = clientPlatformInstance;
			return this;
		}

		public Builder clientSession(String clientSession) {
			this.clientSession = clientSession;
			return this;
		}

		public Builder clientConnection(String clientConnection) {
			this.clientConnection = clientConnection;
			return this;
		}

		public Builder cacheable(boolean cacheable) {
			this.cacheable = cacheable;
			return this;
		}
	}
}