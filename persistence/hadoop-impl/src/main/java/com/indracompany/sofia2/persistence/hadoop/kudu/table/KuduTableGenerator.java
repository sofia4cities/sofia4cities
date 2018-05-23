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
package com.indracompany.sofia2.persistence.hadoop.kudu.table;

import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_CLIENT_SESSION;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_DEVICE;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_DEVICE_TEMPLATE;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_DEVICE_TEMPLATE_CONNECTION;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_TIMESTAMP;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_TIMESTAMP_MILLIS;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_TIMEZONE_ID;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_USER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.persistence.hadoop.common.GeometryType;
import com.indracompany.sofia2.persistence.hadoop.hive.table.HiveColumn;
import com.indracompany.sofia2.persistence.hadoop.util.HiveFieldType;
import com.indracompany.sofia2.persistence.hadoop.util.JsonFieldType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KuduTableGenerator {

	public KuduTable builTable(String ontologyName, String schema) {

		log.debug("generate kudu table for ontology " + ontologyName);

		JSONObject props = getProperties(schema);
		return build(ontologyName, props);
	}

	public JSONObject getProperties(String schema) {

		JSONObject jsonObj = new JSONObject(schema);

		JSONObject properties = jsonObj.getJSONObject(JsonFieldType.PROPERTIES_FIELD);

		@SuppressWarnings("unchecked")
		Iterator<String> it = properties.keys();

		while (it.hasNext()) {
			String key = it.next();
			JSONObject o = (JSONObject) properties.get(key);

			if (o.has("$ref")) {
				Object ref = o.get("$ref");
				String refScript = ((String) ref).replace("#/", "");
				JSONObject refMap = jsonObj.getJSONObject(refScript);
				return refMap.getJSONObject(JsonFieldType.PROPERTIES_FIELD);
			}
		}

		return properties;
	}

	public KuduTable build(String name, JSONObject props) {
		KuduTable table = new KuduTable();
		table.setName(name);

		@SuppressWarnings("unchecked")
		Iterator<String> it = props.keys();

		table.getColumns().add(getPrimaryId());
		table.getColumns().addAll(getContexDataFields());

		List<String> requiredProperties = new ArrayList<>();

		if (props.has("required")) {
			JSONArray array = props.getJSONArray("required");

			for (int i = 0; i < array.length(); i++) {
				requiredProperties.add(array.getString(i));
			}
		}

		while (it.hasNext()) {
			String key = it.next();
			JSONObject o = (JSONObject) props.get(key);

			if (isPrimitive(o)) {
				table.getColumns()
						.add(new HiveColumn(key, pickPrimitiveType(key, o), requiredProperties.contains(key)));
			} else {
				table.getColumns().addAll(pickType(key, o, requiredProperties));
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

				result = GeometryType.Point.name().equalsIgnoreCase(point);

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

	public List<HiveColumn> pickType(String key, JSONObject o, List<String> requiredProperties) {

		List<HiveColumn> columns = new ArrayList<>();

		if (isGeometry(o)) {

			columns.add(new HiveColumn(key + HiveFieldType.LATITUDE_FIELD, "double", true));
			columns.add(new HiveColumn(key + HiveFieldType.LONGITUDE_FIELD, "double", true));

		} else if (isTimestamp(o)) {
			columns.add(new HiveColumn(key, HiveFieldType.TIMESTAMP_FIELD, requiredProperties.contains(key)));
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

	public HiveColumn getPrimaryId() {
		return new HiveColumn(JsonFieldType.PRIMARY_ID_FIELD, HiveFieldType.STRING_FIELD, true);
	}

	public List<HiveColumn> getContexDataFields() {

		List<HiveColumn> columns = new ArrayList<>();

		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_DEVICE_TEMPLATE, HiveFieldType.STRING_FIELD, false));
		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_DEVICE, HiveFieldType.STRING_FIELD, false));
		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_DEVICE_TEMPLATE_CONNECTION, HiveFieldType.STRING_FIELD, false));

		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_CLIENT_SESSION, HiveFieldType.STRING_FIELD, false));
		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_USER, HiveFieldType.STRING_FIELD, false));
		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_TIMEZONE_ID, HiveFieldType.STRING_FIELD, false));
		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_TIMESTAMP, HiveFieldType.STRING_FIELD, false));

		columns.add(new HiveColumn(CONTEXT_DATA_FIELD_TIMESTAMP_MILLIS, HiveFieldType.BIGINT_FIELD, false));

		return columns;
	}
}
