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
import com.indracompany.sofia2.client.exception.MQTTException;

public class ClientMqttWrapper implements Client {

	private MQTTClient client;
	
	private String token;
	private String clientPlatform;
	private String clientPlatformInstance;
	private boolean avoidSSLValidation;
	private int attempts = 0;
	private final static int MAX_ATTEMPTS = 100;
	private final static int ATTEMPTS_DELAY = 500;

	public ClientMqttWrapper(String url) {
		createClient(url);
	}

	@Override
	public void createClient(String url) {
		MQTTClient mqttClient;
		String protocol = url.substring(0, 3);
		if ("tcp".equals(protocol.toLowerCase())) {
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

		if (client != null) {
			client.disconnect();
		}
		client = mqttClient;
	}

	@Override
	public void connect(String token, String clientPlatform, String clientPlatformInstance, boolean avoidSSLValidation)
			throws IOException {
		this.token = token;
		this.clientPlatform = clientPlatform;
		this.clientPlatformInstance = clientPlatformInstance;
		this.avoidSSLValidation = avoidSSLValidation;
		client.connect(token, clientPlatform, clientPlatformInstance, 10);
	}

	private void reconnect() throws InterruptedException {
		Thread.sleep(ATTEMPTS_DELAY);
		if (MAX_ATTEMPTS > attempts) {
			attempts++;
			try {
				connect(token, clientPlatform, clientPlatformInstance, avoidSSLValidation);
			} catch (IOException e) {
				reconnect();
			}
		} else {
			throw new RuntimeException("Impossible to reconnect with the server");
		}
	}
	
	@Override
	public void insertInstance(String ontology, String instance) {
		try {
			client.publish(ontology, instance, 10);
		} catch (MQTTException e) {
			try {
				reconnect();
			} catch (InterruptedException e1) {
				throw new RuntimeException("Error sleeping task");
			}
		}
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
