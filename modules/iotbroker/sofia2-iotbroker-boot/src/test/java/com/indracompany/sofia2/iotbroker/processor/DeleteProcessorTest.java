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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.mock.database.MockMongoOntologies;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteProcessorTest {
	@Autowired
	MessageProcessorDelegate deleteProcessor;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	BasicOpsDBRepository repository;
	@MockBean
	SecurityPluginManager securityPluginManager;

	@Autowired
	MockMongoOntologies mockOntologies;

	Person subject = PojoGenerator.generatePerson();
	String subjectId;

	SSAPMessage<SSAPBodyDeleteMessage> ssapDeletetOperation;
	SSAPMessage<SSAPBodyDeleteByIdMessage> ssapDeleteByIdtOperation;

	@MockBean
	DeviceManager deviceManager;




	private void securityMocks() {
		final IoTSession session = PojoGenerator.generateSession();
		when(deviceManager.registerActivity(any(), any(), any(), any())).thenReturn(true);

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}

	@Before
	public void setUp() throws IOException, Exception {
		mockOntologies.createOntology(Person.class);

		subject = PojoGenerator.generatePerson();
		final String subjectInsertResult = repository.insert(Person.class.getSimpleName(), objectMapper.writeValueAsString(subject));
		//		subjectId = objectMapper.readTree(subjectInsertResult).at("/_id/$oid").asText();
		subjectId = subjectInsertResult;
		ssapDeletetOperation = SSAPMessageGenerator.generateDeleteMessage(Person.class.getSimpleName(), "");
		ssapDeleteByIdtOperation = SSAPMessageGenerator.generateDeleteByIdMessage(Person.class.getSimpleName(), subjectId);

		securityMocks();
	}

	@After
	public void tearDown() {
		mockOntologies.deleteOntology(Person.class);
	}

	@Test
	public void given_OneDeleteProcessor_When_ItProcessesOneValidDeleteById_Then_TheResponseIndicatesTheOperationWasPerformed() {

		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		responseMessage = deleteProcessor.process(ssapDeleteByIdtOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertTrue(responseMessage.getDirection().equals(SSAPMessageDirection.RESPONSE));
		Assert.assertNotNull(responseMessage.getBody().getData());
		Assert.assertEquals(1, responseMessage.getBody().getData().at("/nDeleted").asInt());

	}

	@Test
	public void given_OneDeleteProcessor_When_ItProccessesOneInvalidId_Then_TheResponseIndicatesTheOperationWasNotPerformed() {

		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();

		ssapDeleteByIdtOperation.getBody().setId("5a9b2ef917f81f33589e06d3");
		responseMessage = deleteProcessor.process(ssapDeleteByIdtOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertTrue(responseMessage.getDirection().equals(SSAPMessageDirection.RESPONSE));
		Assert.assertNotNull(responseMessage.getBody().getData());
		Assert.assertEquals(0, responseMessage.getBody().getData().at("/nDeleted").asInt());
	}

	@Test
	public void given_OneDeleteProcessor_When_ItProccessesOneValidNativeQuery_TheResponseIndicatesTheOperationWasPerformed() {

		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		ssapDeletetOperation.getBody().setQuery("db.Person.remove({})");
		responseMessage = deleteProcessor.process(ssapDeletetOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertTrue(responseMessage.getDirection().equals(SSAPMessageDirection.RESPONSE));
		Assert.assertNotNull(responseMessage.getBody().getData());
		Assert.assertEquals(1, responseMessage.getBody().getData().at("/nDeleted").asInt());

	}

	@Test
	public void given_OneDeleteProcessor_When_OneNativeQueryIsPerformedAndNoOccurrencesExist_Then_TheResponseIndicatesThatNoDeletionWasPerformed() {

		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		ssapDeletetOperation.getBody().setQuery("db.Person.remove({\"name\":\"NO_OCURRENCE_NAME\"})");
		responseMessage = deleteProcessor.process(ssapDeletetOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertTrue(responseMessage.getDirection().equals(SSAPMessageDirection.RESPONSE));
		Assert.assertNotNull(responseMessage.getBody().getData());
		Assert.assertEquals(0, responseMessage.getBody().getData().at("/nDeleted").asInt());

	}

	//TODO: Driver has to detect malformed queries
	@Ignore
	@Test
	public void given_OneDeleteProcessor_When_OneMalFormedQueryIsProccesed_Then_TheResponseIndicatesThatNotDeletionWasPerformed() {

		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		ssapDeletetOperation.getBody().setQuery("db.Person.remov({})");
		responseMessage = deleteProcessor.process(ssapDeletetOperation, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertTrue(responseMessage.getDirection().equals(SSAPMessageDirection.ERROR));
		Assert.assertFalse(responseMessage.getBody().isOk());
		//		Assert.assertNotNull(responseMessage.getBody().getData());
		//		Assert.assertEquals(0, responseMessage.getBody().getData().at("/nDeleted").asInt());

	}



}
