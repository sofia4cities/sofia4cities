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
package com.indracompany.sofia2.controlpanel.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.utils.ServiceUtils;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserOperationsService {

	@Autowired
	private ManageDBPersistenceServiceFacade manageFacade;

	@Autowired
	OntologyService ontologyService;

	public void createPostOperationsUser(User user) {

		String collectionAuditName = ServiceUtils.getAuditCollectionName(user.getUserId());

		if (ontologyService.getOntologyByIdentification(collectionAuditName, user.getUserId()) == null) {
			Ontology ontology = new Ontology();
			ontology.setJsonSchema("{}");
			ontology.setIdentification(collectionAuditName);
			ontology.setDescription("Ontology Audit for user " + user.getUserId());
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(false);
			ontology.setUser(user);
			ontology.setRtdbDatasource(RtdbDatasource.ElasticSearch);

			ontologyService.createOntology(ontology);

		}

	}

	private void update(User user, RtdbDatasource datasource) {

		String collectionAuditName = ServiceUtils.getAuditCollectionName(user.getUserId());

		Ontology ontology = ontologyService.getOntologyByIdentification(collectionAuditName, user.getUserId());
		ontology.setRtdbDatasource(datasource);

		ontologyService.updateOntology(ontology, user.getUserId());

	}

	public void createPostOntologyUser(User user) {

		String collectionAuditName = ServiceUtils.getAuditCollectionName(user.getUserId());

		try {
			manageFacade.createTable4Ontology(collectionAuditName, "{}");
		} catch (Exception e) {
			log.error("Audit ontology couldn't be created in ElasticSearch, so we need Mongo to Store Something");
			update(user, RtdbDatasource.Mongo);
			manageFacade.createTable4Ontology(collectionAuditName, "{}");
		}

	}

}
