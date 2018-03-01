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
	List<DeviceSimulation> getAllSimulations();
	DeviceSimulation getSimulatorByIdentification(String identification);
	DeviceSimulation createSimulation(String identification, int interval, String userId, String json) throws JsonProcessingException, IOException;
	void save(DeviceSimulation simulation);
	DeviceSimulation getSimulationById(String id);
	List<DeviceSimulation> getSimulationsForUser(String userId);
}
