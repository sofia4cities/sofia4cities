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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.ActionsDigitalTwinType;
import com.indracompany.sofia2.config.model.ClientConnection;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.ConsoleMenu;
import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.model.DigitalTwinType;
import com.indracompany.sofia2.config.model.EventsDigitalTwinType;
import com.indracompany.sofia2.config.model.EventsDigitalTwinType.Type;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetDatasource;
import com.indracompany.sofia2.config.model.GadgetMeasure;
import com.indracompany.sofia2.config.model.LogicDigitalTwinType;
import com.indracompany.sofia2.config.model.MarketAsset;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyCategory;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
import com.indracompany.sofia2.config.model.PropertyDigitalTwinType;
import com.indracompany.sofia2.config.model.PropertyDigitalTwinType.Direction;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.ClientConnectionRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.ConfigurationRepository;
import com.indracompany.sofia2.config.repository.ConsoleMenuRepository;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.DigitalTwinDeviceRepository;
import com.indracompany.sofia2.config.repository.DigitalTwinTypeRepository;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.config.repository.GadgetDatasourceRepository;
import com.indracompany.sofia2.config.repository.GadgetMeasureRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.MarketAssetRepository;
import com.indracompany.sofia2.config.repository.OntologyCategoryRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessTypeRepository;
import com.indracompany.sofia2.config.repository.RoleRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.repository.UserTokenRepository;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.utils.ServiceUtils;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESBaseApi;
import com.indracompany.sofia2.persistence.services.BasicOpsPersistenceServiceFacade;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;
import com.indracompany.sofia2.persistence.services.QueryToolService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "sofia2.init.elasticdb")
@RunWith(SpringRunner.class)
@SpringBootTest
public class InitElasticSearchDB {

	private static boolean started = false;
	private static User userCollaborator = null;
	private static User userAdministrator = null;
	private static User user = null;
	private static User userAnalytics = null;
	private static User userSysAdmin = null;
	private static User userPartner = null;
	private static User userOperation = null;
	private static Token tokenAdministrator = null;
	private static Ontology ontologyAdministrator = null;
	private static GadgetDatasource gadgetDatasourceAdministrator = null;
	private static Gadget gadgetAdministrator = null;


	@Autowired
	private QueryToolService queryTool;
		
	@Autowired
	private BasicOpsPersistenceServiceFacade basicOpsFacade;
	
	@Autowired
	private ManageDBPersistenceServiceFacade manageFacade;
	

	@Autowired
	private OntologyService ontologyService;
	
	@Autowired
	private OntologyRepository ontologyRepository;
	
	@Autowired
	private UserRepository userCDBRepository;
	
	@Autowired
	private ESBaseApi connector;
	
	@Autowired
	RoleRepository roleRepository;
	

