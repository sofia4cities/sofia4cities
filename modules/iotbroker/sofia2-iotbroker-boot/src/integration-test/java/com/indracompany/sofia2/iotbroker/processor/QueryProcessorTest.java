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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.commons.testing.IntegrationTest;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.iotbroker.audit.aop.IotBrokerAuditableAspect;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.router.RouterServiceGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterService;
import com.indracompany.sofia2.router.service.app.service.RouterSuscriptionService;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyQueryMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

@RunWith(SpringRunner.class)
@SpringBootTest
@Category(IntegrationTest.class)
public class QueryProcessorTest {
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MessageProcessorDelegate queryProcessor;

	@Autowired
	MongoBasicOpsDBRepository repository;

	@MockBean
	SecurityPluginManager securityPluginManager;
	@MockBean
	OntologyService ontologyService;

	// @Autowired
	// MockMongoOntologies mockOntologies;

	@MockBean
	RouterService routerService;
	@MockBean
	RouterSuscriptionService routerSuscriptionService;

	Person subject = PojoGenerator.generatePerson();
	String subjectId;

	SSAPMessage<SSAPBodyQueryMessage> ssapQuery;
	@MockBean
	DeviceManager deviceManager;

	@MockBean
	IotBrokerAuditableAspect iotBrokerAuditableAspect;

	private void auditMocks() {
		// doNothing().when(iotBrokerAuditableAspect).afterReturningExecution(any(),
		// any(), any());
		// doNothing().when(iotBrokerAuditableAspect).beforeExecution(any(), any());
		doNothing().when(iotBrokerAuditableAspect).doRecoveryActions(any(), any(), any(), any(), any());

		final IotBrokerAuditEvent evt = new IotBrokerAuditEvent("", UUID.randomUUID().toString(), EventType.IOTBROKER,
				10l, "formatedTimeStamp", "user", "ontology", "operationType", Module.IOTBROKER, null, "otherType",
				"remoteAddress", ResultOperationType.SUCCESS, "sessionKey", new GatewayInfo(), "query", "data",
				"clientPlatform", "clientPlatformInstance");
		when(iotBrokerAuditableAspect.getEvent(any(), any())).thenReturn(evt);
	}

	private void securityMocks() {
		final IoTSession session = PojoGenerator.generateSession();
		when(deviceManager.registerActivity(any(), any(), any(), any())).thenReturn(true);

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);

		when(ontologyService.hasUserPermissionForQuery(any(String.class), any(String.class))).thenReturn(true);
		when(ontologyService.hasClientPlatformPermisionForQuery(any(String.class), any(String.class))).thenReturn(true);
	}

	@Before
	public void setUp() throws IOException, Exception {

		// mockOntologies.createOntology(Person.class);

		subject = PojoGenerator.generatePerson();
		final String subjectInsertResult = repository.insert(Person.class.getSimpleName(),
				objectMapper.writeValueAsString(subject));
		subjectId = subjectInsertResult;
		ssapQuery = SSAPMessageGenerator.generateQueryMessage(Person.class.getSimpleName(), SSAPQueryType.NATIVE, "");

		securityMocks();
		auditMocks();
	}

	@After
	public void tearDown() {
		// mockOntologies.deleteOntology(Person.class);
	}

	@Test
	public void given_OneQueryProcessor_When_ACorrectNativeQueryIsUsed_Then_TheResponseReturnsTheResults()
			throws Exception {
		ssapQuery.getBody().setQuery("db.Person.find({})");
		SSAPMessage<SSAPBodyReturnMessage> responseMessage;

		final OperationResultModel value = RouterServiceGenerator.generateInserOk("[{},{}]");
		when(routerService.query(any())).thenReturn(value);
		responseMessage = queryProcessor.process(ssapQuery, PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		// Assert.assertTrue(responseMessage.getDirection().equals(SSAPMessageDirection.RESPONSE));
		Assert.assertNotNull(responseMessage.getBody().getData());
		Assert.assertTrue(responseMessage.getBody().getData().isArray());
		final ArrayNode array = (ArrayNode) responseMessage.getBody().getData();
		Assert.assertTrue(array.size() > 0);

	}
}
