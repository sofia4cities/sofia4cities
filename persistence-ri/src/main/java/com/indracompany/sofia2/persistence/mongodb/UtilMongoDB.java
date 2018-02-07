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
package com.indracompany.sofia2.persistence.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.util.CalendarAdapter;
import com.mongodb.client.MongoIterable;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UtilMongoDB {

	public String getCollectionName(String query, String pattern) {
		if (query.contains("db.")) {
			String auxName = query.replace("db.", "");
			auxName = auxName.substring(0, auxName.indexOf("." + pattern));
			return auxName;
		} else {
			throw new DBPersistenceException(new Exception("No found collection in query"));
		}
	}

	/**
	 * add {} into the String representing the JSON if not has
	 * 
	 * @param query
	 * @return
	 */
	public String prepareQuotes4find(String ontology, String query) {
		String result = query.trim();
		result = result.toLowerCase().replace("db." + ontology.toLowerCase() + ".find()", "{}");
		result = result.toLowerCase().replace("db." + ontology.toLowerCase() + ".find({})", "{}");
		String sI = "db." + ontology.toLowerCase() + ".find(";
		int i = result.toLowerCase().indexOf(sI);
		if (i != -1) {
			result = result.replace(sI, "");
			result = result.replace(")", "");
		}
		// newChar)
		if (!result.startsWith("{")) {
			StringBuffer resultObj = new StringBuffer(result);
			resultObj.insert(0, "{");
			resultObj.append("}");
			result = resultObj.toString();
		}
		return result;
	}

	public String prepareQuotes(String query) {
		String result = query.trim();
		// newChar)
		if (!result.startsWith("{")) {
			StringBuffer resultObj = new StringBuffer(result);
			resultObj.insert(0, "{");
			resultObj.append("}");
			result = resultObj.toString();
		}
		return result;
	}

	public String convertObjectIdV1toVLegacy(String query) {

		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern
				.compile("\\{\\\\*\"_id\\\\*\"\\s*:\\s*\\{\\s*\"\\$oid\"\\s*:\\s*\\\\*\"(.*)\\\\*\"\\s*}\\s*}");
		Matcher matcher = pattern.matcher(query);
		boolean changed = false;
		while (matcher.find()) {
			changed = true;
			matcher.group(0);
			String g1 = matcher.group(1);
			matcher.appendReplacement(sb, "{\"_id\":ObjectId(\"" + g1 + "\")}");
		}
		matcher.appendTail(sb);
		if (changed)
			query = sb.toString();

		return query;
	}

	public String getParentProperties(Map<String, Object> elements, Map<String, Object> schema) {
		for (Entry<String, Object> jsonElement : elements.entrySet()) {
			if (jsonElement.getValue() instanceof LinkedHashMap) {
				Map<String, Object> elementMap = (Map<String, Object>) jsonElement.getValue();
				Object ref = elementMap.get("$ref");
				if (ref != null) {
					String refScript = ((String) ref).replace("#/", "");
					// Es una referencia la recupero del raiz
					Object refObjet = schema.get(refScript);
					if (refObjet != null) {
						Map<String, Object> refObjectMap = (Map<String, Object>) refObjet;
						Map<String, Object> properties = (Map<String, Object>) refObjectMap.get("properties");
						if (null != properties && properties.containsKey("geometry")) {
							Map<String, Object> geometry = (Map<String, Object>) properties.get("geometry");
							if (geometry.containsKey("properties")) {
								Map<String, Object> propertiesGeometry = (Map<String, Object>) geometry
										.get("properties");
								if (propertiesGeometry.containsKey("type")
										&& propertiesGeometry.containsKey("coordinates")) {
									Map<String, Object> type = (Map<String, Object>) propertiesGeometry.get("type");
									Map<String, Object> coordinates = (Map<String, Object>) propertiesGeometry
											.get("coordinates");
									if (type.containsKey("enum") && coordinates.containsKey("type")) {
										log.debug("DEBUG.END", "getParentProperties");
										return jsonElement.getKey();
									}
								}
							} else if (geometry.containsKey("oneOf")) {
								geometry.get("oneOf");
								log.debug("DEBUG.END", "getParentProperties");
								return jsonElement.getKey();
							}
						}
					}
				}
			}
		}
		return "";
	}

	public String prepareEsquema(String esquemajson) {
		String esquemajsonAux = esquemajson;
		if (esquemajsonAux != null && esquemajsonAux.length() > 0) {
			esquemajsonAux = esquemajsonAux.replaceAll("&nbsp;", "");
			esquemajsonAux = esquemajsonAux.replaceAll("&amp;", "");
			esquemajsonAux = esquemajsonAux.replaceAll("&quot;", "\"");
		}
		if (esquemajsonAux != null) {
			esquemajsonAux = esquemajsonAux.replace("\t", "");
			esquemajsonAux = esquemajsonAux.replace("\r", "");
			esquemajsonAux = esquemajsonAux.replace("\n", "");
		}
		return esquemajsonAux;
	}

	public String getOntologyFromNativeQuery(String query) {
		String ontology = "";
		// .find or .count or .distinct or .aggregate
		if (query.toLowerCase().contains(".find")) {
			ontology = query.substring(query.indexOf('.') + 1, query.toLowerCase().indexOf(".find"));
		} else if (query.toLowerCase().contains(".count")) {
			ontology = query.substring(query.indexOf('.') + 1, query.toLowerCase().indexOf(".count"));
		} else if (query.toLowerCase().contains(".aggregate")) {
			ontology = query.substring(query.indexOf('.') + 1, query.toLowerCase().indexOf(".aggregate"));
		} else if (query.toLowerCase().contains(".distinct")) {
			ontology = query.substring(query.indexOf('.') + 1, query.toLowerCase().indexOf(".distinct"));
		} else if (query.toLowerCase().contains(".insert")) {
			ontology = query.substring(query.indexOf('.') + 1, query.toLowerCase().indexOf(".insert"));
		} else if (query.toLowerCase().contains(".update")) {
			ontology = query.substring(query.indexOf('.') + 1, query.toLowerCase().indexOf(".update"));
		} else {
			ontology = query.substring(query.indexOf('.') + 1, query.toLowerCase().indexOf(".remove"));
		}
		return ontology;
	}

	public boolean isNativeQuery(String query) {
		boolean isNative = true;
		if ((query.indexOf('.') == -1 || query.toLowerCase().indexOf(".find") == -1
				|| (query.indexOf('.') > query.toLowerCase().indexOf(".find")))
				&& (query.indexOf('.') == -1 || query.toLowerCase().indexOf(".count") == -1
						|| (query.indexOf('.') > query.toLowerCase().indexOf(".count")))
				&& (query.indexOf('.') == -1 || query.toLowerCase().indexOf(".distinct") == -1
						|| (query.indexOf('.') > query.toLowerCase().indexOf(".distinct")))
				&& (query.indexOf('.') == -1 || query.toLowerCase().indexOf(".aggregate") == -1
						|| (query.indexOf('.') > query.toLowerCase().indexOf(".aggregate")))
				&& (query.indexOf('.') == -1 || query.toLowerCase().indexOf(".insert") == -1
						|| (query.indexOf('.') > query.toLowerCase().indexOf(".insert")))
				&& (query.indexOf('.') == -1 || query.toLowerCase().indexOf(".update") == -1
						|| (query.indexOf('.') > query.toLowerCase().indexOf(".update")))
				&& (query.indexOf('.') == -1 || query.toLowerCase().indexOf(".remove") == -1
						|| (query.indexOf('.') > query.toLowerCase().indexOf(".remove")))) {
			isNative = false;
		}
		return isNative;
	}

	public String processInsert(String insert) {
		String pInsert = "";
		String insertAux = insert;
		try {
			String ontology = getCollectionName(insertAux, "insert");
			if (insertAux.contains("db." + ontology)) {
				insertAux = insertAux.replace("db." + ontology, "");
			}

			insertAux = insertAux.trim();
			if (insertAux.contains("insert")) {
				insertAux = insertAux.substring(insertAux.indexOf("insert") + 6).trim();
				if (insertAux.charAt(0) == '(') {
					insertAux = insertAux.substring(1).trim();
					if (insertAux.charAt(0) == '{') {
						pInsert = insertAux.substring(0, insertAux.lastIndexOf('}') + 1);
					}
				}
			}

		} catch (Exception e) {
			log.error("ERROR", e);
			throw new DBPersistenceException(e.getMessage());
		}
		return pInsert;
	}

	public ContextData buildMinimalContextData() {
		ContextData contextData = new ContextData();
		contextData.setTimezoneId(CalendarAdapter.getServerTimezoneId());
		return contextData;
	}

	public <T> Collection<T> toJavaCollection(MongoIterable<T> iterable) {
		return toJavaList(iterable);
	}

	public <T> List<T> toJavaList(MongoIterable<T> iterable) {
		List<T> result = new ArrayList<T>();
		iterable.into(result);
		return result;
	}

	public <T> Map<String, T> toJavaMap(Document document, Class<T> valueType) {
		Map<String, T> result = new HashMap<String, T>();
		for (String key : document.keySet()) {
			result.put(key, document.get(key, valueType));
		}
		return result;
	}

	public String getObjectIdString(ObjectId id) {
		String strId;

		strId = "{\"_id\":{ \"$oid\":\"" + id.toString() + "\"}}";
		// if(ThreadLocalProperties.jsonMode.get() == true) {
		// strId = "{\"_id\":{ \"$oid\":\"" +id.toString() + "\"}}";
		// }
		// else {
		// strId = "{\"_id\":ObjectId(\"" + id.toString() + "\")}";
		// }

		return strId;
	}

}