	@PostConstruct
	@Test
	public void init() {
		if (!started) {
			started = true;

			log.info("Start initConfigDB...");
			// first we need to create users
			init_RoleUser();
			log.info("OK init_RoleUser");
			init_User();
			log.info("OK init_UserCDB");


			initAuditOntology();
			log.info("OK init_AuditOntology");

			//
		

		}

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

	private User getUser() {
		if (user == null)
			user = this.userCDBRepository.findByUserId("user");
		return user;
	}

	private User getUserAnalytics() {
		if (userAnalytics == null)
			userAnalytics = this.userCDBRepository.findByUserId("analytics");
		return userAnalytics;
	}

	private User getUserPartner() {
		if (userPartner == null)
			userPartner = this.userCDBRepository.findByUserId("partner");
		return userPartner;
	}

	private User getUserSysAdmin() {
		if (userSysAdmin == null)
			userSysAdmin = this.userCDBRepository.findByUserId("sysadmin");
		return userSysAdmin;
	}

	private User getUserOperations() {
		if (userOperation == null)
			userOperation = this.userCDBRepository.findByUserId("operations");
		return userOperation;
	}
	
	public void init_RoleUser() {
		log.info("init init_RoleUser");
		List<Role> types = this.roleRepository.findAll();
		if (types.isEmpty()) {
			try {

				log.info("No roles en tabla.Adding...");
				Role type = new Role();
				type.setIdEnum(Role.Type.ROLE_ADMINISTRATOR);
				type.setName("Administrator");
				type.setDescription("Administrator of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.ROLE_DEVELOPER);
				type.setName("Developer");
				type.setDescription("Advanced User of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.ROLE_USER);
				type.setName("User");
				type.setDescription("Basic User of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.ROLE_DATASCIENTIST);
				type.setName("Analytics");
				type.setDescription("Analytics User of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.ROLE_PARTNER);
				type.setName("Partner");
				type.setDescription("Partner in the Platform");
				roleRepository.save(type);
				//
				//
				type = new Role();
				type.setIdEnum(Role.Type.ROLE_SYS_ADMIN);
				type.setName("SysAdmin");
				type.setDescription("System Administradot of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.ROLE_OPERATIONS);
				type.setName("Operations");
				type.setDescription("Operations for the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.ROLE_DEVOPS);
				type.setName("DevOps");
				type.setDescription("DevOps for the Platform");
				roleRepository.save(type);
				//
				// UPDATE of the ROLE_ANALYTICS
				Role typeSon = roleRepository.findById(Role.Type.ROLE_DATASCIENTIST.toString());
				Role typeParent = roleRepository.findById(Role.Type.ROLE_DEVELOPER.toString());
				typeSon.setRoleParent(typeParent);
				roleRepository.save(typeSon);

			} catch (Exception e) {
				log.error("Error initRoleType:" + e.getMessage());
				roleRepository.deleteAll();
				throw new RuntimeException("Error creating Roles...Stopping");
			}

		}
	}
	
	public void init_User() {
		log.info("init UserCDB");
		List<User> types = this.userCDBRepository.findAll();
		User type = null;
		if (types.isEmpty()) {
			try {
				log.info("No types en tabla.Adding...");
				type = new User();
				type.setUserId("administrator");
				type.setPassword("changeIt!");
				type.setFullName("Generic Administrator of the Platform");
				type.setEmail("administrator@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ROLE_ADMINISTRATOR.toString()));

				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("developer");
				type.setPassword("changeIt!");
				type.setFullName("Developer of the Platform");
				type.setEmail("developer@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ROLE_DEVELOPER.toString()));

				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("user");
				type.setPassword("changeIt!");
				type.setFullName("Generic User of the Platform");
				type.setEmail("user@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ROLE_USER.toString()));

				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("analytics");
				type.setPassword("changeIt!");
				type.setFullName("Generic Analytics User of the Platform");
				type.setEmail("analytics@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ROLE_DATASCIENTIST.toString()));

				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("partner");
				type.setPassword("changeIt!");
				type.setFullName("Generic Partner of the Platform");
				type.setEmail("partner@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ROLE_PARTNER.toString()));

				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("sysadmin");
				type.setPassword("changeIt!");
				type.setFullName("Generic SysAdmin of the Platform");
				type.setEmail("sysadmin@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ROLE_SYS_ADMIN.toString()));

				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("operations");
				type.setPassword("changeIt!");
				type.setFullName("Operations of the Platform");
				type.setEmail("operations@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ROLE_OPERATIONS.toString()));
				userCDBRepository.save(type);
				//
			} catch (Exception e) {
				log.error("Error UserCDB:" + e.getMessage());
				userCDBRepository.deleteAll();
				throw new RuntimeException("Error creating users...ignoring creation rest of Tables");
			}
		}
	}
	
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


	

	public void initAuditOntology() {
		log.info("adding audit ontologies...");

		createPostOperationsUser(getUserAdministrator());
		createPostOntologyUser(getUserAdministrator());
		

		createPostOperationsUser(getUserDeveloper());
		createPostOntologyUser(getUserDeveloper());

		createPostOperationsUser(getUser());
		createPostOntologyUser(getUser());

		createPostOperationsUser(getUserAnalytics());
		createPostOntologyUser(getUserAnalytics());

		createPostOperationsUser(getUserPartner());
		createPostOntologyUser(getUserPartner());

		createPostOperationsUser(getUserSysAdmin());
		createPostOntologyUser(getUserSysAdmin());

		createPostOperationsUser(getUserOperations());
		createPostOntologyUser(getUserOperations());

	}

}
