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

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.persistence.elasticsearch.api.ElasticSearchApi;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ElasticSearchBasicApiTest {
	
	 public final static String TEST_INDEX = "elasticsearch-test_index";
	 public final static String TEST_INDEX_GAME_OF_THRONES = TEST_INDEX + "_game_of_thrones";
	 public final static String TEST_INDEX_ONLINE = TEST_INDEX + "_online";

	@Autowired
	ElasticSearchApi connector;
	
	private String createTestIndex(String index) {
		String res =   connector.createIndex(index);
		System.out.println("createTestIndex :"+res);
		return res;
	}
	
	@After
	public  void tearDown() {
		System.out.println("teardown process...");
        deleteTestIndex(TEST_INDEX_GAME_OF_THRONES);
        deleteTestIndex(TEST_INDEX_ONLINE);
        connector.getClient().close();
	}
	
	 private PutMappingResponse  prepareGameOfThronesIndex() {
	        String dataMapping = "{  \"gotCharacters\": { " +
	                " \"properties\": {\n" +
	                " \"nickname\": {\n" +
	                "\"type\":\"text\", "+
	                "\"fielddata\":true"+
	                "},\n"+
	                " \"name\": {\n" +
	                "\"properties\": {\n" +
	                "\"firstname\": {\n" +
	                "\"type\": \"text\",\n" +
	                "  \"fielddata\": true\n" +
	                "},\n" +
	                "\"lastname\": {\n" +
	                "\"type\": \"text\",\n" +
	                "  \"fielddata\": true\n" +
	                "},\n" +
	                "\"ofHerName\": {\n" +
	                "\"type\": \"integer\"\n" +
	                "},\n" +
	                "\"ofHisName\": {\n" +
	                "\"type\": \"integer\"\n" +
	                "}\n" +
	                "}\n" +
	                "}"+
	                "} } }";
	        
	        PutMappingResponse response =  connector.prepareIndex(TEST_INDEX_GAME_OF_THRONES, "gotCharacters", dataMapping);
	        System.out.println("prepareGameOfThronesIndex :"+response.isAcknowledged());
	        return response;
	       
	    }

	private void deleteTestIndex(String index) {
		boolean res =  connector.deleteIndex(index);
		System.out.println("deleteTestIndex :"+res);
	}

	@Test
	public void testCreateTable() {
		try {
			
			
			NodesInfoResponse nodeInfos = connector.getClient().admin().cluster().prepareNodesInfo().get();
			String clusterName = nodeInfos.getClusterName().value();
			System.out.println(String.format("Found cluster... cluster name: %s", clusterName));
			
			BulkResponse response2 = connector.loadBulkFromFile("src/test/resources/online.json", TEST_INDEX_ONLINE);
			System.out.println("Loaded Bulk :"+ response2.getItems().length);
		
			createTestIndex(TEST_INDEX_GAME_OF_THRONES);
			prepareGameOfThronesIndex();
			BulkResponse response = connector.loadBulkFromFile("src/test/resources/game_of_thrones_complex.json", TEST_INDEX_GAME_OF_THRONES);
		
			System.out.println("Loaded Bulk :"+ response.getItems().length);
			
			Assert.assertTrue(response.hasFailures()==false);
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB by Quasar. " + e);
		}
	}

	

}
