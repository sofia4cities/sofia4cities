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
package com.indracompany.sofia2.ssap.body;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPBodyJoinMessage extends SSAPBodyMessage {

	private String token;
	private String clientPlatform;
	private String clientPlatformInstance;
	private JsonNode deviceConfiguration;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClientPlatform() {
		return clientPlatform;
	}

	public void setClientPlatform(String clientPlatform) {
		this.clientPlatform = clientPlatform;
	}

	public JsonNode getDeviceConfiguration() {
		return deviceConfiguration;
	}

	public void setDeviceConfiguration(JsonNode deviceConfiguration) {
		this.deviceConfiguration = deviceConfiguration;
	}

	public String getClientPlatformInstance() {
		return clientPlatformInstance;
	}

	public void setClientPlatformInstance(String clientPlatformInstance) {
		this.clientPlatformInstance = clientPlatformInstance;
	}

	@Override
	public boolean isSessionKeyMandatory() {
		return false;
	}

	@Override
	public boolean isOntologyMandatory() {
		return false;
	}

}
