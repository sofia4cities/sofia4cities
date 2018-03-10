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
package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.mqtt;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoquetteBrokerTest {

	String topic        = "/message";
	String content      = "Message from MqttPublishSample";
	int qos             = 2;
	String broker_url       = "tcp://localhost:1888";
	String clientId     = "JavaSample";
	MemoryPersistence persistence = new MemoryPersistence();

	@Value("${local.server.port}")
	private int port;

	@Autowired
	MoquetteBroker broker;

	@MockBean
	SecurityPluginManager securityPluginManager;

	private CompletableFuture<String> completableFuture;
	private IoTSession session = null;


	private void securityMocks() {
		session = PojoGenerator.generateSession();

		when(securityPluginManager.authenticate(any(), any(), any())).thenReturn(Optional.of(session));
		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}


	@Before
	public void setup() {
		completableFuture = new CompletableFuture<>();
		securityMocks();
	}


	@Test
	public void given_OneMqttClientConnection_When_ItSubscribesToATopicAndSendsMessage_Then_ItGetsTheMessage() throws InterruptedException, SSAPParseException, ExecutionException, TimeoutException, MqttPersistenceException, MqttException {

		final SSAPMessage<SSAPBodyJoinMessage> request = SSAPMessageGenerator.generateJoinMessageWithToken();
		final String requestStr = SSAPJsonParser.getInstance().serialize(request);
		final MqttClient client = new MqttClient(broker_url, clientId, persistence);
		final MqttConnectOptions connOpts = new MqttConnectOptions();

		connOpts.setCleanSession(true);
		client.connect(connOpts);

		client.subscribe("/topic/message/" + client.getClientId(), new IMqttMessageListener() {
			@Override
			public void messageArrived(String arg0, MqttMessage message) throws Exception {
				final String response = new String(message.getPayload());
				completableFuture.complete(response);
			}
		});

		final MqttMessage message = new MqttMessage(requestStr.getBytes());
		message.setQos(qos);
		client.publish(topic, message);

		final String responseStr = completableFuture.get(300, TimeUnit.SECONDS);
		final SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(responseStr);

		Assert.assertNotNull(responseStr);
		Assert.assertEquals(SSAPMessageDirection.RESPONSE, response.getDirection());
		Assert.assertNotNull(response.getSessionKey());
		Assert.assertEquals(session.getSessionKey(), response.getSessionKey());


		client.disconnect();



	}



}
