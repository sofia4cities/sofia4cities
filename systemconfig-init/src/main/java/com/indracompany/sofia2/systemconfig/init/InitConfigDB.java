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

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.ClientConnection;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformContainer;
import com.indracompany.sofia2.config.model.ClientPlatformContainerType;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.ConsoleMenu;
import com.indracompany.sofia2.config.model.ConsoleMenuOption;
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
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
import com.indracompany.sofia2.config.model.RoleType;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.UserCDB;
import com.indracompany.sofia2.config.repository.ClientConnectionRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformContainerRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformContainerTypeRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.ConsoleMenuOptionRepository;
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
import com.indracompany.sofia2.config.repository.RoleTypeRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.UserCDBRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@Slf4j
@Component
public class InitConfigDB {


	@Value("${sofia2.init.configdb:false}")
	private boolean initConfigDB;
	@Autowired
	ClientConnectionRepository clientConnectionRepository;	
	@Autowired
	ClientPlatformContainerRepository clientPlatformContainerRepository;
	@Autowired
	ClientPlatformRepository clientPlatformRepository;
	@Autowired
	ClientPlatformContainerTypeRepository clientPlatformContainerTypeRepository;
	@Autowired
	ClientPlatformOntologyRepository clientPlatformOntologyRepository;	
	@Autowired
	ConsoleMenuRepository consoleMenuRepository;
	@Autowired
	ConsoleMenuOptionRepository consoleMenuOptionRepository;
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
	RoleTypeRepository roleTypeRepository;
	@Autowired
	TokenRepository tokenRepository;	
	@Autowired
	UserCDBRepository userCDBRepository;




	@PostConstruct
	public void init() {
		if (initConfigDB==true) {
			log.info("Start initConfigDB...");
			init_RoleUser();
			init_UserCDB();
			//
			init_DataModel();
			init_OntologyCategory();
			init_Ontology();
			init_OntologyUserAccess();
			init_OntologyUserAccessType();
			init_OntologyEmulator();
			init_OntologyCategory();
			init_OntologyEmulator();
			//
			init_ClientPlatformContainer();
			init_ClientPlatformContainerType();
			init_ClientConnection();
			init_ClientPlatformOntology();
			init_ClientPlatform();
			init_ClientConnection();
			//
			init_Dashboard();
			init_DashboardType();
			init_GadgetDataModel();
			init_GadgetMeasure();
			init_GadgetQuery();
			init_Gadget();
			init_GeneratorType();
			//init_InstanceGenerator();
			//
			init_ConsoleMenu();
			init_ConsoleMenuOption();
			//
			init_Token();
			
		}
		else {
			log.info("Disable Start initConfigDB...");
		}
	}
	public void init_ClientConnection() {
		log.info("init ClientConnection");
		List<ClientConnection> clients= this.clientConnectionRepository.findAll();
		ClientPlatform cp = this.clientPlatformRepository.findAll().get(0);
		if (clients.isEmpty()) {
			log.info("No clients ...");
			ClientConnection con= new ClientConnection();
			//			
			con.setClientPlatformId(cp);			
			con.setIdentification("1");
			con.setIpStrict(true);
			con.setStaticIp(false);
			con.setLastIp("192.168.1.89");
			Calendar date = Calendar.getInstance();
			con.setLastConnection(date);
			con.setClientPlatformId(cp);
			clientConnectionRepository.save(con);
		}
	}
//		List<ClientConnection> clients= this.clientConnectionRepository.findAll();
//		if (clients.isEmpty()) {
//			log.info("No clients ...");
//			ClientConnection con= new ClientConnection();
//			ClientPlatform client= new ClientPlatform();
//			client.setId("06be1962-aa27-429c-960c-d8a324eef6d4");
//			clientPlatformRepository.save(client);
//			con.setClientPlatformId(client);			
//			con.setIdentification("1");
//			con.setIpStrict(true);
//			con.setStaticIp(false);
//			con.setLastIp("192.168.1.89");
//			Calendar date = Calendar.getInstance();
//			con.setLastConnection(date);
//			clientConnectionRepository.save(con);
//		}
	
