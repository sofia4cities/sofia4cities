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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchUtil;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESDataService {

	@Autowired
	ESBaseApi connector;

	public List<String> findQueryData(String jsonQueryString, String... indexes) {

		List<String> list = new ArrayList<String>(Arrays.asList(indexes));

		Search search = new Search.Builder(jsonQueryString).addIndex(list).build();

		SearchResult result;
		try {
			result = connector.getHttpClient().execute(search);
			return result.getSourceAsStringList();
		} catch (IOException e) {
			log.error("Error in findQueryByJSON " + e);
			return null;
		}
	}

	public String findQueryDataAsJson(String jsonQueryString, String... indexes) {

		List<String> list = new ArrayList<String>(Arrays.asList(indexes));
		Search search = new Search.Builder(jsonQueryString).addIndex(list).build();
		SearchResult result;
		try {
			result = connector.getHttpClient().execute(search);
			return ElasticSearchUtil.parseElastiSearchResult(result.getJsonString());
		} catch (IOException e) {
			log.error("Error in findQueryByJSON " + e);
			return null;
		} catch (JSONException e) {
			log.error("Error in findQueryByJSON PArsing result " + e);
			return null;
		}

	}

	public String findByIndex(String index, String type, String documentId) {
		try {
			DocumentResult result = connector.getHttpClient()
					.execute(new Get.Builder(index, documentId).type(type).build());

			return result.getSourceAsString();
		} catch (Exception e) {
			log.error("findDocumentByIndex", e);
		}
		return null;
	}

	public List<String> findAllByType(String ontology) {
		return findQueryData(ESBaseApi.queryAll, ontology);
	}

	public List<String> findAllByType(String ontology, int from, int limit) {
		String query = ESBaseApi.queryAllSizeFromTo;
		query = query.replace("[SIZE]", "" + limit);
		query = query.replace("[FROM]", "" + from);

		return findQueryData(query, ontology);
	}
	
	public List<String> findAllByType(String ontology, String query, int from, int limit) {
		query = "{\r\n" + 
				"  \"size\": [SIZE]\r\n" + 
				"  \"from\": [FROM]\r\n" +
				query+
				" }";
		
		query = query.replace("[SIZE]", "" + limit);
		query = query.replace("[FROM]", "" + from);

		return findQueryData(query, ontology);
	}
	
	public List<String> findAllByType(String ontology, int limit) {
		String query = ESBaseApi.queryAllSize;
		query = query.replace("[SIZE]", "" + limit);

		return findQueryData(query, ontology);
	}

	public String findAllByTypeAsJson(String ontology, int limit) {
		String query = ESBaseApi.queryAllSize;
		query = query.replace("[SIZE]", "" + limit);

		return findQueryDataAsJson(query, ontology);
	}
	
	public String findAllByTypeAsJson(String ontology, int from, int limit) {
		String query = ESBaseApi.queryAllSizeFromTo;
		query = query.replace("[SIZE]", "" + limit);
		query = query.replace("[FROM]", "" + from);

		return findQueryDataAsJson(query, ontology);
	}

}