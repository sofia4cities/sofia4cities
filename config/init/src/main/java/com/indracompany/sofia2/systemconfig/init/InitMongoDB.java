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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.commons.OSDetector;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "sofia2.init.mongodb")
@RunWith(SpringRunner.class)
@SpringBootTest
public class InitMongoDB {

	private static boolean started = false;

	@Autowired
	@Qualifier("MongoManageDBRepository")
	ManageDBRepository manageDb;

	@Autowired
	@Qualifier("MongoBasicOpsDBRepository")
	BasicOpsDBRepository basicOps;

	@Autowired
	DataModelRepository dataModelRepository;
	@Autowired
	OntologyRepository ontologyRepository;
	@Autowired
	UserRepository userCDBRepository;

	private final String USER_DIR = "user.dir";

	@PostConstruct
	@Test
	public void init() {
		if (!started) {
			started = true;
			String userDir = System.getProperty(USER_DIR);
			init_AuditGeneral();
			init_RestaurantsDataSet(userDir);
			init_HelsinkiPopulationDataSet(userDir);
			init_DigitalTwinLogs();
			init_DigitalTwinEvents();
			init_DigitalTwinActionsTurbine();
			init_DigitalTwinPropertiesTurbine();
		}
	}

	private User getUserDeveloper() {
		User userCollaborator = this.userCDBRepository.findByUserId("developer");
		return userCollaborator;
	}

