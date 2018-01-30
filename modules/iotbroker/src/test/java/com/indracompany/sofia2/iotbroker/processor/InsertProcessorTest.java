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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.iotbroker.ssap.generator.PojoGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.pojo.Person;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertProcessorTest {
	
	@Autowired
	MessageProcessorDelegate insertProcessor;
	
	@Autowired
	MongoTemplate springDataMongoTemplate;
	
	@MockBean
	SecurityPluginManager securityPluginManager;
	
	Person subject = PojoGenerator.generatePerson();
	SSAPMessage<SSAPBodyOperationMessage> ssapInsertOperation;
	
	@Before
	public void setUp() throws IOException, Exception {
		if(springDataMongoTemplate.collectionExists(Person.class)) {
			springDataMongoTemplate.createCollection(Person.class);
		}
		springDataMongoTemplate.createCollection(Person.class);
		
		subject = PojoGenerator.generatePerson();
		ssapInsertOperation = SSAPMessageGenerator.generateInsertMessage(
						Person.class.getSimpleName(), 
						subject);
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
