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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

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
import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.ssap.generator.PojoGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.pojo.Person;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPErrorCode;
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
			springDataMongoTemplate.dropCollection(Person.class);
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
	public void test_insert_clientplatform_or_sessionkey_not_present() {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatform(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatformInstance(UUID.randomUUID().toString());
		
		//Scenario: SessionKey is an Empty String
		{
			ssapInsertOperation.setSessionKey("");
			SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
			
			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());
			
		}
		//Scenario: SessionKey is null
		{
			ssapInsertOperation.setSessionKey(null);
			SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
			
			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());			
		}
		
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatform(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatformInstance(UUID.randomUUID().toString());		
		//Scenario: Client Platform is an Empty String
		{
			ssapInsertOperation.getBody().setClientPlatform("");
			SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
			
			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());			
		}
		//Scenario: Client Platform is an null
		{
			ssapInsertOperation.getBody().setClientPlatform(null);
			SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
			
			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());			
		}
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatform(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatformInstance(UUID.randomUUID().toString());		
		//Scenario: Client Platform Instance is an Empty String
		{
			ssapInsertOperation.getBody().setClientPlatformInstance("");
			SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
			
			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());			
		}
		//Scenario: Client Platform Instance is an null
		{
			ssapInsertOperation.getBody().setClientPlatformInstance(null);
			SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
			
			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());			
		}		
	}
	
	@Test
	public void test_insert_sessionkey_invalid() throws AuthenticationException {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatform(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatformInstance(UUID.randomUUID().toString());		
		
		doThrow(new AuthenticationException(MessageException.ERR_SESSIONKEY_NOT_ASSINGED))
			.when(securityPluginManager)
			.authenticate(any());
		
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
		
		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTENTICATION, responseMessage.getBody().getErrorCode());	
		
	}
	
	@Test
	public void test_insert_unauthorized_operation() throws AuthorizationException {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatform(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatformInstance(UUID.randomUUID().toString());		
		
		doThrow(new AuthorizationException(MessageException.ERR_SESSIONKEY_NOT_ASSINGED))
			.when(securityPluginManager)
			.checkAuthorization(any(), any(), any());
		
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
		
		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTHORIZATION, responseMessage.getBody().getErrorCode());	
		
	}
	
	
	@Test
	public void test_basic_insert() throws IOException, Exception {
		
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatform(UUID.randomUUID().toString());
		ssapInsertOperation.getBody().setClientPlatformInstance(UUID.randomUUID().toString());
		
		when(securityPluginManager.getUserIdFromSessionKey(anyString())).thenReturn("valid_user_id");
		
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);
		
		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		JsonNode data = responseMessage.getBody().getData();
		String strOid = data.at("/_id/$oid").asText();
		ObjectId oid = new ObjectId(strOid);
		
		Person savedPerson = springDataMongoTemplate.findById(oid, Person.class);
		Assert.assertNotNull(savedPerson);
		Assert.assertEquals(subject.getTelephone(), savedPerson.getTelephone());
		Assert.assertEquals("valid_user_id", subject.getContextData().getUser());
		Assert.assertNotNull(subject.getContextData().getClientPatform());
		Assert.assertNotNull(subject.getContextData().getClientPatformInstance());
		Assert.assertNotNull(subject.getContextData().getClientSession());
		Assert.assertNotNull(subject.getContextData().getTimezoneId());
		
		
		
	}

}
