package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoquetteBrokerLifeCycleTest {

	@Test
	public void test_lifecycle() throws MqttException {
		final MoquetteBroker broker = new MoquetteBroker();
		broker.setHost("localhost");
		broker.setPort("11883");
		broker.setPool(1);
		broker.setStore("moquette_tes.mamdb");
		broker.init();

		//		Assert.assertTrue(broker.getServer().getConnectionsManager().getSessions().size() == 0);

		final String broker_url       = "tcp://localhost:11883";
		final MemoryPersistence persistence = new MemoryPersistence();
		final MqttClient client = new MqttClient(broker_url, "test", persistence);
		final MqttConnectOptions connOpts = new MqttConnectOptions();

		connOpts.setCleanSession(true);
		client.connect(connOpts);

		Assert.assertTrue(broker.getServer().getConnectionsManager().getSessions().size() > 0);

		broker.stopServer();
	}
}
