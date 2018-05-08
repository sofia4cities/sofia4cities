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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.ClientPlatformOntology.AccessType;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbCleanLapse;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.services.client.dto.DeviceCreateDTO;
import com.indracompany.sofia2.config.services.datamodel.DataModelService;
import com.indracompany.sofia2.config.services.exceptions.ClientPlatformServiceException;
import com.indracompany.sofia2.config.services.exceptions.TokenServiceException;
import com.indracompany.sofia2.config.services.token.TokenService;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	@Autowired
	private DataModelService dataModelService;

	private static final String LOG_ONTOLOGY_PREFIX = "LOG_";
	private static final String LOG_DEVICE_DATA_MODEL = "DeviceLog";

	@Override
	public Token createClientAndToken(List<Ontology> ontologies, ClientPlatform clientPlatform)
			throws TokenServiceException {
		if (this.clientPlatformRepository.findByIdentification(clientPlatform.getIdentification()) == null) {
			final String encryptionKey = UUID.randomUUID().toString();
			clientPlatform.setEncryptionKey(encryptionKey);
			clientPlatform = this.clientPlatformRepository.save(clientPlatform);

			for (final Ontology ontology : ontologies) {
				final ClientPlatformOntology relation = new ClientPlatformOntology();
				relation.setClientPlatform(clientPlatform);
				relation.setAccess(AccessType.ALL.name());
				relation.setOntology(ontology);
				// If relation does not exist then create
				if (this.clientPlatformOntologyRepository.findByOntologyAndClientPlatform(ontology.getIdentification(),
						clientPlatform.getIdentification()) == null) {
					this.clientPlatformOntologyRepository.save(relation);
				}
			}

			final Token token = this.tokenService.generateTokenForClient(clientPlatform);
			return token;
		} else {
			throw new ClientPlatformServiceException("Platform Client already exists");
		}
	}

	@Override
	public ClientPlatform getByIdentification(String identification) {
		return this.clientPlatformRepository.findByIdentification(identification);
	}

	@Override
	public List<ClientPlatform> getAllClientPlatforms() {
		return this.clientPlatformRepository.findAll();
	}

	@Override
	public List<ClientPlatform> getclientPlatformsByUser(User user) {
		return this.clientPlatformRepository.findByUser(user);
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
									.findByOntologyAndClientPlatform(o.getIdentification(), k.getIdentification());
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
									.findByOntologyAndClientPlatform(o.getIdentification(), k.getIdentification());
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

	@Override
	public List<AccessType> getClientPlatformOntologyAccessLevel() {
		List<AccessType> list = new ArrayList<AccessType>();
		list.add(ClientPlatformOntology.AccessType.ALL);
		list.add(ClientPlatformOntology.AccessType.INSERT);
		list.add(ClientPlatformOntology.AccessType.QUERY);
		return list;
	}

	@Override
	public void createClientPlatform(ClientPlatform clientPlatform) {
		if (clientPlatformRepository.findByIdentification(clientPlatform.getIdentification()) != null) {
			throw new ClientPlatformServiceException(
					"Device with identification:" + clientPlatform.getIdentification() + " exists");
		}
		final String encryptionKey = UUID.randomUUID().toString();
		clientPlatform.setEncryptionKey(encryptionKey);

		List<ClientPlatformOntology> clientPlatformOntologyList = new ArrayList<ClientPlatformOntology>();
		if (clientPlatform.getClientPlatformOntologies() != null
				&& clientPlatform.getClientPlatformOntologies().size() > 0) {
			for (ClientPlatformOntology cpoNew : clientPlatform.getClientPlatformOntologies()) {
				ClientPlatformOntology cpo = new ClientPlatformOntology();
				cpo.setOntology(this.ontologyRepository.findByIdentification(cpoNew.getId()));
				cpo.setAccess(cpoNew.getAccess());
				clientPlatformOntologyList.add(cpo);
			}
		}
		clientPlatform.setClientPlatformOntologies(null);
		ClientPlatform cli = clientPlatformRepository.save(clientPlatform);

		final Token token = this.tokenService.generateTokenForClient(cli);

		for (ClientPlatformOntology cpoNew : clientPlatformOntologyList) {
			cpoNew.setClientPlatform(cli);
			this.clientPlatformOntologyRepository.save(cpoNew);
		}

	}

	@Override
	public void updateDevice(DeviceCreateDTO client) {

		ClientPlatform clientPlatform = clientPlatformRepository.findByIdentification(client.getId());

		List<ClientPlatformOntology> cpoList = this.clientPlatformOntologyRepository
				.findByClientPlatform(clientPlatform);

		if (cpoList != null && cpoList.size() > 0) {
			for (Iterator iterator = cpoList.iterator(); iterator.hasNext();) {
				ClientPlatformOntology clientPlatformOntology = (ClientPlatformOntology) iterator.next();
				this.clientPlatformOntologyRepository.delete(clientPlatformOntology.getId());
			}

			clientPlatform.setClientPlatformOntologies(null);
			clientPlatform = this.clientPlatformRepository.save(clientPlatform);

		}

		// clientPlatform =
		// clientPlatformRepository.findByIdentification(client.getId());
		updateDeviceOntologies(clientPlatform, client);

		if (clientPlatform.getClientPlatformOntologies() != null
				&& clientPlatform.getClientPlatformOntologies().size() > 0) {
			List<ClientPlatformOntology> clientPlatformOntologyList = new ArrayList<ClientPlatformOntology>();
			if (clientPlatform.getClientPlatformOntologies() != null
					&& clientPlatform.getClientPlatformOntologies().size() > 0) {

				for (ClientPlatformOntology cpoNew : clientPlatform.getClientPlatformOntologies()) {
					ClientPlatformOntology cpo = new ClientPlatformOntology();
					cpo.setOntology(this.ontologyRepository.findByIdentification(cpoNew.getId()));
					cpo.setAccess(cpoNew.getAccess());
					clientPlatform.setClientPlatformOntologies(null);
					cpo.setClientPlatform(clientPlatform);
					clientPlatformOntologyList.add(cpo);
					this.clientPlatformOntologyRepository.save(cpo);
				}
			}

		}
		clientPlatform.setMetadata(client.getMetadata());
		ClientPlatform cli = clientPlatformRepository.save(clientPlatform);

	}

	private void updateDeviceOntologies(ClientPlatform device, DeviceCreateDTO uDevice) {
		device.setMetadata(uDevice.getMetadata());
		ObjectMapper mapper = new ObjectMapper();
		try {
			device.setClientPlatformOntologies(new HashSet<ClientPlatformOntology>(mapper.readValue(
					uDevice.getClientPlatformOntologies(), new TypeReference<List<ClientPlatformOntology>>() {
					})));
		} catch (JsonParseException e) {
			log.error("Exception reached " + e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error("Exception reached " + e.getMessage(), e);
		} catch (IOException e) {
			log.error("Exception reached " + e.getMessage(), e);
		}

	}

	@Override
	public void createOntologyRelation(Ontology ontology, ClientPlatform clientPlatform) {

		final ClientPlatformOntology relation = new ClientPlatformOntology();
		relation.setClientPlatform(clientPlatform);
		relation.setAccess(AccessType.ALL.name());
		relation.setOntology(ontology);
		// If relation does not exist then create
		if (this.clientPlatformOntologyRepository.findByOntologyAndClientPlatform(ontology.getIdentification(),
				clientPlatform.getIdentification()) == null) {
			this.clientPlatformOntologyRepository.save(relation);
		}

	}

	@Override
	public Ontology createDeviceLogOntology(String clientIdentification) {
		ClientPlatform client = this.clientPlatformRepository.findByIdentification(clientIdentification);
		Ontology logOntology = new Ontology();
		logOntology.setDataModel(this.dataModelService.getDataModelByName(LOG_DEVICE_DATA_MODEL));
		logOntology.setIdentification(LOG_ONTOLOGY_PREFIX + clientIdentification);
		logOntology.setActive(true);
		logOntology.setUser(client.getUser());
		logOntology.setDescription("Ontology for logging devices related to client" + clientIdentification);
		logOntology.setJsonSchema(this.dataModelService.getDataModelByName(LOG_DEVICE_DATA_MODEL).getJsonSchema());
		logOntology.setPublic(true);
		logOntology.setRtdbClean(true);
		logOntology.setRtdbDatasource(RtdbDatasource.Mongo);
		logOntology.setRtdbCleanLapse(RtdbCleanLapse.SixMonths);
		logOntology = this.ontologyRepository.save(logOntology);
		this.createOntologyRelation(logOntology, client);
		return logOntology;

	}

	@Override
	public Ontology getDeviceLogOntology(ClientPlatform client) {
		return this.ontologyRepository.findByIdentification(LOG_ONTOLOGY_PREFIX + client.getIdentification());
	}

}
