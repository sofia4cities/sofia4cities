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
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
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
import com.github.javafaker.Faker;
import com.indracompany.sofia2.iotbroker.common.exception.AuthorizationException;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.ssap.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;

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
	SSAPMessage<SSAPBodyInsertMessage> ssapInsertOperation;

	@Before
	public void setUp() throws IOException, Exception {
		//TODO: Make this checks generics
		if (springDataMongoTemplate.collectionExists(Person.class)) {
			springDataMongoTemplate.dropCollection(Person.class);
		}
		springDataMongoTemplate.createCollection(Person.class);

		subject = PojoGenerator.generatePerson();
		ssapInsertOperation = SSAPMessageGenerator.generateInsertMessage(Person.class.getSimpleName(), subject);
	}

	@After
	public void tearDown() {
		springDataMongoTemplate.dropCollection(Person.class);
	}

	@Test
	public void test_insert_clientplatform_or_sessionkey_not_present() {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		// Scenario: SessionKey is an Empty String
		{
			ssapInsertOperation.setSessionKey("");
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());

		}
		// Scenario: SessionKey is null
		{
			ssapInsertOperation.setSessionKey(null);
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());
		}

	}

	@Test
	public void test_insert_sessionkey_invalid() throws AuthorizationException {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());

		when(securityPluginManager.checkSessionKeyActive(any())).thenReturn(false);


		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTHORIZATION, responseMessage.getBody().getErrorCode());

	}

	@Test
	public void test_insert_unauthorized_operation() throws AuthorizationException {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());

		when(securityPluginManager.checkAuthorization(any(),anyString(),anyString())).thenReturn(false);

		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTHORIZATION, responseMessage.getBody().getErrorCode());

	}

	@Test
	public void test_basic_insert() throws IOException, Exception {

		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		final IoTSession session = new IoTSession();
		session.setUserID("valid_user_id");
		session.setClientPlatform(Faker.instance().chuckNorris().fact());
		session.setClientPlatformInstance(Faker.instance().chuckNorris().fact());
		session.setSessionKey(UUID.randomUUID().toString());

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);

		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation);

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		final JsonNode data = responseMessage.getBody().getData();
		final String strOid = data.at("/_id/$oid").asText();
		final ObjectId oid = new ObjectId(strOid);

		final Person savedPerson = springDataMongoTemplate.findById(oid, Person.class);
		Assert.assertNotNull(savedPerson);
		Assert.assertEquals(subject.getTelephone(), savedPerson.getTelephone());
		Assert.assertEquals("valid_user_id", savedPerson.getContextData().getUser());
		Assert.assertNotNull(savedPerson.getContextData().getClientPatform());
		Assert.assertNotNull(savedPerson.getContextData().getClientPatformInstance());
		Assert.assertNotNull(savedPerson.getContextData().getClientSession());
		Assert.assertNotNull(savedPerson.getContextData().getTimezoneId());

	}

}
