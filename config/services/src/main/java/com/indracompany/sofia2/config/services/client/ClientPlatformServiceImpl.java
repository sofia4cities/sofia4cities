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
package com.indracompany.sofia2.config.services.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.services.exceptions.ClientPlatformServiceException;
import com.indracompany.sofia2.config.services.exceptions.TokenServiceException;
import com.indracompany.sofia2.config.services.token.TokenService;
import com.indracompany.sofia2.config.services.user.UserService;

@Service
public class ClientPlatformServiceImpl implements ClientPlatformService {

	@Autowired
	private ClientPlatformRepository clientPlatformRepository;
	@Autowired
	private ClientPlatformOntologyRepository clientPlatformOntologyRepository;
	@Autowired
	private OntologyRepository ontologyRepository;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private UserService userService;

	@Override
	public Token createClientAndToken(List<Ontology> ontologies, ClientPlatform clientPlatform)
			throws TokenServiceException {
		if (this.clientPlatformRepository.findByIdentification(clientPlatform.getIdentification()) == null) {
			String encryptionKey = UUID.randomUUID().toString();
			clientPlatform.setEncryptionKey(encryptionKey);
			clientPlatform = this.clientPlatformRepository.save(clientPlatform);

			for (Ontology ontology : ontologies) {
				ClientPlatformOntology relation = new ClientPlatformOntology();
				relation.setClientPlatform(clientPlatform);
				relation.setOntology(ontology);
				// If relation does not exist then create
				if (this.clientPlatformOntologyRepository.findByOntologyAndClientPlatform(ontology,
						clientPlatform) == null)
					this.clientPlatformOntologyRepository.save(relation);
			}

			Token token = this.tokenService.generateTokenForClient(clientPlatform);
			return token;
		} else
			throw new ClientPlatformServiceException("Platform Client already exists");
	}

	@Override
	public ClientPlatform getByIdentification(String identification) {
		return this.clientPlatformRepository.findByIdentification(identification);
	}

	@Override
	public List<ClientPlatform> getAllClientPlatformByCriteria(String userId, String identification,
			String[] ontologies) {
		List<ClientPlatform> clients = new ArrayList<ClientPlatform>();

		User user = this.userService.getUser(userId);

		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			if (identification != null) {

				clients.add(this.clientPlatformRepository.findByIdentification(identification));

			} else {

				clients = this.clientPlatformRepository.findAll();
			}
			List<ClientPlatform> clientPlatformAdd = new ArrayList<ClientPlatform>();
			if (ontologies != null && ontologies.length > 0) {
				for (ClientPlatform k : clients) {
					for (int i = 0; i < ontologies.length; i++) {
						Ontology o = ontologyRepository.findByIdentification(ontologies[i]);
						if (o != null) {
							ClientPlatformOntology clpo = clientPlatformOntologyRepository
									.findByOntologyAndClientPlatform(o, k);
							if (clpo != null) {
								if (!clientPlatformAdd.contains(k)) {
									clientPlatformAdd.add(k);
								}
							}
						}

					}

				}
				return clientPlatformAdd;
			}
			return clients;
		} else {
			if (identification != null) {

				clients.add(this.clientPlatformRepository.findByUserAndIdentification(user, identification));

			} else {

				clients = this.clientPlatformRepository.findByUser(user);
			}
			List<ClientPlatform> clientPlatformAdd = new ArrayList<ClientPlatform>();
			if (ontologies != null && ontologies.length > 0) {
				for (ClientPlatform k : clients) {
					for (int i = 0; i < ontologies.length; i++) {
						Ontology o = ontologyRepository.findByIdentification(ontologies[i]);
						if (o != null) {
							ClientPlatformOntology clpo = clientPlatformOntologyRepository
									.findByOntologyAndClientPlatform(o, k);
							if (clpo != null) {
								if (!clientPlatformAdd.contains(k)) {
									clientPlatformAdd.add(k);
								}
							}
						}

					}

				}
				return clientPlatformAdd;
			}
			return clients;
		}
	}
}
