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
package com.indracompany.sofia2.config.services.ontology;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.User;

public interface OntologyService {

	List<Ontology> getAllOntologies(String sessionUserId);

	List<Ontology> getOntologiesByUserId(String sessionUserId);

	List<Ontology> getOntologiesWithDescriptionAndIdentification(String sessionUserId, String identification,
			String description);

	List<String> getAllIdentifications();

	Ontology getOntologyById(String ontologyId, String sessionUserId);

	Ontology getOntologyByIdentification(String identification, String sessionUserId);

	List<DataModel> getAllDataModels();

	List<String> getAllDataModelTypes();

	// TODO unify interface
	boolean hasUserPermissionForQuery(User user, Ontology ontology);

	boolean hasUserPermissionForQuery(String userId, Ontology ontology);

	boolean hasUserPermissionForQuery(String userId, String ontologyId);

	// TODO unify interface
	boolean hasUserPermissionForInsert(User user, Ontology ontology);

	boolean hasUserPermissionForInsert(String userId, String ontologyIdentificator);

	boolean hasUserPermisionForChangeOntology(User user, Ontology ontology);

	void updateOntology(Ontology ontology, String sessionUserId);

	void createOntology(Ontology ontology);

	List<Ontology> getOntologiesByClientPlatform(ClientPlatform clientPlatform);

	/**
	 * This method checks if an ontology has authorizations for other users
	 * different from its owner.
	 * 
	 * @param ontologyId
	 *            the id of the ontology.
	 * @return true if any other user has authorization over the ontology.
	 */
	boolean hasOntologyUsersAuthorized(String ontologyId);

	List<OntologyUserAccess> getOntologyUserAccesses(String ontologyId, String sessionUserId);

	void createUserAccess(String ontologyId, String userId, String typeName, String sessionUserId);

	OntologyUserAccess getOntologyUserAccessByOntologyIdAndUserId(String ontologyId, String userId,
			String sessionUserId);

	OntologyUserAccess getOntologyUserAccessById(String userAccessId, String sessionUserId);

	void deleteOntologyUserAccess(String userAccessId, String sessionUserId);

	void updateOntologyUserAccess(String userAccessId, String typeName, String sessionUserId);

	Map<String, String> getOntologyFieldsQueryTool(String identification, String sessionUserId)
			throws JsonProcessingException, IOException;

	Map<String, String> getOntologyFields(String identification, String sessionUserId)
			throws JsonProcessingException, IOException;

}
