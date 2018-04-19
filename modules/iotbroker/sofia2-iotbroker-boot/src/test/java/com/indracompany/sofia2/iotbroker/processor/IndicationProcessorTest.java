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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.SuscriptionNotificationsModel;
import com.indracompany.sofia2.config.repository.SuscriptionModelRepository;
import com.indracompany.sofia2.iotbroker.audit.aop.IotBrokerAuditableAspect;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.router.RouterServiceGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterService;
import com.indracompany.sofia2.router.service.app.service.RouterSuscriptionService;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodySubscribeMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class IndicationProcessorTest {
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext wac;
	private ResultActions resultAction;
	private final String URL_BASE_PATH = "/advice";
	@Autowired
	ObjectMapper mapper;

	@Autowired
	MessageProcessorDelegate processor;

	@Autowired
	GatewayNotifier notifier;

	@MockBean
	SecurityPluginManager securityPluginManager;
	@MockBean
	RouterService routerService;
	@MockBean
	RouterSuscriptionService routerSuscriptionService;

	//
	// @Autowired
	// MockMongoOntologies mockOntologies;

	@Autowired
	SuscriptionModelRepository repositoy;

	SSAPMessage<SSAPBodySubscribeMessage> ssapSbuscription;

	Person subject = PojoGenerator.generatePerson();

	IoTSession session;

	SSAPMessage<SSAPBodyInsertMessage> ssapInsertOperation;

	String subjectSubscriptionId;

	@MockBean
	DeviceManager deviceManager;

	@MockBean
	SuscriptionModelRepository suscriptionModelRepository;

	@MockBean
	IotBrokerAuditableAspect iotBrokerAuditableAspect;

	private void auditMocks() {
		try {
			doNothing().when(iotBrokerAuditableAspect).processTx(any(), any(), any(), any());
			doNothing().when(iotBrokerAuditableAspect).doRecoveryActions(any(), any(), any(), any(), any());

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void securityMocks() {
		session = PojoGenerator.generateSession();
		when(deviceManager.registerActivity(any(), any(), any(), any())).thenReturn(true);

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}

	@Before
	public void setUp() throws IOException, Exception {
		// repositoy.deleteByOntologyName(Person.class.getSimpleName());
		securityMocks();
		auditMocks();
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		// mockOntologies.createOntology(Person.class);
		//
		ssapSbuscription = SSAPMessageGenerator.generateSubscriptionMessage(Person.class.getSimpleName(),
				session.getSessionKey(), SSAPQueryType.SQL, "select * from Person");
		//
		subject = PojoGenerator.generatePerson();
		ssapInsertOperation = SSAPMessageGenerator.generateInsertMessage(Person.class.getSimpleName(), subject);

		final List<SuscriptionNotificationsModel> value = new ArrayList<>();

		final SuscriptionNotificationsModel sus = new SuscriptionNotificationsModel();
		sus.setSessionKey(session.getSessionKey());
		value.add(sus);
		when(suscriptionModelRepository.findAllBySuscriptionId(any())).thenReturn(value);

	}

	@After
	public void tearDown() {
		// repositoy.deleteBySuscriptionId(subjectSubscriptionId);
		// repositoy.deleteByOntologyName(Person.class.getSimpleName());
		// mockOntologies.deleteOntology(Person.class);
	}

	// @Ignore
	@Test
	public void given_OneIndication_When_ItIsDelivered_Then_ItsProcessedAndDeliveredToClient() throws Exception {
		final CompletableFuture<SSAPMessage<SSAPBodyIndicationMessage>> completableFuture = new CompletableFuture<>();
		SSAPMessage<SSAPBodyIndicationMessage> indication = new SSAPMessage<>();

		notifier.addSubscriptionListener("test_subscriptor", m -> {
			completableFuture.complete(m);
		});

		ssapSbuscription.getBody().setQuery("db.Person.find({})");
		ssapSbuscription.getBody().setQueryType(SSAPQueryType.NATIVE);
		ssapSbuscription.setSessionKey(session.getSessionKey());
		final OperationResultModel value = RouterServiceGenerator.generateSubscriptionOk(UUID.randomUUID().toString());
		when(routerSuscriptionService.suscribe(any())).thenReturn(value);

		final SSAPMessage<SSAPBodyReturnMessage> responseSubscription = processor.process(ssapSbuscription,
				PojoGenerator.generateGatewayInfo());
		final String subscriptionId = responseSubscription.getBody().getData().at("/subscriptionId").asText();
		subjectSubscriptionId = subscriptionId;

		final NotificationCompositeModel model = RouterServiceGenerator
				.generateNotificationCompositeModel(subscriptionId, subject, session);

		final String content = mapper.writeValueAsString(model);
		resultAction = mockMvc.perform(
				MockMvcRequestBuilders.post(URL_BASE_PATH).accept(org.springframework.http.MediaType.APPLICATION_JSON)
						.content(content).contentType(org.springframework.http.MediaType.APPLICATION_JSON));

		resultAction.andExpect(status().is2xxSuccessful());
		final OperationResultModel result = mapper
				.readValue(resultAction.andReturn().getResponse().getContentAsString(), OperationResultModel.class);

		indication = completableFuture.get(5, TimeUnit.SECONDS);
		Assert.assertNotNull(indication);
		Assert.assertEquals(session.getSessionKey(), indication.getSessionKey());
		Assert.assertTrue(SSAPMessageDirection.REQUEST.equals(indication.getDirection()));
		Assert.assertTrue(indication.getBody().getSubsciptionId().equals(subscriptionId));
		Assert.assertTrue(indication.getBody().getData().isArray());
		Assert.assertTrue(indication.getBody().getData().size() == 1);
	}

	// TODO: Internal advice webservice it's not accesible in test context execution
	@Ignore
	@Test
	public void given_OneSubsctiptionToOntologyThenWhenAninsertOccursThenAnIndicationIsReceived() throws Exception {

		// final CompletableFuture<SSAPMessage<SSAPBodyIndicationMessage>> future = new
		// CompletableFuture<>();

		ssapSbuscription.getBody().setQuery("db.Person.find({})");
		ssapSbuscription.getBody().setQueryType(SSAPQueryType.NATIVE);
		final SSAPMessage<SSAPBodyReturnMessage> responseSubscription = processor.process(ssapSbuscription,
				PojoGenerator.generateGatewayInfo());

		final String oid = UUID.randomUUID().toString();
		final OperationResultModel value = RouterServiceGenerator.generateInserOk(oid);
		when(routerService.insert(any())).thenReturn(value);

		final SSAPMessage<SSAPBodyReturnMessage> responseInsert = processor.process(ssapInsertOperation,
				PojoGenerator.generateGatewayInfo());

		try {
			Thread.sleep(600000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
