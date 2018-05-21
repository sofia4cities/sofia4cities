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