	public void init_ClientPlatformContainer() {

		log.info("init ClientPlatformContainer");
		List<ClientPlatformContainer> pcs= this.clientPlatformContainerRepository.findAll();
		if(pcs.isEmpty())
		{

			ClientPlatformContainer cpc=new ClientPlatformContainer();
			cpc.setClientConnection("6930722a-a9fa-4206-a3cf-1441a0318b39");
			cpc.setProgramName("Program 1");
			cpc.setMaxExecutionTime(5);
			cpc.setMessageFilesPrefix("msg_");
			cpc.setLogFilesPrefix("log_");
			cpc.setState("Up");
			ClientPlatform cp;
			if(!this.clientPlatformRepository.findAll().isEmpty()){
				cp=this.clientPlatformRepository.findAll().get(0);
			}else{			
				cp=new ClientPlatform();
				cp.setUserId("1");
				cp.setIdentification("ScadaTags_Alarms_kp");
				cp.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
				cp.setDescription("Kp para la insercion de alarmas de scada");
				clientPlatformRepository.save(cp);

			}
			cpc.setClientPlatformId(cp);
			Token token;
			if(!this.tokenRepository.findAll().isEmpty()){
				token=this.tokenRepository.findAll().get(0);
			}
			else{
				token=new Token();
				token.setClientPlatformId(cp);
				token.setToken("Token1");
				token.setActive(true);
				tokenRepository.save(token);
				this.tokenRepository.save(token);
			}
			
			cpc.setAuthenticationTokenId(token);
			/*
			*/
			//			
			ClientPlatformContainerType type;
			if(!this.clientPlatformContainerTypeRepository.findAll().isEmpty()){
				type=this.clientPlatformContainerTypeRepository.findAll().get(0);
			}
			else{
				type=new ClientPlatformContainerType();
				type.setId(1);
				type.setType("Python");
				clientPlatformContainerTypeRepository.save(type);
			}
			
			cpc.setClientPlatformContainerTypeId(type);
			
			this.clientPlatformContainerRepository.save(cpc);
		}
	}
	
