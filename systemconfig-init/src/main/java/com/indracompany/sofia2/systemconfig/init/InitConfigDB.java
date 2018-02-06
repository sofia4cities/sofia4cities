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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.ClientConnection;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.ConfigurationType;
import com.indracompany.sofia2.config.model.ConsoleMenu;
import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DashboardType;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetDataModel;
import com.indracompany.sofia2.config.model.GadgetMeasure;
import com.indracompany.sofia2.config.model.GadgetQuery;
import com.indracompany.sofia2.config.model.GeneratorType;
import com.indracompany.sofia2.config.model.InstanceGenerator;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyCategory;
import com.indracompany.sofia2.config.model.OntologyEmulator;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.ClientConnectionRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.ConfigurationRepository;
import com.indracompany.sofia2.config.repository.ConfigurationTypeRepository;
import com.indracompany.sofia2.config.repository.ConsoleMenuRepository;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.DashboardTypeRepository;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.GadgetDataModelRepository;
import com.indracompany.sofia2.config.repository.GadgetMeasureRepository;
import com.indracompany.sofia2.config.repository.GadgetQueryRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.GeneratorTypeRepository;
import com.indracompany.sofia2.config.repository.InstanceGeneratorRepository;
import com.indracompany.sofia2.config.repository.OntologyCategoryRepository;
import com.indracompany.sofia2.config.repository.OntologyEmulatorRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessTypeRepository;
import com.indracompany.sofia2.config.repository.RoleRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.repository.UserTokenRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "sofia2.init.configdb")
@RunWith(SpringRunner.class)
@SpringBootTest
public class InitConfigDB {

	private static User userCollaborator = null;
	private static User userAdministrator = null;

	@Autowired
	ClientConnectionRepository clientConnectionRepository;
	@Autowired
	ClientPlatformRepository clientPlatformRepository;
	@Autowired
	ClientPlatformOntologyRepository clientPlatformOntologyRepository;
	@Autowired
	ConsoleMenuRepository consoleMenuRepository;
	@Autowired
	DashboardRepository dashboardRepository;
	@Autowired
	DashboardTypeRepository dashboardTypeRepository;
	@Autowired
	DataModelRepository dataModelRepository;
	@Autowired
	GadgetDataModelRepository gadgetDataModelRepository;
	@Autowired
	GadgetMeasureRepository gadgetMeasureRepository;
	@Autowired
	GadgetQueryRepository gadgetQueryRepository;
	@Autowired
	GadgetRepository gadgetRepository;
	@Autowired
	GeneratorTypeRepository generatorTypeRepository;
	@Autowired
	InstanceGeneratorRepository instanceGeneratorRepository;
	@Autowired
	OntologyRepository ontologyRepository;
	@Autowired
	OntologyCategoryRepository ontologyCategoryRepository;
	@Autowired
	OntologyEmulatorRepository ontologyEmulatorRepository;
	@Autowired
	OntologyUserAccessRepository ontologyUserAccessRepository;
	@Autowired
	OntologyUserAccessTypeRepository ontologyUserAccessTypeRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	TokenRepository tokenRepository;
	@Autowired
	UserRepository userCDBRepository;
	@Autowired
	ConfigurationRepository configurationRepository;
	@Autowired
	ConfigurationTypeRepository configurationTypeRepository;
	
	@Autowired
	UserTokenRepository userTokenRepository;

