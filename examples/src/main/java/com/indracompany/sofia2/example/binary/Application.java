package com.indracompany.sofia2.example.binary;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.client.MQTTClient;
import com.indracompany.sofia2.client.RestClient;
import com.indracompany.sofia2.ssap.binary.Mime;
import com.indracompany.sofia2.ssap.util.BinarySerializer;

public class Application {

	public static void main(String[] args) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		RestClient client = new RestClient("http://localhost:8081/iotbroker");
		String token = "eca34bf3ab1348419f8a5fd61676942f";
		String clientPlatform = "IncidenciasApp";
		String clientPlatformInstance = clientPlatform + ":REST";
		String ontology = "BinaryOnt";
	
		client.connect(token, clientPlatform, clientPlatformInstance);	
		
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
		
		List<JsonNode> instances = client.getOntologyInstances(ontology);
		if(instances.size() == 0)
			client.insertInstance(ontology, instance.toString());

		
		for(JsonNode ontologyInstance : instances) {
			
			serializer.binaryJsonToFile(ontologyInstance.path(ontology).path("Image").get("Image"), "C:/Users/fjgcornejo/Pictures/Binary");
		}
		

	}
}
