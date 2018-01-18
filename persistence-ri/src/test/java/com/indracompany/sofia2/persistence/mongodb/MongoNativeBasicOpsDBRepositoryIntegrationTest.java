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

import org.bson.types.ObjectId;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplateImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

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
	static final String COL_NAME = "contextData";
	static final String DATABASE = "sofia";
	
	String refOid = "";
	
	@Before
	public void setUp() throws PersistenceException, IOException {
		if(!connect.collectionExists(DATABASE, COL_NAME));
		{
			connect.createCollection(DATABASE, COL_NAME);
		}
			
        ContextData data = new ContextData();
        data.setClientConnection(UUID.randomUUID().toString());
        data.setClientPatform(UUID.randomUUID().toString());
        data.setClientSession(UUID.randomUUID().toString());
        data.setTimezoneId(UUID.randomUUID().toString());
        data.setUser(UUID.randomUUID().toString());
        ObjectMapper mapper = new ObjectMapper();
        refOid = repository.insert(COL_NAME, mapper.writeValueAsString(data));
        int init = 17;
        int end = refOid.indexOf("\"}}");
        refOid = refOid.substring(init, end);
  
	}
	
	@After
	public void tearDown() {
		connect.dropCollection(DATABASE, COL_NAME);
	}
	
	@Test
	public void test_count() {
		try {
			Assert.assertTrue(repository.count(COL_NAME)>0);			
		} catch (Exception e) {
			Assert.fail("Error test_count"+e.getMessage());
		}
	}	
	@Test
	public void test_getById() {
		try {
			String data = repository.findById(COL_NAME,refOid);			
			Assert.assertTrue(data!=null && data.indexOf("user")!=-1);			
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}	
	@Test
	public void test_getAll() {
		try {
			String data = repository.findAllAsOneJSON(COL_NAME);		
			List<String> asList= repository.findAll(COL_NAME);
			Assert.assertTrue(asList.size() > 0);		
			Assert.assertTrue(data.indexOf("clientSession")>0);				
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}	
}
