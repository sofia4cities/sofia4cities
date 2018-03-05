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
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessTypeRepository;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.user.UserService;

@Service
public class OntologyServiceImpl implements OntologyService {

	@Autowired
	private OntologyRepository ontologyRepository;
	@Autowired
	private OntologyUserAccessRepository ontologyUserAccessRepository;
	@Autowired
	private OntologyUserAccessTypeRepository ontologyUserAccessTypeRepository;
	@Autowired
	private DataModelRepository dataModelRepository;
	@Autowired
	private UserService userService;

	@Override
	public List<Ontology> getAllOntologies(String sessionUserId) {
		
		User sessionUser = this.userService.getUser(sessionUserId);
		if (sessionUser.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return ontologyRepository.findAll();
		} else {
			return ontologyRepository
					.findByUserAndOntologyUserAccessAndAllPermissions(sessionUser);
		}
	}

	@Override
	public List<Ontology> getOntologiesByUserId(String sessionUserId) {
		User sessionUser = this.userService.getUser(sessionUserId);
		
		return ontologyRepository
					.findByUserAndOntologyUserAccessAndAllPermissions(sessionUser);
	}

	@Override
	public List<Ontology> getOntologiesWithDescriptionAndIdentification(String sessionUserId, String identification,
			String description) {
		List<Ontology> ontologies;
		User sessionUser = this.userService.getUser(sessionUserId);

		if (sessionUser.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
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

				ontologies = this.ontologyRepository.findByUserAndIdentificationContainingAndDescriptionContaining(sessionUser,
						identification, description);

			} else if (description == null && identification != null) {

				ontologies = this.ontologyRepository.findByUserAndIdentificationContaining(sessionUser, identification);

			} else if (description != null && identification == null) {

				ontologies = this.ontologyRepository.findByUserAndDescriptionContaining(sessionUser, description);

			} else {

				ontologies = this.ontologyRepository.findByUser(sessionUser);
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
	public Ontology getOntologyById(String ontologyId, String sessionUserId) {
		Ontology ontology = ontologyRepository.findById(ontologyId);
		User sessionUser = this.userService.getUser(sessionUserId);
		if (ontology != null) {
			if (hasUserPermissionForQuery(sessionUser, ontology)) {
				return ontology;
			} else {
				throw new OntologyServiceException("The user is not authorized");
			}
		} else {
			return null;
		}
		
	}

	@Override
	public Ontology getOntologyByIdentification(String identification, String sessionUserId) {
		User sessionUser = this.userService.getUser(sessionUserId);		
		Ontology ontology = ontologyRepository.findByIdentification(identification);
		
		if (ontology != null) {
			if (hasUserPermissionForQuery(sessionUser, ontology)) {
				return ontology;
			} else {
				throw new OntologyServiceException("The user is not authorized");
			}
		} else {
			return null;
		}
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
	public boolean hasUserPermissionForQuery(User user, Ontology ontology) {
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return true;
		} else if (ontology.getUser().getUserId().equals(user.getUserId())) {
			return true;
		} else {
			OntologyUserAccess userAuthorization = ontologyUserAccessRepository.findByOntologyAndUser(ontology, user);
			if (userAuthorization != null) {
				switch (OntologyUserAccessType.Type.valueOf(userAuthorization.getOntologyUserAccessType().getName())) {
				case ALL:
				case INSERT:
				case QUERY:
					return true;
				default:
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean hasUserPermissionForQuery(String userId, Ontology ontology) {
		User user = userService.getUser(userId);
		return hasUserPermissionForQuery(user, ontology);
	}
	
	@Override
	public boolean hasUserPermissionForQuery(String userId, String ontologyIdentificator) {
		Ontology ontology = ontologyRepository.findByIdentification(ontologyIdentificator);
		return hasUserPermissionForQuery(userId, ontology);
	}

	@Override
	public boolean hasUserPermissionForInsert(User user, Ontology ontology) {
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return true;
		} else if (ontology.getUser().getUserId().equals(user.getUserId())) {
			return true;
		} else {
			OntologyUserAccess userAuthorization = ontologyUserAccessRepository.findByOntologyAndUser(ontology, user);
			if (userAuthorization != null) {
				switch (OntologyUserAccessType.Type.valueOf(userAuthorization.getOntologyUserAccessType().getName())) {
				case ALL:
				case INSERT:
					return true;
				default:
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean hasUserPermissionForInsert(String userId, String ontologyIdentificator) {
		User user = userService.getUser(userId);
		Ontology ontology = ontologyRepository.findByIdentification(ontologyIdentificator);
		return hasUserPermissionForInsert(user, ontology);
	}

	@Override
	public Map<String, String> getOntologyFields(String identification, String sessionUserId) throws JsonProcessingException, IOException {
		Map<String, String> fields = new TreeMap<String, String>();
		Ontology ontology = getOntologyByIdentification(identification, sessionUserId);
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
	public void updateOntology(Ontology ontology, String sessionUserId) {
		Ontology ontologyDb = this.ontologyRepository.findById(ontology.getId());
		User sessionUser = this.userService.getUser(sessionUserId);
		
		if (ontologyDb != null) {	
			if (hasUserPermisionForChangeOntology(sessionUser, ontologyDb)) {
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
			} else {
				throw new OntologyServiceException("The user is not authorized");
			}
		} else
			throw new OntologyServiceException("Ontology does not exist");
	}

	//TODO it should be checked that onotologies are assigned to the session user.
	@Override
	public void createOntology(Ontology ontology) {
		try {
			if (ontologyRepository.findByIdentification(ontology.getIdentification()) == null) {
				
				if (ontology.getDataModel() != null) {
					DataModel dataModel = dataModelRepository.findById(ontology.getDataModel().getId());
					ontology.setDataModel(dataModel);
				}
				User user = userService.getUser(ontology.getUser().getUserId());
				if (user != null) {
					ontology.setUser(user);
					this.ontologyRepository.save(ontology);
				} else {
					throw new OntologyServiceException("Invalid user");
				}				
			} else {
				throw new OntologyServiceException(
						"Ontology with identification:" + ontology.getIdentification() + " exists");
			}
		} catch (Exception e) {
			throw new OntologyServiceException("Problems creating the ontology", e);
		}
	}

	private Map<String, String> extractSubFieldsFromJson(Map<String, String> fields, JsonNode jsonNode, String property,
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
	public List<OntologyUserAccess> getOntologyUserAccesses(String ontologyId, String sessionUserId) {
		Ontology ontology = getOntologyById(ontologyId, sessionUserId);
		List<OntologyUserAccess> authorizations = ontologyUserAccessRepository.findByOntology(ontology);
		return authorizations;
	}

	@Override
	public void createUserAccess(String sessionUserId, String ontologyId, String userId, String typeName) {
	
		Ontology ontology = ontologyRepository.findById(ontologyId);
		User sessionUser = userService.getUser(sessionUserId);
		
		if (hasUserPermisionForChangeOntology(sessionUser, ontology)) {
			List<OntologyUserAccessType> managedTypes = ontologyUserAccessTypeRepository.findByName(typeName);
			OntologyUserAccessType managedType = managedTypes != null && managedTypes.size() > 0 ? managedTypes.get(0) : null;
			User userToBeAutorized = this.userService.getUser(userId);
			if (ontology != null && managedType != null && userToBeAutorized != null) {
				OntologyUserAccess ontologyUserAccess = new OntologyUserAccess();
				ontologyUserAccess.setOntology(ontology);
				ontologyUserAccess.setUser(userToBeAutorized);
				ontologyUserAccess.setOntologyUserAccessType(managedType);
				ontologyUserAccessRepository.save(ontologyUserAccess);
			} else {
				throw new OntologyServiceException("Problem creating the authorization");
			}			
		} else {
			throw new OntologyServiceException("The user is not authorized");
		}
	}
	
	@Override
	public OntologyUserAccess getOntologyUserAccessByOntologyIdAndUserId(String ontologyId, String userId, String sessionUserId) {
		Ontology ontology = getOntologyById(ontologyId, sessionUserId);
		User user = this.userService.getUser(userId);
		OntologyUserAccess userAccess = ontologyUserAccessRepository.findByOntologyAndUser(ontology, user);
		if (userAccess == null) {
			throw new OntologyServiceException("Problem obtaining user data");
		} else {
			return userAccess;
		}
	}

	@Override
	public OntologyUserAccess getOntologyUserAccessById(String userAccessId, String sessionUserId) {
		User sessionUser = this.userService.getUser(sessionUserId);
		OntologyUserAccess userAccess = ontologyUserAccessRepository.findById(userAccessId);
		if (hasUserPermissionForQuery(sessionUser, userAccess.getOntology())) {
			return userAccess;
		} else {
			throw new OntologyServiceException("The user is not authorized");
		}
	}

	@Override
	public void deleteOntologyUserAccess(String userAccessId, String sessionUserId) {
		User sessionUser = this.userService.getUser(sessionUserId);
		OntologyUserAccess userAccess = ontologyUserAccessRepository.findById(userAccessId);
		if (hasUserPermisionForChangeOntology(sessionUser, userAccess.getOntology())) {
			ontologyUserAccessRepository.delete(userAccessId);
		} else {
			throw new OntologyServiceException("The user is not authorized");
		}
	}

	@Override
	public void updateOntologyUserAccess(String userAccessId, String typeName, String sessionUserId) {
		User sessionUser = this.userService.getUser(sessionUserId);
		OntologyUserAccess userAccess = ontologyUserAccessRepository.findById(userAccessId);
		List<OntologyUserAccessType> types = ontologyUserAccessTypeRepository.findByName(typeName);
		if (types != null && types.size() > 0) {
			if (hasUserPermisionForChangeOntology(sessionUser, userAccess.getOntology())) {
				OntologyUserAccessType typeDB = types.get(0);
				userAccess.setOntologyUserAccessType(typeDB);
				ontologyUserAccessRepository.save(userAccess);
			} else {
				throw new OntologyServiceException("The user is not authorized");
			}			
		} else {
			throw new IllegalStateException("Incorrect type of access");
		}
		
		
	}
	
	@Override
	public boolean hasUserPermisionForChangeOntology(User user, Ontology ontology) {
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return true;
		} else if (ontology.getUser().getUserId().equals(user.getUserId())) {
			return true;
		} else {
			OntologyUserAccess userAuthorization = ontologyUserAccessRepository.findByOntologyAndUser(ontology, user);
			
			if (userAuthorization != null) {
				switch (OntologyUserAccessType.Type.valueOf(userAuthorization.getOntologyUserAccessType().getName())) {
				case ALL:
					return true; 
				default:
					return false;
				}
			} else {
				return false;
			}
		}	
	}


}
