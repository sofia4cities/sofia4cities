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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.	SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.ssap.generator.PojoGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.ssap.generator.pojo.Person;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateProcessorTest {
	MessageProcessorDelegate processor;

	@Autowired
	MessageProcessorDelegate updateProcessor;

	@Autowired
	MongoTemplate springDataMongoTemplate;

	@MockBean
	SecurityPluginManager securityPluginManager;

	Person subject = PojoGenerator.generatePerson();

	SSAPMessage<SSAPBodyUpdateMessage> ssapUpdate;

	ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void setUp() throws IOException, Exception {
		//TODO: Make these checks generics
		if (springDataMongoTemplate.collectionExists(Person.class)) {
			springDataMongoTemplate.dropCollection(Person.class);
		}
		springDataMongoTemplate.createCollection(Person.class);

		subject = PojoGenerator.generatePerson();
		springDataMongoTemplate.insert(subject);
		ssapUpdate = SSAPMessageGenerator.generateUpdatetMessage(Person.class.getSimpleName(), "");
	}

	@After
	public void tearDown() {
		//TODO: Make this action generics
		springDataMongoTemplate.dropCollection(Person.class);
	}

	@Test
	public void test_upate_basic() {

		final IoTSession session = new IoTSession();
		session.setUserID("valid_user_id");
		session.setClientPlatform(Faker.instance().chuckNorris().fact());
		session.setClientPlatformInstance(Faker.instance().chuckNorris().fact());
		session.setSessionKey(UUID.randomUUID().toString());

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);

		ssapUpdate.getBody().setQuery("db.Person.update({\"name\":\""+subject.getName()+"\"},{$set: { \"name\": \"NAME_NEW\" }})");

		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = updateProcessor.process(ssapUpdate);

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertTrue(responseMessage.getDirection().equals(SSAPMessageDirection.RESPONSE));
		Assert.assertNotNull(responseMessage.getBody().getData());
		Assert.assertEquals(1, responseMessage.getBody().getData().at("/nModified").asInt());

	}

	//TODO: Update by ID
	//TODO: Update by ID that doesnt exists or null or empty
	//TODO: Update with null or empty query
	//TODO: Update more than one register
	//TODO: Update where without ocurrences
	//TODO: Check if update corrupts schema
	//TODO: Try too update ontology without permissions
	//TODO: Update by SQL?

}
