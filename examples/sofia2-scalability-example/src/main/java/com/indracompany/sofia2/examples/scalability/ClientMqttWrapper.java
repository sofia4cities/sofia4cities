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

import org.springframework.core.io.ClassPathResource;

import com.indracompany.sofia2.client.MQTTClient;
import com.indracompany.sofia2.client.configuration.MQTTSecureConfiguration;

public class ClientMqttWrapper implements Client {

	private MQTTClient client;
	
	public ClientMqttWrapper(String url) {
		createClient(url);
	}
	
	@Override
	public void createClient(String url) {
		MQTTClient mqttClient;
		String protocol = url.substring(0, 3);
		if("tcp".equals(protocol.toLowerCase())) {
			mqttClient = new MQTTClient(url);
		} else {
			ClassPathResource classPathResource = new ClassPathResource("clientdevelkeystore.jks");
			String keystore;
			try {
				keystore = classPathResource.getFile().getAbsolutePath();
			} catch (IOException e) {
				throw new RuntimeException("Error opening clientdevelkeystore.jks");
			}
			MQTTSecureConfiguration sslConfig = new MQTTSecureConfiguration(keystore, "changeIt!");
			mqttClient = new MQTTClient(url, sslConfig);	
		}
		
		if(client != null) {
			client.disconnect();
		}
		client = mqttClient;
	}

	@Override
	public void connect(String token, String clientPlatform, String clientPlatformInstance, boolean avoidSSLValidation)
			throws IOException {
		client.connect(token, clientPlatform, clientPlatformInstance, 10);
	}

	@Override
	public void insertInstance(String ontology, String instance) {
		client.publish(ontology, instance, 10);
	}

	@Override
	public void disconnect() {
		client.disconnect();
	}

	@Override
	public String getProtocol() {
		return "mqtt";
	}

}
