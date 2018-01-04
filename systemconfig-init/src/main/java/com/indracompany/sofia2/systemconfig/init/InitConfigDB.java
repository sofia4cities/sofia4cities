package com.indracompany.sofia2.systemconfig.init;

import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.KPContainerType;
import com.indracompany.sofia2.config.model.RoleType;
import com.indracompany.sofia2.config.model.Template;
import com.indracompany.sofia2.config.model.UserCDB;
import com.indracompany.sofia2.config.repository.KPContainerTypeRepository;
import com.indracompany.sofia2.config.repository.RoleTypeRepository;
import com.indracompany.sofia2.config.repository.UserCDBRepository;
import com.indracompany.sofia2.config.repository.TemplateRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@Slf4j
@Component
public class InitConfigDB {
	
	@Autowired
	KPContainerTypeRepository kpContainerTypeRepository;	
	@Autowired
	RoleTypeRepository roleTypeRepository;
	@Autowired
	UserCDBRepository userCDBRepository;
	@Autowired
	TemplateRepository templateRepository;


	@PostConstruct
	public void init() {
		init_KPContainerType();
		init_RoleUser();
		init_UserCDB();
	}
	
	public void init_KPContainerType() {
		log.info("init KPContainerType");
		List<KPContainerType> types = this.kpContainerTypeRepository.findAll();
		if (types.isEmpty()) {
			try {
				log.info("No types en tabla.Adding...");
				KPContainerType type=new KPContainerType();
				type.setId(1);
				type.setType("Python");
				kpContainerTypeRepository.save(type);
				//
				type=new KPContainerType();
				type.setId(2);
				type.setType("Java");
				kpContainerTypeRepository.save(type);
				//
				type=new KPContainerType();
				type.setId(3);
				type.setType("URL");
				kpContainerTypeRepository.save(type);		
				
			} catch (Exception e) {
				log.error("Error KPContainerType:"+e.getMessage());
				kpContainerTypeRepository.deleteAll();
			}
		}
	}
	 
	public void init_RoleUser() {
		log.info("init KPContainerType");
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
				
			} catch (Exception e) {
				log.error("Error UserCDB:"+e.getMessage());
				userCDBRepository.deleteAll();
			}
		}
	}
	
	@Test
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

}
