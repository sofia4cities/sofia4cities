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

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyEmulatorRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.repository.TwitterListeningRepository;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;


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
	
	
	
	
	@Override
	@Transactional
	public void deleteOntology(String id) {
		Ontology ontology = this.ontologyRepository.findById(id);
		try{
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
		}catch(Exception e){
			throw new OntologyServiceException("Couldn't delete ontology");
		}
	}
}
