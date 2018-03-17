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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;

import io.moquette.BrokerConstants;
import io.moquette.interception.AbstractInterceptHandler;
//import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(
		prefix="sofia2.iotbroker.plugable.gateway.moquette",
		name="enable",
		havingValue="true"
		)
@Slf4j
@Component
public class MoquetteBroker {

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.port:1883}")
	private String port;

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.pool:10}")
	private int pool;

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.host:localhost}")
	private String host;

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.store:moquette_store.mapdb}")
	private String store;

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.outbound_topic:/topic/message}")
	private String outbound_topic;

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.inbound_topic:/queue/message}")
	private String inbound_topic;

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.subscription_topic:/topic/subscription}")
	private String subscription_topic;

	@Value("${sofia2.iotbroker.plugable.gateway.moquette.command_topic:/topic/command}")
	private String command_topic;



	@Autowired
	protected MessageProcessor processor;

	private final Server server = new Server();

	@Autowired
	GatewayNotifier subscriptor;

	public Server getServer() {
		return server;
	}

	class PublisherListener extends AbstractInterceptHandler {

		@Override
		public String getID() {
			return "ssapInterceptor";
		}

		@Override
		public void onPublish(InterceptPublishMessage msg) {
			final ByteBuf byteBuf = msg.getPayload();
			final String playload = new String(ByteBufUtil.getBytes(byteBuf), Charset.forName("UTF-8"));
			final String response = MoquetteBroker.this.processor.process(playload, getGatewayInfo());

			final MqttPublishMessage message = MqttMessageBuilders.publish()
					.topicName(outbound_topic + "/" + msg.getClientID())
					.retained(false)
					.qos(MqttQoS.EXACTLY_ONCE)
					.payload(Unpooled.copiedBuffer(response.getBytes()))
					.build();

			MoquetteBroker.this.getServer().internalPublish(message, msg.getClientID());
		}
	}

	@PostConstruct
	public void init() {
		try {

			subscriptor.addSubscriptionListener("mqtt_gateway",
					(s) ->{
						String playload="";
						try {
							playload = SSAPJsonParser.getInstance().serialize(s);
						} catch (final SSAPParseException e) {
							log.error("Error serializing indicator message" + e.getMessage());
						}
						final MqttPublishMessage message = MqttMessageBuilders.publish()
								.topicName(subscription_topic + "/" + s.getSessionKey())
								.retained(false)
								.qos(MqttQoS.EXACTLY_ONCE)
								.payload(Unpooled.copiedBuffer(playload.getBytes()))
								.build();

						MoquetteBroker.this.getServer().internalPublish(message, s.getSessionKey());
					});

			subscriptor.addCommandListener("mqtt_gateway",

					(s) -> {
						String playload="";
						try {
							playload = SSAPJsonParser.getInstance().serialize(s);
						} catch (final SSAPParseException e) {
							log.error("Error serializing indicator message" + e.getMessage());
						}
						final MqttPublishMessage message = MqttMessageBuilders.publish()
								.topicName(command_topic + "/" + s.getSessionKey())
								.retained(false)
								.qos(MqttQoS.EXACTLY_ONCE)
								.payload(Unpooled.copiedBuffer(playload.getBytes()))
								.build();

						MoquetteBroker.this.getServer().internalPublish(message, s.getSessionKey());
						return null;
					});

			final Properties brokerProperties = new Properties();
			brokerProperties.put(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME, store);
			brokerProperties.put(BrokerConstants.PORT_PROPERTY_NAME, port);
			brokerProperties.put(BrokerConstants.BROKER_INTERCEPTOR_THREAD_POOL_SIZE, pool);
			brokerProperties.put(BrokerConstants.HOST_PROPERTY_NAME, host);
			//			brokerProperties.put(BrokerConstants.NETTY_CHANNEL_TIMEOUT_SECONDS_PROPERTY_NAME, 5);
			//			brokerProperties.put(BrokerConstants.NETTY_EPOLL_PROPERTY_NAME, "localhost");
			//			brokerProperties.put(BrokerConstants.NETTY_SO_BACKLOG_PROPERTY_NAME, 100);
			//			brokerProperties.put(BrokerConstants.NETTY_SO_KEEPALIVE_PROPERTY_NAME, false);
			//			brokerProperties.put(BrokerConstants.NETTY_SO_REUSEADDR_PROPERTY_NAME, false);
			//			brokerProperties.put(BrokerConstants.NETTY_TCP_NODELAY_PROPERTY_NAME, false);


			final MemoryConfig memoryConfig = new MemoryConfig(brokerProperties);
			server.startServer(memoryConfig);
			server.addInterceptHandler(new PublisherListener());

			try {
				Thread.sleep(2000);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				log.warn("Error initializing MoquetteBroker", e);
			}

		} catch (final IOException e) {
			log.error("Error initializing MoquetteBroker", e);
		}

	}

	@PreDestroy
	public  void stopServer()  {
		log.info("Stopping Moquette server...");
		try {
			this.server.stopServer();
		} catch (final Throwable e) {
			log.error("Unable to stop Moquette server. Cause = {}, errorMessage = {}.", e.getCause(),
					e.getMessage());
			throw new RuntimeException("Unable to stop Moquette server.", e);
		}
		log.info("The Moquette server has been stopped.");

		log.info("Resetting connection limits...");
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getPool() {
		return pool;
	}

	public void setPool(int pool) {
		this.pool = pool;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	private GatewayInfo getGatewayInfo() {
		final GatewayInfo info = new GatewayInfo();
		info.setName("moquette_gateway");
		info.setProtocol("MQTT");

		return info;
	}
}
