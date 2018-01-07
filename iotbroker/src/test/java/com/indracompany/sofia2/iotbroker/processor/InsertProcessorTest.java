package com.indracompany.sofia2.iotbroker.processor;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.iotbroker.processor.impl.InsertProcessor;
import com.indracompany.sofia2.iotbroker.ssap.generator.PojoGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.pojo.Person;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertProcessorTest {
	
	@Autowired
	InsertProcessor insertProcessor;
	
	@Autowired
	MongoTemplate springDataMongoTemplate;
	
	@Before
	public void setUp() {
		springDataMongoTemplate.createCollection(Person.class);
	}
	
	@After
	public void tearDown() {
		springDataMongoTemplate.dropCollection(Person.class);
	}
	
	@Test
	public void test_basic_insert() throws IOException, Exception {
		
		Person toInsertPerson = PojoGenerator.generatePerson();
		SSAPMessage<SSAPBodyOperationMessage> ssapInsertOperation = 
				SSAPMessageGenerator.generateInsertMessage(
						Person.class.getSimpleName(), 
						toInsertPerson);
		
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
		
		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		JsonNode data = responseMessage.getBody().getData();
		String strOid = data.at("/_id/$oid").asText();
		ObjectId oid = new ObjectId(strOid);
		
		Person savedPerson = springDataMongoTemplate.findById(oid, Person.class);
		Assert.assertNotNull(savedPerson);
		Assert.assertEquals(toInsertPerson.getTelephone(), savedPerson.getTelephone());
		
		System.out.println("hola");
		
	}

}
