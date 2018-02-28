package com.indracompany.sofia2.config.services.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.DeviceSimulation;
import com.indracompany.sofia2.config.model.DeviceSimulation.Type;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DeviceSimulationRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;

@Service
public class DeviceSimulationServiceImpl implements DeviceSimulationService {

	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private ClientPlatformRepository clientPlatformRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private DeviceSimulationRepository deviceSimulationRepository;

	@Override
	public List<String> getClientsForUser(String userId) {
		List<String> clientIdentifications = new ArrayList<String>();
		List<ClientPlatform> clients = new ArrayList<ClientPlatform>();
		User user = this.userService.getUser(userId);
		if (user.getRole().getName().equals(Role.Type.ROLE_ADMINISTRATOR.name())) {

			clients = this.clientPlatformRepository.findAll();

		} else {

			clients = this.userService.getClientsForUser(user);
		}
		for (ClientPlatform client : clients) {

			clientIdentifications.add(client.getIdentification());
		}
		return clientIdentifications;
	}

	@Override
	public List<String> getClientTokensIdentification(String clientPlatformId) {
		ClientPlatform clientPlatform = this.clientPlatformRepository.findByIdentification(clientPlatformId);
		List<String> tokens = new ArrayList<String>();
		for (Token token : this.tokenRepository.findByClientPlatform(clientPlatform)) {
			tokens.add(token.getToken());
		}
		return tokens;
	}

	@Override
	public List<String> getClientOntologiesIdentification(String clientPlatformId) {
		List<String> ontologies = new ArrayList<String>();
		for (Ontology ontology : this.ontologyService
				.getOntologiesByClientPlatform(this.clientPlatformRepository.findByIdentification(clientPlatformId))) {
			ontologies.add(ontology.getIdentification());
		}
		return ontologies;
	}

	@Override
	public List<String> getSimulatorTypes() {
		List<String> simulators = new ArrayList<String>();
		for (Type type : DeviceSimulation.Type.values()) {
			simulators.add(type.name());
		}
		return simulators;
	}

	@Override
	public List<DeviceSimulation> getAllSimulators() {
		List<DeviceSimulation> simulators = this.deviceSimulationRepository.findAll();
		return simulators;
	}

	@Override
	public DeviceSimulation getSimulatorByIdentification(String identification) {
		return this.deviceSimulationRepository.findByIdentification(identification);
	}

	@Override
	public String getDeviceSimulationJson(int interval, String clientPlatform, String token, String ontology,
			String jsonMap) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode();
		((ObjectNode) rootNode).put("interval", interval);
		((ObjectNode) rootNode).put("clientPlatform", clientPlatform);
		((ObjectNode) rootNode).put("token", token);
		((ObjectNode) rootNode).put("ontology", ontology);
		((ObjectNode) rootNode).set("fields", mapper.readTree(jsonMap));
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
	}

	@Override
	public void createSimulation(String identification, String json) {
		
		DeviceSimulation simulation = new DeviceSimulation();
		simulation.setIdentification(identification);
		simulation.setJson(json);
		this.deviceSimulationRepository.save(simulation);
		
	}
}
