package com.indracompany.sofia2.example.binary;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.client.RestClient;
import com.indracompany.sofia2.ssap.binary.Mime;
import com.indracompany.sofia2.ssap.util.BinarySerializer;

public class BinaryOntologyExample {

	public static void main(String[] args) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		RestClient client = new RestClient("http://localhost:8081/iotbroker");
		String token = "e7ef0742d09d4de5a3687f0cfdf7f626";
		String clientPlatform = "Ticketing App";
		String clientPlatformInstance = clientPlatform + ":REST";
		String ontology = "Ticket";

		client.connect(token, clientPlatform, clientPlatformInstance);

		String pathFile = "S:\\sofia2-s4c\\examples\\binary-ontology-example\\logo_S4C.png";
		String outputPath = "S:\\sofia2-s4c\\examples\\binary-ontology-example\\";

		BinarySerializer serializer = new BinarySerializer();

		JsonNode image = mapper.createObjectNode();
		try {
			image = serializer.getJsonBinary("File", new File(pathFile), Mime.IMAGE_PNG);
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}

		String ontIns = "{ \"Ticket\" : { \"Identification\" : \"Road\", \"Status\" : \"STOPPED\", \"Email\" : \"some@gmail.com\", \"Name\" : \"Javier\", \"Response_via\" : \"Email\", \"Coordinates\" : { \"coordinates\" : { \"0\" : 40.5295428, \"1\" : -3.641471 }, \"type\" : \"Point\" }, \"Type\" : \"ROAD\", \"Description\" : \"Roads in bad shape\" }, \"contextData\" : { \"clientPatform\" : \"Ticketing App\", \"clientPatformInstance\" : \"Ticketing App: Web\", \"clientConnection\" : \"\", \"clientSession\" : \"5c4b87d2-9d43-4cb7-851e-b14b4d023f2b\", \"user\" : \"developer\", \"timezoneId\" : \"Europe/Paris\", \"timestamp\" : \"Wed Apr 11 17:11:36 CEST 2018\" } }";
		JsonNode object = mapper.readTree(ontIns);
		((ObjectNode) object.path(ontology)).set("File", image.path("File"));

		// perform POST
		client.insertInstance(ontology, object.toString());

		// perform GET
		List<JsonNode> instances = client.getOntologyInstances(ontology);

		for (JsonNode ontologyInstance : instances) {
			if (!ontologyInstance.path(ontology).path("File").isMissingNode()) {
				serializer.binaryJsonToFile(ontologyInstance.path(ontology).path("File"), outputPath);

			}
		}

	}
}
