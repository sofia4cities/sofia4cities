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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class JSONPersistenceUtilsMongo {
	
	public static List<String> getGeoIndexes(String schemaString) throws Exception {
		JSONObject jsonObj = new JSONObject(schemaString);
		List<String> output = new ArrayList<String>();
		
		
		JSONObject properties = jsonObj.getJSONObject("properties");
		Iterator it = properties.keys();
		while (it.hasNext())
		{
			String key = (String)it.next();
			JSONObject o = (JSONObject)properties.get(key);
			Object type= o.get("type");
			if (type instanceof String) {
				if ("object".equalsIgnoreCase((String)type)) {
					try {
						JSONObject coordinates = o.getJSONObject("properties").getJSONObject("coordinates");
						JSONObject theType = o.getJSONObject("properties").getJSONObject("type");
						if (theType!=null && coordinates!=null)
							output.add(key);
					} catch (Exception e) {}
				}
			}

		}
		return output;
	}
}
