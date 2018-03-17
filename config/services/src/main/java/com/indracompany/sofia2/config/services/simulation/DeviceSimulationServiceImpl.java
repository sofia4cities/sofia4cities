/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	public List<DeviceSimulation> getAllSimulations() {
		List<DeviceSimulation> simulators = this.deviceSimulationRepository.findAll();
		return simulators;
	}

	@Override
	public DeviceSimulation getSimulatorByIdentification(String identification) {
		return this.deviceSimulationRepository.findByIdentification(identification);
	}

	@Override
	public DeviceSimulation createSimulation(String identification, int interval, String userId, String json) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		DeviceSimulation simulation = new DeviceSimulation();
		
		simulation.setOntology(this.ontologyService.getOntologyByIdentification(mapper.readTree(json).path("ontology").asText(),userId));
		simulation.setClientPlatform(this.clientPlatformRepository.findByIdentification(mapper.readTree(json).path("clientPlatform").asText()));
		simulation.setToken(this.tokenRepository.findByToken(mapper.readTree(json).path("token").asText()));
		simulation.setIdentification(identification);
		simulation.setJson(json);
		simulation.setInterval(interval);
		
		int minutes = 0;
		int seconds = interval;
		if (interval >= 0) {
			for (int i = 0; i < (interval / 60); i++) {
				minutes++;
				seconds = seconds - 60;
			}
		}
		if (minutes == 0)
			simulation.setCron("0/" + String.valueOf(seconds) + " * * ? * * *");
		else
			simulation.setCron("0/" + String.valueOf(seconds) + " 0/" + String.valueOf(minutes) + " * ? * * *");
		simulation.setActive(false);
		simulation.setUser(this.userService.getUser(userId));
		return this.deviceSimulationRepository.save(simulation);

	}
	
	@Override
	public DeviceSimulation updateSimulation(String identification, int interval, String json, DeviceSimulation simulation) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		simulation.setOntology(this.ontologyService.getOntologyByIdentification(mapper.readTree(json).path("ontology").asText(),simulation.getUser().getUserId()));
		simulation.setClientPlatform(this.clientPlatformRepository.findByIdentification(mapper.readTree(json).path("clientPlatform").asText()));
		simulation.setToken(this.tokenRepository.findByToken(mapper.readTree(json).path("token").asText()));
		simulation.setIdentification(identification);
		simulation.setJson(json);
		simulation.setInterval(interval);
		
		int minutes = 0;
		int seconds = interval;
		if (interval >= 0) {
			for (int i = 0; i < (interval / 60); i++) {
				minutes++;
				seconds = seconds - 60;
			}
		}
		if (minutes == 0)
			simulation.setCron("0/" + String.valueOf(seconds) + " * * ? * * *");
		else
			simulation.setCron("0/" + String.valueOf(seconds) + " 0/" + String.valueOf(minutes) + " * ? * * *");
		simulation.setActive(false);
		return this.deviceSimulationRepository.save(simulation);
	}

	@Override
	public void save(DeviceSimulation simulation) {		
		this.deviceSimulationRepository.save(simulation);
	}

	@Override
	public DeviceSimulation getSimulationById(String id) {
		
		return this.deviceSimulationRepository.findById(id);
	}

	@Override
	public List<DeviceSimulation> getSimulationsForUser(String userId) {
		
		return this.deviceSimulationRepository.findByUser(this.userService.getUser(userId));
	}

	@Override
	public DeviceSimulation getSimulationByJobName(String jobName) {
		return this.deviceSimulationRepository.findByJobName(jobName);
	}


}
