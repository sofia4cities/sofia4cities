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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.DataModel.MainType;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.user.UserService;

@Service
public class OntologyServiceImpl implements OntologyService {

	@Autowired
	OntologyRepository ontologyRepository;
	@Autowired
	DataModelRepository dataModelRepository;
	@Autowired
	UserService userService;

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

		if (user.getRole().getId().equals(Role.Type.ADMINISTRATOR.toString())) {
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
		if(this.ontologyRepository.findByIdentification(ontology.getIdentification())==null)
			return this.ontologyRepository.save(ontology);
		else
			throw new OntologyServiceException("Ontology Exists");

	}

	@Override
	public List<DataModel> getAllDataModels() {
		return this.dataModelRepository.findAll();
	}
	
	@Override
	public List<MainType> getAllDataModelTypes()
	{
		return Arrays.asList(DataModel.MainType.values());
	}

}