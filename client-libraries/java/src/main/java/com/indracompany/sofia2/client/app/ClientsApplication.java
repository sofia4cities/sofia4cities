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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.client.MQTTClient;
import com.indracompany.sofia2.client.MQTTClient.LOG_LEVEL;
import com.indracompany.sofia2.client.MQTTClient.QUERY_TYPE;
import com.indracompany.sofia2.client.MQTTClient.STATUS_TYPE;
import com.indracompany.sofia2.client.SubscriptionListener;
import com.indracompany.sofia2.client.configuration.MQTTSecureConfiguration;

public class ClientsApplication {

	public static void main(String[] args) throws InterruptedException, IOException, UnrecoverableKeyException,
			KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException {

		String url = "";
		String keyStorePath = "";
		String keyStorePassword = "";
		MQTTSecureConfiguration sslConfig = null;
		MQTTClient clientSecure;

		if (args.length == 3) {
			url = args[0];
			keyStorePath = args[1];
			keyStorePassword = args[2];
			sslConfig = new MQTTSecureConfiguration(keyStorePath, keyStorePassword);

		} else if (args.length == 1) {
			url = args[0];
		}

		if (sslConfig != null) {
			clientSecure = new MQTTClient(url, sslConfig);
		} else {
			clientSecure = new MQTTClient(url);
		}

		int timeout = 50;
		String token = "e7ef0742d09d4de5a3687f0cfdf7f626";
		String clientPlatform = "Ticketing App";
		String clientPlatformInstance = "Raspberry pi 3 : SOUTH HQ";
		String ontology = "LOG_TicketingApp";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode deviceConfig = mapper.readTree(
				"[{\"action_power\":{\"shutdown\":0,\"start\":1,\"reboot\":2}},{\"action_light\":{\"on\":1,\"off\":0}}]");
		clientSecure.connect(token, clientPlatform, clientPlatformInstance, timeout, deviceConfig);

		// String jsonData = "{\"year\":1993, \"population\" : 3500,
		// \"population_women\":1500, \"population_men\":2000}";

		// clientSecure.publish(ontology, jsonData, timeout);
		clientSecure.subscribeCommands(new SubscriptionListener() {

			@Override
			public void onMessageArrived(String message) {
				try {
					JsonNode cmdMsg = mapper.readTree(message);
					generateLogMessage(clientSecure, timeout, cmdMsg);
				} catch (IOException e) {

					e.printStackTrace();
				}

			}

		});
		String subsId = clientSecure.subscribe(ontology, "SELECT * FROM " + ontology, QUERY_TYPE.SQL, timeout,
				new SubscriptionListener() {

					@Override
					public void onMessageArrived(String message) {
						try {
							JsonNode cmdMsg = mapper.readTree(message);
							System.out.println("["
									+ cmdMsg.get("data").get(0).get("DeviceLog").get("location").get("coordinates")
											.get("latitude").asDouble()
									+ "," + cmdMsg.get("data").get(0).get("DeviceLog").get("location")
											.get("coordinates").get("longitude").asDouble()
									+ "]");
							// clientSecure.log(cmdMsg.get("data").get(0).get("DeviceLog").get("message").asText(),
							// cmdMsg.get("data").get(0).get("DeviceLog").get("location").get("coordinates")
							// .get("latitude").asDouble(),
							// cmdMsg.get("data").get(0).get("DeviceLog").get("location").get("coordinates")
							// .get("longitude").asDouble(),
							// STATUS_TYPE
							// .valueOf(cmdMsg.get("data").get(0).get("DeviceLog").get("status").asText()),
							// LOG_LEVEL.valueOf(cmdMsg.get("data").get(0).get("DeviceLog").get("level").asText()),
							// timeout);
						} catch (IOException e) {

							e.printStackTrace();
						}

					}

				});
		// clientSecure.log("Battery critical", 40.407816, -3.714255, STATUS_TYPE.ERROR,
		// LOG_LEVEL.ERROR, timeout);

		// clientSecure.logCommand("Executed command ", 40.448277, -3.490684,
		// STATUS_TYPE.OK, LOG_LEVEL.INFO, "command1",
		// timeout);
		// Thread.sleep(150000);

		// clientSecure.publish(ontology, jsonData, timeout);

		// clientSecure.unsubscribe(subsId);

		// clientSecure.disconnect();

		// System.exit(0);

	}

	public static void generateLogMessage(MQTTClient client, int timeout, JsonNode responseMsg) {
		client.logCommand("Executed command " + responseMsg.get("params").toString(), 40.448277, -3.490684,
				STATUS_TYPE.OK, LOG_LEVEL.INFO, responseMsg.get("commandId").asText(), timeout);
	}
}
