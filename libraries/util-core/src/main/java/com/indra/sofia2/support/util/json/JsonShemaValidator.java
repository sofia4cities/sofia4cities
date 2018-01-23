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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indra.sofia2.support.util.json.exceptions.ParseOntoException;

@Component
@SuppressWarnings("unchecked")
public class JsonShemaValidator {

	private static final Log LOG = LogFactory.getLog(JsonShemaValidator.class);

	public boolean validatorSchemaOnto (String template, String ontology)throws ParseOntoException{
		boolean compare = false;
		String auxTemplate = prepareEsquema(template);
		String auxOntology = prepareEsquema(ontology);

		try{
			Map<String, Object> mapTemplate = new ObjectMapper().readValue(auxTemplate, new TypeReference<Map<String, Object>>(){});
			Map<String, Object> mapOntology = new ObjectMapper().readValue(auxOntology, new TypeReference<Map<String, Object>>(){});
			if(mapTemplate.containsKey("properties")){
				try{
					Map<String,Object> propertiesMap = (Map<String,Object>) mapTemplate.get("properties");

					for (Entry <String, Object> jsonElement : propertiesMap.entrySet()){
						if (jsonElement.getValue() instanceof LinkedHashMap){
							Map<String,Object> elementMap = (Map<String,Object>)jsonElement.getValue();
							Object ref = elementMap.get("$ref");
							if (ref!=null){
								String refScript = ((String)ref).replace("#/", "");
								//Es una referencia la recupero del raiz
								Object refObjet = mapTemplate.get(refScript);
								Object refObjectOnt = mapOntology.get(refScript);
								if(refObjectOnt == null){
									LOG.info("No found a key with value: " + refScript + " into ontology schema ");
								}
								if(refObjet!=null){
									if(mapOntology.containsKey(refScript)){
										Map<String,Object> refObjectMap = (Map<String,Object>) refObjet;
										Map<String,Object> refObjectMapOnt = (Map<String,Object>)refObjectOnt;
										compare = validateProperties(refObjectMap.get("properties"), refObjectMapOnt.get("properties"), mapTemplate, mapOntology);
										if(!compare){
											return false;
										}
									}else{
										return false;
									}
								}else{
									LOG.error("Schema of template invalid");
									return false;
								}
							}else{
								LOG.warn("No found the element $ref into schema . Please, review it.");
								throw new ParseOntoException("No found the element $ref. Please, review the schema");
							}
						}else if(jsonElement.getValue()instanceof Map<?,?>){
							compare = validateProperties(jsonElement.getValue(), mapOntology.get("properties"), mapTemplate, mapOntology);
							if(!compare){
								return false;
							}
						}else if(jsonElement.getValue() instanceof ArrayList<?>){
							compare = validateProperties(jsonElement.getValue(), mapOntology.get("properties"), mapTemplate, mapOntology);
							if(!compare){
								return false;
							}
						}else if(mapOntology.containsValue(jsonElement.getValue())){
							compare = true;
						}else{
							LOG.warn("Imposible parser schema. Please review it");
							throw new ParseOntoException("Imposible parser schema. Please review it");
						}
					}
				}catch(Exception e){
					compare = true;
				}
			}else{
				compare = true;
			}
		}catch(Exception e){
			LOG.error("Error validation ontology schema " + e.getMessage());
			throw new ParseOntoException ("Error validation ontology schema ");
		}
		return  compare;
	}

	private boolean validateProperties(Object template, Object ontology, Object templateParent, Object ontologyParent){
		boolean compare = false;
		if(template != null && ontology != null){
			if((template instanceof Map<?,?> && ((Map<String,Object>)template).entrySet().size() == 0) || 
			   (template instanceof ArrayList<?> && ((ArrayList<?>)template).size() == 0)) {
				compare = true;			
			}else if(template instanceof Map<?,?> && ontology instanceof Map<?,?>){ 
				Iterator<Map.Entry<String, Object>> it = ((Map<String,Object>)template).entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String, Object> ent = it.next();
					if(((Map<String,Object>)ontology).containsKey(ent.getKey())){
						try{
							if(ent.getKey().equals("type")){
								LOG.info(" Validating a type ");
								if(((Map<String,Object>)ontology).containsKey(ent.getKey())){
									if(((Map<String,Object>)ontology).get(ent.getKey()).equals(ent.getValue())){
										compare = true;
									}else{
										return false;
									}
								}
							}else if(ent.getKey().equals("required") || ent.getKey().equals("additionalProperties")){
								LOG.info(" Ignored elements : \"required\", \"additionalProperties\" ");

							}else{
								compare = validateProperties(ent.getValue(), ((Map<String,Object>)ontology).get(ent.getKey()),templateParent, ontologyParent);
								if(!compare){
									return false;
								}
							}
						}catch(Exception e){
							LOG.warn("Error to get Value in ontology ");
						}
					}else{
						LOG.info("The key: " + ent.getKey() + " is not contained in ontology.");
						return false;
					}
				}
			}else if(template instanceof ArrayList<?> && ontology instanceof ArrayList<?>){			
				Iterator<?> it = ((ArrayList<?>)template).iterator();
				while(it.hasNext()) {
					Object element = it.next();
					if(element instanceof Map<?,?>){
						Map<String,Object> ent = (Map<String,Object>) element;
						if(ent.containsKey("$ref")){
							Object ref = ent.get("$ref");
							if (ref!=null){
								String refScript = ((String)ref).replace("#/", "");
								//Es una referencia la recupero del raiz
								Object refObjet = ((Map<String,Object>)templateParent).get(refScript);
								Object refObjectOnt = ((Map<String,Object>)ontologyParent).get(refScript);
								if(refObjectOnt == null){
									LOG.info("No found a key with value: " + refScript + " into ontology schema ");
									return false;
								}
								if(refObjet!=null){
									if(((Map<String,Object>)ontologyParent).containsKey(refScript)){
										Map<String,Object> refObjectMap = (Map<String,Object>) refObjet;
										Map<String,Object> refObjectMapOnt = (Map<String,Object>)refObjectOnt;
										compare = validateProperties(refObjectMap.get("properties"), refObjectMapOnt.get("properties"), templateParent,ontologyParent);
										if(!compare){
											return false;
										}
									}else{
										compare = false;
									}
								}else{
									LOG.error("Schema of template invalid");
									return false;
								}
							}else{
								LOG.warn("No found the element $ref into schema . Please, review it.");
								throw new ParseOntoException("No found the element $ref. Please, review the schema");
							}
						}else{
							if(((ArrayList<?>)ontology).contains(ent)){
								compare = true;
							}else{
								LOG.info("The element: " + ent + " is not contained in ontology.");
								return false;
							}
						}
					}else if(element instanceof ArrayList<?>){
						if(((ArrayList<?>)ontology).contains(element)){
							compare = true;
						}else{
							LOG.info("The element: " + element + " is not contained in ontology.");
							return false;
						}
					}else if(((ArrayList<?>)ontology).contains(element)){
						compare = true;
					}else{
						LOG.info("The element: " + element + " is not contained in ontology.");
						return false;
					}
				}
			}else if(template.equals(ontology)){
				compare = true;
			}else{
				return false;
			}
		}else{
			LOG.warn("Imposible to process the element.");
		}
		return compare;
	}

	private String prepareEsquema(String esquemajson) {
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
