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

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
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

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
// @ContextConfiguration(classes = EmbeddedMongoConfiguration.class)
// @Ignore
public class ElasticSearchBasicApiTest {
	
	 public final static String TEST_INDEX = "elasticsearch-test_index";
	 public final static String TEST_INDEX_GAME_OF_THRONES = TEST_INDEX + "_game_of_thrones";

	@Autowired
	ElasticSearchApi connector;
	
	private String createTestIndex(String index) {
		return connector.createIndex(index);
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
	        
	      return connector.prepareIndex(TEST_INDEX_GAME_OF_THRONES, "gotCharacters", dataMapping);
	       
	    }

	private  void deleteTestIndex(String index) {
		return connector.createIndex(index);
	}

	@Test
	public void testCreateTable() {
		try {
			createTestIndex(TEST_INDEX_GAME_OF_THRONES);
			prepareGameOfThronesIndex();
			loadBulk("src/test/resources/game_of_thrones_complex.json", TEST_INDEX_GAME_OF_THRONES);
		
			Assert.assertTrue(result.length() > 0);
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB by Quasar. " + e);
		}
	}

	@Test
	public void testQueryAsTable() {
		try {
			String query = "select * from shakespeare";
			String result = connector.queryAsJson(query, 0, 100);
			log.info("Returned:" + result);
			Assert.assertTrue(result.length() > 0);
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB by Quasar. " + e);
		}
	}
	
	@Test
	public void testQueryAsTableScroll() {
		try {
			String query = "select * from shakespeare";
			String result = connector.queryAsJson(query, 10, 20);
			log.info("Returned:" + result);
			Assert.assertTrue(result.length() > 0);
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB by Quasar. " + e);
		}
	}

}
