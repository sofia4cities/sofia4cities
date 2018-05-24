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
package com.indracompany.sofia2.persistence.hadoop.util;

import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_CLIENT_SESSION;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_DEVICE;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_DEVICE_TEMPLATE;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_DEVICE_TEMPLATE_CONNECTION;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_TIMESTAMP;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_TIMESTAMP_MILLIS;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_TIMEZONE_ID;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.CONTEXT_DATA_FIELD_USER;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_CLIENT_SESSION;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_DEVICE;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_DEVICE_TEMPLATE;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_DEVICE_TEMPLATE_CONNECTION;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_TIMESTAMP;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_TIMESTAMP_MILLIS;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_TIMEZONE_ID;
import static com.indracompany.sofia2.persistence.hadoop.common.ContextDataNameFields.FIELD_USER;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.common.geometry.GeometryType;
import com.indracompany.sofia2.persistence.hadoop.kudu.table.KuduTable;
import com.indracompany.sofia2.persistence.hadoop.kudu.table.KuduTableGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JsonRelationalHelperKuduImpl {

	@Autowired
	private KuduTableGenerator kuduTableGenerator;

	public String getInsertStatement(String ontology, String schema, String instance, String id) {

		StringBuilder sqlInsert = new StringBuilder();
		StringBuilder sqlValues = new StringBuilder();

		sqlInsert.append("INSERT INTO " + ontology + " ( " + JsonFieldType.PRIMARY_ID_FIELD);
		sqlValues.append(" VALUES ('" + id + "' ");

		KuduTable table = kuduTableGenerator.builTable(ontology, schema);

		Map<String, String> columnTypes = table.getColumns().stream()
				.collect(Collectors.toMap(x -> x.getName(), x -> x.getColumnType()));

		JSONObject obj = new JSONObject(instance);

		@SuppressWarnings("unchecked")
		Iterator<String> it = obj.keys();

		while (it.hasNext()) {

			String key = it.next();

			sqlInsert.append(", ");
			sqlValues.append(", ");

			if (obj.get(key) instanceof JSONObject) {

				JSONObject o = obj.getJSONObject(key);

				if (isGeometry(obj.getJSONObject(key))) {
					JSONArray coordinates = o.getJSONArray("coordinates");
					sqlInsert.append(key + HiveFieldType.LATITUDE_FIELD).append(", ")
							.append(key + HiveFieldType.LONGITUDE_FIELD);
					sqlValues.append(coordinates.getDouble(0)).append(",").append(coordinates.getDouble(1));
				} else if (isTimestamp(obj.getJSONObject(key))) {
					sqlInsert.append(key);
					sqlValues.append("'").append(o.get("$date")).append("'");
				} else if (isContextData(key)) {

					sqlInsert.append(CONTEXT_DATA_FIELD_DEVICE_TEMPLATE).append(", ");
					sqlValues.append("'").append(o.get(FIELD_DEVICE_TEMPLATE)).append("', ");

					sqlInsert.append(CONTEXT_DATA_FIELD_DEVICE).append(", ");
					sqlValues.append("'").append(o.get(FIELD_DEVICE)).append("', ");

					sqlInsert.append(CONTEXT_DATA_FIELD_DEVICE_TEMPLATE_CONNECTION).append(", ");
					sqlValues.append("'").append(o.get(FIELD_DEVICE_TEMPLATE_CONNECTION)).append("', ");

					sqlInsert.append(CONTEXT_DATA_FIELD_CLIENT_SESSION).append(", ");
					sqlValues.append("'").append(o.get(FIELD_CLIENT_SESSION)).append("', ");

					sqlInsert.append(CONTEXT_DATA_FIELD_USER).append(", ");
					sqlValues.append("'").append(o.get(FIELD_USER)).append("', ");

					sqlInsert.append(CONTEXT_DATA_FIELD_TIMEZONE_ID).append(", ");
					sqlValues.append("'").append(o.get(FIELD_TIMEZONE_ID)).append("', ");

					sqlInsert.append(CONTEXT_DATA_FIELD_TIMESTAMP).append(", ");
					sqlValues.append("'").append(o.get(FIELD_TIMESTAMP)).append("', ");

					sqlInsert.append(CONTEXT_DATA_FIELD_TIMESTAMP_MILLIS);
					sqlValues.append(o.get(FIELD_TIMESTAMP_MILLIS));

				}

			} else {
				String columnType = columnTypes.get(key);
				sqlInsert.append(key);

				if (HiveFieldType.STRING_FIELD.equals(columnType)) {
					sqlValues.append("'").append(obj.get(key)).append("'");
				} else {
					sqlValues.append(obj.get(key));
				}
			}

		}

		return sqlInsert.append(")").append(sqlValues).append(")").toString();
	}

	public boolean isGeometry(JSONObject o) {
		/*
		 * "geometry": { "coordinates": [ 1, 2 ], "type": "Point" }
		 */
		boolean result = false;

		try {

			String jsonType = (String) o.get(JsonFieldType.TYPE_FIELD);
			result = (GeometryType.Point.name()).equalsIgnoreCase(jsonType);

		} catch (Exception e) {
			log.error("error checking if a object is a geometry");
		}

		return result;
	}

	public boolean isTimestamp(JSONObject o) {
		/*
		 * "measurestimestamp": { "$date": "2018-05-22T12:19:42.296Z" }
		 */
		boolean result = o.has("$date");
		return result;
	}

	public boolean isContextData(String key) {
		return key.equalsIgnoreCase(JsonFieldType.CONTEXT_DATA_FIELD);
	}

	public Map<String, Object> transfromJSON(String json) throws DBPersistenceException {

		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String nombreClave = "";

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> obj = new ObjectMapper().readValue(json, Map.class);

			Iterator<Entry<String, Object>> it = obj.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<String, Object> e = it.next();
				nombreClave = e.getKey().toString();
				map.put(nombreClave, e.getValue());
			}
		} catch (Exception e) {
			throw new DBPersistenceException(e);
		}

		return map;
	}
}
