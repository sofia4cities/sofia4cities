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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest
public class StompWebSocketTest {
	@Value("${local.server.port}")
	private int port;

	private CompletableFuture<SSAPMessage<SSAPBodyReturnMessage>> completableFuture;
	private String URL;

	@MockBean
	SecurityPluginManager securityPluginManager;

	private void securityMocks() {
		final IoTSession session = PojoGenerator.generateSession();

		when(securityPluginManager.authenticate(any(), any(), any())).thenReturn(Optional.of(session));
		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}


	@Before
	public void setup() {
		completableFuture = new CompletableFuture<>();
		URL = "ws://localhost:" + port + "/iotbroker/message";

		securityMocks();
	}
	@Test
	public void test_stomp_web_socket_join() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
		final String uuid = UUID.randomUUID().toString();

		final WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		final StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);

		stompSession.subscribe("/topic/message/" + uuid, new MyStompFrameHandler());
		stompSession.send("/stomp/message/" + uuid, SSAPMessageGenerator.generateJoinMessageWithToken());

		final SSAPMessage<SSAPBodyReturnMessage> response = completableFuture.get(3, TimeUnit.SECONDS);
		Assert.assertNotNull(response);
		Assert.assertEquals(SSAPMessageDirection.RESPONSE, response.getDirection());
		Assert.assertNotNull(response.getSessionKey());
	}



	private List<Transport> createTransportClient() {
		final List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		return transports;
	}

	private class MyStompFrameHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return SSAPMessage.class;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			final SSAPMessage<SSAPBodyReturnMessage> message = (SSAPMessage<SSAPBodyReturnMessage>) payload;
			completableFuture.complete(message);

		}

	}
}
