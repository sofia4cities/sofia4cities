/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.controlpanel.services.ontology;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OntologyLogicService {

	@Autowired
	private OntologyService ontologyService;

	@Autowired
	private ManageDBPersistenceServiceFacade manageDBPersistenceServiceFacade;

	public void createOntology(Ontology ontology) {

		log.debug("create ontology ");
		ontologyService.createOntology(ontology);

		try {

			log.debug("create ontology in db " + ontology.getRtdbDatasource());
			manageDBPersistenceServiceFacade.createTable4Ontology(ontology.getIdentification(),
					ontology.getJsonSchema());

		} catch (Exception e) {

			try {
				ontologyService.delete(ontology);
			} catch (Exception ex) {
				log.error("error deleting ontology " + ontology.getId());
			}

			throw new OntologyServiceException("Problems creating the ontology", e);
		}

		log.debug("ontology created");
	}

	public void createOntologyFromHive() {

	}

}