	public void init_ClientPlatformContainerType() {
		log.info("init ClientPlatformContainerType");
		List<ClientPlatformContainerType> types = this.clientPlatformContainerTypeRepository.findAll();
		if (types.isEmpty()) {
			try {
				log.info("No types en tabla.Adding...");
				ClientPlatformContainerType type=new ClientPlatformContainerType();
				type.setId(1);
				type.setType("Python");
				clientPlatformContainerTypeRepository.save(type);
				//
				type=new ClientPlatformContainerType();
				type.setId(2);
				type.setType("Java");
				clientPlatformContainerTypeRepository.save(type);
				//
				type=new ClientPlatformContainerType();
				type.setId(3);
				type.setType("URL");
				clientPlatformContainerTypeRepository.save(type);		

			} catch (Exception e) {
				log.error("Error ClientPlatformContainerType:"+e.getMessage());
				clientPlatformContainerTypeRepository.deleteAll();
			}
		}
	}
	public void init_ClientPlatformOntology() {

		log.info("init ClientPlatformOntology");
		List<ClientPlatformOntology> cpos=this.clientPlatformOntologyRepository.findAll();
		if(cpos.isEmpty())
		{
			log.info("No Client Platform Ontologies");
			ClientPlatformOntology cpo=new ClientPlatformOntology();
			ClientPlatform cp = null;
			Ontology o=null;
			if(!this.clientPlatformRepository.findAll().isEmpty()){
				cp=this.clientPlatformRepository.findAll().get(0);
			}
			if(!this.ontologyRepository.findAll().isEmpty()){
				o=this.ontologyRepository.findAll().get(0);
			}
			if(cp==null)
			{
				cp=new ClientPlatform();
				cp.setUserId("1");
				cp.setIdentification("ScadaTags_Alarms_kp");
				cp.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
				cp.setDescription("Kp para la insercion de alarmas de scada");
				clientPlatformRepository.save(cp);			
			}
			if(o==null) {
				o=new Ontology();
				o.setJsonSchema("{}");
				o.setIdentification("Id 1");
				o.setDescription("Description");
				o.setActive(true);
				o.setRtdbClean(true);
				o.setPublic(true);
				ontologyRepository.save(o);
			}
			
			cpo.setClientPlatformId(cp);
			cpo.setOntologyId(o);
			this.clientPlatformOntologyRepository.save(cpo);
		}
	}
	public void init_ClientPlatform() {
		log.info("init ClientPlatform");
		List<ClientPlatform> clients= this.clientPlatformRepository.findAll();
		if (clients.isEmpty()) {
			log.info("No clients ...");
			ClientPlatform client= new ClientPlatform();
			client.setUserId("1");
			client.setIdentification("ScadaTags_Alarms_kp");
			client.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
			client.setDescription("Kp para la insercion de alarmas de scada");
			clientPlatformRepository.save(client);			
			client= new ClientPlatform();
			client.setUserId("6");
			client.setIdentification("GTKP-fjgcornejo");
			client.setEncryptionKey("f9dfe72e-7082-4fe8-ba37-3f569b30a691");
			clientPlatformRepository.save(client);

		}


	}
	public void init_ConsoleMenu() {

		log.info("init ConsoleMenu");

		List<ConsoleMenu> menus=this.consoleMenuRepository.findAll();

		if(menus.isEmpty())
		{
			log.info("No menu elements found...adding");
			ConsoleMenu menu=new ConsoleMenu();
			menu.setId("menu_category_ontologias_label");
			menu.setName("menu_category_ontologias_label");
			this.consoleMenuRepository.save(menu);

		}


	}
	public void init_ConsoleMenuOption() {

		List<ConsoleMenuOption> options=this.consoleMenuOptionRepository.findAll();
		if(options.isEmpty())
		{
			log.info("No menu options found...adding");
			ConsoleMenuOption option=new ConsoleMenuOption();
			option.setId("menu_item_ontologias_listar_label");
			option.setOption("menu_item_ontologias_listar_label");
			ConsoleMenu menu=this.consoleMenuRepository.findById("menu_category_ontologias_label");
			if(menu==null){
				menu=new ConsoleMenu();
				menu.setId("menu_category_ontologias_label");
				menu.setName("menu_category_ontologias_label");
				this.consoleMenuRepository.save(menu);
			}
			option.setConsoleMenuId(menu);
			this.consoleMenuOptionRepository.save(option);



		}

	}
	public void init_Dashboard() {

		log.info("init Dashboard");
		List<Dashboard> dashboards=this.dashboardRepository.findAll();
		if(dashboards.isEmpty())
		{
			log.info("No dashboards...adding");
			Dashboard dashboard= new Dashboard();
			dashboard.setModel("Modelo 1");
			dashboard.setUserId("1");
			dashboard.setName("Nombre del modelo 1");
			dashboard.setDashboardTypeId("9");
			dashboardRepository.save(dashboard);


		}


	}
	public void init_DashboardType() {

		log.info("init DashboardType");
		List<DashboardType> dashboardTypes=this.dashboardTypeRepository.findAll();
		if(dashboardTypes.isEmpty())
		{
			log.info("No dashboards...adding");
			DashboardType dashboardType= new DashboardType();
			dashboardType.setId(1);
			dashboardType.setModel("Modelo 1");
			dashboardType.setUserId("1");
			dashboardType.setPublic(true);
			dashboardType.setType("Tipo de modelo 1");
			dashboardTypeRepository.save(dashboardType);


		}


	}
	public void init_DataModel() {

		log.info("init DataModel");
		List<DataModel> dataModels= this.dataModelRepository.findAll();
		if (dataModels.isEmpty()) {
			log.info("No DataModels ...");
			DataModel dataModel= new DataModel();
			dataModel.setIdentification("GSMA-Weather Forecast");
			dataModel.setType("0");
			dataModel.setJsonSchema("{    '$schema': 'http://json-schema.org/draft-04/schema#', 'title': 'Weather Forecast',    'type': 'object',    'properties': {        'id': {            'type': 'string'        },        'type': {            'type': 'string'        },        'address': {            'type': 'object',            'properties': {                'addressCountry': {                    'type': 'string'                },                'postalCode': {                    'type': 'string'                },                'addressLocality': {                    'type': 'string'                }            },            'required': [                'addressCountry',                'postalCode',                'addressLocality'            ]        },        'dataProvider': {            'type': 'string'        },        'dateIssued': {            'type': 'string'        },        'dateRetrieved': {            'type': 'string'        },        'dayMaximum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'dayMinimum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'feelsLikeTemperature': {            'type': 'integer'        },        'precipitationProbability': {            'type': 'number'        },        'relativeHumidity': {            'type': 'number'        },        'source': {            'type': 'string'        },        'temperature': {            'type': 'integer'        },        'validFrom': {            'type': 'string'        },        'validTo': {            'type': 'string'        },        'validity': {            'type': 'string'        },        'weatherType': {            'type': 'string'        },        'windDirection': {            'type': 'null'        },        'windSpeed': {            'type': 'integer'        }    },    'required': [        'id',        'type',        'address',        'dataProvider',        'dateIssued',        'dateRetrieved',        'dayMaximum',        'dayMinimum',        'feelsLikeTemperature',        'precipitationProbability',        'relativeHumidity',        'source',        'temperature',        'validFrom',        'validTo',        'validity',        'weatherType',        'windDirection',        'windSpeed'    ]}");
			dataModel.setDescription("This contains a harmonised description of a Weather Forecast.");
			dataModel.setCategory("plantilla_categoriaGSMA");
			dataModel.setRelational(false);
			dataModelRepository.save(dataModel);
			///
			dataModel=new DataModel();
			dataModel.setIdentification("TagsProjectBrandwatch");
			dataModel.setType("1");
			dataModel.setJsonSchema("{  '$schema': 'http://json-schema.org/draft-04/schema#',  'title': 'TagsProjectBrandwatch Schema',  'type': 'object',  'required': [    'TagsProjectBrandwatch'  ],  'properties': {    'TagsProjectBrandwatch': {      'type': 'string',      '$ref': '#/datos'    }  },  'datos': {    'description': 'Info TagsProjectBrandwatch',    'type': 'object',    'required': [      'id',      'name'    ],    'properties': {      'id': {        'type': 'integer'      },      'name': {        'type': 'string'      }    }  }}");
			dataModel.setDescription("Plantilla para almacenar los TAG definidos en un PROJECT Brandwatch");
			dataModel.setCategory("plantilla_categoriaSocial");
			dataModel.setRelational(false);
			dataModelRepository.save(dataModel);

		}


	}
	public void init_GadgetDataModel() {

		log.info("init GadgetDataModel");
		List<GadgetDataModel> gadgetDataModels= this.gadgetDataModelRepository.findAll();
		if (gadgetDataModels.isEmpty()) {
			log.info("No gadget data models ...");
			GadgetDataModel gadgetDM=new GadgetDataModel();
			gadgetDM.setIdentification("1");
			gadgetDM.setImage("ea02 2293 e344 8e16 df15 86b6".getBytes());
			gadgetDM.setUserId("1");
			gadgetDM.setPublic(true);			
			gadgetDataModelRepository.save(gadgetDM);
		}


	}
	public void init_GadgetMeasure() {

		log.info("init GadgetMeasure");
		List<GadgetMeasure> gadgetMeasures= this.gadgetMeasureRepository.findAll();
		if (gadgetMeasures.isEmpty()) {
			log.info("No gadget measures ...");
			GadgetMeasure gadgetMeasure=new GadgetMeasure();
			gadgetMeasure.setAttribute("Attr1");
			List<Gadget> gadgets= this.gadgetRepository.findAll();
			Gadget gadget;
			if(gadgets.isEmpty()){
				log.info("No gadgets ...");
				gadget=new Gadget();
				gadget.setDbType("DBC");
				gadget.setUserId("6");
				gadget.setPublic(true);
				gadget.setName("Gadget1");
				gadget.setType("Tipo 1");

				gadgetRepository.save(gadget);
			}else{
				gadget=gadgetRepository.findAll().get(0);
			}
			gadgetMeasure.setGadgetId(gadget);
			gadgetMeasureRepository.save(gadgetMeasure);
		}


	}
	public void init_GadgetQuery() {

		log.info("init GadgetQuery");
		List<GadgetQuery> gadgetQuerys= this.gadgetQueryRepository.findAll();
		if (gadgetQuerys.isEmpty()) {
			log.info("No gadget querys ...");
			GadgetQuery gadgetQuery=new GadgetQuery();
			gadgetQuery.setQuery("Query1");
			List<Gadget> gadgets= this.gadgetRepository.findAll();
			Gadget gadget;
			if(gadgets.isEmpty()){
				log.info("No gadgets ...");
				gadget=new Gadget();
				gadget.setDbType("DBC");
				gadget.setUserId("6");
				gadget.setPublic(true);
				gadget.setName("Gadget1");
				gadget.setType("Tipo 1");

				gadgetRepository.save(gadget);
			}else{
				gadget=gadgetRepository.findAll().get(0);
			}
			gadgetQuery.setGadgetId(gadget);
			gadgetQueryRepository.save(gadgetQuery);
		}


	}
	public void init_Gadget() {

		log.info("init Gadget");
		List<Gadget> gadgets= this.gadgetRepository.findAll();
		if (gadgets.isEmpty()) {
			log.info("No gadgets ...");
			Gadget gadget=new Gadget();
			gadget.setDbType("DBC");
			gadget.setUserId("6");
			gadget.setPublic(true);
			gadget.setName("Gadget1");
			gadget.setType("Tipo 1");

			gadgetRepository.save(gadget);
		}


	}
	public void init_GeneratorType() {

		log.info("init GeneratorType");
		List<GeneratorType> types=this.generatorTypeRepository.findAll();
		if(types.isEmpty())
		{

			log.info("No generator types found..adding");
			GeneratorType type=new GeneratorType();
			type.setId(1);
			type.setIdentification("Random Number");
			type.setKeyType("desde,number;hasta,number;numdecimal,number");
			type.setKeyValueDef("desde,100;hasta,10000;numdecimal,0");
			this.generatorTypeRepository.save(type);

		}


	}
	public void init_InstanceGenerator() {

		log.info("init InstanceGenerator");
		List<InstanceGenerator> generators=this.instanceGeneratorRepository.findAll();
		if(generators.isEmpty())
		{
			log.info("No instance generators found...adding");
			InstanceGenerator generator=new InstanceGenerator();
			generator.setId(1);
			generator.setValues("desde,0;hasta,400");
			generator.setIdentification("Integer 0 a 400");
			GeneratorType type=this.generatorTypeRepository.findById(4);
			if(type==null)
			{
				type=new GeneratorType();
				type.setId(1);
				type.setIdentification("Random Number");
				type.setKeyType("desde,number;hasta,number;numdecimal,number");
				type.setKeyValueDef("desde,100;hasta,10000;numdecimal,0");
				this.generatorTypeRepository.save(type);
			}
			generator.setGeneratorTypeId(type);
			this.instanceGeneratorRepository.save(generator);

		}


	}
	public void init_OntologyCategory() {

		log.info("init OntologyCategory");
		List<OntologyCategory> categories=this.ontologyCategoryRepository.findAll();
		if(categories.isEmpty()) {
			log.info("No ontology categories found..adding");
			OntologyCategory category=new OntologyCategory();
			category.setId(1);
			category.setIdentificator("ontologias_categoria_cultura");
			category.setDescription("ontologias_categoria_cultura_desc");
			this.ontologyCategoryRepository.save(category);
		}


	}
	public void init_OntologyEmulator() {

		log.info("init OntologyEmulator");
		List<OntologyEmulator> oes=this.ontologyEmulatorRepository.findAll();
		if(oes.isEmpty())
		{
			log.info("No ontology emulators, adding...");
			OntologyEmulator oe=new OntologyEmulator();
			oe.setMeasures("2.5,3.4,4.5");
			oe.setIdentification("Id 1");
			oe.setUserId("1");
			oe.setInsertEvery(5);
			Ontology o=this.ontologyRepository.findAll().get(0);
			if(o==null)
			{
				o=new Ontology();
				o.setJsonSchema("{}");
				o.setIdentification("Id 1");
				o.setDescription("Description");
				o.setActive(true);
				o.setRtdbClean(true);
				o.setPublic(true);
				ontologyRepository.save(o);

			}
			oe.setOntologyId(o);
			this.ontologyEmulatorRepository.save(oe);



		}


	}
	public void init_Ontology() {

		log.info("init Ontology");
		List<Ontology> ontologies=this.ontologyRepository.findAll();
		if(ontologies.isEmpty())
		{
			log.info("No ontologies..adding");
			Ontology ontology=new Ontology();
			ontology.setJsonSchema("{}");
			ontology.setIdentification("Id 1");
			ontology.setDescription("Description");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontologyRepository.save(ontology);
			
			ontology=new Ontology();
			ontology.setJsonSchema("{Data:,Temperature:}");
			ontology.setDescription("Description");
			ontology.setIdentification("Id 2");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontologyRepository.save(ontology);

		}


	}
	
