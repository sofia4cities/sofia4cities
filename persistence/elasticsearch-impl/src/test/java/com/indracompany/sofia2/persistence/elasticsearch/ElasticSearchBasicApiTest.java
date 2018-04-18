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
package com.indracompany.sofia2.persistence.elasticsearch;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.persistence.elasticsearch.api.ESBaseApi;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESInsertService;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
@Ignore
public class ElasticSearchBasicApiTest {
	
	public final static String TEST_INDEX = "test"+System.currentTimeMillis();
	 public final static String TEST_INDEX_GAME_OF_THRONES = TEST_INDEX + "_game_of_thrones";
	 public final static String TEST_INDEX_ONLINE = TEST_INDEX + "_online";

	@Autowired
	ESBaseApi connector;
		
	@Autowired
	ESInsertService sSInsertService;
	
	private String createTestIndex(String index) {
		String res =   connector.createIndex(index);
		System.out.println("createTestIndex :"+res);
		return res;
	}
	
	@After
	public  void tearDown() {
		System.out.println("teardown process...");
		
		try {	
	        deleteTestIndex(TEST_INDEX_GAME_OF_THRONES);
	        deleteTestIndex(TEST_INDEX_ONLINE);
		} catch (Exception e) {
			log.error("Something happens when deleting indexes :"+e.getMessage());
		}
	
	}
	
	 private boolean  prepareGameOfThronesIndex() {
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
	        
	        boolean response =  connector.createType(TEST_INDEX_GAME_OF_THRONES, "gotCharacters", dataMapping);
	        System.out.println("prepareGameOfThronesIndex :"+response);
	        return response;
	       
	    }

	private void deleteTestIndex(String index) {
		boolean res =  connector.deleteIndex(index);
		System.out.println("deleteTestIndex :"+res);
	}

	@Test
	public void testCreateTable() {
		try {
			
			
			deleteTestIndex(TEST_INDEX_ONLINE);
			createTestIndex(TEST_INDEX_ONLINE);
			String jsonPath = "src/test/resources/online.json";
			List<String> list = ESInsertService.readLines(new File(jsonPath));
			    
			List<String> result = list.stream()               
		                .filter(x -> x.startsWith("{\"0\""))    
		                .collect(Collectors.toList());  
				
			List<BulkWriteResult> r =sSInsertService.load(TEST_INDEX_ONLINE, TEST_INDEX_ONLINE, result);

			System.out.println("Loaded Bulk :"+ r.size());
		
			deleteTestIndex(TEST_INDEX_GAME_OF_THRONES);
			createTestIndex(TEST_INDEX_GAME_OF_THRONES);
			prepareGameOfThronesIndex();
			
			jsonPath = "src/test/resources/game_of_thrones_complex.json";
			list = ESInsertService.readLines(new File(jsonPath));
		    
			result = list.stream()               
		                .filter(x -> x.startsWith("{\"name\":"))    
		                .collect(Collectors.toList());  
				
			 r =sSInsertService.load(TEST_INDEX_GAME_OF_THRONES, TEST_INDEX_GAME_OF_THRONES, result);

			System.out.println("Loaded Bulk :"+ r.size());
					

			
			Assert.assertTrue(r.size()>0);
		} catch (Exception e) {
			Assert.fail("No connection. " + e);
		}
	}

	

}
