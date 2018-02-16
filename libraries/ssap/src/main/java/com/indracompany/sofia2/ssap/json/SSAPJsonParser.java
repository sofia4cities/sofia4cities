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
