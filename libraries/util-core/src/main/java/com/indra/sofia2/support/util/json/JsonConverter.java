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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.json;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.indra.sofia2.support.util.json.exceptions.JsonConverterException;
import com.indra.sofia2.support.util.xml.schemas.DelayJustification;
import com.indra.sofia2.support.util.xml.schemas.ForecastMovement;
import com.indra.sofia2.support.util.xml.schemas.RealMovement;
import com.indra.sofia2.support.util.xml.schemas.TrainSetShort;

@Component
public class JsonConverter {
	
	private static final Log LOG = LogFactory.getLog(JsonConverter.class);
	
	/**
	 * Trata cada uno de los niveles de profundidad del fichero xml
	 * 
	 * @param jsonTransformado
	 * @param xsdClass
	 * @param instancia
	 */
	private void tratarNivel(JsonNode jsonTransformado, Class<?> xsdClass, Object instancia) throws Exception {
		try {
			
			Method method;
			List<Method> methods = Arrays.asList(xsdClass.getDeclaredMethods());
			Map<String, Method> hashMethods = new HashMap<String, Method>();
			for (Method m : methods) {
				hashMethods.put(m.getName(), m);
			}
					
			Iterator<Entry<String, JsonNode>> it = jsonTransformado.fields();	
			while (it.hasNext()) {
				Entry<String, JsonNode> campo = it.next();
				
				method = null;
				String atributo = campo.getKey();
				atributo = atributo.substring(0,1).toUpperCase() + atributo.substring(1);
				try {
					method = hashMethods.get("set" + atributo);

				} catch(Exception e) {
					LOG.error(e.getMessage());
				}
				
				if (method != null) {
					if (campo.getValue() instanceof ObjectNode) {
						Class<?> clazz = method.getParameterTypes()[0];
						Object subInstancia = clazz.newInstance();
						tratarNivel(campo.getValue(), method.getParameterTypes()[0], subInstancia); // Recursion
						method.invoke(instancia, subInstancia);
					} else {
						method.invoke(instancia, campo.getValue().asText());
					}
				}				
			}
				
			
		} catch (Exception e) {
			throw e;
		}
	}	
	
	public String toXml(String json, String ontologyName) throws JsonConverterException {
		String xml = "";
		
		try {
			
			Class<?> xsdClass = null;      
			
			SITRAEnum ontology = SITRAEnum.valueOf(ontologyName.toUpperCase());
			switch (ontology) {
			case DELAYJUSTIFICATION:
				xsdClass = Class.forName(DelayJustification.class.getName());
				break;
			case FORECASTMOVEMENT:	
				xsdClass = Class.forName(ForecastMovement.class.getName());
				break;		
			case REALMOVEMENT:
				xsdClass = Class.forName(RealMovement.class.getName());
				break;		
			case TRAINSET_SHORT:
				xsdClass = Class.forName(TrainSetShort.class.getName());
				break;				
			default:
				break;
			}
			
			Object instancia = xsdClass.newInstance();	
			
			JsonNode jsonTransformado = JsonLoader.fromString(json.toString());    
					
			Method method;
			List<Method> methods = Arrays.asList(xsdClass.getDeclaredMethods());
			Map<String, Method> hashMethods = new HashMap<String, Method>();
			for (Method m : methods) {
				hashMethods.put(m.getName(), m);
			}		
			
			Iterator<JsonNode> it = jsonTransformado.elements();
			while (it.hasNext()) {
				JsonNode nodo = it.next();
				Iterator<Entry<String, JsonNode>> it2 = nodo.fields();
				
				while (it2.hasNext()) {
					Entry<String, JsonNode> campo = it2.next();
					
					method = null;
					String atributo = campo.getKey();
					atributo = atributo.substring(0,1).toUpperCase() + atributo.substring(1);
					try {
						method = hashMethods.get("set" + atributo);

					} catch(Exception e) {
						LOG.error(e.getMessage());
					}
					
					if (method != null) {
						if (campo.getValue() instanceof ObjectNode) {
							Class<?> clazz = method.getParameterTypes()[0];
							Object subInstancia = clazz.newInstance();
							tratarNivel(campo.getValue(), method.getParameterTypes()[0], subInstancia);
							method.invoke(instancia, subInstancia);
						} else {
							method.invoke(instancia, campo.getValue().asText());
						}
					}
				}				
			}
			
			JAXBContext ctx = JAXBContext.newInstance(xsdClass);
			StringWriter sw = new StringWriter();
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);    
			marshaller.marshal(instancia, sw);
			
			xml = sw.toString();
			
		} catch (Exception e) {
			throw new JsonConverterException(e);
		}		
        
		return xml;		
	}
	
	public String toJson(String txt) {
		String json = "";
		return json;
	}
}
