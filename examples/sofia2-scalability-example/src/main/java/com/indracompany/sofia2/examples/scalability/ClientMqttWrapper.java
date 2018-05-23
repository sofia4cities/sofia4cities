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
