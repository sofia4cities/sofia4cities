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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.DataModel.MainType;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.user.UserService;

@Service
public class OntologyServiceImpl implements OntologyService {

	@Autowired
	private OntologyRepository ontologyRepository;
	@Autowired
	private OntologyUserAccessRepository ontologyUserAccessRepository;
	@Autowired
	private DataModelRepository dataModelRepository;
	@Autowired
	private UserService userService;

	@Override
	public List<Ontology> getAllOntologies() {
		List<Ontology> ontologies = this.ontologyRepository.findAll();

		return ontologies;
	}

	@Override
	public List<Ontology> getOntologiesByUserId(String userId) {
		return this.ontologyRepository
				.findByUserAndOntologyUserAccessAndAllPermissions(this.userService.getUser(userId));
	}

	@Override
	public List<Ontology> getOntologiesWithDescriptionAndIdentification(String userId, String identification,
			String description) {
		List<Ontology> ontologies;
		User user = this.userService.getUser(userId);

		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			if (description != null && identification != null) {
				ontologies = this.ontologyRepository
						.findByIdentificationContainingAndDescriptionContaining(identification, description);

			} else if (description == null && identification != null) {

				ontologies = this.ontologyRepository.findByIdentificationContaining(identification);

			} else if (description != null && identification == null) {

				ontologies = this.ontologyRepository.findByDescriptionContaining(description);

			} else {

				ontologies = this.ontologyRepository.findAll();
			}
		} else {
			if (description != null && identification != null) {

				ontologies = this.ontologyRepository.findByUserAndIdentificationContainingAndDescriptionContaining(user,
						identification, description);

			} else if (description == null && identification != null) {

				ontologies = this.ontologyRepository.findByUserAndIdentificationContaining(user, identification);

			} else if (description != null && identification == null) {

				ontologies = this.ontologyRepository.findByUserAndDescriptionContaining(user, description);

			} else {

				ontologies = this.ontologyRepository.findByUser(user);
			}
		}
		return ontologies;
	}

	@Override
	public List<String> getAllIdentifications() {
		List<Ontology> ontologies = this.ontologyRepository.findAllByOrderByIdentificationAsc();
		List<String> identifications = new ArrayList<String>();
		for (Ontology ontology : ontologies) {
			identifications.add(ontology.getIdentification());

		}
		return identifications;
	}

	@Override
	public Ontology getOntologyById(String id) {
		return this.ontologyRepository.findById(id);
	}

	@Override
	public Ontology getOntologyByIdentification(String identification) {
		return this.ontologyRepository.findByIdentification(identification);
	}

	@Override
	public Ontology saveOntology(Ontology ontology) {
		if (this.ontologyRepository.findByIdentification(ontology.getIdentification()) == null)
			return this.ontologyRepository.save(ontology);
		else
			throw new OntologyServiceException(
					"Ontology with identification:" + ontology.getIdentification() + " exists");

	}

	@Override
	public List<DataModel> getAllDataModels() {
		return this.dataModelRepository.findAll();
	}

	@Override
	public List<String> getAllDataModelTypes() {
		List<MainType> types = Arrays.asList(DataModel.MainType.values());
		List<String> typesString = new ArrayList<String>();
		for (MainType type : types) {
			typesString.add(type.toString());
		}
		return typesString;
	}

	@Override
	public boolean hasUserPermissionForQuery(String userId, String ontologyIdentification) {
		List<Ontology> ontologies = this.ontologyRepository
				.findByUserAndOntologyUserAccessAndPermissionsQuery(this.userService.getUser(userId));
		for (Ontology ontology : ontologies) {
			if (ontology.getIdentification().equals(ontologyIdentification))
				return true;
		}
		return false;

	}

	@Override
	public boolean hasUserPermissionForInsert(String userId, String ontologyIdentification) {
		List<Ontology> ontologies = this.ontologyRepository
				.findByUserAndOntologyUserAccessAndPermissionsInsert(this.userService.getUser(userId));
		for (Ontology ontology : ontologies) {
			if (ontology.getIdentification().equals(ontologyIdentification))
				return true;
		}
		return false;
	}

	@Override
	public Map<String, String> getOntologyFields(String identification) throws JsonProcessingException, IOException {
		Map<String, String> fields = new TreeMap<String, String>();
		Ontology ontology = this.ontologyRepository.findByIdentification(identification);
		if (ontology != null) {
			ObjectMapper mapper = new ObjectMapper();

			// String prefix =
			// mapper.readTree(ontology.getJsonSchema()).get("title").asText();

			JsonNode jsonNode = mapper.readTree(ontology.getJsonSchema());
			// Predefine Path to data properties
			jsonNode = jsonNode.path("datos").path("properties");
			Iterator<String> iterator = jsonNode.fieldNames();
			String property;
			while (iterator.hasNext()) {
				property = iterator.next();

				if (jsonNode.path(property).get("type").asText().equals("object")) {
					this.extractSubFieldsFromJson(fields, jsonNode, property, property, false);
				} else if (jsonNode.path(property).get("type").asText().equals("array")) {
					this.extractSubFieldsFromJson(fields, jsonNode, property, property, true);
				} else {
					fields.put(property, jsonNode.path(property).get("type").asText());
				}

			}
		}
		return fields;
	}

	@Override
	public void updateOntology(Ontology ontology) {
		Ontology ontologyDb = this.ontologyRepository.findById(ontology.getId());
		if (ontologyDb != null) {
			ontologyDb.setActive(ontology.isActive());
			ontologyDb.setPublic(ontology.isPublic());
			ontologyDb.setDescription(ontology.getDescription());
			ontologyDb.setIdentification(ontology.getIdentification());
			ontologyDb.setRtdbClean(ontology.isRtdbClean());
			ontologyDb.setRtdbToHdb(ontology.isRtdbToHdb());
			if (!ontology.getUser().getUserId().equals(ontologyDb.getUser().getUserId()))
				ontologyDb.setUser(this.userService.getUser(ontology.getUser().getUserId()));
			ontologyDb.setJsonSchema(ontology.getJsonSchema());
			ontologyDb.setDataModel(this.dataModelRepository.findById(ontology.getDataModel().getId()));
			ontologyDb.setDataModelVersion(ontology.getDataModelVersion());
			ontologyDb.setMetainf(ontology.getMetainf());
			this.ontologyRepository.save(ontologyDb);

		} else
			throw new OntologyServiceException("Ontology does not exist");
	}

	@Override
	public void createOntology(Ontology ontology) {
		ontology.setDataModel(this.dataModelRepository.findById(ontology.getDataModel().getId()));
		this.saveOntology(ontology);

	}

	public Map<String, String> extractSubFieldsFromJson(Map<String, String> fields, JsonNode jsonNode, String property,
			String parentField, boolean isPropertyArray) {
		if (isPropertyArray)
			jsonNode = jsonNode.path(property).path("items").path("properties");
		else
			jsonNode = jsonNode.path(property).path("properties");
		Iterator<String> iterator = jsonNode.fieldNames();
		String subProperty;
		while (iterator.hasNext()) {
			subProperty = iterator.next();

			if (jsonNode.path(subProperty).get("type").equals("object")) {
				this.extractSubFieldsFromJson(fields, jsonNode, subProperty, parentField + "." + subProperty, false);
			} else if (jsonNode.path(subProperty).get("type").equals("array")) {
				this.extractSubFieldsFromJson(fields, jsonNode, subProperty, parentField + "." + subProperty, true);
			} else {
				fields.put(parentField + "." + subProperty, jsonNode.path(subProperty).get("type").asText());
			}
		}

		return fields;

	}

	@Override
	public boolean hasOntologyUsersAuthorized(String ontologyId) {
		Ontology ontology = ontologyRepository.findById(ontologyId);
		List<OntologyUserAccess> authorizations = ontologyUserAccessRepository.findByOntology(ontology);
		return authorizations != null && authorizations.size() > 0;
	}

	@Override
	public List<OntologyUserAccess> getOntologyUserAccesses(String ontologyId) {
		Ontology ontology = ontologyRepository.findById(ontologyId);
		List<OntologyUserAccess> authorizations = ontologyUserAccessRepository.findByOntology(ontology);
		return authorizations;
	}

	@Override
	public void createUserAccess(Ontology ontology, OntologyUserAccess ontologyUserAccess) {
		Ontology fetchedOntology = ontologyRepository.findById(ontology.getId());
		if (fetchedOntology != null) {
			ontologyUserAccess.setOntology(fetchedOntology);
			ontologyUserAccessRepository.save(ontologyUserAccess);
		} else {
			throw new OntologyServiceException("Ontology does not exist");
		}
	}
	

}