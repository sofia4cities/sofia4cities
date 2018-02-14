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

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.services.exceptions.ClientPlatformServiceException;
import com.indracompany.sofia2.config.services.exceptions.TokenServiceException;
import com.indracompany.sofia2.config.services.token.TokenService;
@Service
public class ClientPlatformServiceImpl implements ClientPlatformService{
	
	
	@Autowired
	ClientPlatformRepository clientPlatformRepository;
	@Autowired
	ClientPlatformOntologyRepository clientPlatformOntologyRepository;
	@Autowired
	TokenService tokenService;
	
	@Override
	public Token createClientAndToken(List<Ontology> ontologies, ClientPlatform clientPlatform) 
			throws TokenServiceException
	{
		if(this.clientPlatformRepository.findByIdentification(clientPlatform.getIdentification())==null)
		{
			String encryptionKey=UUID.randomUUID().toString();
			clientPlatform.setEncryptionKey(encryptionKey);
			clientPlatform = this.clientPlatformRepository.save(clientPlatform);
			
							
			for(Ontology ontology:ontologies)
			{
				ClientPlatformOntology relation = new ClientPlatformOntology();
				relation.setClientPlatform(clientPlatform);
				relation.setOntology(ontology);
				//If relation does not exist then create
				if(this.clientPlatformOntologyRepository.findByOntologyAndClientPlatform(ontology, clientPlatform)==null)
					this.clientPlatformOntologyRepository.save(relation);
			}
			
			Token token = this.tokenService.generateTokenForClient(clientPlatform);
			return token;
		}else
			throw new ClientPlatformServiceException("Platform Client already exists");
	}
	
	@Override
	public ClientPlatform getByIdentification(String identification) {
		return this.clientPlatformRepository.findByIdentification(identification);
	}

}
