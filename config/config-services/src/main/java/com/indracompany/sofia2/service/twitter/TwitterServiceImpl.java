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
package com.indracompany.sofia2.service.twitter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.TwitterListening;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.ConfigurationRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.TwitterListeningRepository;
import com.indracompany.sofia2.service.ontology.OntologyService;
import com.indracompany.sofia2.service.user.UserService;

@Service
public class TwitterServiceImpl implements TwitterService {

	@Autowired
	TwitterListeningRepository twitterListeningRepository;
	@Autowired
	ConfigurationRepository configurationRepository;
	@Autowired
	ClientPlatformRepository clientPlatformRepository;
	@Autowired
	ClientPlatformOntologyRepository clientPlatformOntologyRepository;
	@Autowired
	TokenRepository tokenRepository;
	@Autowired
	OntologyService ontologyService;
	@Autowired
	UserService userService;

	@Override
	public List<TwitterListening> getAllListenings() {
		return this.twitterListeningRepository.findAll();
	}

	@Override
	public List<TwitterListening> getAllListeningsByUser(String userId) {
		User user = userService.getUser(userId);
		return this.twitterListeningRepository.findByUser(user);
	}

	@Override
	public TwitterListening getListenById(String id) {
		return this.twitterListeningRepository.findById(id);
	}

	@Override
	public List<Configuration> getAllConfigurations() {
		return this.configurationRepository.findAll();
	}

	@Override
	public List<Configuration> getConfigurationsByUserId(String userId) {
		return this.configurationRepository.findByUser(this.userService.getUser(userId));

	}

	@Override
	public List<String> getClientsFromOntology(String ontologyId) {
		Ontology ontology = this.ontologyService.getOntologyByIdentification(ontologyId);
		List<String> clients = new ArrayList<String>();
		for (ClientPlatformOntology clientPlatform : this.clientPlatformOntologyRepository.findByOntology(ontology)) {
			clients.add(clientPlatform.getClientPlatform().getIdentification());
		}
		return clients;
	}

	@Override
	public List<String> getTokensFromClient(String clientPlatformId) {
		ClientPlatform clientPlatform = this.clientPlatformRepository.findByIdentification(clientPlatformId);
		List<String> tokens = new ArrayList<String>();
		for (Token token : this.tokenRepository.findByClientPlatform(clientPlatform)) {
			tokens.add(token.getToken());
		}
		return tokens;
	}

	@Override
	public void createListening(TwitterListening twitterListening) {
		this.twitterListeningRepository.save(twitterListening);
	}
}
