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
package com.indracompany.sofia2.systemconfig.init;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sofia2.init.mongodb")
@RunWith(SpringRunner.class)
@SpringBootTest
public class InitMongoDB {

	@Autowired
	ManageDBRepository manageDb;

	@Autowired
	BasicOpsDBRepository basicOps;
	@Autowired
	DataModelRepository dataModelRepository;
	@Autowired
	OntologyRepository ontologyRepository;
	@Autowired
	UserRepository userCDBRepository;

	@PostConstruct
	@Test
	public void init() {
		init_AuditGeneral();
		init_RestaurantsDataSet();
	}

	private User getUserDeveloper() {
		User userCollaborator = this.userCDBRepository.findByUserId("developer");
		return userCollaborator;
	}

	public void init_RestaurantsDataSet() {
		Process p = null;
		try {
			log.info("init RestaurantsDataSet");
			Runtime r = Runtime.getRuntime();
			String command = "s:/tools/mongo/bin/mongoimport --db sofia2_s4c --collection Restaurants --drop --file s:/sources/sofia2-s4c/config/init/src/main/resources/restaurants-dataset.json";
			p = r.exec(command);
			log.info("Reading JSON into Database...");
			if (manageDb.getListOfTables4Ontology("Restaurants").isEmpty()) {
				log.info("No Collection Restaurants, creating...");
				manageDb.createTable4Ontology("restaurants", "{}");
			}
			if (ontologyRepository.findByIdentification("Restaurants") == null) {
				Ontology ontology = new Ontology();
				ontology.setJsonSchema(this.loadFromResources("Restaurants_schema.json"));
				ontology.setIdentification("Restaurants");
				ontology.setDescription("Ontology Restaurants for testing");
				ontology.setActive(true);
				ontology.setRtdbClean(true);
				ontology.setDataModel(this.dataModelRepository.findByName("EmptyBase").get(0));
				ontology.setRtdbToHdb(true);
				ontology.setPublic(true);
				ontology.setUser(getUserDeveloper());
				ontologyRepository.save(ontology);

			}

		} catch (Exception e) {
			log.error("Error creating Restaurants DataSet...ignoring");
		}
	}

	public void init_AuditGeneral() {
		log.info("init AuditGeneral");
		/*
		 * db.createCollection("AuditGeneral"); db.AuditGeneral.createIndex({type: 1});
		 * db.AuditGeneral.createIndex({user: 1});
		 * db.AuditGeneral.createIndex({ontology: 1}); db.AuditGeneral.createIndex({kp:
		 * 1});
		 */
		if (manageDb.getListOfTables4Ontology("AuditGeneral").isEmpty()) {
			try {
				log.info("No Collection AuditGeneral...");
				manageDb.createTable4Ontology("AuditGeneral", "{}");
				manageDb.createIndex("AuditGeneral", "type");
				manageDb.createIndex("AuditGeneral", "user");
				manageDb.createIndex("AuditGeneral", "ontology");
				manageDb.createIndex("AuditGeneral", "kp");
			} catch (Exception e) {
				log.error("Error init_AuditGeneral:" + e.getMessage());
				manageDb.removeTable4Ontology("AuditGeneral");
			}
		}
	}
	private String loadFromResources(String name) {
		try {
			return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(name).toURI())),
					Charset.forName("UTF-8"));

		} catch (Exception e) {
			log.error("**********************************************");
			log.error("Error loading resource: " + name + ".Please check if this error affect your database");
			log.error(e.getMessage());
			return null;
		}
	}


}