	public void init_RestaurantsDataSet(String path) {
		try {
			log.info("init RestaurantsDataSet");
			if (basicOps.count("Restaurants") == 0) {
				Runtime r = Runtime.getRuntime();
				String command = null;

				if (OSDetector.isWindows()) {
					command = "s:/tools/mongo/bin/mongoimport --db sofia2_s4c --collection Restaurants --drop --file "
							+ path + "/src/main/resources/restaurants-dataset.json";
				} else {
					command = "mongoimport --db sofia2_s4c --collection Restaurants --drop --file " + path
							+ "/src/main/resources/restaurants-dataset.json";

				}
				r.exec(command);
				log.info("Reading JSON into Database...");
			}
			if (manageDb.getListOfTables4Ontology("Restaurants").isEmpty()) {
				log.info("No Collection Restaurants, creating...");
				manageDb.createTable4Ontology("restaurants", "{}");
			}
			if (ontologyRepository.findByIdentification("Restaurants") == null) {
				Ontology ontology = new Ontology();
				ontology.setJsonSchema(this.loadFromResources("examples/Restaurants-schema.json"));
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
			log.error("Error creating Restaurants DataSet...ignoring", e);
		}
	}

	public void init_HelsinkiPopulationDataSet(String path) {
		try {
			log.info("init init_HelsinkiPopulationDataSet");
			if (basicOps.count("HelsinkiPopulation") == 0) {
				Runtime r = Runtime.getRuntime();
				String command = null;
				if (OSDetector.isWindows()) {
					command = "s:/tools/mongo/bin/mongoimport --db sofia2_s4c --collection HelsinkiPopulation --drop --file "
							+ path + "/src/main/resources/HelsinkiPopulation-dataset.json";
				} else {
					command = "mongoimport --db sofia2_s4c --collection HelsinkiPopulation --drop --file " + path
							+ "/src/main/resources/HelsinkiPopulation-dataset.json";

				}
				r.exec(command);
				log.info("Reading JSON into Database...");
			}
			if (manageDb.getListOfTables4Ontology("HelsinkiPopulation").isEmpty()) {
				log.info("No Collection HelsinkiPopulation, creating...");
				manageDb.createTable4Ontology("HelsinkiPopulation", "{}");
			}
			if (ontologyRepository.findByIdentification("HelsinkiPopulation") == null) {
				Ontology ontology = new Ontology();
				ontology.setJsonSchema(this.loadFromResources("examples/HelsinkiPopulation-schema.json"));
				ontology.setIdentification("HelsinkiPopulation");
				ontology.setDescription("Ontology HelsinkiPopulation for testing");
				ontology.setActive(true);
				ontology.setRtdbClean(true);
				ontology.setDataModel(this.dataModelRepository.findByName("EmptyBase").get(0));
				ontology.setRtdbToHdb(true);
				ontology.setPublic(false);
				ontology.setUser(getUserDeveloper());
				ontologyRepository.save(ontology);
			}

		} catch (Exception e) {
			log.error("Error creating HelsinkiPopulation DataSet...ignoring", e);
		}
	}

	public void init_AndroidIoTFrame(String path) {
		try {
			log.info("init init_androidIoTFrame");
			if (manageDb.getListOfTables4Ontology("androidIoTFrame").isEmpty()) {
				log.info("No Collection androidIoTFrame, creating...");
				manageDb.createTable4Ontology("androidIoTFrame", "{}");
			}
			if (ontologyRepository.findByIdentification("androidIoTFrame") == null) {
				Ontology ontology = new Ontology();
				ontology.setJsonSchema(this.loadFromResources("examples/androidIoTFrame-schema.json"));
				ontology.setIdentification("androidIoTFrame");
				ontology.setDescription("Ontology androidIoTFrame for measures");
				ontology.setActive(true);
				ontology.setRtdbClean(true);
				ontology.setDataModel(this.dataModelRepository.findByName("EmptyBase").get(0));
				ontology.setRtdbToHdb(true);
				ontology.setPublic(false);
				ontology.setUser(getUserDeveloper());
				ontologyRepository.save(ontology);
			}

		} catch (Exception e) {
			log.error("Error creating init_androidIoTFrame DataSet...ignoring", e);
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

	public void init_DigitalTwinActionsTurbine() {
		log.info("init TwinActionsTurbine for Digital Twin");
		/*
		 * db.createCollection("Logs");
		 */
		if (basicOps.count("TwinActionsTurbine") == 0) {
			try {
				log.info("No Collection TwinActionsTurbine...");
				manageDb.createTable4Ontology("TwinActionsTurbine", "{}");
			} catch (Exception e) {
				log.error("Error init_DigitalTwinActionsTurbine:" + e.getMessage());
				manageDb.removeTable4Ontology("TwinActionsTurbine");
			}
		}
	}

	public void init_DigitalTwinPropertiesTurbine() {
		log.info("init TwinPropertiesTurbine for Digital Twin");
		/*
		 * db.createCollection("Logs");
		 */
		if (basicOps.count("TwinPropertiesTurbine") == 0) {
			try {
				log.info("No Collection Logs...");
				manageDb.createTable4Ontology("TwinPropertiesTurbine", "{}");
			} catch (Exception e) {
				log.error("Error init_DigitalTwinPropertiesTurbine:" + e.getMessage());
				manageDb.removeTable4Ontology("TwinPropertiesTurbine");
			}
		}
	}

	public void init_DigitalTwinLogs() {
		log.info("init TwinLogs for Digital Twin");
		/*
		 * db.createCollection("Logs");
		 */
		if (basicOps.count("TwinLogs") == 0) {
			try {
				log.info("No Collection Logs...");
				manageDb.createTable4Ontology("TwinLogs", "{}");
			} catch (Exception e) {
				log.error("Error init_DigitalTwinLogs:" + e.getMessage());
				manageDb.removeTable4Ontology("TwinLogs");
			}
		}
	}

	public void init_DigitalTwinEvents() {
		log.info("init TwinEvents for Digital Twin");
		/*
		 * db.createCollection("Logs");
		 */
		if (basicOps.count("TwinEvents") == 0) {
			try {
				log.info("No Collection TwinEvents...");
				manageDb.createTable4Ontology("TwinEvents", "{}");
			} catch (Exception e) {
				log.error("Error init_DigitalTwinEvents:" + e.getMessage());
				manageDb.removeTable4Ontology("TwinEvents");
			}
		}
	}

	private String loadFromResources(String name) {
		try {
			return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(name).toURI())),
					Charset.forName("UTF-8"));

		} catch (Exception e) {
			try {
				return new String(IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name)).getBytes(),
						Charset.forName("UTF-8"));
			} catch (IOException e1) {
				log.error("**********************************************");
				log.error("Error loading resource: " + name + ".Please check if this error affect your database");
				log.error(e.getMessage());
				return null;
			}
		}
	}

}
