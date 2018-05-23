package com.indracompany.sofia2.examples.scalability;

import java.io.IOException;

public interface Client {
	public void createClient(String url);
	public void connect(String token, String clientPlatform, String clientPlatformInstance, boolean avoidSSLValidation) throws IOException;
	public void insertInstance(String ontology, String instance);
	public void disconnect();
	public String getProtocol();
}
