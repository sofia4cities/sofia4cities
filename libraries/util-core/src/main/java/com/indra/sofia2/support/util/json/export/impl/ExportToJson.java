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
package com.indra.sofia2.support.util.json.export.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("exportToJson")
public class ExportToJson  {
	
	@Autowired
	@Qualifier("restApiObjectMapper")
	private ObjectMapper mapper;

	private static final Logger log = LoggerFactory.getLogger(ExportToJson.class);

	
	//Crea un fichero json a partir del json 
	public ByteArrayOutputStream extractJSONtoFile(String resultQuery) {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		log.debug("Se recoge el JSON: {}",resultQuery);

		try {
			Object json = mapper.readValue(resultQuery, Object.class);
			resultQuery = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (JsonParseException e) {
			log.error("extractJSONtoFile -> JsonParseException: {}",e.getMessage());
		} catch (JsonMappingException e) {
			log.error("extractJSONtoFile -> JsonMappingException: {}",e.getMessage());
		} catch (IOException e) {
			log.error("extractJSONtoFile -> IOException: {}",e.getMessage());
		}
		
		escribirSalida(resultQuery,out);

		return out;
	}

	private synchronized void escribirSalida(String cadena,ByteArrayOutputStream out) {

		byte buf[];
		
		try {
			buf = cadena.getBytes();
			out.write(buf);
		} catch (IOException e) {
			log.error("Error al escribir en el fichero JSON",e);
		}
	}
}