	public void init_OntologyUserAccess() {
		log.info("init OntologyUserAccess");
		/*
		List<OntologyUserAccess> users=this.ontologyUserAccessRepository.findAll();
		if(users.isEmpty())
		{
			log.info("No users found...adding");
			OntologyUserAccess user=new OntologyUserAccess();
			user.setUserId("6");
			user.setOntologyId(ontologyRepository.findAll().get(0));
			user.setOntologyUserAccessTypeId(ontologyUserAccessTypeId);
			this.ontologyUserAccessRepository.save(user);
		}
		*/
	}
	
	public void init_OntologyUserAccessType() {

		log.info("init OntologyUserAccessType");
		List<OntologyUserAccessType> types=this.ontologyUserAccessTypeRepository.findAll();
		if(types.isEmpty()){
			log.info("No user access types found...adding");
			OntologyUserAccessType type=new OntologyUserAccessType();
			type.setId(1);
			type.setName("ALL");
			type.setDescription("Todos los permisos");
			this.ontologyUserAccessTypeRepository.save(type);
			type=new OntologyUserAccessType();
			type.setId(2);
			type.setName("QUERY");
			type.setDescription("Todos los permisos");
			this.ontologyUserAccessTypeRepository.save(type);
			type=new OntologyUserAccessType();
			type.setId(3);
			type.setName("INSERT");
			type.setDescription("Todos los permisos");
			this.ontologyUserAccessTypeRepository.save(type);
		}
		


	}	

