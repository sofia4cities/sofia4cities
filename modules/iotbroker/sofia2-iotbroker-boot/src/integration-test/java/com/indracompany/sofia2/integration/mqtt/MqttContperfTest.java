package com.indracompany.sofia2.integration.mqtt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.commons.testing.IntegrationTest;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;

@Category(IntegrationTest.class)
public class MqttContperfTest {

	private MemoryPersistence persistence = new MemoryPersistence();
	private String brokerURI_noSSL = "tcp://localhost:1883";
	private String brokerURI_SSL = "ssl://localhost:8883";
	private CompletableFuture<String> completableFutureMessage = new CompletableFuture<>();
	private String topic = "/message";
	private String topic_message = "/topic/message";
	private String topic_subscription = "/topic/subscription";
	private MqttClient client;
	private String jksPath = "src/main/resources/clientdevelkeystore.jks";
	private String clientkeyStorePassword = "changeIt!";
	private String clientPlatform = "ContPerf device";
	private String clientPlatformInstance = "ContPerfTest";
	private String token = "56686a5a0d7e497d9cafbbbd4b2563ee";
	private String sessionKey;
	private String ontology = "ContPerf";
	private ObjectMapper mapper = new ObjectMapper();

	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	@Before
	public void setUp() throws MqttSecurityException, MqttException, KeyStoreException, CertificateException,
			GeneralSecurityException, IOException {
		this.client = new MqttClient(this.brokerURI_SSL, this.clientPlatform, this.persistence);
		final SSLSocketFactory ssf = configureSSLSocketFactory(this.jksPath);
		final MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);
		this.client.connect(options);

		this.client.subscribe(topic_message + "/" + client.getClientId(), new IMqttMessageListener() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				final String response = new String(message.getPayload());
				completableFutureMessage.complete(response);
				completableFutureMessage = new CompletableFuture<>();
			}
		});
	}

	@Test
	@PerfTest(invocations = 100)
	public void test_MqttConnection() throws MqttPersistenceException, MqttException, SSAPParseException,
			InterruptedException, ExecutionException {
		client.publish(topic, getMqttMessageJoin());
		@SuppressWarnings("unchecked")
		SSAPMessage<SSAPBodyReturnMessage> response = SSAPJsonParser.getInstance()
				.deserialize(completableFutureMessage.get());
		Assert.assertNotNull(response.getSessionKey());
	}

	public MqttMessage getMqttMessageJoin() throws SSAPParseException {
		final MqttMessage message = new MqttMessage();
		final SSAPMessage<SSAPBodyJoinMessage> join = new SSAPMessage<SSAPBodyJoinMessage>();
		join.setDirection(SSAPMessageDirection.REQUEST);
		join.setMessageType(SSAPMessageTypes.JOIN);
		SSAPBodyJoinMessage body = new SSAPBodyJoinMessage();
		body.setClientPlatform(this.clientPlatform);
		body.setClientPlatformInstance(this.clientPlatformInstance);
		body.setToken(this.token);
		join.setBody(body);
		message.setPayload(SSAPJsonParser.getInstance().serialize(join).getBytes());
		return message;
	}

	public MqttMessage getMqttMessageInsert(String data) throws IOException, SSAPParseException {

		JsonNode jsonData = mapper.readTree(data);

		final MqttMessage message = new MqttMessage();
		final SSAPMessage<SSAPBodyInsertMessage> insert = new SSAPMessage<SSAPBodyInsertMessage>();
		final SSAPBodyInsertMessage body = new SSAPBodyInsertMessage();
		insert.setDirection(SSAPMessageDirection.REQUEST);
		insert.setMessageType(SSAPMessageTypes.INSERT);
		insert.setSessionKey(this.sessionKey);
		body.setOntology(this.ontology);
		body.setData(jsonData);
		insert.setBody(body);
		message.setPayload(SSAPJsonParser.getInstance().serialize(insert).getBytes());
		return message;
	}

	private SSLSocketFactory configureSSLSocketFactory(String keyStore)
			throws KeyStoreException, GeneralSecurityException, CertificateException, IOException {
		final KeyStore ks = KeyStore.getInstance("JKS");
		final InputStream jksInputStream = new FileInputStream(keyStore);
		ks.load(jksInputStream, this.clientkeyStorePassword.toCharArray());

		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, this.clientkeyStorePassword.toCharArray());

		final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		final SSLContext sc = SSLContext.getInstance("TLS");
		final TrustManager[] trustManagers = tmf.getTrustManagers();
		sc.init(kmf.getKeyManagers(), trustManagers, null);

		final SSLSocketFactory ssf = sc.getSocketFactory();
		return ssf;
	}
}