	@PostConstruct
	@Test
	public void init() {
		log.info("Start initConfigDB...");
		// first we need to create users
		init_RoleUser();
		log.info("OK init_RoleUser");
		init_User();
		log.info("OK init_UserCDB");
		//
		init_DataModel();
		log.info("OK init_DataModel");
		init_OntologyCategory();
		log.info("OK init_OntologyCategory");
		init_Ontology();
		log.info("OK init_Ontology");
		init_OntologyUserAccess();
		log.info("OK init_OntologyUserAccess");
		init_OntologyUserAccessType();
		log.info("OK init_OntologyUserAccessType");
		init_OntologyEmulator();
		log.info("OK init_OntologyEmulator");
		init_OntologyCategory();
		log.info("OK init_OntologyCategory");
		init_OntologyEmulator();
		log.info("OK init_OntologyEmulator");
		//
		init_ClientPlatform();
		log.info("OK init_ClientPlatform");
		init_ClientPlatformOntology();
		log.info("OK init_ClientPlatformOntology");
		init_ClientConnection();
		log.info("OK init_ClientConnection");
		//
		init_Token();
		log.info("OK init_Token");
		
		init_UserToken();
		log.info("OK USER_Token");
		//
		init_DashboardType();
		log.info("OK init_DashboardType");
		init_Dashboard();
		log.info("OK init_Dashboard");
		init_GadgetDataModel();
		log.info("OK init_GadgetDataModel");
		init_GadgetMeasure();
		log.info("OK init_GadgetMeasure");
		init_GadgetQuery();
		log.info("OK init_GadgetQuery");
		init_Gadget();
		log.info("OK init_Gadget");
		init_GeneratorType();
		log.info("OK init_GeneratorType");
		// init_InstanceGenerator();
		//
		init_ConsoleMenu();
		log.info("OK init_ConsoleMenu");
		init_Configuration();
		log.info("OK init_Configuration");
	}

	private void init_Configuration() {
		log.info("init_Configuration");
		if (this.configurationRepository.count() == 0) {

			ConfigurationType type = new ConfigurationType();
			Configuration config = new Configuration();
			type.setIdEnum(ConfigurationType.Type.TwitterConfiguration);
			type.setDescription("Configuration for access Twitter account (Token and Key)");
			this.configurationTypeRepository.save(type);
			config = new Configuration();
			config.setConfigurationType(type);
			config.setUser(getUserAdministrator());
			config.setEnvironmentEnum(Configuration.Environment.ALL);
			config.setYmlConfig(loadFromResources("TwitterConfiguration.yml"));
			this.configurationRepository.save(config);
			//
			config = new Configuration();
			config.setConfigurationType(type);
			config.setUser(getUserAdministrator());
			config.setEnvironmentEnum(Configuration.Environment.ALL);
			config.setSuffix("lmgracia");
			config.setYmlConfig(loadFromResources("TwitterConfiguration.yml"));
			this.configurationRepository.save(config);
			//
			type = new ConfigurationType();
			type.setIdEnum(ConfigurationType.Type.EndpointModulesConfiguration);
			type.setDescription("Endpoints of Sofia2 Modules Configuration p");
			this.configurationTypeRepository.save(type);
			config = new Configuration();
			config.setConfigurationType(type);
			config.setUser(getUserAdministrator());
			config.setEnvironmentEnum(Configuration.Environment.DEV);
			config.setYmlConfig(loadFromResources("EndpointModulesConfiguration.yml"));
			this.configurationRepository.save(config);
			//
			type = new ConfigurationType();
			type.setIdEnum(ConfigurationType.Type.MailConfiguration);
			type.setDescription("Mail Configuration por mail sending");
			this.configurationTypeRepository.save(type);
			config = new Configuration();
			config.setConfigurationType(type);
			config.setUser(getUserAdministrator());
			config.setEnvironmentEnum(Configuration.Environment.ALL);
			config.setYmlConfig(loadFromResources("MailConfiguration.yml"));
			this.configurationRepository.save(config);
			//
			type = new ConfigurationType();
			type.setIdEnum(ConfigurationType.Type.RTDBConfiguration);
			type.setDescription("Configuration for the default RealTime DB (MongoDB)");
			this.configurationTypeRepository.save(type);
			config = new Configuration();
			config.setConfigurationType(type);
			config.setUser(getUserAdministrator());
			config.setEnvironmentEnum(Configuration.Environment.LOCAL);
			config.setYmlConfig(loadFromResources("RTDBConfiguration.yml"));
			this.configurationRepository.save(config);
			//
			type = new ConfigurationType();
			type.setIdEnum(ConfigurationType.Type.MonitoringConfiguration);
			type.setDescription("Configuration for report to Monitoring UI");
			this.configurationTypeRepository.save(type);
			config = new Configuration();
			config.setConfigurationType(type);
			config.setUser(getUserAdministrator());
			config.setEnvironmentEnum(Configuration.Environment.LOCAL);
			config.setYmlConfig(loadFromResources("MonitoringConfiguration.yml"));
			this.configurationRepository.save(config);

		}

	}

