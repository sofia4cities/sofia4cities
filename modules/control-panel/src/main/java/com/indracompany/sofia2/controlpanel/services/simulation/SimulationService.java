package com.indracompany.sofia2.controlpanel.services.simulation;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface SimulationService {

	void scheduleSimulation(String identification, int interval, String userId, String json);

	String getDeviceSimulationJson(String clientPlatform, String token, String ontology, String jsonMap)
			throws JsonProcessingException, IOException;
}