	public void init_RoleUser() {
		log.info("init ClientPlatformContainerType");
		List<RoleType> types = this.roleTypeRepository.findAll();
		if (types.isEmpty()) {
			try {

				log.info("No roles en tabla.Adding...");
				RoleType type=new RoleType();
				type.setId(1);
				type.setName("ROLE_ADMINISTRATOR");
				type.setDescription("Administrator of the Platform");
				roleTypeRepository.save(type);
				//
				type=new RoleType();
				type.setId(2);
				type.setName("ROLE_COLLABORATOR");
				type.setDescription("Advanced User of the Platform");
				roleTypeRepository.save(type);
				//
				type=new RoleType();
				type.setId(3);
				type.setName("ROLE_USER");
				type.setDescription("Basic User of the Platform");
				roleTypeRepository.save(type);
				//
				type=new RoleType();
				type.setId(4);
				type.setName("ROLE_ANALYTICS");
				type.setDescription("Analytics User of the Platform");
				//RoleType typeParent=new RoleType();
				//typeParent.setId(2);
				//type.setRoleparent(typeParent);
				roleTypeRepository.save(type);
				//
				type=new RoleType();
				type.setId(5);
				type.setName("ROLE_PARTNER");
				type.setDescription("Partner in the Platform");
				roleTypeRepository.save(type);
				//	
				//
				type=new RoleType();
				type.setId(6);
				type.setName("ROLE_SYS_ADMIN");
				type.setDescription("System Administradot of the Platform");
				roleTypeRepository.save(type);
				//
				type=new RoleType();
				type.setId(7);
				type.setName("ROLE_OPERATIONS");
				type.setDescription("Operations for the Platform");
				roleTypeRepository.save(type);
				//
				//UPDATE of the ROLE_ANALYTICS
				RoleType typeSon=roleTypeRepository.findOne(4);
				RoleType typeParent=roleTypeRepository.findOne(2);
				typeSon.setRoleparent(typeParent);
				roleTypeRepository.save(typeSon);

			} catch (Exception e) {
				log.error("Error initRoleType:"+e.getMessage());
				roleTypeRepository.deleteAll();
			}

		}
	}
	public void init_Token() {

		log.info("init token");
		List<Token> tokens= this.tokenRepository.findAll();
		if (tokens.isEmpty()) {
			log.info("No Tokens, adding ...");

			ClientPlatform client= new ClientPlatform();
			client.setId("06be1962-aa27-429c-960c-d8a324eef6d4");
			Set<Token> hashSetTokens=new HashSet<Token>();

			Token token=new Token();
			token.setClientPlatformId(client);
			token.setToken("Token1");
			token.setActive(true);
			hashSetTokens.add(token);
			client.setTokens(hashSetTokens);
			tokenRepository.save(token);
		}


	}

