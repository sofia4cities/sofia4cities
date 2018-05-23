/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.client;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.client.configuration.MQTTSecureConfiguration;
import com.indracompany.sofia2.client.exception.MQTTException;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLeaveMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLogMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodySubscribeMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUnsubscribeMessage;
import com.indracompany.sofia2.ssap.enums.SSAPLogLevel;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;
import com.indracompany.sofia2.ssap.enums.SSAPStatusType;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MQTTClient {

	private MemoryPersistence persistence = new MemoryPersistence();
	private String brokerURI;
	private CompletableFuture<String> completableFutureMessage = new CompletableFuture<>();
	private String sessionKey;
	private String topic = "/message";
	private String topic_message = "/topic/message";
	private String topic_subscription = "/topic/subscription";
	private String topic_subscription_command = "/topic/command";
	private MqttClient client;
	private Map<String, SubscriptionListener> subscriptions = new HashMap<String, SubscriptionListener>();
	private MQTTSecureConfiguration sslConfig;
	private ObjectMapper mapper = new ObjectMapper();

	public enum QUERY_TYPE {
		NATIVE, SQL
	}

	public enum LOG_LEVEL {
		ERROR, INFO, WARNING
	}

	public enum STATUS_TYPE {
		OK, ERROR, WARNING, COMPLETED, EXECUTED, UP, DOWN, CRITICAL
	}

	public MQTTClient(String brokerURI) {
		this.brokerURI = brokerURI;
	}

	public MQTTClient(String brokerURI, MQTTSecureConfiguration sslConfig) {
		this.brokerURI = brokerURI;
		this.sslConfig = sslConfig;

	}

	/**
	 * Creates a MQTT session.
	 *
	 * @param token
	 *            The token associated with the device/template
	 * @param deviceTemplate
	 *            The device template identification
	 * @param device
	 *            The identification of the device
	 * @param tags
	 *            Tags for the device, separated by commas
	 * @param timeout
	 *            Time in seconds for waiting response from Broker
	 * @param commandConfiguration
	 *            A JSON object containing command configuration for this device
	 * @return The session key for the session established between client and IoT
	 *         Broker
	 * @throws MQTTException
	 */

	@SuppressWarnings("unchecked")
	public String connect(String token, String deviceTemplate, String device, String tags, int timeout,
			JsonNode commandConfiguration) throws MQTTException {

		try {
			// JOIN SSAP MESSAGE FOR SESSIONKEY
			final SSAPMessage<SSAPBodyJoinMessage> join = new SSAPMessage<SSAPBodyJoinMessage>();
			join.setDirection(SSAPMessageDirection.REQUEST);
			join.setMessageType(SSAPMessageTypes.JOIN);
			SSAPBodyJoinMessage body = new SSAPBodyJoinMessage();
			body.setDeviceTemplate(deviceTemplate);
			body.setDevice(device);
			body.setToken(token);
			body.setDeviceConfiguration(commandConfiguration);
			body.setTags(tags);
			join.setBody(body);

			// Connect client MQTT

			this.client = new MqttClient(brokerURI, device, persistence);

			// Unsecure connection
			if (this.sslConfig == null) {
				// log.info("Connecting to broker MQTT without secure connection");
				this.client.connect();

			} else {
				// log.info("Connecting to broker MQTT with SSL");
				MqttConnectOptions options = new MqttConnectOptions();
				options.setSocketFactory(this.sslConfig.configureSSLSocketFactory());
				this.client.connect(options);
			}

			// log.info("Connecting to broker MQTT at " + brokerURI);

			this.client.subscribe(topic_message + "/" + client.getClientId(), new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					final String response = new String(message.getPayload());
					// log.info("Message arrived " + response);
					completableFutureMessage.complete(response);
					completableFutureMessage = new CompletableFuture<>();
				}
			});

			// SEND JOIN VIA MQTT
			final MqttMessage mqttJoin = new MqttMessage();
			mqttJoin.setPayload(SSAPJsonParser.getInstance().serialize(join).getBytes());
			this.client.publish(topic, mqttJoin);
			// log.info("Trying to get Session Key...");

			// GET JOIN RESPONSE
			String joinResponse = completableFutureMessage.get(timeout, TimeUnit.SECONDS);
			SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(joinResponse);
			if (response.getSessionKey() != null) {
				this.sessionKey = response.getSessionKey();
				// log.info("Session Key established: " + this.sessionKey);
			} else
				throw new MQTTException("Could not stablish connection, error code is "
						+ response.getBody().getErrorCode() + ":" + response.getBody().getError());

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
		} catch (Exception e) {
			throw new MQTTException("Error: " + e);
		}

		return this.sessionKey;
	}

	/**
	 * Publishes a message through MQTT session.
	 * 
	 * @param listener
	 *            Callback listener that will handle command messages. Resulting
	 *            message will have JSON structure, with parameters: Command,
	 *            CommandId, Params
	 * 
	 */
	public void subscribeCommands(SubscriptionListener listener) {
		try {
			this.client.subscribe(topic_subscription_command + "/" + this.sessionKey, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					// log.info("Command message available");
					final String response = new String(message.getPayload());
					delegateCommandMessage(response, listener);

				}
			});
		} catch (MqttException e) {
			log.error("Could not connect to MQTT broker");
			throw new MQTTException("Could not connect to MQTT broker");
		}
	}

	/**
	 * Publishes a message through MQTT session.
	 *
	 * @param ontology
	 *            Ontology to be subscribed
	 * @param query
	 *            Query (to match conditions)
	 * @param queryType
	 *            Type of query: NATIVE, SQL
	 * @param timeout
	 *            Time in seconds for waiting subscription ACK
	 * @param listener
	 *            Callback listener that will handle subscription messages.
	 *            Resulting message will have JSON structure, with parameters:
	 *            ontology and data.
	 * @return The subscription ID
	 */

	public String subscribe(String ontology, String query, QUERY_TYPE queryType, int timeout,
			SubscriptionListener listener) {
		String subscriptionId = null;
		final SSAPMessage<SSAPBodySubscribeMessage> subscription = new SSAPMessage<SSAPBodySubscribeMessage>();
		subscription.setSessionKey(this.sessionKey);
		final SSAPBodySubscribeMessage body = new SSAPBodySubscribeMessage();
		body.setOntology(ontology);
		body.setQueryType(SSAPQueryType.valueOf(queryType.name()));
		body.setQuery(query);
		subscription.setBody(body);
		subscription.setDirection(SSAPMessageDirection.REQUEST);
		subscription.setMessageType(SSAPMessageTypes.SUBSCRIBE);

		try {
			final String subscriptionStr = SSAPJsonParser.getInstance().serialize(subscription);

			final MqttMessage message = new MqttMessage(subscriptionStr.getBytes());
			client.publish(topic, message);
			// log.info("Subscribed to query " + query);
			// GET SUBS RESPONSE
			String subsResponse = completableFutureMessage.get(timeout, TimeUnit.SECONDS);
			SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(subsResponse);
			subscriptionId = response.getBody().getData().at("/subscriptionId").asText();

			if (!subscriptions.containsKey(subscriptionId))
				subscriptions.put(subscriptionId, listener);

			this.client.subscribe(topic_subscription + "/" + this.sessionKey, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {

					final String response = new String(message.getPayload());
					// log.info("Subscription message available" + response);
					delegateMessageFromSubscription(response);

				}
			});

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

		return subscriptionId;
	}

	/**
	 * Publishes a message through MQTT session.
	 *
	 * @param subscriptionId
	 *
	 * @return The subscription ID
	 */

	public void unsubscribe(String subscriptionId) {

		final SSAPMessage<SSAPBodyUnsubscribeMessage> unsubscribe = new SSAPMessage<SSAPBodyUnsubscribeMessage>();
		unsubscribe.setSessionKey(this.sessionKey);

		final SSAPBodyUnsubscribeMessage body = new SSAPBodyUnsubscribeMessage();
		body.setSubscriptionId(subscriptionId);

		unsubscribe.setBody(body);
		unsubscribe.setDirection(SSAPMessageDirection.REQUEST);
		unsubscribe.setMessageType(SSAPMessageTypes.UNSUBSCRIBE);

		try {
			final String unsubscriptionStr = SSAPJsonParser.getInstance().serialize(unsubscribe);

			final MqttMessage message = new MqttMessage(unsubscriptionStr.getBytes());
			client.publish(topic, message);

			// GET SUBS RESPONSE
			String subsResponse = completableFutureMessage.get();
			SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(subsResponse);
			if (response.getBody().isOk()) {
				// log.info("Unsubscribed successfully");
				this.subscriptions.remove(subscriptionId);
				client.unsubscribe(topic_subscription + "/" + this.sessionKey);
			} else
				log.error("Could not unsubscribe");

		} catch (MqttException e) {
			log.error("Could not connect to MQTT broker");
			throw new MQTTException("Could not connect to MQTT broker");
		} catch (InterruptedException e) {
			log.error("Could not retrieve message from broker, interrupted thread");
			throw new MQTTException("Could not retrieve message from broker, interrupted thread");
		} catch (ExecutionException e) {
			log.error("Could not get result from retrieved message at CompletableFuture object");
			throw new MQTTException("Could not get result from retrieved message at CompletableFuture object");
		} catch (SSAPParseException e) {
			log.error("Could not parse SSAP message");
			throw new MQTTException("Could not parse SSAP message");
		}
	}

	/**
	 * Publishes a message through MQTT session.
	 *
	 * @param message
	 *            Log message
	 * @param latitude
	 *            Latitude (geolocation)
	 * @param longitude
	 *            Longitude (geolocation)
	 * @param status
	 *            Status of the device
	 * @param level
	 *            Log level
	 * @param commandId
	 *            Identification of the command associated to this log message
	 * @param timeout
	 *            Time in seconds for waiting response from Broker
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void logCommand(String message, double latitude, double longitude, STATUS_TYPE status, LOG_LEVEL level,
			String commandId, int timeout) {
		final SSAPMessage<SSAPBodyLogMessage> logMessage = new SSAPMessage<>();
		final SSAPBodyLogMessage body = new SSAPBodyLogMessage();
		logMessage.setDirection(SSAPMessageDirection.REQUEST);
		logMessage.setMessageType(SSAPMessageTypes.LOG);
		logMessage.setSessionKey(this.sessionKey);
		Point2D.Double coordinates = new Point2D.Double(latitude, longitude);
		coordinates.setLocation(coordinates);
		body.setCoordinates(coordinates);
		body.setLevel(SSAPLogLevel.valueOf(level.name()));
		body.setMessage(message);
		body.setCommandId(commandId);
		body.setStatus(SSAPStatusType.valueOf(status.name()));
		logMessage.setBody(body);

		final MqttMessage mqttLog = new MqttMessage();
		try {
			mqttLog.setPayload(SSAPJsonParser.getInstance().serialize(logMessage).getBytes());
			this.client.publish(topic, mqttLog);
			String response = completableFutureMessage.get(timeout, TimeUnit.SECONDS);
			SSAPMessage<SSAPBodyReturnMessage> responseSSAP = SSAPJsonParser.getInstance().deserialize(response);
			if (responseSSAP.getBody().isOk())
				log.debug("Log message published");
			else {

				throw new MQTTException("Could not publish message \nError Code: "
						+ responseSSAP.getBody().getErrorCode() + ":\n" + responseSSAP.getBody().getError());
			}
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
			throw new MQTTException("Timeout");
		}
	}

	/**
	 * Publishes a message through MQTT session.
	 *
	 * @param message
	 *            Log message
	 * @param latitude
	 *            Latitude (geolocation)
	 * @param longitude
	 *            Longitude (geolocation)
	 * @param status
	 *            Status of the device
	 * @param level
	 *            Log level
	 *
	 * @param timeout
	 *            Time in seconds for waiting response from Broker
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void log(String message, double latitude, double longitude, STATUS_TYPE status, LOG_LEVEL level,
			int timeout) {
		final SSAPMessage<SSAPBodyLogMessage> logMessage = new SSAPMessage<>();
		final SSAPBodyLogMessage body = new SSAPBodyLogMessage();
		logMessage.setDirection(SSAPMessageDirection.REQUEST);
		logMessage.setMessageType(SSAPMessageTypes.LOG);
		logMessage.setSessionKey(this.sessionKey);
		Point2D.Double coordinates = new Point2D.Double(latitude, longitude);
		coordinates.setLocation(coordinates);
		body.setCoordinates(coordinates);
		body.setLevel(SSAPLogLevel.valueOf(level.name()));
		body.setMessage(message);
		body.setStatus(SSAPStatusType.valueOf(status.name()));
		logMessage.setBody(body);

		final MqttMessage mqttLog = new MqttMessage();
		try {
			mqttLog.setPayload(SSAPJsonParser.getInstance().serialize(logMessage).getBytes());
			this.client.publish(topic, mqttLog);
			String response = completableFutureMessage.get(timeout, TimeUnit.SECONDS);
			SSAPMessage<SSAPBodyReturnMessage> responseSSAP = SSAPJsonParser.getInstance().deserialize(response);
			if (responseSSAP.getBody().isOk())
				log.debug("Log message published");
			else {

				throw new MQTTException("Could not publish message \nError Code: "
						+ responseSSAP.getBody().getErrorCode() + ":\n" + responseSSAP.getBody().getError());
			}
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
			throw new MQTTException("Timeout");
		}
	}

	@SuppressWarnings("unchecked")
	public void publish(String ontology, String jsonData, int timeout) {

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

			String insertResponse = completableFutureMessage.get(timeout, TimeUnit.SECONDS);

			SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance().deserialize(insertResponse);
			if (response.getBody().isOk())
				log.debug("Message published");
			else {
				throw new MQTTException("Could not publish message \nError Code: " + response.getBody().getErrorCode()
						+ ":\n" + response.getBody().getError());
			}

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
	 * Closes MQTT session.
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

	private void delegateMessageFromSubscription(String message) throws JsonProcessingException, IOException {

		JsonNode indMsg = mapper.createObjectNode();
		((ObjectNode) indMsg).put("ontology", mapper.readTree(message).get("body").get("ontology").asText());
		((ObjectNode) indMsg).set("data", mapper.readTree(message).get("body").get("data"));
		String subsId = mapper.readTree(message).get("body").get("subsciptionId").asText();
		SubscriptionListener listener = this.subscriptions.get(subsId);
		new Thread(new Runnable() {
			public void run() {
				listener.onMessageArrived(indMsg.toString());
			}
		}).start();

	}

	private void delegateCommandMessage(String message, SubscriptionListener listener) throws IOException {
		JsonNode cmdMsg = mapper.createObjectNode();
		String command = mapper.readTree(message).get("body").get("command").asText();
		JsonNode params = mapper.readTree(message).get("body").get("params");
		String commandId = mapper.readTree(message).get("body").get("commandId").asText();
		((ObjectNode) cmdMsg).put("commandId", commandId);
		((ObjectNode) cmdMsg).put("command", command);
		((ObjectNode) cmdMsg).set("params", params);
		// log.info(cmdMsg.toString());
		new Thread(new Runnable() {
			public void run() {
				listener.onMessageArrived(cmdMsg.toString());
			}
		}).start();

	}

}
