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

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
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
import com.indracompany.sofia2.config.model.ConsoleMenu;
import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetDatasource;
import com.indracompany.sofia2.config.model.GadgetMeasure;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyCategory;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
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
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.config.repository.GadgetDatasourceRepository;
import com.indracompany.sofia2.config.repository.GadgetMeasureRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.OntologyCategoryRepository;
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
	private static Token tokenAdministrator = null;
	private static Ontology ontologyAdministrator = null;
	private static GadgetDatasource gadgetDatasourceAdministrator = null;
	private static Gadget gadgetAdministrator = null;

	@Autowired
	ClientConnectionRepository clientConnectionRepository;
	@Autowired
	ClientPlatformRepository clientPlatformRepository;
	@Autowired
	ClientPlatformOntologyRepository clientPlatformOntologyRepository;
	@Autowired
	ConsoleMenuRepository consoleMenuRepository;
	@Autowired
	DataModelRepository dataModelRepository;
	@Autowired
	DashboardRepository dashboardRepository;
	@Autowired
	GadgetMeasureRepository gadgetMeasureRepository;
	@Autowired
	GadgetDatasourceRepository gadgetDatasourceRepository;
	@Autowired
	GadgetRepository gadgetRepository;
	@Autowired
	OntologyRepository ontologyRepository;
	@Autowired
	OntologyCategoryRepository ontologyCategoryRepository;

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
	FlowDomainRepository domainRepository;

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

		init_OntologyCategory();
		log.info("OK init_OntologyCategory");

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
		init_Dashboard();
		log.info("OK init_Dashboard");
		init_Gadget();
		log.info("OK init_Gadget");
		init_GadgetDatasource();
		log.info("OK init_GadgetDatasource");
		init_GadgetMeasure();
		log.info("OK init_GadgetMeasure");

		init_Menu_ControlPanel();
		log.info("OK init_ConsoleMenu");
		init_Configuration();
		log.info("OK init_Configuration");

		init_FlowDomain();
		log.info("OK init_FlowDomain");
	}

	private void init_FlowDomain() {
		log.info("init_FlowDomain");
		// Domain for administrator
		List<FlowDomain> domains = domainRepository.findAll();
		if (domains == null || domains.isEmpty()) {
			FlowDomain domain = new FlowDomain();
			domain.setActive(true);
			domain.setIdentification("adminDomain");
			domain.setUser(userCDBRepository.findByUserId("administrator"));
			domain.setHome("/tmp/administrator");
			domain.setState("START");
			domain.setPort(8000);
			domain.setServicePort(7000);
			domainRepository.save(domain);
			// Domain for developer
			domain = new FlowDomain();
			domain.setActive(true);
			domain.setIdentification("devDomain");
			domain.setUser(userCDBRepository.findByUserId("developer"));
			domain.setHome("/tmp/developer");
			domain.setState("START");
			domain.setPort(8001);
			domain.setServicePort(7001);
			domainRepository.save(domain);
		}
	}

	private void init_Configuration() {
		log.info("init_Configuration");
		if (this.configurationRepository.count() == 0) {

			Configuration config = new Configuration();
			config = new Configuration();
			config.setType(Configuration.Type.TwitterConfiguration);
			config.setUser(getUserAdministrator());
			config.setEnvironment("dev");
			config.setYmlConfig(loadFromResources("TwitterConfiguration.yml"));
			this.configurationRepository.save(config);
			//
			config = new Configuration();
			config.setType(Configuration.Type.TwitterConfiguration);
			config.setUser(getUserAdministrator());
			config.setEnvironment("default");
			config.setSuffix("lmgracia");
			config.setDescription("Twitter");
			config.setYmlConfig(loadFromResources("TwitterConfiguration.yml"));
			this.configurationRepository.save(config);
			//

			config = new Configuration();
			config.setType(Configuration.Type.EndpointModulesConfiguration);
			config.setUser(getUserAdministrator());
			config.setEnvironment("default");
			config.setDescription("Endpoints default profile");
			config.setYmlConfig(loadFromResources("EndpointModulesConfigurationDefault.yml"));
			this.configurationRepository.save(config);
			//
			//

			config = new Configuration();
			config.setType(Configuration.Type.EndpointModulesConfiguration);
			config.setUser(getUserAdministrator());
			config.setEnvironment("docker");
			config.setDescription("Endpoints docker profile");
			config.setYmlConfig(loadFromResources("EndpointModulesConfigurationDocker.yml"));
			this.configurationRepository.save(config);
			//

			config = new Configuration();
			config.setType(Configuration.Type.MailConfiguration);
			config.setUser(getUserAdministrator());
			config.setEnvironment("default");
			config.setYmlConfig(loadFromResources("MailConfiguration.yml"));
			this.configurationRepository.save(config);
			//

			config = new Configuration();
			config.setType(Configuration.Type.RTDBConfiguration);
			config.setUser(getUserAdministrator());
			config.setEnvironment("default");
			config.setYmlConfig(loadFromResources("RTDBConfiguration.yml"));
			this.configurationRepository.save(config);
			//

			config = new Configuration();
			config.setType(Configuration.Type.MonitoringConfiguration);
			config.setUser(getUserAdministrator());
			config.setEnvironment("default");
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
	// List<ClientConnection> clients=
	// this.clientConnectionRepository.findAll();
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
			cpo.setAccesEnum(ClientPlatformOntology.AccessType.ALL);
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
			client.setUser(getUserDeveloper());
			client.setIdentification("Client-MasterData");
			client.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
			client.setDescription("ClientPatform created as MasterData");
			clientPlatformRepository.save(client);
			client = new ClientPlatform();
			client.setId("2");
			client.setUser(getUserDeveloper());
			client.setIdentification("GTKP-Example");
			client.setEncryptionKey("f9dfe72e-7082-4fe8-ba37-3f569b30a691");
			client.setDescription("ClientPatform created as Example");
			clientPlatformRepository.save(client);

		}

	}

	public void init_Menu_ControlPanel() {
		log.info("init ConsoleMenu");
		List<ConsoleMenu> menus = this.consoleMenuRepository.findAll();

		if (!menus.isEmpty()) {
			this.consoleMenuRepository.deleteAll();
		}

		log.info("No menu elents found...adding");
		try {
			log.info("Adding menu for role ADMIN");
			ConsoleMenu menu = new ConsoleMenu();
			menu.setId("1");
			menu.setJson(loadFromResources("menu_admin.json"));
			menu.setRoleType(roleRepository.findById(Role.Type.ROLE_ADMINISTRATOR.toString()));
			this.consoleMenuRepository.save(menu);
		} catch (Exception e) {
			log.error("Error adding menu for role ADMIN");
		}
		try {
			log.info("Adding menu for role DEVELOPER");
			ConsoleMenu menu = new ConsoleMenu();
			menu.setId("2");
			menu.setJson(loadFromResources("menu_developer.json"));
			menu.setRoleType(roleRepository.findById(Role.Type.ROLE_DEVELOPER.toString()));
			this.consoleMenuRepository.save(menu);
		} catch (Exception e) {
			log.error("Error adding menu for role DEVELOPER");
		}
		try {
			log.info("Adding menu for role USER");
			ConsoleMenu menu = new ConsoleMenu();
			menu.setId("3");
			menu.setJson(loadFromResources("menu_user.json"));
			menu.setRoleType(roleRepository.findById(Role.Type.ROLE_USER.toString()));
			this.consoleMenuRepository.save(menu);
		} catch (Exception e) {
			log.error("Error adding menu for role USER");
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

	public void init_Dashboard() {
		log.info("init Dashboard");
		List<Dashboard> dashboards = this.dashboardRepository.findAll();
		if (dashboards.isEmpty()) {
			log.info("No dashboards...adding");
			Dashboard dashboard = new Dashboard();
			dashboard.setIdentification("TempDashboard");
			dashboard.setDescription("Dashboard show temperatures around the country");
			dashboard.setJsoni18n("");
			dashboard.setCustomcss("");
			dashboard.setCustomjs("");
			dashboard.setPublic(true);
			dashboard.setUser(getUserAdministrator());

			dashboardRepository.save(dashboard);
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

	private Token getTokenAdministrator() {
		if (tokenAdministrator == null)
			tokenAdministrator = this.tokenRepository.findByToken("acbca01b-da32-469e-945d-05bb6cd1552e");
		return tokenAdministrator;
	}

	private Ontology getOntologyAdministrator() {
		if (ontologyAdministrator == null)
			ontologyAdministrator = this.ontologyRepository.findByIdentification("OntologyMaster");
		return ontologyAdministrator;
	}

	private GadgetDatasource getGadgetDatasourceAdministrator() {
		if (gadgetDatasourceAdministrator == null)
			gadgetDatasourceAdministrator = this.gadgetDatasourceRepository.findAll().get(0);
		return gadgetDatasourceAdministrator;
	}

	private Gadget getGadgetAdministrator() {
		if (gadgetAdministrator == null)
			gadgetAdministrator = this.gadgetRepository.findAll().get(0);
		return gadgetAdministrator;

	}

	public void init_DataModel() {

		log.info("init DataModel");
		List<DataModel> dataModels = this.dataModelRepository.findAll();
		if (dataModels.isEmpty()) {
			log.info("No DataModels ...");
			DataModel dataModel = new DataModel();
			dataModel.setName("Alarm");
			dataModel.setTypeEnum(DataModel.MainType.General);
			dataModel.setJsonSchema(loadFromResources("DataModel_Alarm.json"));
			dataModel.setDescription("Base Alarm: assetId, timestamp, severity, source, details and status..");
			dataModel.setLabels("Alarm,General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Audit");
			dataModel.setTypeEnum(DataModel.MainType.General);
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
			dataModel.setTypeEnum(DataModel.MainType.SocialMedia);
			dataModel.setJsonSchema(loadFromResources("DataModel_Twitter_Temp.json"));
			dataModel.setDescription("Twitter DataModel");
			dataModel.setLabels("Twitter,Social Media");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("BasicSensor");
			dataModel.setTypeEnum(DataModel.MainType.IoT);
			dataModel.setJsonSchema(loadFromResources("DataModel_BasicSensor.json"));
			dataModel.setDescription("DataModel for sensor sending measures for an assetId");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-AirQualityObserved");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-AirQualityObserved.json"));
			dataModel.setDescription("An observation of air quality conditions at a certain place and time");
			dataModel.setLabels("General,IoT,GSMA,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-AirQualityStation");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-AirQualityStation.json"));
			dataModel.setDescription("Air Quality Station observing quality conditions at a certain place and time");
			dataModel.setLabels("General,IoT,GSMA,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-AirQualityThreshold");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-AirQualityThreshold.json"));
			dataModel.setDescription(
					"Provides the air quality thresholds in Europe. Air quality thresholds allow to calculate an air quality index (AQI).");
			dataModel.setLabels("General,IoT,GSMA,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-Device");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-Device.json"));
			dataModel.setDescription(
					"A Device is a tangible object which contains some logic and is producer and/or consumer of data. A Device is always assumed to be capable of communicating electronically via a network.");
			dataModel.setLabels("General,IoT,GSMA,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-KPI");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-KPI.json"));
			dataModel.setDescription(
					"Key Performance Indicator (KPI) is a type of performance measurement. KPIs evaluate the success of an organization or of a particular activity in which it engages.");
			dataModel.setLabels("General,IoT,GSMA,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-OffstreetParking");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-OffstreetParking.json"));
			dataModel.setDescription(
					"A site, off street, intended to park vehicles, managed independently and with suitable and clearly marked access points (entrances and exits).");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-Road");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-Road.json"));
			dataModel.setDescription("Contains a harmonised geographic and contextual description of a road.");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-StreetLight");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-StreetLight.json"));
			dataModel.setDescription("GSMA Model that represents an urban streetlight");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-Vehicle");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-Vehicle.json"));
			dataModel.setDescription("A harmonised description of a Vehicle");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-WasteContainer");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-WasteContainer.json"));
			dataModel.setDescription("GSMA WasteContainer");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-WeatherObserved");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-WeatherObserved.json"));
			dataModel.setDescription("An observation of weather conditions at a certain place and time.");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("GSMA-WeatherStation");
			dataModel.setTypeEnum(DataModel.MainType.GSMA);
			dataModel.setJsonSchema(loadFromResources("DataModel_GSMA-WeatherStation.json"));
			dataModel.setDescription("GSMA Weather Station Model");
			dataModel.setLabels("General,IoT,Smart Cities");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Request");
			dataModel.setTypeEnum(DataModel.MainType.General);
			dataModel.setJsonSchema(loadFromResources("DataModel_Request.json"));
			dataModel.setDescription("Request for something.");
			dataModel.setLabels("General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Response");
			dataModel.setTypeEnum(DataModel.MainType.General);
			dataModel.setJsonSchema(loadFromResources("DataModel_Response.json"));
			dataModel.setDescription("Response for a request.");
			dataModel.setLabels("General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("MobileElement");
			dataModel.setTypeEnum(DataModel.MainType.IoT);
			dataModel.setJsonSchema(loadFromResources("DataModel_MobileElement.json"));
			dataModel.setDescription("Generic Mobile Element representation.");
			dataModel.setLabels("General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Log");
			dataModel.setTypeEnum(DataModel.MainType.General);
			dataModel.setJsonSchema(loadFromResources("DataModel_Log.json"));
			dataModel.setDescription("Log representation.");
			dataModel.setLabels("General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
			//
			dataModel = new DataModel();
			dataModel.setName("Issue");
			dataModel.setTypeEnum(DataModel.MainType.General);
			dataModel.setJsonSchema(loadFromResources("DataModel_Issue.json"));
			dataModel.setDescription("Issue representation.");
			dataModel.setLabels("General,IoT");
			dataModel.setUser(getUserAdministrator());
			dataModelRepository.save(dataModel);
		}
	}

	public void init_Gadget() {
		log.info("init Gadget");
		List<Gadget> gadgets = this.gadgetRepository.findAll();
		if (gadgets.isEmpty()) {
			log.info("No gadgets ...");
			Gadget gadget = new Gadget();

			gadget.setIdentification("My Gadget");
			gadget.setPublic(true);
			gadget.setDescription("This is my new RT gadget for temperature evolution");
			gadget.setType("Area");
			gadget.setConfig("");
			gadget.setUser(getUserAdministrator());
			gadgetRepository.save(gadget);
		}
	}

	public void init_GadgetDatasource() {

		log.info("init GadgetDatasource");
		List<GadgetDatasource> gadgetDatasource = this.gadgetDatasourceRepository.findAll();
		if (gadgetDatasource.isEmpty()) {
			log.info("No gadget querys ...");
			GadgetDatasource gadgetDatasources = new GadgetDatasource();
			gadgetDatasources.setId("1");
			gadgetDatasources.setIdentification("DsRawRestaurants");
			gadgetDatasources.setMode("Query");
			gadgetDatasources.setQuery("select * from Restaurants limit 100");
			gadgetDatasources.setDbtype("RTDB");
			gadgetDatasources.setRefresh(0);
			gadgetDatasources.setOntology(null);
			gadgetDatasources.setMaxvalues(150);
			gadgetDatasources.setConfig("[]");
			gadgetDatasources.setUser(getUserAdministrator());
			gadgetDatasourceRepository.save(gadgetDatasources);
		}

	}

	public void init_GadgetMeasure() {

		log.info("init GadgetMeasure");
		List<GadgetMeasure> gadgetMeasures = this.gadgetMeasureRepository.findAll();
		if (gadgetMeasures.isEmpty()) {
			log.info("No gadget measures ...");
			GadgetMeasure gadgetMeasure = new GadgetMeasure();
			// inicializo el id?
			// gadgetMeasure.setId("1");
			gadgetMeasure.setDatasource(getGadgetDatasourceAdministrator());
			gadgetMeasure.setConfig("'field':'temperature','transformation':''}],'name':'Avg. Temperature'");
			gadgetMeasure.setGadget(getGadgetAdministrator());
			gadgetMeasureRepository.save(gadgetMeasure);
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

	public void init_Ontology() {

		log.info("init Ontology");
		List<Ontology> ontologies = this.ontologyRepository.findAll();
		if (ontologies.isEmpty()) {
			log.info("No ontologies..adding");
			Ontology ontology = new Ontology();
			ontology.setId("1");
			ontology.setJsonSchema("{}");
			ontology.setIdentification("OntologyMaster");
			ontology.setDescription("Ontology created as Master Data");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setId("2");
			ontology.setJsonSchema("{Data:,Temperature:}");
			ontology.setDescription("Ontology created as example");
			ontology.setIdentification("OntologyTest");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setJsonSchema(loadFromResources("OntologySchema_HelsinkiPopulation.json"));
			ontology.setDescription("Ontology HelsinkiPopulation for testing");
			ontology.setIdentification("HelsinkiPopulation");
			ontology.setActive(true);
			ontology.setRtdbClean(false);
			ontology.setRtdbToHdb(false);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setJsonSchema(loadFromResources("OntologySchema_TweetSentiment.json"));
			ontology.setDescription("TweetSentiment");
			ontology.setIdentification("TweetSentiment");
			ontology.setActive(true);
			ontology.setRtdbClean(false);
			ontology.setRtdbToHdb(false);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setJsonSchema(loadFromResources("OntologySchema_GeoAirQuality.json"));
			ontology.setDescription("Air quality retrieved from https://api.waqi.info/search");
			ontology.setIdentification("GeoAirQuality");
			ontology.setActive(true);
			ontology.setRtdbClean(false);
			ontology.setRtdbToHdb(false);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setJsonSchema(loadFromResources("OntologySchema_CityPopulation.json"));
			ontology.setDescription(
					"Population of Urban Agglomerations with 300,000 Inhabitants or More in 2014, by Country, 1950-2030 (thousands)");
			ontology.setIdentification("CityPopulation");
			ontology.setActive(true);
			ontology.setRtdbClean(false);
			ontology.setRtdbToHdb(false);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setJsonSchema(loadFromResources("OntologySchema_AirQuality_gr2.json"));
			ontology.setDescription("AirQuality_gr2");
			ontology.setIdentification("AirQuality_gr2");
			ontology.setActive(true);
			ontology.setRtdbClean(false);
			ontology.setRtdbToHdb(false);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setJsonSchema(loadFromResources("OntologySchema_AirQuality.json"));
			ontology.setDescription("AirQuality");
			ontology.setIdentification("AirQuality");
			ontology.setActive(true);
			ontology.setRtdbClean(false);
			ontology.setRtdbToHdb(false);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

			ontology = new Ontology();
			ontology.setJsonSchema(loadFromResources("OntologySchema_AirCOMeter.json"));
			ontology.setDescription("AirCOMeter");
			ontology.setIdentification("AirCOMeter");
			ontology.setActive(true);
			ontology.setRtdbClean(false);
			ontology.setRtdbToHdb(false);
			ontology.setPublic(true);
			ontology.setUser(getUserDeveloper());
			ontologyRepository.save(ontology);

		}

	}

	public void init_OntologyUserAccess() {
		log.info("init OntologyUserAccess");
		/*
		 * List<OntologyUserAccess>
		 * users=this.ontologyUserAccessRepository.findAll();
		 * if(users.isEmpty()) { log.info("No users found...adding");
		 * OntologyUserAccess user=new OntologyUserAccess(); user.setUser("6");
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
				User user = this.userCDBRepository.findAll().get(0);
				UserToken userToken = new UserToken();

				userToken.setToken("acbca01b-da32-469e-945d-05bb6cd1552e");
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

	/*
	 * public void init_Template() { log.info("init template"); List<Template>
	 * templates= this.templateRepository.findAll();
	 * 
	 * if (templates.isEmpty()) { try {
	 * 
	 * log.info("No templates Adding..."); Template template= new Template();
	 * template.setIdentification("GSMA-Weather Forecast");
	 * template.setType("0"); template.
	 * setJsonschema("{    '$schema': 'http://json-schema.org/draft-04/schema#', 'title': 'Weather Forecast',    'type': 'object',    'properties': {        'id': {            'type': 'string'        },        'type': {            'type': 'string'        },        'address': {            'type': 'object',            'properties': {                'addressCountry': {                    'type': 'string'                },                'postalCode': {                    'type': 'string'                },                'addressLocality': {                    'type': 'string'                }            },            'required': [                'addressCountry',                'postalCode',                'addressLocality'            ]        },        'dataProvider': {            'type': 'string'        },        'dateIssued': {            'type': 'string'        },        'dateRetrieved': {            'type': 'string'        },        'dayMaximum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'dayMinimum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'feelsLikeTemperature': {            'type': 'integer'        },        'precipitationProbability': {            'type': 'number'        },        'relativeHumidity': {            'type': 'number'        },        'source': {            'type': 'string'        },        'temperature': {            'type': 'integer'        },        'validFrom': {            'type': 'string'        },        'validTo': {            'type': 'string'        },        'validity': {            'type': 'string'        },        'weatherType': {            'type': 'string'        },        'windDirection': {            'type': 'null'        },        'windSpeed': {            'type': 'integer'        }    },    'required': [        'id',        'type',        'address',        'dataProvider',        'dateIssued',        'dateRetrieved',        'dayMaximum',        'dayMinimum',        'feelsLikeTemperature',        'precipitationProbability',        'relativeHumidity',        'source',        'temperature',        'validFrom',        'validTo',        'validity',        'weatherType',        'windDirection',        'windSpeed'    ]}"
	 * ); template.
	 * setDescription("This contains a harmonised description of a Weather Forecast."
	 * ); template.setCategory("plantilla_categoriaGSMA");
	 * template.setIsrelational(false); templateRepository.save(template); ///
	 * template=new Template();
	 * template.setIdentification("TagsProjectBrandwatch");
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
