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

import java.io.IOException;
import java.util.List;
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

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class MongoNativeBasicOpsDBRepositoryIntegrationTest {

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
		if (!connect.collectionExists(DATABASE, ONT_NAME)) {
			connect.createCollection(DATABASE, ONT_NAME);
		}
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
		// 4º
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
	public void test_count() {
		try {
			Assert.assertTrue(repository.count(ONT_NAME) > 0);
		} catch (Exception e) {
			Assert.fail("Error test_count" + e.getMessage());
		}
	}

	@Test
	public void test_remove() {
		try {
			Assert.assertTrue(repository.deleteNative(ONT_NAME, "{}") == 4);
		} catch (Exception e) {
			Assert.fail("Error test_remove" + e.getMessage());
		}
	}

	@Test
	public void test_update() {
		try {
			Assert.assertTrue(repository.updateNative(ONT_NAME,
					"{user:'other'},{clientPlatform:'" + UUID.randomUUID().toString() + "'}") == 2);
		} catch (Exception e) {
			Assert.fail("Error test_update" + e.getMessage());
		}
	}

	@Test
	public void test_getById() {
		try {
			String data = repository.findById(ONT_NAME, refOid);
			Assert.assertTrue(data != null && data.indexOf("user") != -1);
		} catch (Exception e) {
			Assert.fail("Error test_getById" + e.getMessage());
		}
	}

	@Test
	public void test_getAll() {
		try {
			String data = repository.findAllAsJson(ONT_NAME);
			List<String> asList = repository.findAll(ONT_NAME);
			Assert.assertTrue(asList.size() > 0);
			Assert.assertTrue(data.indexOf("clientSession") > 0);
		} catch (Exception e) {
			Assert.fail("Error test_getAll" + e.getMessage());
		}
	}

}