	public void init_ClientConnection() {
		log.info("init ClientConnection");
		List<ClientConnection> clients = this.clientConnectionRepository.findAll();
		ClientPlatform cp = this.clientPlatformRepository.findAll().get(0);
		if (clients.isEmpty()) {
			log.info("No clients ...");
			ClientConnection con = new ClientConnection();
			//
			con.setClientPlatform(cp);
			con.setIdentification("1");
			con.setIpStrict(true);
			con.setStaticIp(false);
			con.setLastIp("192.168.1.89");
			Calendar date = Calendar.getInstance();
			con.setLastConnection(date);
			con.setClientPlatform(cp);
			clientConnectionRepository.save(con);
		}
	}
	// List<ClientConnection> clients= this.clientConnectionRepository.findAll();
	// if (clients.isEmpty()) {
	// log.info("No clients ...");
	// ClientConnection con= new ClientConnection();
	// ClientPlatform client= new ClientPlatform();
	// client.setId("06be1962-aa27-429c-960c-d8a324eef6d4");
	// clientPlatformRepository.save(client);
	// con.setClientPlatformId(client);
	// con.setIdentification("1");
	// con.setIpStrict(true);
	// con.setStaticIp(false);
	// con.setLastIp("192.168.1.89");
	// Calendar date = Calendar.getInstance();
	// con.setLastConnection(date);
	// clientConnectionRepository.save(con);
	// }

	public void init_ClientPlatformOntology() {

		log.info("init ClientPlatformOntology");
		List<ClientPlatformOntology> cpos = this.clientPlatformOntologyRepository.findAll();
		if (cpos.isEmpty()) {
			if (this.clientPlatformRepository.findAll().isEmpty())
				throw new RuntimeException("There must be at least a ClientPlatform with id=1 created");
			if (this.ontologyRepository.findAll().isEmpty())
				throw new RuntimeException("There must be at least a Ontology with id=1 created");
			log.info("No Client Platform Ontologies");
			ClientPlatformOntology cpo = new ClientPlatformOntology();
			cpo.setClientPlatform(this.clientPlatformRepository.findAll().get(0));
			cpo.setOntology(this.ontologyRepository.findAll().get(0));
			this.clientPlatformOntologyRepository.save(cpo);
		}
	}

	public void init_ClientPlatform() {
		log.info("init ClientPlatform");
		List<ClientPlatform> clients = this.clientPlatformRepository.findAll();
		if (clients.isEmpty()) {
			log.info("No clients ...");
			ClientPlatform client = new ClientPlatform();
			client.setId("1");
			client.setUser(getUserCollaborator());
			client.setIdentification("Client-MasterData");
			client.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
			client.setDescription("ClientPatform created as MasterData");
			clientPlatformRepository.save(client);
			client = new ClientPlatform();
			client.setId("2");
			client.setUser(getUserCollaborator());
			client.setIdentification("GTKP-Example");
			client.setEncryptionKey("f9dfe72e-7082-4fe8-ba37-3f569b30a691");
			client.setDescription("ClientPatform created as Example");
			clientPlatformRepository.save(client);

		}

	}

	public void init_ConsoleMenu() {
		log.info("init ConsoleMenu");
		List<ConsoleMenu> menus = this.consoleMenuRepository.findAll();

		if (menus.isEmpty()) {
			log.info("No menu elements found...adding");
			try {
				log.info("Adding menu for role ADMIN");
				ConsoleMenu menu = new ConsoleMenu();
				menu.setId("1");
				menu.setJson(loadFromResources("menu_admin.json"));
				menu.setRoleType(roleRepository.findById(Role.Type.ADMINISTRATOR.toString()));
				this.consoleMenuRepository.save(menu);
			} catch (Exception e) {
				log.error("Error adding menu for role ADMIN");
			}
			try {
				log.info("Adding menu for role COLLABORATOR");
				ConsoleMenu menu = new ConsoleMenu();
				menu.setId("2");
				menu.setJson(loadFromResources("menu_collaborator.json"));
				menu.setRoleType(roleRepository.findById(Role.Type.COLLABORATOR.toString()));
				this.consoleMenuRepository.save(menu);
			} catch (Exception e) {
				log.error("Error adding menu for role COLLABORATOR");
			}
			try {
				log.info("Adding menu for role USER");
				ConsoleMenu menu = new ConsoleMenu();
				menu.setId("3");
				menu.setJson(loadFromResources("menu_user.json"));
				menu.setRoleType(roleRepository.findById(Role.Type.USER.toString()));
				this.consoleMenuRepository.save(menu);
			} catch (Exception e) {
				log.error("Error adding menu for role USER");
			}
		}
	}

