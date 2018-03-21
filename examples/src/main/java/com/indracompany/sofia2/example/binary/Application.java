package com.indracompany.sofia2.example.binary;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.client.MQTTClient;
import com.indracompany.sofia2.ssap.binary.Mime;
import com.indracompany.sofia2.ssap.util.BinarySerializer;

public class Application {

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		
		MQTTClient client = new MQTTClient("tcp://localhost:1883");
		String token = "eca34bf3ab1348419f8a5fd61676942f";
		String clientPlatform = "IncidenciasApp";
		String clientPlatformInstance = clientPlatform + ":mqtt";
		String ontology = "BinaryOnt";
		int timeout = 5;
		String sessionKey = client.connect(token, clientPlatform, clientPlatformInstance, timeout);
		//String jsonData ="{\"Restaurant\":{\"address\":{\"building\":null,\"coordinates\":{\"0\":null,\"1\":null},\"street\":null,\"zipcode\":null},\"borough\":null,\"cuisine\":null,\"grades\":{\"date\":\"6\",\"grade\":null,\"score\":null},\"name\":null,\"restaurant_id\":null}}"; 
		
		
		JsonNode instance = mapper.createObjectNode();
		BinarySerializer  serializer = new BinarySerializer();	
		String pathFile = "C:/Users/fjgcornejo/Pictures/logo_S4C.png";
		
		JsonNode image = mapper.createObjectNode();
		try {
			image = serializer.getJsonBinary("Image", new File(pathFile), Mime.IMAGE_PNG);
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		JsonNode object = mapper.createObjectNode();
		
		((ObjectNode) object).put("Name", "Logo Select 4 cities");
		((ObjectNode) object).set("Image", image);
		
		((ObjectNode) instance).set(ontology, object);
		System.out.println(instance.toString());
		client.publish(ontology, instance.toString(), timeout);
		client.disconnect();
	}
}
