package com.indracompany.sofia2.ssap.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;

//JSON parser is a singleton to avoid Spring dependencies for SSAP Library
public class SSAPJsonParser {
	
	private static volatile SSAPJsonParser instance;
	private static Object mutex = new Object();
	
	private final ObjectMapper objectMapper;
	
	
	
	protected final ObjectMapper getObjectMapper() {
		return objectMapper;
	}


	public SSAPJsonParser() {
		//ObjectMapper it is supossed to be thread-safe. So this one is to reuse for all SSAP parser operations
		objectMapper = new ObjectMapper();
		SSAPJsonModule m = new SSAPJsonModule();
		objectMapper.registerModule(m);
		
		//TODO: Configure object mapper
	}
	
	public static SSAPJsonParser getInstance() {
		SSAPJsonParser result = instance;
		if(result == null) {
			synchronized (mutex) {
				result = instance;
				if(result==null) {
					instance = result = new SSAPJsonParser();
				}
			}
		}
		
		return result;
		
	}
	
	public final String serialize(Object o) throws SSAPParseException {
		try {
			return this.getObjectMapper().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			
			throw new SSAPParseException(e.getMessage());
		}
	}
	
	public <T> T deserialize(String json, Class<T> clazz) throws SSAPParseException  {
		try {
			return this.getObjectMapper().readValue(json, clazz);
		} catch (IOException e) {
			throw new SSAPParseException(e.getMessage());
		}	    
	}
	
	public SSAPMessage deserialize(String json) throws SSAPParseException {
		return this.deserialize(json, SSAPMessage.class);
	}

	
	

}
