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

	public String load(String index, String type, String jsonDoc) throws Exception {

		Bulk bulk = new Bulk.Builder().addAction(new Index.Builder(jsonDoc).index(index).type(type).build()).build();

		BulkResult result = connector.getHttpClient().execute(bulk);

		log.info("Document has been inserted..." + result.getJsonString());

		log.info("Document has been inserted..." + result.getJsonString());

		JsonObject object = result.getJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject();
		log.info("Object "+object);
		
		JsonObject the= object.get("index").getAsJsonObject();
		return the.get("_id").getAsString();
		

	}
	
	public List<BulkWriteResult> load(String index, String type, List<String> jsonDoc) throws Exception {

		
		List<BulkWriteResult> listResult = new ArrayList<BulkWriteResult>();
		
		List<Index> list = new ArrayList<Index>();
		for (String string : jsonDoc) {
			Index i = new Index.Builder(string).index(index).type(type).build();
			list.add(i);
		}
			
		Bulk bulk = new Bulk.Builder().addAction(list).build();
		BulkResult result = connector.getHttpClient().execute(bulk);
		
		JsonArray object = result.getJsonObject().get("items").getAsJsonArray();
	
		for (int i=0; i < object.size(); i++) {
			JsonElement element = object.get(i);
			JsonObject o = element.getAsJsonObject();
			JsonObject the= o.get("index").getAsJsonObject();
			String id =  the.get("_id").getAsString();
			String created =  the.get("result").getAsString();
			
			BulkWriteResult bulkr = new BulkWriteResult();
			bulkr.setId(id);
			bulkr.setErrorMessage(created);
			bulkr.setOk(true);
			listResult.add(bulkr);

		}

		log.info("Documents has been inserted..." + listResult.size());

		return listResult;
		

	}
	
	public static List<String> readLines(File file) throws Exception {
	      if (!file.exists()) {
	          return new ArrayList<String>();
	      }
	      BufferedReader reader = new BufferedReader(new FileReader(file));
	      List<String> results = new ArrayList<String>();
	      String line = reader.readLine();
	      while (line != null) {
	          results.add(line);
	          line = reader.readLine();
	      }
	      return results;
	  }

}