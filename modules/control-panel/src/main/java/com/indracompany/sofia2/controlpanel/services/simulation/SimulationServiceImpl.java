package com.indracompany.sofia2.controlpanel.services.simulation;



import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SimulationServiceImpl implements SimulationService  {

	@Override
	public void simulate(String json) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		
		int fieldNumber = rootNode.path("fields").size();
		
		JsonNode nodeInsert = mapper.createObjectNode();
		
		for(int i=0; i<fieldNumber; i++) {
			JsonNode simulatorNode = rootNode.path("fields").path(i).get("simulator");
			String type = simulatorNode.get("type").asText();
			//if(type.contains("NUMBER") || type.contains("INTEGER"))))
		}
	}

	
	
}
