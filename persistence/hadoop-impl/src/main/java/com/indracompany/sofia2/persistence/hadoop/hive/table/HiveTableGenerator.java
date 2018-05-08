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
package com.indracompany.sofia2.persistence.hadoop.hive.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.persistence.hadoop.util.HiveFieldType;
import com.indracompany.sofia2.persistence.hadoop.util.JsonFieldType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HiveTableGenerator {

	// https://github.com/hortonworks/hive-json/tree/master/src/main/java/org/apache/hadoop/hive/json

	public HiveTable buildHiveTable(String ontologyName, String schema, String hdfsWorkingDirectory) {

		log.debug("generate hive table for ontology " + ontologyName);

		JSONObject props = getProperties(schema);
		return analize(ontologyName, props, hdfsWorkingDirectory);
	}

	public JSONObject getProperties(String schema) {

		JSONObject jsonObj = new JSONObject(schema);

		JSONObject properties = jsonObj.getJSONObject(JsonFieldType.PROPERTIES_FIELD);

		@SuppressWarnings("unchecked")
		Iterator<String> it = properties.keys();

		while (it.hasNext()) {
			String key = it.next();
			JSONObject o = (JSONObject) properties.get(key);

			Object ref = o.get("$ref");

			if (ref != null) {
				String refScript = ((String) ref).replace("#/", "");
				JSONObject refMap = jsonObj.getJSONObject(refScript);
				return refMap.getJSONObject(JsonFieldType.PROPERTIES_FIELD);
			}
		}

		return properties;
	}

	public HiveTable analize(String name, JSONObject props, String hdfsWorkingDirectory) {
		HiveTable table = new HiveTable();
		table.setName(name);
		table.setHdfsDir(hdfsWorkingDirectory);

		@SuppressWarnings("unchecked")
		Iterator<String> it = props.keys();

		while (it.hasNext()) {
			String key = it.next();
			JSONObject o = (JSONObject) props.get(key);

			if (isPrimitive(o)) {
				HiveColumn column = new HiveColumn();
				column.setName(key);
				column.setColumnType(pickPrimitiveType(key, o));
				table.getColumns().add(column);
			} else {
				table.getColumns().addAll(pickType(key, o));
			}
		}

		return table;
	}

	public boolean isPrimitive(JSONObject o) {
		String jsonType = (String) o.get(JsonFieldType.TYPE_FIELD);
		return JsonFieldType.PRIMITIVE_TYPES.contains(jsonType);
	}

	public boolean isGeometry(JSONObject o) {

		boolean result = false;

		try {
			String jsonType = (String) o.get(JsonFieldType.TYPE_FIELD);

			if ((JsonFieldType.OBJECT_FIELD).equalsIgnoreCase(jsonType)) {

				JSONArray enume = o.getJSONObject(JsonFieldType.PROPERTIES_FIELD)
						.getJSONObject(JsonFieldType.TYPE_FIELD).getJSONArray("enum");
				String point = enume.getString(0);
				if ("Point".equalsIgnoreCase(point)) {

					result = true;
				}
			}
		} catch (Exception e) {
			log.error("error checking if a object is a geometry");
		}
		return result;
	}

	public boolean isTimestamp(JSONObject o) {

		boolean result = false;

		try {
			String jsonType = (String) o.get(JsonFieldType.TYPE_FIELD);

			if ((JsonFieldType.OBJECT_FIELD).equalsIgnoreCase(jsonType)
					&& o.get(JsonFieldType.PROPERTIES_FIELD) != null) {
				JSONObject other = (JSONObject) o.get(JsonFieldType.PROPERTIES_FIELD);
				if (other.get("$date") != null) {
					result = true;
				}
			}
		} catch (Exception e) {
			log.error("error checking if a object is a timestamp");
		}
		return result;
	}

	public List<HiveColumn> pickType(String key, JSONObject o) {

		List<HiveColumn> columns = new ArrayList<>();

		if (isGeometry(o)) {

			HiveColumn latitude = new HiveColumn();
			latitude.setName(HiveFieldType.LATITUDE_FIELD);
			latitude.setColumnType("float");
			columns.add(latitude);

			HiveColumn longitude = new HiveColumn();
			longitude.setName(HiveFieldType.LONGITUDE_FIELD);
			longitude.setColumnType("float");
			columns.add(longitude);

		} else if (isTimestamp(o)) {

			HiveColumn timestamp = new HiveColumn();
			timestamp.setName(key);
			timestamp.setColumnType(HiveFieldType.TIMESTAMP_FIELD);
			columns.add(timestamp);

		}

		return columns;
	}

	public String pickPrimitiveType(String key, JSONObject o) {
		String result = "";

		String jsonType = (String) o.get(JsonFieldType.TYPE_FIELD);

		if ((JsonFieldType.STRING_FIELD).equalsIgnoreCase(jsonType)) {
			result = HiveFieldType.STRING_FIELD;
		} else if ((JsonFieldType.NUMBER_FIELD).equalsIgnoreCase(jsonType)) {
			result = HiveFieldType.FLOAT_FIELD;
		} else if ((JsonFieldType.INTEGER_FIELD).equalsIgnoreCase(jsonType)) {
			result = HiveFieldType.INTEGER_FIELD;
		} else if ((JsonFieldType.BOOLEAN_FIELD).equalsIgnoreCase(jsonType)) {
			result = HiveFieldType.BOOLEAN_FIELD;
		}

		return result;
	}

}
