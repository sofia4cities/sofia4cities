package com.indracompany.sofia2.metricExample.endpoints;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

@Component
public class CustomEndpoint implements Endpoint<List<String>> {

	@Override
	public String getId() {
		return "CustomEndPoint";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isSensitive() {
		return false;
	}

	@Override
	public List<String> invoke() {
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("Hola 1");
		messages.add("Hola 2");
		return messages;
	}
	
	

}
