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
package com.indracompany.sofia2.client.app;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.indracompany.sofia2.client.MQTTClient;
import com.indracompany.sofia2.client.MQTTClient.QUERY_TYPE;
import com.indracompany.sofia2.client.SubscriptionListener;
import com.indracompany.sofia2.client.configuration.MQTTSecureConfiguration;

public class ClientsApplication {

	public static void main(String[] args) throws InterruptedException, IOException, UnrecoverableKeyException,
			KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException {

		String url = "ssl://localhost:8883";
		String keyStorePath = "S:\\sofia2-s4c\\modules\\iotbroker\\sofia2-iotbroker-boot\\src\\main\\resources\\clientdevelkeystore.jks";
		String keyStorePassword = "changeIt!";

		if (args.length == 3) {
			url = args[0];
			keyStorePath = args[1];
			keyStorePassword = args[2];

		} else if (args.length == 1) {
			url = args[0];
		}

		MQTTSecureConfiguration sslConfig = new MQTTSecureConfiguration(keyStorePath, keyStorePassword);
		MQTTClient clientSecure = new MQTTClient(url, sslConfig);

		int timeout = 50;
		String token = "e7ef0742d09d4de5a3687f0cfdf7f626";
		String clientPlatform = "Ticketing App";
		String clientPlatformInstance = clientPlatform + ":MQTT";
		String ontology = "HelsinkiPopulation";
		clientSecure.connect(token, clientPlatform, clientPlatformInstance, timeout);

		String jsonData = "{ \"Helsinki\":{\"year\":1993, \"population\" : 3500, \"population_women\":1500, \"population_men\":2000}}";

		clientSecure.publish(ontology, jsonData, timeout);

		String subsId = clientSecure.subscribe(ontology, "SELECT * FROM " + ontology, QUERY_TYPE.SQL, timeout,
				new SubscriptionListener() {

					@Override
					public void onMessageArrived(String message) {
						System.out.println(message);

					}

				});

		clientSecure.publish(ontology, jsonData, timeout);

		Thread.sleep(5000);
		clientSecure.unsubscribe(subsId);

		clientSecure.disconnect();

		System.exit(0);

	}
}
