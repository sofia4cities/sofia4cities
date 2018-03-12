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
package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.websocket;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.router.RouterServiceGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodySubscribeMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest
public class StompWebSocketTest {
	@Value("${local.server.port}")
	private int port;

	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext wac;
	private ResultActions resultAction;
	private final String URL_BASE_PATH  = "/advice";
	@Autowired
	ObjectMapper mapper;


	private CompletableFuture<SSAPMessage<SSAPBodyReturnMessage>> completableFutureMessage;
	private CompletableFuture<SSAPMessage<SSAPBodyIndicationMessage>> completableFutureIndication;
	private String URL;
	@MockBean
	SecurityPluginManager securityPluginManager;

	Person subject;

	IoTSession session;

	private void securityMocks() {
		session = PojoGenerator.generateSession();

		when(securityPluginManager.authenticate(any(), any(), any())).thenReturn(Optional.of(session));
		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}


	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

		subject = PojoGenerator.generatePerson();
		completableFutureMessage = new CompletableFuture<>();
		completableFutureIndication = new CompletableFuture<>();
		URL = "ws://localhost:" + port + "/iotbroker/message";

		securityMocks();
	}

	@Test
	public void given_OneWebSocketConnection_When_ItsubscribesToOntologyAndInsertionOccurs_Then_iTGetsNotification() throws IOException, Exception {
		final String uuid = UUID.randomUUID().toString();
		final WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		final StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);
		//Subscription to message response
		stompSession.subscribe("/topic/message/" + uuid, new MyStompFrameHandler());
		//Subscription for indication on SSAPSubscription
		stompSession.subscribe("/topic/subscription/" + session.getSessionKey(), new MyStompIndicationHandler());

		//Sends Joins message and waits for response
		completableFutureMessage = new CompletableFuture<>();
		stompSession.send("/stomp/message/" + uuid, SSAPMessageGenerator.generateJoinMessageWithToken());
		SSAPMessage<SSAPBodyReturnMessage> response = completableFutureMessage.get();

		//Sends Subscription message and waits form response
		completableFutureMessage = new CompletableFuture<>();
		final SSAPMessage<SSAPBodySubscribeMessage> subscription = SSAPMessageGenerator.generateSubscriptionMessage(Person.class.getSimpleName(), session.getSessionKey(), SSAPQueryType.SQL, "SELECT * FROM Person");
		stompSession.send("/stomp/message/" + uuid, subscription);
		response = completableFutureMessage.get();

		//Avice indication simulated by calling advice IotBroker rest service
		final NotificationCompositeModel model = RouterServiceGenerator.generateNotificationCompositeModel(response.getBody().getData().at("/subscriptionId").asText(), subject, session);
		final String content = mapper.writeValueAsString(model);
		resultAction = mockMvc.perform(MockMvcRequestBuilders.post(URL_BASE_PATH)
				.accept(org.springframework.http.MediaType.APPLICATION_JSON)
				.content(content)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON));

		resultAction.andExpect(status().is2xxSuccessful());
		final OperationResultModel result = mapper.readValue(resultAction.andReturn().getResponse().getContentAsString(),
				OperationResultModel.class);

		//TODO: Not working because with test context advice rest service it's no accesible
		//Insert message that should trigger an indication
		//final SSAPMessage<SSAPBodyInsertMessage> insert = SSAPMessageGenerator.generateInsertMessage(Person.class.getName(), subject);
		//response = processor.process(insert);

		final SSAPMessage<SSAPBodyIndicationMessage> indication = completableFutureIndication.get(3, TimeUnit.SECONDS);

		Assert.assertNotNull(indication);
	}

	@Test
	public void given_OneWebSocketClientConnection_When_ItSubscribesToATopicAndSendsMessage_Then_ItGetsTheMessage() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
		final String uuid = UUID.randomUUID().toString();

		final WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		final StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);

		stompSession.subscribe("/topic/message/" + uuid, new MyStompFrameHandler());

		stompSession.send("/stomp/message/" + uuid, SSAPMessageGenerator.generateJoinMessageWithToken());

		final SSAPMessage<SSAPBodyReturnMessage> response = completableFutureMessage.get(120, TimeUnit.SECONDS);
		Assert.assertNotNull(response);
		Assert.assertEquals(SSAPMessageDirection.RESPONSE, response.getDirection());
		Assert.assertNotNull(response.getSessionKey());
	}

	private class MyStompFrameHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return SSAPMessage.class;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			final SSAPMessage<SSAPBodyReturnMessage> message = (SSAPMessage<SSAPBodyReturnMessage>) payload;
			completableFutureMessage.complete(message);

		}

	}

	private class MyStompIndicationHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return SSAPMessage.class;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			final SSAPMessage<SSAPBodyIndicationMessage> message = (SSAPMessage<SSAPBodyIndicationMessage>) payload;
			completableFutureIndication.complete(message);

		}

	}

	private List<Transport> createTransportClient() {
		final List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		return transports;
	}


}
