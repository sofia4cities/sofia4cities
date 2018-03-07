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
package com.indracompany.sofia2.config.services.deletion;

import java.util.Iterator;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ClientConnection;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.TwitterListening;
import com.indracompany.sofia2.config.repository.ClientConnectionRepository;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.OntologyEmulatorRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.TwitterListeningRepository;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;


@Service
public class EntityDeletionServiceImpl implements EntityDeletionService{
	
	@Autowired
	private OntologyRepository ontologyRepository;
	@Autowired
	private OntologyEmulatorRepository ontologyEmulatorRepository;
	@Autowired
	private OntologyUserAccessRepository ontologyUserAccessRepository;
	@Autowired
	private ClientPlatformOntologyRepository clientPlatformOntologyRepository;
	
    @Autowired
	private TwitterListeningRepository twitterListeningRepository;
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private UserService userService;
	@Autowired
	private ClientConnectionRepository clientConnectionRepository;
	@Autowired
	private ClientPlatformRepository clientPlatformRepository;
	@Autowired
	private TokenRepository tokenRepository;

	
	@Override
	@Transactional
	public void deleteOntology(String id, String userId) {
		
		try{
			User user = userService.getUser(userId);
			Ontology ontology = ontologyService.getOntologyById(id, userId);
			if (ontologyService.hasUserPermisionForChangeOntology(user, ontology)) {
				if(this.clientPlatformOntologyRepository.findByOntology(ontology) != null) {
					this.clientPlatformOntologyRepository.deleteByOntology(ontology);
				}
				if(this.ontologyEmulatorRepository.findByOntology(ontology) != null) {
					this.ontologyEmulatorRepository.deleteByOntology(ontology);
				}
				if(this.ontologyUserAccessRepository.findByOntology(ontology) != null) {
					this.ontologyUserAccessRepository.deleteByOntology(ontology);
				}
				if(this.twitterListeningRepository.findByOntology(ontology) != null) {
					this.twitterListeningRepository.deleteByOntology(ontology);
				}
				this.ontologyRepository.deleteById(id);
			} else {
				throw new OntologyServiceException("Couldn't delete ontology");
			}
		}catch(Exception e){
			throw new OntologyServiceException("Couldn't delete ontology", e);
		}
	}
	
	@Override
	@Transactional
	public void deleteTwitterListening(TwitterListening twitterListening) {
		this.twitterListeningRepository.deleteById(twitterListening.getId());
	}
	@Override
	@Transactional
	public void deleteClient(String id) {
		try {

			ClientPlatform client = clientPlatformRepository.findByIdentification(id);
			List<ClientPlatformOntology> cpf = this.clientPlatformOntologyRepository.findByClientPlatform(client);
			if (cpf != null && cpf.size() > 0) {
				for (Iterator iterator = cpf.iterator(); iterator.hasNext();) {
					ClientPlatformOntology clientPlatformOntology = (ClientPlatformOntology) iterator.next();
					this.clientPlatformOntologyRepository.delete(clientPlatformOntology);
				}

			}
			List<ClientConnection> cc = this.clientConnectionRepository.findByClientPlatform(client);
			if (cc != null && cc.size() > 0) {
				for (Iterator iterator = cc.iterator(); iterator.hasNext();) {
					ClientConnection clientConnection = (ClientConnection) iterator.next();
					this.clientConnectionRepository.delete(clientConnection);
				}
			}

			this.clientPlatformRepository.delete(client);

		} catch (Exception e) {
			throw new OntologyServiceException("Couldn't delete ClientPlatform");
		}
	}

	@Override
	public void deleteToken(String id) {
		try {
			Token token = tokenRepository.findById(id);
			tokenRepository.delete(token);
		} catch (Exception e) {
			throw new OntologyServiceException("Couldn't delete Token");
		}

	}	
}
