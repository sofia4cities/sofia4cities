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
package com.indracompany.sofia2.examples.scalability;

import java.io.IOException;

import com.indracompany.sofia2.client.RestClient;

public class ClientRestWrapper implements Client{

	private RestClient client;
	
	public ClientRestWrapper(String url) {
		createClient(url);
	}
	
	@Override
	public void createClient(String url) {
		RestClient restClient = new RestClient(url);
		if (client != null) {
			client.disconnect();
		}
		client = restClient;
	}

	@Override
	public void connect(String token, String clientPlatform, String clientPlatformInstance,
			boolean avoidSSLValidation) throws IOException {
		client.connect(token, clientPlatform, clientPlatformInstance, avoidSSLValidation);
	}

	@Override
	public void insertInstance(String ontology, String instance) {
		client.insertInstance(ontology, instance);
	}

	@Override
	public void disconnect() {
		client.disconnect();
	}

	@Override
	public String getProtocol() {
		return "rest";
	}
	
}
