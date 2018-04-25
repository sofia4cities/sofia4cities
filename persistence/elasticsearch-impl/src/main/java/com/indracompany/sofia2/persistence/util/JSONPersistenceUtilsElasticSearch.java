/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.persistence.util;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONPersistenceUtilsElasticSearch {
	
	public static boolean isJSONSchema(String schemaString) {
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(schemaString);
			Object o = jsonObj.get("$schema");
			return (o!=null);
		} catch (JSONException e) {
			return false;
		}
		
		
	}
	
	public static String getElasticSearchSchemaFromJSONSchema(String ontology,String schemaString) throws JSONException {
		JSONObject jsonObj = new JSONObject(schemaString);
		JSONObject props = new JSONObject();
		JSONObject elasticSearch = new JSONObject();
		JSONObject theObject = new JSONObject();
		
		JSONObject properties = jsonObj.getJSONObject("properties");
		Iterator it = properties.keys();
		while (it.hasNext())
		{
			String key = (String)it.next();
			JSONObject o = (JSONObject)properties.get(key);
			Object type= o.get("type");
			if (type instanceof String) {
				if ("string".equalsIgnoreCase((String)type)) type = "text";
				else if ("number".equalsIgnoreCase((String)type)) type = "float";
				else if ("object".equalsIgnoreCase((String)type)) {
					try {
						JSONArray enume = o.getJSONObject("properties").getJSONObject("type").getJSONArray("enum");
						String point = enume.getString(0);
						if ("Point".equalsIgnoreCase(point)) {
							type = "geo_point";
						}
						else type = "geo_shape";
						
					} catch (Exception e) {}
					
					try {
						JSONObject enume = o.getJSONObject("properties").getJSONObject("$date");
						if (enume!=null)
						{
							type="date";
						}
						
					} catch (Exception e) {}
				}
				o = new JSONObject();
				o.put("type", type);
			}
			props.put(key, o);
		}
		
		elasticSearch.put("properties", props);
		theObject.put(ontology, elasticSearch);
		return theObject.toString();
	}
	
	
	
}
