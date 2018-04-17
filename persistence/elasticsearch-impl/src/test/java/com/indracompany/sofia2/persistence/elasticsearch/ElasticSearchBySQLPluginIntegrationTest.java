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

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.persistence.elasticsearch.api.ESBaseApi;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESInsertService;
import com.indracompany.sofia2.persistence.elasticsearch.sql.connector.ElasticSearchSQLDbHttpConnector;

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
public class ElasticSearchBySQLPluginIntegrationTest {

    public final static String TEST_INDEX_ACCOUNT = "account";
	
	@Autowired
	ElasticSearchSQLDbHttpConnector httpConnector;
	
	@Autowired
	ESInsertService insertService;

	@Autowired
	ESBaseApi connector;
	
	@Before
	public  void doBefore() throws Exception {
		
		NodesInfoResponse nodeInfos = connector.getClient().admin().cluster().prepareNodesInfo().get();
		String clusterName = nodeInfos.getClusterName().value();
		System.out.println(String.format("Found cluster... cluster name: %s", clusterName));
		connector.deleteIndex(TEST_INDEX_ACCOUNT);
		connector.createIndex(TEST_INDEX_ACCOUNT);
	    System.out.println(prepareAccountsIndex());
	    
		BulkResponse response2 = insertService.loadBulkFromFileResource(TEST_INDEX_ACCOUNT,"src/test/resources/accounts.json");
		System.out.println("Loaded Bulk :"+ response2.getItems().length);
		
		Thread.sleep(10000);
	}
	
	@After
	public  void tearDown() {
		System.out.println("teardown process...");
		try {
			 deleteTestIndex(TEST_INDEX_ACCOUNT);
		        //connector.getClient().close();
		} catch (Exception e) {
			log.error("Something happens when deleting indexes :"+e.getMessage());
		}
       
	}
	
	
	private void deleteTestIndex(String index) {
		boolean res =  connector.deleteIndex(index);
		System.out.println("deleteTestIndex :"+res);
	}
	
	private  boolean prepareAccountsIndex() {
		String dataMapping = "{  \""+TEST_INDEX_ACCOUNT+"\": {" +
				" \"properties\": {\n" +
				"          \"gender\": {\n" +
				"            \"type\": \"text\",\n" +
				"            \"fielddata\": true\n" +
				"          }," +
				"          \"address\": {\n" +
				"            \"type\": \"text\",\n" +
				"            \"fielddata\": true\n" +
				"          }," +
				"          \"state\": {\n" +
				"            \"type\": \"text\",\n" +
				"            \"fielddata\": true\n" +
				"          }" +
				"       }"+
				"   }" +
				"}";
		boolean response =  connector.createType(TEST_INDEX_ACCOUNT, TEST_INDEX_ACCOUNT, dataMapping);
		return response;
	}
	
	@Test
	public void given_MongoDbAndQuasar_When_AnSQLQueryIsExecuted_Then_MongoDb_ReturnsTheResult() {
		try {
			String query = "select * from "+TEST_INDEX_ACCOUNT;
			String result = httpConnector.queryAsJson(query, 100);
			log.info("Returned:" + result);
			Assert.assertTrue(result.length() > 0);
		} catch (Exception e) {
			Assert.fail("No connection with ES. " + e);
		}
	}

	@Test
	public void testQueryAsTable() {
		try {
			String query = "select * from "+TEST_INDEX_ACCOUNT;
			String result = httpConnector.queryAsJson(query, 0, 100);
			log.info("Returned:" + result);
			Assert.assertTrue(result.length() > 0);
		} catch (Exception e) {
			Assert.fail("No connection with ES. " + e);
		}
	}
	
	@Test
	public void testQueryAsTableScroll() {
		try {
			String query = "select * from "+TEST_INDEX_ACCOUNT;
			String result = httpConnector.queryAsJson(query, 10, 20);
			log.info("Returned:" + result);
			Assert.assertTrue(result.length() > 0);
		} catch (Exception e) {
			Assert.fail("No connection with ES. " + e);
		}
	}

}
