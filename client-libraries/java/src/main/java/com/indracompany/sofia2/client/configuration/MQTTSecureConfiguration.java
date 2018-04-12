package com.indracompany.sofia2.client.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class MQTTSecureConfiguration {

	private String keyStorePath;
	private String password;
	private final String DEFAULT_KEYSTORE_PATH = getClass().getResource("/clientdevelkeystore.jks").getPath();
	private final String DEFAULT_KEYSTORE_PASSWORD = "changeIt!";

	public MQTTSecureConfiguration(String keyStorePath, String password) {
		if (keyStorePath == null || password == null) {
			this.keyStorePath = DEFAULT_KEYSTORE_PATH;
			this.password = DEFAULT_KEYSTORE_PASSWORD;
		} else {
			this.keyStorePath = keyStorePath;
			this.password = password;
		}

	}

	public SSLSocketFactory configureSSLSocketFactory() throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {

		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream jksInputStream = new FileInputStream(this.keyStorePath);
		ks.load(jksInputStream, this.password.toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, this.password.toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		SSLContext sc = SSLContext.getInstance("TLS");
		TrustManager[] trustManagers = tmf.getTrustManagers();
		sc.init(kmf.getKeyManagers(), trustManagers, null);

		SSLSocketFactory ssf = sc.getSocketFactory();
		return ssf;
	}

}
