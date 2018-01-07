package com.indracompany.sofia2.persistence.mongodb;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
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
import com.indracompany.sofia2.persistence.mongodb.config.MongoDbCredentials;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplateImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
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
	static final String DATABASE = "sofia";
			
	@Test
	public void test1_MongoDbCredentials() {
		try {
			Assert.assertEquals(credentials.getAuthenticationDatabase(),"");			
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}		
	@Test
	public void test2_getConnection() {
		try {
			Assert.assertTrue(connect.getConnection().listDatabaseNames().first()!=null);			
		} 
		catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}		
	@Test
	@Ignore
	public void test3_SpringData_getConnection() {
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