	public void init_UserCDB() {
		log.info("init UserCDB");
		List<UserCDB> types = this.userCDBRepository.findAll();
		if (types.isEmpty()) {
			try {
				RoleType role = new RoleType();
				role.setId(1);

				log.info("No types en tabla.Adding...");
				UserCDB type=new UserCDB();
				type.setId("1");
				type.setUserId("administrator");
				type.setPassword("changeIt!");
				type.setFullName("Generic Administrator of the Platform");
				type.setEmail("administrator@sofia2.com");
				type.setActive(true);
				type.setRole(role);
				type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//	
				type=new UserCDB();
				type.setId("2");
				type.setUserId("collaborator");
				type.setPassword("changeIt!");
				type.setFullName("Generic Advanced User of the Platform");
				type.setEmail("collaborator@sofia2.com");
				type.setActive(true);
				type.setRole(roleTypeRepository.findOne(2));
				type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//	
				type=new UserCDB();
				type.setId("3");
				type.setUserId("user");
				type.setPassword("changeIt!");
				type.setFullName("Generic User of the Platform");
				type.setEmail("user@sofia2.com");
				type.setActive(true);
				type.setRole(roleTypeRepository.findOne(3));
				type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//	
				type=new UserCDB();
				type.setId("4");
				type.setUserId("analytics");
				type.setPassword("changeIt!");
				type.setFullName("Generic Analytics User of the Platform");
				type.setEmail("analytics@sofia2.com");
				type.setActive(true);
				type.setRole(roleTypeRepository.findOne(4));
				type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//	
				type=new UserCDB();
				type.setId("5");
				type.setUserId("partner");
				type.setPassword("changeIt!");
				type.setFullName("Generic Partner of the Platform");
				type.setEmail("partner@sofia2.com");
				type.setActive(true);
				type.setRole(roleTypeRepository.findOne(5));
				type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//	
				type=new UserCDB();
				type.setId("6");
				type.setUserId("sysadmin");
				type.setPassword("changeIt!");
				type.setFullName("Generic SysAdmin of the Platform");
				type.setEmail("sysadmin@sofia2.com");
				type.setActive(true);
				type.setRole(roleTypeRepository.findOne(6));
				type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
				//	
				type=new UserCDB();
				type.setId("7");
				type.setUserId("operations");
				type.setPassword("changeIt!");
				type.setFullName("Operations of the Platform");
				type.setEmail("operations@sofia2.com");
				type.setActive(true);
				type.setRole(roleTypeRepository.findOne(7));
				type.setDateCreated(Calendar.getInstance().getTime());
				userCDBRepository.save(type);
			} 
			catch (Exception e) {
				log.error("Error UserCDB:"+e.getMessage());
				userCDBRepository.deleteAll();
			}
		}
	}

