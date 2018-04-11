package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.mqtt;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.processor.DeviceManager;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoquetteSSLTest {

	String topic        = "/message";
	String content      = "Message from MqttPublishSample";
	int qos             = 2;
	String broker_url       = "ssl://localhost:8883";
	String clientId     = "JavaSample";
	String jksWithCorrectCertificate = "src/main/resources/clientdevelkeystore.jks";
	String jksWithWrongCertificate = "src/main/resources/nocertclientdevelkeystore.jks";
	String clientkeyStorePassword = "changeIt!";
	String clientkeyManagerPassword = "changeIt!";
	MemoryPersistence persistence = new MemoryPersistence();

	@MockBean
	SecurityPluginManager securityPluginManager;

	@MockBean
	DeviceManager deviceManager;

	private CompletableFuture<String> completableFutureMessage;
	private CompletableFuture<String> completableFutureIndication;
	private CompletableFuture<String> completableFutureCommand;
	private IoTSession session = null;

	Person subject;

	private void securityMocks() {
		session = PojoGenerator.generateSession();

		when(deviceManager.registerActivity(any(), any(), any(), any())).thenReturn(true);
		when(securityPluginManager.authenticate(any(), any(), any(), any())).thenReturn(Optional.of(session));
		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}

	@Before
	public void setUp() {
		securityMocks();
	}

	@Test
	public void given_MqttBrokerWithSSLSupport_When_ClientWithCorrentCredential_Then_ConnectionIsGranted() throws Exception {
		final MqttClient client = new MqttClient(broker_url, clientId, persistence);
		final MqttConnectOptions opts = new MqttConnectOptions();

		final SSLSocketFactory ssf = configureSSLSocketFactory(jksWithCorrectCertificate);
		final MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);
		client.connect(options);
		Assert.assertTrue(client.isConnected());

	}

	@Test(expected=MqttException.class)
	public void given_MqttBrokerWithSSLSupport_When_ClientWithCorrentCredential_Then_ConnectionIsRevoked() throws Exception {
		final MqttClient client = new MqttClient(broker_url, clientId, persistence);

		final SSLSocketFactory ssf = configureSSLSocketFactory(jksWithWrongCertificate);
		final MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);
		client.connect(options);
		fail("Expected an MqttException to be thrown");
	}

	private SSLSocketFactory configureSSLSocketFactory(String keyStore) throws Exception {
		final KeyStore ks = KeyStore.getInstance("JKS");
		final InputStream jksInputStream = new FileInputStream(keyStore);
		ks.load(jksInputStream, clientkeyStorePassword.toCharArray());

		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, clientkeyStorePassword.toCharArray());

		final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		final SSLContext sc = SSLContext.getInstance("TLS");
		final TrustManager[] trustManagers = tmf.getTrustManagers();
		sc.init(kmf.getKeyManagers(), trustManagers, null);

		final SSLSocketFactory ssf = sc.getSocketFactory();
		return ssf;
	}

}