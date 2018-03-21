package com.indracompany.sofia2.example.binary;

import com.indracompany.sofia2.client.MQTTClient;

public class Application {

	public static void main(String[] args) {
		MQTTClient client = new MQTTClient("tcp://localhost:1883");
		String token = "3f77b9a6af8642c683e5d786217afb0b";
		String clientPlatform = "RestaurantsCp";
		String clientPlatformInstance = clientPlatform + ":mqtt";
		int timeout = 5;
		String sessionKey = client.connect(token, clientPlatform, clientPlatformInstance, timeout);
		String jsonData ="{\"Restaurant\":{\"address\":{\"building\":null,\"coordinates\":{\"0\":null,\"1\":null},\"street\":null,\"zipcode\":null},\"borough\":null,\"cuisine\":null,\"grades\":{\"date\":\"6\",\"grade\":null,\"score\":null},\"name\":null,\"restaurant_id\":null}}"; 

		client.publish("Restaurant", jsonData, timeout);

		
		client.disconnect();
	}
}
