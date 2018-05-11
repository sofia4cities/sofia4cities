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

	public MQTTSecureConfiguration(String keyStorePath, String password) {
		this.keyStorePath = keyStorePath;
		this.password = password;

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
