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
package com.indracompany.sofia2.persistence.mongodb.services;

import java.io.IOException;
import java.util.UUID;

import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplateImpl;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
// @Ignore
public class QueryAsTextMongoDBImplIntegrationTest {

	@Autowired
	QueryAsTextMongoDBImpl queryTool;

	@Autowired
	MongoDbTemplateImpl connect;

	@Autowired
	BasicOpsDBRepository repository;

	@Autowired
	MongoTemplate nativeTemplate;
	static final String ONT_NAME = "contextData";
	static final String DATABASE = "sofia2_s4c";

	String refOid = "";

	@Before
	public void setUp() throws PersistenceException, IOException {
		if (!connect.collectionExists(DATABASE, ONT_NAME))
			connect.createCollection(DATABASE, ONT_NAME);
		// 1º
		ContextData data = new ContextData();
		data.setClientConnection(UUID.randomUUID().toString());
		data.setClientPatform(UUID.randomUUID().toString());
		data.setClientSession(UUID.randomUUID().toString());
		data.setTimezoneId(UUID.randomUUID().toString());
		data.setUser("user");
		ObjectMapper mapper = new ObjectMapper();
		refOid = repository.insert(ONT_NAME, mapper.writeValueAsString(data));
		// 2º
		data = new ContextData();
		data.setClientConnection(UUID.randomUUID().toString());
		data.setClientPatform(UUID.randomUUID().toString());
		data.setClientSession(UUID.randomUUID().toString());
		data.setTimezoneId(UUID.randomUUID().toString());
		data.setUser("admin");
		mapper = new ObjectMapper();
		refOid = repository.insert(ONT_NAME, mapper.writeValueAsString(data));
		// 3º
		data = new ContextData();
		data.setClientConnection(UUID.randomUUID().toString());
		data.setClientPatform(UUID.randomUUID().toString());
		data.setClientSession(UUID.randomUUID().toString());
		data.setTimezoneId(UUID.randomUUID().toString());
		data.setUser("other");
		mapper = new ObjectMapper();
		refOid = repository.insert(ONT_NAME, mapper.writeValueAsString(data));
	}

	@After
	public void tearDown() {
		connect.dropCollection(DATABASE, ONT_NAME);
	}

	@Test
	public void test1_remove() {
		try {
			String json = queryTool.querySQLAsJson(ONT_NAME, "select count(*) from contextData", 0);
			Assert.assertTrue(json.indexOf("3") != -1);
			json = queryTool.queryNativeAsJson(ONT_NAME, "db." + ONT_NAME + ".remove({})");
			json = queryTool.querySQLAsJson(ONT_NAME, "select count(*) from contextData", 0);
			Assert.assertTrue(json.indexOf("3") == -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test1_QueryNativeLimit() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME, "db." + ONT_NAME + ".find({'user':'user'}).limit(2)", 0,
					0);
			Assert.assertTrue(json.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test1_QueryNativeProjections() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME,
					"db." + ONT_NAME + ".find({'user':'user'},{user:1,_id:0})", 0, 0);
			Assert.assertTrue(json.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test1_QuerySort() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME, "db." + ONT_NAME + ".find().sort({'user':-1})", 0, 0);
			Assert.assertTrue(json.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test1_QuerySkip() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME, "db." + ONT_NAME + ".find().skip(2)", 0, 0);
			Assert.assertTrue(json.indexOf("other") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test1_QueryNativeType4() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME, "db." + ONT_NAME + ".find()", 0, 0);
			Assert.assertTrue(json.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test_QueryNativeType1() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME, "{}", 0, 0);
			Assert.assertTrue(json.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test_QueryNativeType2() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME, "{'user':'user'}", 0, 0);
			Assert.assertTrue(json.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test_QueryNativeType3() {
		try {
			String json = queryTool.queryNativeAsJson(ONT_NAME, "db." + ONT_NAME + ".find({\"user\":\"admin\"})", 0, 0);
			Assert.assertTrue(json.indexOf("admin") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QueryNative" + e.getMessage());
		}
	}

	@Test
	public void test_QuerySQL() {
		try {
			String json = queryTool.querySQLAsJson(ONT_NAME, "select * from contextData", 0);
			Assert.assertTrue(json.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QuerySQL" + e.getMessage());
		}
	}

	@Test
	public void test_QueryCountSQL() {
		try {
			String json = queryTool.querySQLAsJson(ONT_NAME, "select count(*) from contextData", 0);
			Assert.assertTrue(json.indexOf("3") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_QuerySQL" + e.getMessage());
		}
	}

	@Test
	public void test_createAndDropIndex() {
		try {
			String result = queryTool.queryNativeAsJson(ONT_NAME,
					"db.contextData.createIndex({'user':1},{'name':'user_i'})");
			Assert.assertTrue(result.indexOf("Created index") != -1);
			result = queryTool.queryNativeAsJson(ONT_NAME, "db.contextData.getIndexes()");
			Assert.assertTrue(result.indexOf("user_i") != -1);
			result = queryTool.queryNativeAsJson(ONT_NAME, "db.contextData.dropIndex('user_i')");
			Assert.assertTrue(result.indexOf("Dropped index") != -1);
		} catch (Exception e) {
			Assert.fail("test1_createIndex:" + e.getMessage());
		}
	}

	@Test
	public void test_InsertAndUpdateAndRemove() {
		try {
			String result = queryTool.queryNativeAsJson(ONT_NAME, "db.contextData.count()");
			Assert.assertTrue(result.indexOf("0") == -1);
			//
			result = queryTool.queryNativeAsJson(ONT_NAME,
					"db.contextData.insert({\"user\":\"user_temp_1\",\"clientPatform\":\"1\"})");
			Assert.assertTrue(result.indexOf("Inserted row") != -1);
			result = queryTool.queryNativeAsJson(ONT_NAME, "db.contextData.remove({\"user\":\"user_temp_1\"})");
			Assert.assertTrue(result.indexOf("Deleted 1") != -1);
			//
			result = queryTool.queryNativeAsJson(ONT_NAME,
					"db.contextData.insert({'user':'user_temp_2','clientPatform':'2'})");
			Assert.assertTrue(result.indexOf("Inserted row") != -1);
			result = queryTool.queryNativeAsJson(ONT_NAME,
					"db.contextData.update({'user':'user_temp_2'},{'clientPatform':'3'})");
			Assert.assertTrue(result.indexOf("Updated 1") != -1);
			//

			result = queryTool.queryNativeAsJson(ONT_NAME,
					"db.contextData.remove({'user':'user_temp_2','clientPatform':'3'})");
			Assert.assertTrue(result.indexOf("Deleted 1") != -1);

		} catch (Exception e) {
			Assert.fail("test_InsertAndUpdateAndRemove:" + e.getMessage());
		}
	}

}
