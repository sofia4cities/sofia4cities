package com.indracompany.sofia2.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MQTTClient {

	private MemoryPersistence persistence;
	private String broker_url;
	private CompletableFuture<String> completableFutureMessage;
	private String sessionKey;
	private String topic;

	@PostConstruct
	public void setUp() {
		broker_url = "tcp://localhost:1883"; // TODO application.yml
		persistence = new MemoryPersistence();
		topic = "/message";
		completableFutureMessage = new CompletableFuture<>();
	}

	@SuppressWarnings("unchecked")
	public String connect(String token, String clientPlatform, String clientPlatformInstance)
			throws MqttException, SSAPParseException {

		// Connect client MQTT
		final MqttClient client = new MqttClient(broker_url, clientPlatform, persistence);
		client.connect();
		log.info("Connecting to broker MQTT at " + broker_url);
		
		client.subscribe("/topic/message/" + client.getClientId(), new IMqttMessageListener() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				final String response = new String(message.getPayload());
				log.debug("Message arrived " + response);
				completableFutureMessage.complete(response);
			}
		});

		// JOIN SSAP MESSAGE FOR SESSIONKEY
		final SSAPMessage<SSAPBodyJoinMessage> join = new SSAPMessage<SSAPBodyJoinMessage>();
		join.setDirection(SSAPMessageDirection.REQUEST);
		join.setMessageType(SSAPMessageTypes.JOIN);
		SSAPBodyJoinMessage body = new SSAPBodyJoinMessage();
		body.setClientPlatform(clientPlatform);
		body.setClientPlatformInstance(clientPlatformInstance);
		body.setToken(token);
		join.setBody(body);

		// SEND JOIN VIA MQTT
		final MqttMessage mqttJoin = new MqttMessage();
		mqttJoin.setPayload(SSAPJsonParser.getInstance().serialize(join).getBytes());
		client.publish(topic, mqttJoin);
		log.info("Trying to get Session Key...");
		// GET JOIN RESPONSE
		try {
			String joinResponse = completableFutureMessage.get(5, TimeUnit.SECONDS);
			SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(joinResponse);
			if (response.getSessionKey() != null)
				this.sessionKey = response.getSessionKey();
			log.info("Session Key established: "+this.sessionKey);
		} catch (InterruptedException e) {
			log.error("Could not retrieve session key");
			e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Could not retrieve session key");
			e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Timeout");
			log.error("Could not retrieve session key");
			e.printStackTrace();
		}
		
		return sessionKey;
	}
}
