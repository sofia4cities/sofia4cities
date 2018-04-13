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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.conversions.Bson;

import com.mongodb.util.JSON;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoQueryAndParams {

	@Getter
	@Setter
	private String originalQuery;
	@Getter
	@Setter
	private Bson finalQuery = null;
	@Getter
	@Setter
	private int limit = -1;

	@Getter
	@Setter
	private Bson sort = null;

	@Getter
	@Setter
	private int skip = -1;

	@Getter
	@Setter
	private Bson projection = null;

	public MongoQueryAndParams() {
	}

	public void parseQuery(String originalQuery, int limit, int skip) throws Exception {
		this.originalQuery = originalQuery;
		this.limit = limit;
		this.skip = skip;
		//
		String query = originalQuery;
		String subquery = null;
		StringBuffer sb;
		String temp = null;
		try {
			sb = new StringBuffer();
			Pattern pattern = Pattern
					.compile("\\{\\\\*\"_id\\\\*\"\\s*:\\s*\\{\\s*\"\\$oid\"\\s*:\\s*\\\\*\"(.*)\\\\*\"\\s*}\\s*}");
			Matcher matcher = pattern.matcher(query);
			boolean changed = false;
			while (matcher.find()) {
				changed = true;
				matcher.group(0);
				temp = matcher.group(1);
				matcher.appendReplacement(sb, "{\"_id\":ObjectId(\"" + temp + "\")}");
			}
			matcher.appendTail(sb);
			if (changed)
				query = sb.toString();

			if (query.indexOf(".limit(") != -1) {
				subquery = query.substring(query.indexOf(".limit("), query.length());
				temp = subquery.substring(0 + 7, subquery.indexOf(")"));
				this.limit = Integer.parseInt(temp);
			}
			if (query.indexOf(".sort(") != -1) {
				subquery = query.substring(query.indexOf(".sort("), query.length());
				temp = subquery.substring(0 + 6, subquery.indexOf(")"));
				this.sort = (Bson) JSON.parse(temp);
			}
			if (query.indexOf(".skip(") != -1) {
				subquery = query.substring(query.indexOf(".skip("), query.length());
				temp = subquery.substring(0 + 6, subquery.indexOf(")"));
				this.skip = Integer.parseInt(temp);
			}
			//
			if (query.indexOf(".find(") != -1) {
				subquery = query.substring(query.indexOf(".find("), query.length());
				temp = subquery.substring(0 + 6, subquery.indexOf(")"));
				if (temp.trim().equals(""))
					temp = "{}";
			} else {
				if (!query.startsWith("{")) {
					sb = new StringBuffer(query);
					sb.insert(0, "{");
					sb.append("}");
					query = sb.toString();
				}
				temp = query;
			}
			this.finalQuery = (Bson) JSON.parse(temp);
			if (temp.indexOf("},") != -1) {
				String temp2 = temp.substring(0, temp.indexOf("},") + 1);
				this.finalQuery = (Bson) JSON.parse(temp2);
				temp = temp.substring(temp.indexOf("},") + 2, temp.length());
				this.projection = (Bson) JSON.parse(temp);
			}

		} catch (Exception e) {
			log.error("Error parseQuery:" + e.getMessage(), e);
			throw e;
		}
	}

}
