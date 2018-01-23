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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indra.sofia2.support.bbdd.sib.utils.exceptions.ParsePlainException;
import com.indra.sofia2.support.bbdd.sib.utils.exceptions.ParsePlainSpecificException;

@Component
public final class JsonSchemaPlanner {
	
	private static final String ID_FIELD = "_id";
	private static final Logger logger = LoggerFactory.getLogger(JsonSchemaPlanner.class);
	
	public String getDataType(String dataTypeDefinition) {
		try {
			JsonNode dataTypeDefinitionObj = new ObjectMapper().readTree(dataTypeDefinition);
			return dataTypeDefinitionObj.get("properties").get("value").get("type").asText();
		} catch (Exception e) {
			logger.error("Error parsing schema type data.", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getAtributeRootName(String json) {
		String rootName=null;
		try {
			String myJson = prepareEsquema(json);
			Map<String,Object> obj = new ObjectMapper().readValue(myJson, Map.class);
			//Analizamos el objeto padre con una estructura fija.
			List<String> requiredList = (List<String>)obj.get("required");
			//Para las ontologías de tipo GSMA (sin elemento raiz)
			if(requiredList.size()!=1){
				return null;
			}else{
				Map<String,Object> propertiesMap = (Map<String,Object>)obj.get("properties");
				for (Entry <String, Object> jsonElement : propertiesMap.entrySet()){
					rootName = jsonElement.getKey();
				}
			}
			
		}catch (Exception e) {
			throw new ParsePlainException(e);		
		} 
		return rootName;
	}
	
	@SuppressWarnings("unchecked")
	private OntologySchema processJsonSchema(String json, boolean appendOntologyPrefix){
		OntologySchema schema = new OntologySchema();
		try {
			String myJson = prepareEsquema(json);
			Map<String,Object> obj = new ObjectMapper().readValue(myJson, Map.class);
			//Analizamos el objeto padre con una estructura fija.
			Map<String,Object> propertiesMap = (Map<String,Object>)obj.get("properties");
			for (Entry <String, Object> jsonElement : propertiesMap.entrySet()){
				if (jsonElement.getValue() instanceof LinkedHashMap && !jsonElement.getKey().equals(ID_FIELD)){
					Map<String,Object> elementMap = (Map<String,Object>)jsonElement.getValue();
					Object ref = elementMap.get("$ref");
					if (ref!=null){
						String refScript = ((String)ref).replace("#/", "");
						//Es una referencia la recupero del raiz
						Object refObjet = obj.get(refScript);
						if (refObjet!=null){
							Map<String,Object> refObjectMap = (Map<String,Object>) refObjet;
							for (Entry <String, Object> jsonRefElement : refObjectMap.entrySet()){
								if ("properties".equals(jsonRefElement.getKey())){
									Map<String,Object> properties = (Map<String,Object>)jsonRefElement.getValue();
									analizeProperties(schema, properties, obj, (appendOntologyPrefix ? jsonElement.getKey() : null));
								}	
							}
						}
					}else{
						analizeProperties(schema, propertiesMap, obj, null);
					}
				}
			}
		} catch (Exception e) {
			throw new ParsePlainException(e);		
		}
		return schema;
	}

	public OntologySchema buildOntologySchema(String json){
		return processJsonSchema(json, true);
	}
	
	public Map<String, String> getAtributesValuesString(String json) {
		return processJsonSchema(json, false).getAttributes();
	}

	@SuppressWarnings("unchecked")
	private void analizeProperties(OntologySchema schema, Map<String,Object> properties, Map<String,Object> raiz, String identificadorBase){
		for (Entry <String, Object> jsonPropertiesElement : properties.entrySet()){
			String identificador;
			String key = jsonPropertiesElement.getKey();
			if (identificadorBase == null || key.equals("geometry") || key.equals("affectedLocations")
					|| key.equals("array")){
				identificador = jsonPropertiesElement.getKey();
			}else{
				identificador =  identificadorBase + "." + key;
			}
			
			if (jsonPropertiesElement.getValue() instanceof Map<?,?>){
				/*
				 * The properties that can't be casted to maps contain schema metadata. We'll skip them.
				 */
				Map<String,Object> jsonPropertyElement= (Map<String,Object>)jsonPropertiesElement.getValue();
				String type = (String)jsonPropertyElement.get("type");
				if (type!=null){
					if ("object".equals(type)) {
						StringBuffer descomposicion = null;
						//si existe properties
						if (jsonPropertyElement.get("properties")!=null){
							analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("properties"), raiz, identificador);
						}else{
							if (jsonPropertyElement.get("allOf")!=null){
								if (jsonPropertyElement.get("allOf") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("allOf"), raiz, identificador);
								}else{
									descomposicion=analizeArray(schema, null, (ArrayList<?>) jsonPropertyElement.get("allOf"), raiz, identificador);
								}	
							}else if (jsonPropertyElement.get("anyOf")!=null){
								if (jsonPropertyElement.get("anyOf") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("anyOf"), raiz, identificador);
								}else{
									descomposicion=analizeArray(schema, null, (ArrayList<?>) jsonPropertyElement.get("anyOf"), raiz, identificador);
								}	
							}else if (jsonPropertyElement.get("oneOf")!=null){
								if (jsonPropertyElement.get("oneOf") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("oneOf"), raiz, identificador);
								}else{
									descomposicion=analizeArray(schema, null, (ArrayList<?>) jsonPropertyElement.get("oneOf"), raiz, identificador);
								}	
							}else if (jsonPropertyElement.get("not")!=null){
								if (jsonPropertyElement.get("not") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("not"), raiz, identificador);
								}else{
									descomposicion=analizeArray(schema, null, (ArrayList<?>) jsonPropertyElement.get("not"), raiz, identificador);
								}	
							}
							if (descomposicion!=null){
								descomposicion.insert(0, "array[");
								descomposicion.append("]");
								if(!schema.containsAttribute(identificador)){
									schema.addAttribute(identificador, descomposicion.toString());
								}
							}
						}					
					} else if ("array".equals(type)) {
						StringBuffer descomposicion = null;
						if(jsonPropertyElement.get("items") instanceof ArrayList<?> ){
							descomposicion = analizeArray(schema, null,(ArrayList<?>) jsonPropertyElement.get("items"), raiz, null);
						}else{
							descomposicion = analizeArray(schema, null, (Map<String, Object>) jsonPropertyElement.get("items"), raiz, identificador);
						}
	
						if (descomposicion!=null){
							descomposicion.insert(0, "array[");
							descomposicion.append("]");
							if(!schema.containsAttribute(identificador))
								schema.addAttribute(identificador, descomposicion.toString());
						}
					}else{
						if (identificador.equals("geometry.type")){
							/*
							 * Si es un geometry.type en vez de devolver el tipo fisico del dato devolvemos el tipo logico 
							 */
							try{
								if(!schema.containsAttribute(identificador))
									schema.addAttribute(identificador, ((ArrayList<String>)jsonPropertyElement.get("enum")).get(0));
							}catch (Exception e){
								throw new ParsePlainSpecificException(e);
							}
						}else{
							if(!schema.containsAttribute(identificador))
								schema.addAttribute(identificador, type);
						}
					}
				}else{
					String ref = (String)jsonPropertyElement.get("$ref");
					if (ref!=null){
						String refScript = ((String)ref).replace("#/", "");
						//Es una referencia la recupero del raiz
						Object refObjet = raiz.get(refScript);
						if (refObjet!=null){
							Map<String,Object> refObjectMap = (Map<String,Object>) refObjet;
							for (Entry <String, Object> jsonRefElement : refObjectMap.entrySet()){
								if ("properties".equals(jsonRefElement.getKey())) {
									Map<String,Object> propertiesRefElement = (Map<String,Object>)jsonRefElement.getValue();
									analizeProperties(schema, propertiesRefElement, raiz, identificador);
								}	
							}
						}
					}
				} // end of else type == null
			}
		}
	}

	@SuppressWarnings("unchecked")
	private StringBuffer analizeArray(OntologySchema schema, StringBuffer descomposicion, ArrayList<?> items, Map<String,Object> raiz, String identificadorBase){
		StringBuffer myDescomposicion = descomposicion;
		if(descomposicion !=null && !descomposicion.toString().startsWith(",")){
			myDescomposicion.insert(0, ",");
		}
		if(items != null && raiz != null){
			Iterator<?> it = items.iterator();
			while(it.hasNext()) {
				Map<String,Object> ent = (Map<String,Object>)it.next();
				String type = (String)ent.get("type");
				if (type!=null){
					if (myDescomposicion==null){
						myDescomposicion=new StringBuffer();
					}
					if ("object".equals(type)) {
						if(ent.get("properties") !=null){
							StringBuffer descomposicionAux = analizeArrayObject(schema, null, (Map<String, Object>) ent.get("properties"), raiz, null);
							if (descomposicionAux!=null){
								descomposicionAux.insert(0, "object[");
								descomposicionAux.append("]");
								myDescomposicion.insert(0, descomposicionAux);
							}
						}else{
							StringBuffer descomposicionAux = new StringBuffer();
							if (ent.get("allOf")!=null){
								if (ent.get("allOf") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) ent.get("allOf"), raiz, identificadorBase);
								}else{
									descomposicionAux = analizeArray(schema, null, (ArrayList<?>) ent.get("allOf"), raiz, identificadorBase);
								}	
							}else if (ent.get("anyOf")!=null){
								if (ent.get("anyOf") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) ent.get("anyOf"), raiz, identificadorBase);
								}else{
									descomposicionAux = analizeArray(schema, null, (ArrayList<?>) ent.get("anyOf"), raiz, identificadorBase);
								}	
							}else if (ent.get("oneOf")!=null){
								if (ent.get("oneOf") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) ent.get("oneOf"), raiz, identificadorBase);
								}else{
									descomposicionAux = analizeArray(schema, null, (ArrayList<?>) ent.get("oneOf"), raiz, identificadorBase);
								}	
							}else if (ent.get("not")!=null){
								if (ent.get("not") instanceof Map){
									analizeProperties(schema, (Map<String, Object>) ent.get("not"), raiz, identificadorBase);
								}else{
									descomposicionAux = analizeArray(schema, null, (ArrayList<?>) ent.get("not"), raiz, identificadorBase);
								}	
							}
							if (descomposicionAux!=null){
								descomposicionAux.insert(0, "array[");
								descomposicionAux.append("]");
								myDescomposicion.insert(0, identificadorBase + "." + descomposicionAux.toString());
							}
						}
					} else if ("array".equals(type)) {
						StringBuffer descomposicionAux= analizeArray(schema, null, (Map<String, Object>) ent.get("items"), raiz, null);
						if (descomposicionAux!=null){
							descomposicionAux.insert(0, "array[");
							descomposicionAux.append("]");
							myDescomposicion.insert(0, descomposicionAux);
						}
					}else{
						myDescomposicion.append(type);//insert(0, type);
					}
					if(it.hasNext()){
						myDescomposicion.append(",");
					}
				}else{
					
					String ref = (String)ent.get("$ref");
					if (ref!=null){
						String refScript = ((String)ref).replace("#/", "");
						//Es una referencia la recupero del raiz
						Object refObjet = raiz.get(refScript);
						if (refObjet!=null){
							Map<String,Object> refObjectMap = (Map<String,Object>) refObjet;
							
							Iterator<Entry <String, Object>> it2 = refObjectMap.entrySet().iterator();
							while(it2.hasNext()) {
								Entry <String, Object> jsonRefElement = it2.next();
								if ("properties".equals(jsonRefElement.getKey())) {
									Map<String,Object> propertiesRefElement = (Map<String,Object>)jsonRefElement.getValue();
									myDescomposicion = analizeArrayObject(schema, myDescomposicion, propertiesRefElement, raiz, identificadorBase);
								}
							}
						}
					}
				}
			}
		}
		return myDescomposicion;
	}
	@SuppressWarnings("unchecked")
	private StringBuffer analizeArray(OntologySchema schema, StringBuffer descomposicion, Map<String,Object> items, Map<String,Object> raiz, String identificador){
		StringBuffer myDescomposicion = descomposicion;
		if(descomposicion !=null && !descomposicion.toString().startsWith(",")){
			myDescomposicion.insert(0, ",");
		}
		
		String type = (String)items.get("type");
		if (type!=null){
			if (myDescomposicion==null){
				myDescomposicion=new StringBuffer();
			}
			if ("object".equals(type)) {
				if((Map<String,Object>)items.get("properties") !=null){

					StringBuffer descomposicionAux = analizeArrayObject(schema, null, (Map<String, Object>) items.get("properties"), raiz, null);
					if (descomposicionAux!=null){
						descomposicionAux.insert(0, "object[");
						descomposicionAux.append("]");
						myDescomposicion.insert(0, descomposicionAux);
					}
				}else{
					StringBuffer descomposicionAux = new StringBuffer();
					if (items.get("allOf")!=null){
						if (items.get("allOf") instanceof Map){
							analizeProperties(schema, (Map<String, Object>) items.get("allOf"), raiz, identificador);
						}else{
							descomposicionAux = analizeArray(schema, null, (ArrayList<?>) items.get("allOf"), raiz, identificador);
						}	
					}else if (items.get("anyOf")!=null){
						if (items.get("anyOf") instanceof Map){
							analizeProperties(schema, (Map<String, Object>) items.get("anyOf"), raiz, identificador);
						}else{
							descomposicionAux = analizeArray(schema, null, (ArrayList<?>) items.get("anyOf"), raiz, identificador);
						}	
					}else if (items.get("oneOf")!=null){
						if (items.get("oneOf") instanceof Map){
							analizeProperties(schema, (Map<String, Object>) items.get("oneOf"), raiz, identificador);
						}else{
							descomposicionAux = analizeArray(schema, null, (ArrayList<?>) items.get("oneOf"), raiz, identificador);
						}	
					}else if (items.get("not")!=null){
						if (items.get("not") instanceof Map){
							analizeProperties(schema, (Map<String, Object>) items.get("not"), raiz, identificador);
						}else{
							descomposicionAux = analizeArray(schema, null, (ArrayList<?>) items.get("not"), raiz, identificador);
						}	
					}
					if (descomposicionAux!=null){
						descomposicionAux.insert(0, "array[");
						descomposicionAux.append("]");
						schema.addAttribute(identificador, descomposicionAux.toString());
					}
				}
			} else if ("array".equals(type)) {
				StringBuffer descomposicionAux = null;
				if(items.get("items") instanceof ArrayList<?> ){
					descomposicionAux = analizeArray(schema, null,(ArrayList<?>) items.get("items"), raiz, null);
				}else{
					descomposicionAux = analizeArray(schema, null, (Map<String, Object>) items.get("items"), raiz, null);
				}
				if (descomposicionAux!=null){
					descomposicionAux.insert(0, "array[");
					descomposicionAux.append("]");
					myDescomposicion.insert(0, descomposicionAux);
				}
			}else{
				myDescomposicion.insert(0, type);
			}
		}
		String aux = myDescomposicion.toString();
		if(myDescomposicion.toString().endsWith(",")){
			aux = aux.substring(0, aux.lastIndexOf(","));
			myDescomposicion = new StringBuffer(aux);
		}
		return myDescomposicion;
	}

	@SuppressWarnings("unchecked")
	private StringBuffer analizeArrayObject(OntologySchema schema, StringBuffer descomposicion, Map<String,Object> properties, Map<String,Object> raiz, String identificadorBase){
		StringBuffer myDescomposicion = descomposicion;
		
		Iterator<Entry <String, Object>> it = properties.entrySet().iterator();
		while(it.hasNext()) {
			Entry <String, Object> jsonPropertiesElement = it.next();
		//for (Entry <String, Object> jsonPropertiesElement : properties.entrySet()){
			String identificador;
			if (identificadorBase==null){
				identificador = jsonPropertiesElement.getKey();
			}else{
				identificador =  identificadorBase + "." + jsonPropertiesElement.getKey();
			}
			Map<String,Object> jsonPropertyElement= (Map<String,Object>)jsonPropertiesElement.getValue();
			String type = (String)jsonPropertyElement.get("type");
			if (type!=null){
				if (myDescomposicion==null){
					myDescomposicion=new StringBuffer();
				}
				if ("object".equals(type)) {
					Map<String, Object> propertiesObject = (Map<String, Object>)jsonPropertyElement.get("properties");
					//ES UN CASO ESPECIAL EN EL QUE EL OBJETO NO CONTIENE PROPERTIES
					if (propertiesObject==null){
						if (jsonPropertyElement.get("allOf")!=null){
							if (jsonPropertyElement.get("allOf") instanceof Map){
								//								propertiesObject=(Map<String, Object>)jsonPropertyElement.get("allOf");
								analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("allOf"), raiz, identificador);
							}else{
								myDescomposicion = analizeArray(schema, myDescomposicion, (ArrayList<?>) jsonPropertyElement.get("allOf"), raiz, identificador);
							}	
						}else if (jsonPropertyElement.get("anyOf")!=null){
							if (jsonPropertyElement.get("anyOf") instanceof Map){
								//								propertiesObject=(Map<String, Object>)jsonPropertyElement.get("anyOf");
								analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("anyOf"), raiz, identificador);
							}else{
								myDescomposicion = analizeArray(schema, myDescomposicion, (ArrayList<?>) jsonPropertyElement.get("anyOf"), raiz, identificador);
							}	
						}else if (jsonPropertyElement.get("oneOf")!=null){
							if (jsonPropertyElement.get("oneOf") instanceof Map){
								//								propertiesObject=(Map<String, Object>)jsonPropertyElement.get("oneOf");
								analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("oneOf"), raiz, identificador);
							}else{
								myDescomposicion = analizeArray(schema, myDescomposicion, (ArrayList<?>) jsonPropertyElement.get("oneOf"), raiz, identificador);
							}	
						}else if (jsonPropertyElement.get("not")!=null){
							if (jsonPropertyElement.get("not") instanceof Map){
								//								propertiesObject=(Map<String, Object>)jsonPropertyElement.get("not");
								analizeProperties(schema, (Map<String, Object>) jsonPropertyElement.get("not"), raiz, identificador);
							}else{
								myDescomposicion = analizeArray(schema, myDescomposicion, (ArrayList<?>) jsonPropertyElement.get("not"), raiz, identificador);
							}	
						}
					}else{
						StringBuffer descomposicionAux = analizeArrayObject(schema, null, propertiesObject, raiz, null);
						if (descomposicionAux!=null){
							descomposicionAux.insert(0, "object[");
							descomposicionAux.insert(0, ":");
							descomposicionAux.insert(0, identificador);
							descomposicionAux.append("]");
							myDescomposicion.insert(0, descomposicionAux);
						}
					}
				} else if ("array".equals(type)) {
					StringBuffer descomposicionAux = null;
					if(jsonPropertyElement.get("items") instanceof ArrayList<?> ){
						descomposicionAux = analizeArray(schema, null, (ArrayList<?>) jsonPropertyElement.get("items"), raiz, null);
					}else{
						descomposicionAux = analizeArray(schema, null, (Map<String, Object>) jsonPropertyElement.get("items"), raiz, null);
					}
					if (descomposicionAux!=null){
						descomposicionAux.insert(0, "array[");
						descomposicionAux.insert(0, ":");
						descomposicionAux.insert(0, identificador);
						descomposicionAux.append("]");
						if(! myDescomposicion.toString().startsWith(",") && myDescomposicion != null){
							myDescomposicion.insert(0, ",");	
						}
						myDescomposicion.insert(0, descomposicionAux);
					}
				}else{
					if (identificador.equals("geometry.type")){
						/*
						 * Si es un geometry.type en vez de devolver el tipo fisico del dato devolvemos el tipo logico 
						 */
						try{
							myDescomposicion.insert(0, identificador + ":" + ((ArrayList<String>)jsonPropertyElement.get("enum")).get(0));
						}catch (Exception e){
							throw new ParsePlainSpecificException(e);
						}
					}else{
						myDescomposicion.insert(0, identificador + ":" + type);
					}
				}

				if(it.hasNext()){
					myDescomposicion.insert(0,",");
				}
			}else{
				String ref = (String)jsonPropertyElement.get("$ref");
				if (ref!=null){
					String refScript = ((String)ref).replace("#/", "");
					//Es una referencia la recupero del raiz
					Object refObjet = raiz.get(refScript);
					if (refObjet!=null){
						Map<String,Object> refObjectMap = (Map<String,Object>) refObjet;
						
						Iterator<Entry <String, Object>> it2 = refObjectMap.entrySet().iterator();
						while(it2.hasNext()) {
							Entry <String, Object> jsonRefElement = it2.next();
							if ("properties".equals(jsonRefElement.getKey())) {
								Map<String,Object> propertiesRefElement = (Map<String,Object>)jsonRefElement.getValue();
								analizeArrayObject(schema, myDescomposicion, propertiesRefElement, raiz, identificador);
							}
						}
					}
				}
			}
		}
		String aux = myDescomposicion.toString();
		if(myDescomposicion.toString().endsWith(",")){
			aux = aux.substring(0, aux.lastIndexOf(","));
			myDescomposicion = new StringBuffer(aux);
		}
		return myDescomposicion;

	}

	public String prepareEsquema(String esquemajson) {
		String myEsquemaJson = esquemajson;
		if (myEsquemaJson != null && myEsquemaJson.length() > 0){
			myEsquemaJson = myEsquemaJson.replaceAll("&nbsp;", "");
			myEsquemaJson = myEsquemaJson.replaceAll("&amp;", "");
			myEsquemaJson = myEsquemaJson.replaceAll("&quot;", "\"");
			myEsquemaJson = myEsquemaJson.replace("'", "\"");
		}
		if (myEsquemaJson != null) {
			myEsquemaJson = myEsquemaJson.replace("\t", "");
			myEsquemaJson = myEsquemaJson.replace("\r", "");
			myEsquemaJson = myEsquemaJson.replace("\n", "");
			myEsquemaJson = myEsquemaJson.replace("'", "\"");
		}

		return myEsquemaJson;
	}

}
