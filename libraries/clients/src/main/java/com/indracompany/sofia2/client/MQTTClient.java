package com.indracompany.sofia2.client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.client.exception.MQTTException;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLeaveMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MQTTClient {

	private MemoryPersistence persistence = new MemoryPersistence();;
	private String brokerURI;
	private CompletableFuture<String> completableFutureMessage = new CompletableFuture<>();;
	private String sessionKey;
	private String topic = "/message";;
	private MqttClient client;

	public MQTTClient(String brokerURI) {
		this.brokerURI = brokerURI;
	}

	/**
	 * Creates a MQTT session.
	 *
	 * @param token
	 *            The token associated with the device/client
	 * @param clientPlatform
	 *            The device/client identification
	 * @param clientPlatformInstance
	 *            The instance of the device
	 * @param timeout
	 *            Time in seconds for waiting response from Broker
	 * @return The session key for the session established between client and IoT
	 *         Broker
	 * 
	 */

	@SuppressWarnings("unchecked")
	public String connect(String token, String clientPlatform, String clientPlatformInstance, int timeout)
			throws MQTTException {

		// JOIN SSAP MESSAGE FOR SESSIONKEY
		final SSAPMessage<SSAPBodyJoinMessage> join = new SSAPMessage<SSAPBodyJoinMessage>();
		join.setDirection(SSAPMessageDirection.REQUEST);
		join.setMessageType(SSAPMessageTypes.JOIN);
		SSAPBodyJoinMessage body = new SSAPBodyJoinMessage();
		body.setClientPlatform(clientPlatform);
		body.setClientPlatformInstance(clientPlatformInstance);
		body.setToken(token);
		join.setBody(body);

		try {
			// Connect client MQTT
			this.client = new MqttClient(brokerURI, clientPlatform, persistence);
			this.client.connect();
			log.info("Connecting to broker MQTT at " + brokerURI);

			this.client.subscribe("/topic/message/" + client.getClientId(), new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					final String response = new String(message.getPayload());
					log.debug("Message arrived " + response);
					completableFutureMessage.complete(response);
					completableFutureMessage = new CompletableFuture<>();
				}
			});

			// SEND JOIN VIA MQTT
			final MqttMessage mqttJoin = new MqttMessage();
			mqttJoin.setPayload(SSAPJsonParser.getInstance().serialize(join).getBytes());
			this.client.publish(topic, mqttJoin);
			log.info("Trying to get Session Key...");

			// GET JOIN RESPONSE
			String joinResponse = completableFutureMessage.get(timeout, TimeUnit.SECONDS);
			SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(joinResponse);
			if (response.getSessionKey() != null)
				this.sessionKey = response.getSessionKey();
			log.info("Session Key established: " + this.sessionKey);

		} catch (MqttException e) {
			log.error("Could not connect to MQTT broker");
			throw new MQTTException("Could not connect to MQTT broker");
		} catch (InterruptedException e) {
			log.error("Could not retrieve message from broker, interrupted thread");
			throw new MQTTException("Could not retrieve message from broker, interrupted thread");
		} catch (ExecutionException e) {
			log.error("Could not get result from retrieved message at CompletableFuture object");
			throw new MQTTException("Could not get result from retrieved message at CompletableFuture object");
		} catch (TimeoutException e) {
			log.error("Timeout, could not retrieve session key");
			throw new MQTTException("Timeout, could not retrieve session key");
		} catch (SSAPParseException e) {
			log.error("Could not parse SSAP message");
			throw new MQTTException("Could not parse SSAP message");
		}

		if (this.sessionKey == null)
			log.info("Session key is null, either Token or ClientPlatform params are not valid");

		return this.sessionKey;
	}

	/**
	 * Publishes a message through MQTT session.
	 *
	 * @param ontology
	 *            Ontology associated with the message
	 * @param jsonData
	 *            Ontology message payload
	 * @param timeout
	 *            Time in seconds for waiting response from Broker
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void publish(String ontology, String jsonData, int timeout) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode data;
		try {
			data = mapper.readTree(jsonData);

			final SSAPMessage<SSAPBodyInsertMessage> insert = new SSAPMessage<SSAPBodyInsertMessage>();
			final SSAPBodyInsertMessage body = new SSAPBodyInsertMessage();
			insert.setDirection(SSAPMessageDirection.REQUEST);
			insert.setMessageType(SSAPMessageTypes.INSERT);
			insert.setSessionKey(this.sessionKey);
			body.setOntology(ontology);
			body.setData(data);
			insert.setBody(body);

			final MqttMessage mqttInsert = new MqttMessage();
			mqttInsert.setPayload(SSAPJsonParser.getInstance().serialize(insert).getBytes());

			this.client.publish(topic, mqttInsert);
			log.info("Publishing message for insert to IoT broker...");

			String insertResponse = completableFutureMessage.get(timeout, TimeUnit.SECONDS);

			SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(insertResponse);
			if (response.getBody().isOk())
				log.info("Message published");
			else
				log.error("Could not publish message");

		} catch (JsonProcessingException e) {
			log.error("Could not read json data, invalid format");
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Could not create Json Node");
			e.printStackTrace();
		} catch (SSAPParseException e) {
			log.error("Could not parse SSAP message");
			throw new MQTTException("Could not parse SSAP message");
		} catch (MqttException e) {
			log.error("Could not connect to MQTT broker");
			throw new MQTTException("Could not disconnect from MQTT broker");

		} catch (InterruptedException e) {
			log.error("Could not retrieve message from broker, interrupted thread");
			throw new MQTTException("Could not retrieve message from broker, interrupted thread");

		} catch (ExecutionException e) {
			log.error("Could not get result from retrieved message at CompletableFuture object");
			throw new MQTTException("Could not get result from retrieved message at CompletableFuture object");

		} catch (TimeoutException e) {
			log.error("Timeout, could not retrieve session key");
			throw new MQTTException("Timeout, could not retrieve session key");

		}

	}

	/**
	 * Closses MQTT session.
	 *
	 **/
	public void disconnect() {
		// SSAP LEAVE MESSAGE
		final SSAPMessage<SSAPBodyLeaveMessage> leave = new SSAPMessage<SSAPBodyLeaveMessage>();
		leave.setDirection(SSAPMessageDirection.REQUEST);
		leave.setMessageType(SSAPMessageTypes.LEAVE);
		leave.setSessionKey(this.sessionKey);
		leave.setBody(new SSAPBodyLeaveMessage());

		try {
			// MQTT LEAVE MESSAGE
			final MqttMessage mqttLeave = new MqttMessage();
			mqttLeave.setPayload(SSAPJsonParser.getInstance().serialize(leave).getBytes());
			this.client.publish(topic, mqttLeave);
			log.info("Disconnecting from the server");

			this.client.disconnect();
			this.sessionKey = null;
			this.client = null;
		} catch (SSAPParseException e) {
			log.error("Could not parse SSAP message");
			throw new MQTTException("Could not parse SSAP message");

		} catch (MqttException e) {
			log.error("Could not connect to MQTT broker");
			throw new MQTTException("Could not disconnect from MQTT broker");
		}
		log.info("Session clossed");

	}
}
