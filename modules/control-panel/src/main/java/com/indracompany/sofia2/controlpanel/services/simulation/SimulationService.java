package com.indracompany.sofia2.controlpanel.services.simulation;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indracompany.sofia2.config.model.DeviceSimulation;

public interface SimulationService {

	void createSimulation(String identification, int interval, String userId, String json) throws JsonProcessingException, IOException;

	String getDeviceSimulationJson(String clientPlatform, String token, String ontology, String jsonMap)
			throws JsonProcessingException, IOException;

	void scheduleSimulation(DeviceSimulation deviceSimulation);

	void unscheduleSimulation(DeviceSimulation deviceSimulation);
}
