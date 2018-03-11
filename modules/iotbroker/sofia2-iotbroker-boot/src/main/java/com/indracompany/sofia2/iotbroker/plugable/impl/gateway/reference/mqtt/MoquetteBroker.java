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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;

import io.moquette.BrokerConstants;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
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
		prefix="sofia2.iotbroker.plugbable.gateway.moquette",
		name="enable",
		havingValue="true"
		)
@Slf4j
@Component
public class MoquetteBroker {

	@Value("${sofia2.iotbroker.plugbable.gateway.moquette.port:1883}")
	private String port;

	@Value("${sofia2.iotbroker.plugbable.gateway.moquette.pool:10}")
	private int pool;

	@Value("${sofia2.iotbroker.plugbable.gateway.moquette.host:localhost}")
	private String host;

	@Value("${sofia2.iotbroker.plugbable.gateway.moquette.store:moquette_store.mapdb}")
	private String store;

	@Value("${sofia2.iotbroker.plugbable.gateway.moquette.outbound_topic:}")
	private String outbound_topic;

	@Value("${sofia2.iotbroker.plugbable.gateway.moquette.store:moquette_store.inbound_topic}")
	private String inbound_topic;



	@Autowired
	protected MessageProcessor processor;

	private final Server server = new Server();
	private Properties m_properties;

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

			subscriptor.addSubscriptionListener("mqtt_gateway",  (s) -> System.out.println("mqtt_gateway fake processing") );

			final ByteBuf byteBuf = msg.getPayload();
			final String playload = new String(ByteBufUtil.getBytes(byteBuf), Charset.forName("UTF-8"));
			final SSAPJsonParser parser = new SSAPJsonParser();
			final String response = MoquetteBroker.this.processor.process(playload);

			final MqttPublishMessage message = MqttMessageBuilders.publish()
					.topicName(outbound_topic + "/" + msg.getClientID())
					.retained(false)
					.qos(MqttQoS.EXACTLY_ONCE)
					.payload(Unpooled.copiedBuffer(response.getBytes()))
					.build();

			MoquetteBroker.this.getServer().internalPublish(message, msg.getClientID());
			System.out.println(response);
		}
	}

	@PostConstruct
	public void init() {
		try {

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
			final List<? extends InterceptHandler> messsageHandlers = Collections.singletonList(new PublisherListener());
			server.startServer(memoryConfig);
			server.addInterceptHandler(new PublisherListener());

			try {
				Thread.sleep(2000);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@PreDestroy
	public  void stopServer()  {
		if (this.server == null) {
			log.warn("The Moquette server has already been stopped.");
			return;
		}
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
}
