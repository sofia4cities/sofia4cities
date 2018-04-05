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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.iotbroker.common.exception.AuthorizationException;
import com.indracompany.sofia2.iotbroker.mock.database.MockMongoOntologies;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
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
	BasicOpsDBRepository repository;

	@MockBean
	SecurityPluginManager securityPluginManager;

	@Autowired
	MockMongoOntologies mockOntologies;

	Person subject = PojoGenerator.generatePerson();
	SSAPMessage<SSAPBodyInsertMessage> ssapInsertOperation;

	@MockBean
	DeviceManager deviceManager;

	@MockBean
	OntologyRepository ontologyRepository;

	@Before
	public void setUp() throws IOException, Exception {
		mockOntologies.createOntology(Person.class);
		when(deviceManager.registerActivity(any(), any(), any(), any())).thenReturn(true);

		subject = PojoGenerator.generatePerson();
		ssapInsertOperation = SSAPMessageGenerator.generateInsertMessage(Person.class.getSimpleName(), subject);
	}

	@After
	public void tearDown() {
		mockOntologies.deleteOntology(Person.class);
	}

	@Test
	public void given_OneInsertProcessor_When_InvalidClientPlatformOrSessionKeyIsNotPresent_Then_TheResponseIndicatesProcessorError() {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		// Scenario: SessionKey is an Empty String
		{
			ssapInsertOperation.setSessionKey("");
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation, PojoGenerator.generateGatewayInfo());

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());

		}
		// Scenario: SessionKey is null
		{
			ssapInsertOperation.setSessionKey(null);
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation, PojoGenerator.generateGatewayInfo());

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());
		}

	}

	@Test
	public void given_OneInsertProcessor_When_AnInvalidSessionIsUsed_Then_TheResponseIndicatesAuthorizationError () throws AuthorizationException {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());

		when(securityPluginManager.checkSessionKeyActive(any())).thenReturn(false);


		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTHORIZATION, responseMessage.getBody().getErrorCode());

	}

	@Test
	public void given_OneInsertProcessor_When_AnUnauthorizedOperationIsPerformed_Then_TheResponseIndicatesAnAuthorizationError () throws AuthorizationException {
		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());

		when(securityPluginManager.checkAuthorization(any(),anyString(),anyString())).thenReturn(false);

		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTHORIZATION, responseMessage.getBody().getErrorCode());

	}

	@Test
	public void given_OneInsertProcessor_When_AnAuthorizedOperationWithCorrectSessionIsPerformed_Then_TheResponseIndicatesTheDataIsInserted() throws IOException, Exception {

		ssapInsertOperation.setSessionKey(UUID.randomUUID().toString());
		final IoTSession session = PojoGenerator.generateSession();
		
		Ontology ontology = new Ontology();
		ontology.setJsonSchema(mockOntologies.getJSONSchema(Person.class));

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
		when(ontologyRepository.findByIdentification(anyString())).thenReturn(ontology);
		
		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = insertProcessor.process(ssapInsertOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		final JsonNode data = responseMessage.getBody().getData();
		final String strOid = data.at("/id").asText();

		final String created = repository.findById(Person.class.getSimpleName(), strOid);
		System.out.println(created);
		Assert.assertNotNull(created);
		Assert.assertTrue(created.contains("valid_user_id"));

	}

}
