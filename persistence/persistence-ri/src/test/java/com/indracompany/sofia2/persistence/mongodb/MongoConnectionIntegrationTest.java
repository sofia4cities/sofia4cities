package com.indracompany.sofia2.persistence.mongodb;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
public class MongoConnectionIntegrationTest {
	
	@Autowired
	MongoDbTemplateImpl connect;	
	@Autowired
	MongoDbCredentials credentials;
	@Autowired
	MongoClient client;
		
	@Test
	public void test1_MongoDbCredentials() {
		try {
			//Assert.assertTrue(connect.getConnection().listDatabaseNames().first()!=null);
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
	public void test3_SpringData_getConnection() {
		try {
			MongoDatabase database = client.getDatabase("sofia");
			log.info("Options",client.getMongoClientOptions().getMaxWaitTime());
			Assert.assertTrue(database.listCollections().first()!=null);			
			Assert.assertEquals(database.getCollection("MensajesPlataforma").count(),0);
		} 
		catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}

	

}
