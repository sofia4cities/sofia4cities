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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESBaseApi {

	private JestClient httpClient;

	@Value("${sofia2.database.elasticsearch.cluster.name:sofia2_s4c}")
	private String clusterName;
	
	@Value("${sofia2.database.elasticsearch.sql.connector.http.endpoint:http://localhost:9200}")
	private String httpEndpoint;
	
	public static String queryAllSize = "{\r\n" + 
			"  \"size\": [SIZE]\r\n" + 
			"  ,\"from\": 0\r\n" + 
			"  ,\"query\":\r\n" + 
			"   {\r\n" + 
			"    \"match_all\": {}\r\n" + 
			"   }\r\n" + 
			"}";
	
	public static String queryAllSizeFromTo = "{\r\n" + 
			"  \"size\": [SIZE]\r\n" + 
			"  ,\"from\": [FROM]\r\n" + 
			"  ,\"query\":\r\n" + 
			"   {\r\n" + 
			"    \"match_all\": {}\r\n" + 
			"   }\r\n" + 
			"}";
	
	public static String queryAllSizeFromToQuery = "{\r\n" + 
			"  \"size\": [SIZE]\r\n" + 
			"  ,\"from\": [FROM]\r\n" + 
			"  ,[QUERY] }";

	public static final String queryAll = "{\r\n" + 
			"\"query\" : {\r\n" + 
			"    \"match_all\" : {}\r\n" + 
			"  }\r\n" + 
			"}";
	
	@PostConstruct
	void initializeIt() {
		
		try {
			 JestClientFactory factory = new JestClientFactory();
			 factory.setHttpClientConfig(new HttpClientConfig
			                        .Builder(httpEndpoint)
			                        .multiThreaded(true)
			                        .build());
			 httpClient = factory.getObject();
		} catch (Exception e) {
			log.error(String.format("Cannot Instantiate ElasticSearch Rest Client due to : %s ", e.getMessage()));
		}
		
	}

	public String createIndex(String index) {
		JestResult i=null;
		try {
			i= getHttpClient().execute(new CreateIndex.Builder(index).build());
		} catch (IOException e) {
			log.error("Error Creating Index "+e.getMessage());
			return null;
		}
		return i.getJsonString();
	}
	
	public String updateIndex(String index, String type, String id, String jsonData) {
		 try {
			DocumentResult result = getHttpClient().execute(new Update.Builder(jsonData).index(index).type(type).id(id).build());
			return result.getJsonString();
		} catch (IOException e) {
			log.error("UpdateIndex", e);
			return null;
		}
	}
	
	public boolean createType(String index, String type, String dataMapping) {
		// createIndex(index);
		 try {
			String result = prepareIndex(index, type, dataMapping);
			log.info("Create Type result :"+result);
		} catch (IOException e) {
			log.error("Error Creating Type "+e.getMessage());
			return false;
		}
		 return true;
		 
	}
	public boolean deleteIndex(String index) {

		  DeleteIndex indicesExists = new DeleteIndex.Builder(index).build();
		  try {
			JestResult result = getHttpClient().execute(indicesExists);
			log.info("Delete index result :"+result.isSucceeded());
			return true;
		} catch (IOException e) {
			log.error("Error Deleting Type "+e.getMessage());
			return false;
		}
	}
	
	private String prepareIndex(String index, String type, String dataMapping) throws IOException {
		
		 PutMapping putMapping = new PutMapping.Builder(
   			 index,
   			 type,
   			 dataMapping
   	    ).build();
   	 JestResult result= getHttpClient().execute(putMapping);
   	 return result.getJsonString();
   }
	
	public JestClient getHttpClient() {
		return httpClient;
	}

}
