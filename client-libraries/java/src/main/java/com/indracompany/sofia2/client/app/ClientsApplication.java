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

import com.indracompany.sofia2.client.MQTTClient;
import com.indracompany.sofia2.client.MQTTClient.QUERY_TYPE;
import com.indracompany.sofia2.client.SubscriptionListener;

public class ClientsApplication {

	public static void main(String[] args) throws InterruptedException, IOException {

		MQTTClient client = new MQTTClient("tcp://localhost:1883");
		String token = "a5bdc70f74da414eaa7d72daac397626";
		String clientPlatform = "DeviceTemp";
		String clientPlatformInstance = clientPlatform + ":MQTT";
		String ontology = "TempOnt";
		int timeout = 50;
		String sessionKey = client.connect(token, clientPlatform, clientPlatformInstance, timeout);
		String jsonData = "{\"TempOnt\":{ \"Temp\":28.6}}";
		String subsId = client.subscribe(ontology, "SELECT * FROM TempOnt", QUERY_TYPE.SQL, timeout,
				new SubscriptionListener() {

					@Override
					public void onMessageArrived(String message) {
						System.out.println(message);

					}

				});
		// client.unsubscribe(subsId);
		Thread.sleep(50000);
		// client.publish("TempOnt", jsonData, timeout);
		//
		// while(true);
		client.disconnect();

		// RestClient restClient = new RestClient("http://localhost:8081/iotbroker");
		// String sessionKey = restClient.connect(token, clientPlatform,
		// clientPlatformInstance);
		// String instance = "{\"BinaryOnt\":{
		// \"Name\":\"string\",\"Image\":{\"data\":\"string\",\"media\":{\"name\":\"fichero.pdf\",\"storageArea\":\"SERIALIZED\",\"binaryEncoding\":\"Base64\",\"mime\":\"application/pdf\"}}}}";
		// restClient.insertInstance(ontology, instance);
		// restClient.disconnect();
		//
	}
}
