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
package com.indracompany.sofia2.persistence.hadoop.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultResultSetExtractor implements ResultSetExtractor<String> {

	@Override
	public String extractData(ResultSet rs) throws SQLException, DataAccessException {
		JSONArray jsonArray = new JSONArray();
		while (rs.next()) {
			int total_rows = rs.getMetaData().getColumnCount();
			JSONObject obj = new JSONObject();
			for (int i = 0; i < total_rows; i++) {
				try {
					obj.put(rs.getMetaData().getColumnLabel(i + 1), rs.getObject(i + 1));
				} catch (JSONException e) {
					log.error("error parsing json ", e);
				}
			}
			jsonArray.put(obj);
		}
		return jsonArray.toString();
	}

	/**
	 * Convert a result set into a XML List
	 * 
	 * @param resultSet
	 * @return a XML String with list elements
	 * @throws Exception
	 *             if something happens
	 */
	/*
	 * public static String convertToXML(ResultSet resultSet) throws Exception {
	 * StringBuffer xmlArray = new StringBuffer("<results>"); while
	 * (resultSet.next()) { int total_rows =
	 * resultSet.getMetaData().getColumnCount(); xmlArray.append("<result "); for
	 * (int i = 0; i < total_rows; i++) { xmlArray.append(" " +
	 * resultSet.getMetaData().getColumnLabel(i + 1) .toLowerCase() + "='" +
	 * resultSet.getObject(i + 1) + "'"); } xmlArray.append(" />"); }
	 * xmlArray.append("</results>"); return xmlArray.toString(); }
	 */

}
