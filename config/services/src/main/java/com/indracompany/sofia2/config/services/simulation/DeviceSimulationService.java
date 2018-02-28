package com.indracompany.sofia2.config.services.simulation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.DeviceSimulation;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;

public interface DeviceSimulationService {

	List<String> getClientsForUser(String userId);
	List<String> getClientTokensIdentification(String clientPlatformId);
	List<String> getClientOntologiesIdentification(String clientPlatformId);
	List<String> getSimulatorTypes();
	List<DeviceSimulation> getAllSimulators();
	DeviceSimulation getSimulatorByIdentification(String identification);
	String getDeviceSimulationJson(int interval,String clientPlatform, String token, String ontology, String jsonMap) throws JsonProcessingException,IOException;
	void createSimulation(String identification, String json);
}