	private String loadFromResources(String name) {
		try {
			return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(name).toURI())));

		} catch (Exception e) {
			log.error("**********************************************");
			log.error("Error loading resource: " + name + ".Please check if this error affect your database");
			log.error(e.getMessage());
			return null;
		}
	}

	public void init_Dashboard() {
		log.info("init Dashboard");
		List<Dashboard> dashboards = this.dashboardRepository.findAll();
		if (dashboards.isEmpty()) {
			log.info("No dashboards...adding");
			Dashboard dashboard = new Dashboard();
			dashboard.setId("1");
			dashboard.setModel("Model Dashboard Master");
			dashboard.setUser(getUserCollaborator());
			dashboard.setName("Dashboard Master");
			dashboard.setDashboardType(this.dashboardTypeRepository.findAll().get(0));
			dashboardRepository.save(dashboard);
		}
	}

	private User getUserCollaborator() {
		if (userCollaborator == null)
			userCollaborator = this.userCDBRepository.findByUserId("collaborator");
		return userCollaborator;
	}

	private User getUserAdministrator() {
		if (userAdministrator == null)
			userAdministrator = this.userCDBRepository.findByUserId("administrator");
		return userAdministrator;
	}

	public void init_DashboardType() {

		log.info("init DashboardType");
		List<DashboardType> dashboardTypes = this.dashboardTypeRepository.findAll();
		if (dashboardTypes.isEmpty()) {
			log.info("No dashboards...adding");
			DashboardType dashboardType = new DashboardType();
			dashboardType.setId(1);
			dashboardType.setModel("Modelo 1");
			dashboardType.setUser(getUserCollaborator());
			dashboardType.setPublic(true);
			dashboardType.setType("Tipo de modelo 1");
			dashboardTypeRepository.save(dashboardType);

		}

	}

	public void init_DataModel() {

		log.info("init DataModel");
		List<DataModel> dataModels = this.dataModelRepository.findAll();
		if (dataModels.isEmpty()) {
			log.info("No DataModels ...");
			DataModel dataModel = new DataModel();
			dataModel.setName("Alarm");
			dataModel.setTypeEnum(DataModel.MainType.SmartCities);
			dataModel.setJsonSchema(loadFromResources("DataModel_Alarm.json"));
			dataModel.setDescription("Base Alarm: assetId, timestamp, severity, source, details and status..");
			dataModel.setLabels("Alarm,General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Audit");
			dataModel.setTypeEnum(DataModel.MainType.SmartCities);
			dataModel.setJsonSchema(loadFromResources("DataModel_Audit.json"));
			dataModel.setDescription("Base Audit");
			dataModel.setLabels("Audit,General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Device");
			dataModel.setTypeEnum(DataModel.MainType.IoT);
			dataModel.setJsonSchema(loadFromResources("DataModel_Device.json"));
			dataModel.setDescription("Base Device");
			dataModel.setLabels("Audit,General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("EmptyBase");
			dataModel.setTypeEnum(DataModel.MainType.General);
			dataModel.setJsonSchema(loadFromResources("DataModel_EmptyBase.json"));
			dataModel.setDescription("Base DataModel");
			dataModel.setLabels("General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Feed");
			dataModel.setTypeEnum(DataModel.MainType.IoT);
			dataModel.setJsonSchema(loadFromResources("DataModel_Feed.json"));
			dataModel.setDescription("Base Feed");
			dataModel.setLabels("Audit,General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Twitter");
			dataModel.setTypeEnum(DataModel.MainType.Twitter);
			dataModel.setJsonSchema(loadFromResources("DataModel_Twitter.json"));
			dataModel.setDescription("Twitter DataModel");
			dataModel.setLabels("Twitter,Social Media");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("WasteContainer");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_WasteContainer.json"));
			dataModel.setDescription("GSMA WasteContainer");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//

		}

	}

	public void init_GadgetDataModel() {

		log.info("init GadgetDataModel");
		List<GadgetDataModel> gadgetDataModels = this.gadgetDataModelRepository.findAll();
		if (gadgetDataModels.isEmpty()) {
			log.info("No gadget data models ...");
			GadgetDataModel gadgetDM = new GadgetDataModel();
			gadgetDM.setIdentification("1");
			gadgetDM.setImage("ea02 2293 e344 8e16 df15 86b6".getBytes());
			gadgetDM.setUser(getUserCollaborator());
			gadgetDM.setPublic(true);
			gadgetDataModelRepository.save(gadgetDM);
		}

	}

	public void init_GadgetMeasure() {

		log.info("init GadgetMeasure");
		List<GadgetMeasure> gadgetMeasures = this.gadgetMeasureRepository.findAll();
		if (gadgetMeasures.isEmpty()) {
			log.info("No gadget measures ...");
			GadgetMeasure gadgetMeasure = new GadgetMeasure();
			gadgetMeasure.setAttribute("Attr1");
			List<Gadget> gadgets = this.gadgetRepository.findAll();
			Gadget gadget;
			if (gadgets.isEmpty()) {
				log.info("No gadgets ...");
				gadget = new Gadget();
				gadget.setDbType("DBC");
				gadget.setUser(getUserCollaborator());
				gadget.setPublic(true);
				gadget.setName("Gadget1");
				gadget.setType("Tipo 1");

				gadgetRepository.save(gadget);
			} else {
				gadget = gadgetRepository.findAll().get(0);
			}
			gadgetMeasure.setGadget(gadget);
			gadgetMeasureRepository.save(gadgetMeasure);
		}

	}

	public void init_GadgetQuery() {

		log.info("init GadgetQuery");
		List<GadgetQuery> gadgetQuerys = this.gadgetQueryRepository.findAll();
		if (gadgetQuerys.isEmpty()) {
			log.info("No gadget querys ...");
			GadgetQuery gadgetQuery = new GadgetQuery();
			gadgetQuery.setQuery("Query1");
			List<Gadget> gadgets = this.gadgetRepository.findAll();
			Gadget gadget;
			if (gadgets.isEmpty()) {
				log.info("No gadgets ...");
				gadget = new Gadget();
				gadget.setDbType("DBC");
				gadget.setUser(getUserCollaborator());
				gadget.setPublic(true);
				gadget.setName("Gadget1");
				gadget.setType("Tipo 1");

				gadgetRepository.save(gadget);
			} else {
				gadget = gadgetRepository.findAll().get(0);
			}
			gadgetQuery.setGadget(gadget);
			gadgetQueryRepository.save(gadgetQuery);
		}

	}

	public void init_Gadget() {

		log.info("init Gadget");
		List<Gadget> gadgets = this.gadgetRepository.findAll();
		if (gadgets.isEmpty()) {
			log.info("No gadgets ...");
			Gadget gadget = new Gadget();
			gadget.setDbType("RTDB");
			gadget.setUser(getUserCollaborator());
			gadget.setPublic(true);
			gadget.setName("Gadget Example");
			gadget.setType("Type 1");

			gadgetRepository.save(gadget);
		}

	}

	public void init_GeneratorType() {

		log.info("init GeneratorType");
		List<GeneratorType> types = this.generatorTypeRepository.findAll();
		if (types.isEmpty()) {

			log.info("No generator types found..adding");
			GeneratorType type = new GeneratorType();
			type.setId(1);
			type.setIdentification("Random Number");
			type.setKeyType("desde,number;hasta,number;numdecimal,number");
			type.setKeyValueDef("desde,100;hasta,10000;numdecimal,0");
			this.generatorTypeRepository.save(type);

		}

	}

	public void init_InstanceGenerator() {

		log.info("init InstanceGenerator");
		List<InstanceGenerator> generators = this.instanceGeneratorRepository.findAll();
		if (generators.isEmpty()) {
			log.info("No instance generators found...adding");
			InstanceGenerator generator = new InstanceGenerator();
			generator.setId(1);
			generator.setValues("desde,0;hasta,400");
			generator.setIdentification("Integer 0 a 400");
			GeneratorType type = this.generatorTypeRepository.findById(4);
			if (type == null) {
				type = new GeneratorType();
				type.setId(1);
				type.setIdentification("Random Number");
				type.setKeyType("desde,number;hasta,number;numdecimal,number");
				type.setKeyValueDef("desde,100;hasta,10000;numdecimal,0");
				this.generatorTypeRepository.save(type);
			}
			generator.setGeneratorType(type);
			this.instanceGeneratorRepository.save(generator);

		}

	}

	public void init_OntologyCategory() {

		log.info("init OntologyCategory");
		List<OntologyCategory> categories = this.ontologyCategoryRepository.findAll();
		if (categories.isEmpty()) {
			log.info("No ontology categories found..adding");
			OntologyCategory category = new OntologyCategory();
			category.setId(1);
			category.setIdentificator("ontologias_categoria_cultura");
			category.setDescription("ontologias_categoria_cultura_desc");
			this.ontologyCategoryRepository.save(category);
		}

	}

	public void init_OntologyEmulator() {
		log.info("init OntologyEmulator");
		List<OntologyEmulator> oes = this.ontologyEmulatorRepository.findAll();
		if (oes.isEmpty()) {
			log.info("No ontology emulators, adding...");
			OntologyEmulator oe = new OntologyEmulator();
			oe.setMeasures("2.5,3.4,4.5");
			oe.setIdentification("Id 1");
			oe.setUser(getUserCollaborator());
			oe.setInsertEvery(5);
			Ontology o = this.ontologyRepository.findAll().get(0);
			if (o == null) {
				o = new Ontology();
				o.setJsonSchema("{}");
				o.setIdentification("Id 1");
				o.setDescription("Description");
				o.setActive(true);
				o.setRtdbClean(true);
				o.setPublic(true);
				ontologyRepository.save(o);

			}
			oe.setOntology(o);
			this.ontologyEmulatorRepository.save(oe);

		}

	}

	public void init_Ontology() {

		log.info("init Ontology");
		List<Ontology> ontologies = this.ontologyRepository.findAll();
		if (ontologies.isEmpty()) {
			log.info("No ontologies..adding");
			Ontology ontology = new Ontology();
			ontology.setId("1");
			ontology.setJsonSchema("{}");
			ontology.setIdentification("Ontology Master");
			ontology.setDescription("Ontology created as Master Data");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setUser(getUserCollaborator());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setId("2");
			ontology.setJsonSchema("{Data:,Temperature:}");
			ontology.setDescription("Ontology Example");
			ontology.setIdentification("Ontology crated as example");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setUser(getUserCollaborator());
			ontologyRepository.save(ontology);

		}

	}

	public void init_OntologyUserAccess() {
		log.info("init OntologyUserAccess");
		/*
		 * List<OntologyUserAccess> users=this.ontologyUserAccessRepository.findAll();
		 * if(users.isEmpty()) { log.info("No users found...adding"); OntologyUserAccess
		 * user=new OntologyUserAccess(); user.setUser("6");
		 * user.setOntology(ontologyRepository.findAll().get(0));
		 * user.setOntologyUserAccessTypeId(ontologyUserAccessTypeId);
		 * this.ontologyUserAccessRepository.save(user); }
		 */
	}

	public void init_OntologyUserAccessType() {

		log.info("init OntologyUserAccessType");
		List<OntologyUserAccessType> types = this.ontologyUserAccessTypeRepository.findAll();
		if (types.isEmpty()) {
			log.info("No user access types found...adding");
			OntologyUserAccessType type = new OntologyUserAccessType();
			type.setId(1);
			type.setName("ALL");
			type.setDescription("Todos los permisos");
			this.ontologyUserAccessTypeRepository.save(type);
			type = new OntologyUserAccessType();
			type.setId(2);
			type.setName("QUERY");
			type.setDescription("Todos los permisos");
			this.ontologyUserAccessTypeRepository.save(type);
			type = new OntologyUserAccessType();
			type.setId(3);
			type.setName("INSERT");
			type.setDescription("Todos los permisos");
			this.ontologyUserAccessTypeRepository.save(type);
		}

	}

	public void init_RoleUser() {
		log.info("init init_RoleUser");
		List<Role> types = this.roleRepository.findAll();
		if (types.isEmpty()) {
			try {

				log.info("No roles en tabla.Adding...");
				Role type = new Role();
				type.setIdEnum(Role.Type.ADMINISTRATOR);
				type.setName("ROLE_ADMINISTRATOR");
				type.setDescription("Administrator of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.COLLABORATOR);
				type.setName("ROLE_COLLABORATOR");
				type.setDescription("Advanced User of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.USER);
				type.setName("ROLE_USER");
				type.setDescription("Basic User of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.ANALYTICS);
				type.setName("ROLE_ANALYTICS");
				type.setDescription("Analytics User of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.PARTNER);
				type.setName("ROLE_PARTNER");
				type.setDescription("Partner in the Platform");
				roleRepository.save(type);
				//
				//
				type = new Role();
				type.setIdEnum(Role.Type.SYS_ADMIN);
				type.setName("ROLE_SYS_ADMIN");
				type.setDescription("System Administradot of the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.OPERATIONS);
				type.setName("ROLE_OPERATIONS");
				type.setDescription("Operations for the Platform");
				roleRepository.save(type);
				//
				type = new Role();
				type.setIdEnum(Role.Type.DEVOPS);
				type.setName("ROLE_DEVOPS");
				type.setDescription("DevOps for the Platform");
				roleRepository.save(type);
				//
				// UPDATE of the ROLE_ANALYTICS
				Role typeSon = roleRepository.findById(Role.Type.ANALYTICS.toString());
				Role typeParent = roleRepository.findById(Role.Type.COLLABORATOR.toString());
				typeSon.setRoleParent(typeParent);
				roleRepository.save(typeSon);

			} catch (Exception e) {
				log.error("Error initRoleType:" + e.getMessage());
				roleRepository.deleteAll();
				throw new RuntimeException("Error creating Roles...Stopping");
			}

		}
	}

	public void init_Token() {

		log.info("init token");
		List<Token> tokens = this.tokenRepository.findAll();
		if (tokens.isEmpty()) {
			log.info("No Tokens, adding ...");
			if (this.clientPlatformRepository.findAll().isEmpty())
				throw new RuntimeException("You need to create ClientPlatform before Token");

			ClientPlatform client = this.clientPlatformRepository.findAll().get(0);
			Set<Token> hashSetTokens = new HashSet<Token>();

			Token token = new Token();
			token.setClientPlatform(client);
			token.setToken("acbca01b-da32-469e-945d-05bb6cd1552e");
			token.setActive(true);
			hashSetTokens.add(token);
			client.setTokens(hashSetTokens);
			tokenRepository.save(token);
		}

	}
	
	public void init_UserToken() {

		log.info("init user token");
		List<UserToken> tokens = this.userTokenRepository.findAll();
		if (tokens.isEmpty()) {
			
			try {
				Token token = this.tokenRepository.findAll().get(0);
				User user = this.userCDBRepository.findAll().get(0);
				UserToken userToken = new UserToken();
				
				userToken.setToken(token);
				userToken.setUser(user);
				userToken.setCreatedAt(Calendar.getInstance().getTime());
				
				
				userTokenRepository.save(userToken);
			} catch (Exception e) {
				log.info("Could not create user token");
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
				type.setRole(this.roleRepository.findById(Role.Type.ADMINISTRATOR.toString()));
			//type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("collaborator");
				type.setPassword("changeIt!");
				type.setFullName("Generic Advanced User of the Platform");
				type.setEmail("collaborator@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.COLLABORATOR.toString()));
				//type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("user");
				type.setPassword("changeIt!");
				type.setFullName("Generic User of the Platform");
				type.setEmail("user@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.USER.toString()));
				//type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("analytics");
				type.setPassword("changeIt!");
				type.setFullName("Generic Analytics User of the Platform");
				type.setEmail("analytics@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.ANALYTICS.toString()));
			//	type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("partner");
				type.setPassword("changeIt!");
				type.setFullName("Generic Partner of the Platform");
				type.setEmail("partner@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.PARTNER.toString()));
				//type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("sysadmin");
				type.setPassword("changeIt!");
				type.setFullName("Generic SysAdmin of the Platform");
				type.setEmail("sysadmin@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.SYS_ADMIN.toString()));
				//type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//
				type = new User();
				type.setUserId("operations");
				type.setPassword("changeIt!");
				type.setFullName("Operations of the Platform");
				type.setEmail("operations@sofia2.com");
				type.setActive(true);
				type.setRole(this.roleRepository.findById(Role.Type.OPERATIONS.toString()));
				//type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//
			} catch (Exception e) {
				log.error("Error UserCDB:" + e.getMessage());
				userCDBRepository.deleteAll();
			}
		}
	}

	/*
	 * public void init_Template() { log.info("init template"); List<Template>
	 * templates= this.templateRepository.findAll();
	 * 
	 * if (templates.isEmpty()) { try {
	 * 
	 * log.info("No templates Adding..."); Template template= new Template();
	 * template.setIdentification("GSMA-Weather Forecast"); template.setType("0");
	 * template.
	 * setJsonschema("{    '$schema': 'http://json-schema.org/draft-04/schema#', 'title': 'Weather Forecast',    'type': 'object',    'properties': {        'id': {            'type': 'string'        },        'type': {            'type': 'string'        },        'address': {            'type': 'object',            'properties': {                'addressCountry': {                    'type': 'string'                },                'postalCode': {                    'type': 'string'                },                'addressLocality': {                    'type': 'string'                }            },            'required': [                'addressCountry',                'postalCode',                'addressLocality'            ]        },        'dataProvider': {            'type': 'string'        },        'dateIssued': {            'type': 'string'        },        'dateRetrieved': {            'type': 'string'        },        'dayMaximum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'dayMinimum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'feelsLikeTemperature': {            'type': 'integer'        },        'precipitationProbability': {            'type': 'number'        },        'relativeHumidity': {            'type': 'number'        },        'source': {            'type': 'string'        },        'temperature': {            'type': 'integer'        },        'validFrom': {            'type': 'string'        },        'validTo': {            'type': 'string'        },        'validity': {            'type': 'string'        },        'weatherType': {            'type': 'string'        },        'windDirection': {            'type': 'null'        },        'windSpeed': {            'type': 'integer'        }    },    'required': [        'id',        'type',        'address',        'dataProvider',        'dateIssued',        'dateRetrieved',        'dayMaximum',        'dayMinimum',        'feelsLikeTemperature',        'precipitationProbability',        'relativeHumidity',        'source',        'temperature',        'validFrom',        'validTo',        'validity',        'weatherType',        'windDirection',        'windSpeed'    ]}"
	 * ); template.
	 * setDescription("This contains a harmonised description of a Weather Forecast."
	 * ); template.setCategory("plantilla_categoriaGSMA");
	 * template.setIsrelational(false); templateRepository.save(template); ///
	 * template=new Template(); template.setIdentification("TagsProjectBrandwatch");
	 * template.setType("1"); template.
	 * setJsonschema("{  '$schema': 'http://json-schema.org/draft-04/schema#',  'title': 'TagsProjectBrandwatch Schema',  'type': 'object',  'required': [    'TagsProjectBrandwatch'  ],  'properties': {    'TagsProjectBrandwatch': {      'type': 'string',      '$ref': '#/datos'    }  },  'datos': {    'description': 'Info TagsProjectBrandwatch',    'type': 'object',    'required': [      'id',      'name'    ],    'properties': {      'id': {        'type': 'integer'      },      'name': {        'type': 'string'      }    }  }}"
	 * ); template.
	 * setDescription("Plantilla para almacenar los TAG definidos en un PROJECT Brandwatch"
	 * ); template.setCategory("plantilla_categoriaSocial");
	 * template.setIsrelational(false); templateRepository.save(template);
	 * 
	 * 
	 * 
	 * } catch (Exception e) { templateRepository.deleteAll(); }
	 * 
	 * } }
	 */

}