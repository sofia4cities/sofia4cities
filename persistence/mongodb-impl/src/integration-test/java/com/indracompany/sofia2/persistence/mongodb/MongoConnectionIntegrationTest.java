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

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.commons.testing.IntegrationTest;
import com.indracompany.sofia2.persistence.mongodb.config.MongoDbCredentials;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplateImpl;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
@Category(IntegrationTest.class)
//@ContextConfiguration(classes = EmbeddedMongoConfiguration.class)
public class MongoConnectionIntegrationTest {
	
	@Autowired
	MongoDbTemplateImpl connect;	
	@Autowired
	MongoDbCredentials credentials;
	@Autowired
	MongoClient client;
	@Autowired
	MongoTemplate nativeTemplate;
	static final String COL_NAME = "jjcollection";
	static final String DATABASE = "sofia2_s4c";
			
	@Test
	public void given_MongoDbCredentials_When_AuthenticationIsChecked_Then_ItIsCheckedTheAuthenticationIsDissabled() {
		try {
			Assert.assertEquals(credentials.getAuthenticationDatabase(),"");			
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}		
	
	@Test
	public void given_MongoDbConnection_When_DatabasesAreRequested_Then_TheExistenDatabasesAreReturned() {
		try {
			Assert.assertTrue(connect.getConnection().listDatabaseNames().first()!=null);			
		} 
		catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}		
	
	@Test
	@Ignore
	public void given_OneMongoDbDatabase_When_CollectionsAreRequested_Then_TheyAreReturned() {
		try {
			MongoDatabase database = client.getDatabase(client.listDatabaseNames().first());
			
			log.info("Options",client.getMongoClientOptions().getMaxWaitTime());
			Assert.assertTrue(database.listCollections().first()!=null);
			String collection = database.listCollections().first().getString("name");
			Assert.assertEquals(0, database.getCollection(collection).count());
		} 
		catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}

	

}
