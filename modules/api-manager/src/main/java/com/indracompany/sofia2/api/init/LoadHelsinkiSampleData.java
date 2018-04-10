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
package com.indracompany.sofia2.api.init;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.fiql.ApiFIQL;
import com.indracompany.sofia2.api.service.api.ApiServiceRest;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplateImpl;

@Component
public class LoadHelsinkiSampleData implements ApplicationRunner {

	@Autowired
	private ApiServiceRest apiService;

	@Autowired
	private ApiFIQL apiFIQL;
	
	@Autowired
	MongoDbTemplateImpl connect;

	@Autowired
	BasicOpsDBRepository repository;

	@Autowired
	MongoTemplate nativeTemplate;
	
	@Autowired
	OntologyRepository ontologyRepository;
	
	@Autowired
	UserRepository userCDBRepository;
	
	static final String ONT_NAME = "HelsinkiPopulation";
	static final String APINAME = "HelsinkiPopulationAPI";
	static final String DATABASE = "sofia2_s4c";
	static User userCollaborator = null;
	static User userAdministrator = null;

	
	String refOid = "";
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void run(ApplicationArguments arg0) {
		try {
			createAPI();
		} catch (Exception e) {}
	
	}

	private User getUserDeveloper() {
		if (userCollaborator == null)
			userCollaborator = this.userCDBRepository.findByUserId("developer");
		return userCollaborator;
	}

	private User getUserAdministrator() {
		if (userAdministrator == null)
			userAdministrator = this.userCDBRepository.findByUserId("administrator");
		return userAdministrator;
	}

	public void createAPI() throws Exception {
		String token = "acbca01b-da32-469e-945d-05bb6cd1552e";
		try {
			Api theApi = apiService.findApi(APINAME, token);
			
			List<Ontology> ontologies = this.ontologyRepository.findByIdentificationIgnoreCase(ONT_NAME);
			if (!ontologies.isEmpty()) {
				theApi.setOntology(ontologies.get(0));
			}
			
			
		} catch (Exception e) {
			File in = new ClassPathResource("data/helsinki.json").getFile();
			
			ApiDTO api = mapper.readValue(in, ApiDTO.class);
			List<Ontology> ontologies = this.ontologyRepository.findByIdentificationIgnoreCase(ONT_NAME);
			if (!ontologies.isEmpty()) {
				api.setOntologyId(ontologies.get(0).getId());
				api.setIdentification(APINAME);
				
			}
			apiService.createApi(api, token);
			ApiDTO out = apiFIQL.toApiDTO(apiService.findApi(APINAME, token));
			
			System.out.println(out);
		}

	}
	
	public void init_Ontology() {

	
		List<Ontology> ontologies = this.ontologyRepository.findByIdentificationIgnoreCase(ONT_NAME);
		if (ontologies.isEmpty()) {
			
			Ontology ontology = new Ontology();
			ontology.setJsonSchema("{}");
			ontology.setIdentification(ONT_NAME);
			ontology.setDescription(ONT_NAME);
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

		}

	}
	
	
	
	private void loadDataForMongo() throws DBPersistenceException, JsonProcessingException {
		
		Product data = PojoFactoryLoadData.createProduct("name1");
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writeValueAsString(data));
		
		refOid = repository.insert(ONT_NAME, mapper.writeValueAsString(data));
		int init = 17;
		int end = refOid.indexOf("\"}}");
		refOid = refOid.substring(init, end);
		// 2º
		data =  PojoFactoryLoadData.createProduct("admin");
		mapper = new ObjectMapper();
		refOid = repository.insert(ONT_NAME, mapper.writeValueAsString(data));
		// 3º
		data =  PojoFactoryLoadData.createProduct("other");
		mapper = new ObjectMapper();
		refOid = repository.insert(ONT_NAME, mapper.writeValueAsString(data));	
			
		
	}
}