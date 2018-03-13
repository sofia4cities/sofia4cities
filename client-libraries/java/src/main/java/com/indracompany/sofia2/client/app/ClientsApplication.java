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

import com.indracompany.sofia2.client.MQTTClient;

public class ClientsApplication {

	public static void main(String[] args) throws InterruptedException {

		MQTTClient client = new MQTTClient("tcp://localhost:1883");
		String token = "3f77b9a6af8642c683e5d786217afb0b";
		String clientPlatform = "RestaurantsCp";
		String clientPlatformInstance = clientPlatform + ":mqtt";
		int timeout = 5;
		String sessionKey = client.connect(token, clientPlatform, clientPlatformInstance, timeout);
		String jsonData ="{\"Restaurant\":{\"address\":{\"building\":null,\"coordinates\":{\"0\":null,\"1\":null},\"street\":null,\"zipcode\":null},\"borough\":null,\"cuisine\":null,\"grades\":{\"date\":\"6\",\"grade\":null,\"score\":null},\"name\":null,\"restaurant_id\":null}}"; 
		Thread.sleep(5000);
		client.publish("Restaurant", jsonData, timeout);
		Thread.sleep(5000);
		
		client.disconnect();
	}
}