	/*	
	public void init_Template() {
		log.info("init template");
		List<Template> templates= this.templateRepository.findAll();

		if (templates.isEmpty()) {
			try {

				log.info("No templates Adding...");
				Template template= new Template();
				template.setIdentification("GSMA-Weather Forecast");
				template.setType("0");
				template.setJsonschema("{    '$schema': 'http://json-schema.org/draft-04/schema#', 'title': 'Weather Forecast',    'type': 'object',    'properties': {        'id': {            'type': 'string'        },        'type': {            'type': 'string'        },        'address': {            'type': 'object',            'properties': {                'addressCountry': {                    'type': 'string'                },                'postalCode': {                    'type': 'string'                },                'addressLocality': {                    'type': 'string'                }            },            'required': [                'addressCountry',                'postalCode',                'addressLocality'            ]        },        'dataProvider': {            'type': 'string'        },        'dateIssued': {            'type': 'string'        },        'dateRetrieved': {            'type': 'string'        },        'dayMaximum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'dayMinimum': {            'type': 'object',            'properties': {                'feelsLikeTemperature': {                    'type': 'integer'                },                'temperature': {                    'type': 'integer'                },                'relativeHumidity': {                    'type': 'number'                }            },            'required': [                'feelsLikeTemperature',                'temperature',                'relativeHumidity'            ]        },        'feelsLikeTemperature': {            'type': 'integer'        },        'precipitationProbability': {            'type': 'number'        },        'relativeHumidity': {            'type': 'number'        },        'source': {            'type': 'string'        },        'temperature': {            'type': 'integer'        },        'validFrom': {            'type': 'string'        },        'validTo': {            'type': 'string'        },        'validity': {            'type': 'string'        },        'weatherType': {            'type': 'string'        },        'windDirection': {            'type': 'null'        },        'windSpeed': {            'type': 'integer'        }    },    'required': [        'id',        'type',        'address',        'dataProvider',        'dateIssued',        'dateRetrieved',        'dayMaximum',        'dayMinimum',        'feelsLikeTemperature',        'precipitationProbability',        'relativeHumidity',        'source',        'temperature',        'validFrom',        'validTo',        'validity',        'weatherType',        'windDirection',        'windSpeed'    ]}");
				template.setDescription("This contains a harmonised description of a Weather Forecast.");
				template.setCategory("plantilla_categoriaGSMA");
				template.setIsrelational(false);
				templateRepository.save(template);
				///
				template=new Template();
				template.setIdentification("TagsProjectBrandwatch");
				template.setType("1");
				template.setJsonschema("{  '$schema': 'http://json-schema.org/draft-04/schema#',  'title': 'TagsProjectBrandwatch Schema',  'type': 'object',  'required': [    'TagsProjectBrandwatch'  ],  'properties': {    'TagsProjectBrandwatch': {      'type': 'string',      '$ref': '#/datos'    }  },  'datos': {    'description': 'Info TagsProjectBrandwatch',    'type': 'object',    'required': [      'id',      'name'    ],    'properties': {      'id': {        'type': 'integer'      },      'name': {        'type': 'string'      }    }  }}");
				template.setDescription("Plantilla para almacenar los TAG definidos en un PROJECT Brandwatch");
				template.setCategory("plantilla_categoriaSocial");
				template.setIsrelational(false);
				templateRepository.save(template);



			} catch (Exception e) {
				templateRepository.deleteAll();
			}

		}
	}
	 */

}
