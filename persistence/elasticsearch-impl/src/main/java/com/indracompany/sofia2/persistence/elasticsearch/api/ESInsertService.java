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
package com.indracompany.sofia2.persistence.elasticsearch.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESInsertService {

	@Autowired
	ESBaseApi connector;

	private void fixPosibleNonCapitalizedGeometryPoint(String s) {
		try {
			JsonObject o = new JsonParser().parse(s).getAsJsonObject();
			JsonObject geometry = (JsonObject) o.get("geometry");
			JsonElement type = geometry.get("type");
			String value = type.getAsString();
			geometry.addProperty("type", value.toLowerCase());

		} catch (Exception e) {
		}

	}

	public List<BulkWriteResult> load(String index, String type, List<String> jsonDoc) {

		List<BulkWriteResult> listResult = new ArrayList<BulkWriteResult>();

		List<Index> list = new ArrayList<Index>();
		for (String s : jsonDoc) {

			s = s.replaceAll("\\n", "");
			s = s.replaceAll("\\r", "");

			fixPosibleNonCapitalizedGeometryPoint(s);

			Index i = new Index.Builder(s).index(index).type(type).build();
			list.add(i);
		}

		Bulk bulk = new Bulk.Builder().addAction(list).build();
		BulkResult result;
		try {
			result = connector.getHttpClient().execute(bulk);
			JsonArray object = result.getJsonObject().get("items").getAsJsonArray();

			for (int i = 0; i < object.size(); i++) {
				JsonElement element = object.get(i);
				JsonObject o = element.getAsJsonObject();
				JsonObject the = o.get("index").getAsJsonObject();
				String id = the.get("_id").getAsString();
				String created = the.get("result").getAsString();

				BulkWriteResult bulkr = new BulkWriteResult();
				bulkr.setId(id);
				bulkr.setErrorMessage(created);
				bulkr.setOk(true);
				listResult.add(bulkr);

			}
		} catch (Exception e) {
			log.error("Error Loading document " + e.getMessage());
		}

		log.info("Documents has been inserted..." + listResult.size());

		return listResult;

	}

	public static List<String> readLines(File file) throws Exception {
		if (!file.exists()) {
			return new ArrayList<String>();
		}

		BufferedReader reader = null;
		List<String> results = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(file));

			String line = reader.readLine();
			while (line != null) {
				results.add(line);
				line = reader.readLine();
			}
			return results;
		} catch (Exception e) {
			return new ArrayList<String>();
		} finally {
			if (reader != null)
				reader.close();
		}

	}

}