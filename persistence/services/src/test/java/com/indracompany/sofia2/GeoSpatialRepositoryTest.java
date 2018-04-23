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
package com.indracompany.sofia2;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.services.BasicOpsPersistenceServiceFacade;
import com.indracompany.sofia2.persistence.services.GeoSpatialOpsService;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
@Ignore
public class GeoSpatialRepositoryTest {
	
	public final static String TEST_INDEX = "newdoc";
	public final static String TEST_INDEX_PIN = "newpin";
	public final static String TEST_INDEX_MONGO = TEST_INDEX+System.currentTimeMillis() ;
	public final static String TEST_INDEX_MONGO_PIN = TEST_INDEX_PIN+System.currentTimeMillis() ;
	
	private static User userAdministrator = null;

		
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
	
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private GeoSpatialOpsService geoService;
	
	private User getUserAdministrator() {
		if (userAdministrator == null)
			userAdministrator = this.userCDBRepository.findByUserId("administrator");
		return userAdministrator;
	}
			
	private String partial_envelope =  "partial_envelope.json";
	private String partial_polygon =   "partial_polygon.json";
	private String partial_polygon_agnostic =   "partial_polygon_agnostic.json";
	private String partial_polygon_mongo =   "partial_polygon_mongo.json";

	
	public static String getString(String file) throws IOException {
		File in = new ClassPathResource(file).getFile();
		return FileUtils.readFileToString(in);
	}
	
	@Before
	public  void doBefore() throws Exception {	
		log.info("up process...");
		
		doBefore1();
		doBefore3();
		doBefore2();
		doBefore4();
		
	}
	
	public  void doBefore1() throws Exception {	
		log.info("doBefore1 up process...");
		
		File in = new ClassPathResource("type.json").getFile();
		String TYPE = FileUtils.readFileToString(in);
			
		File data = new ClassPathResource("type_data.json").getFile();
		String DATA  = FileUtils.readFileToString(data);
			
		try {
			manageFacade.removeTable4Ontology(TEST_INDEX);	
		} catch (Exception e) {
			log.info("Issue deleting table4ontology "+e);
		}
		
		try {
			ontologyRepository.deleteById(TEST_INDEX);
		} catch (Exception e) {
			log.info("Issue deleting TEST_INDEX_ONLINE_ELASTIC "+e);
		}
		
		try {
			Ontology ontology = new Ontology();
			ontology.setJsonSchema(TYPE);
			ontology.setIdentification(TEST_INDEX);
			ontology.setDescription(TEST_INDEX);
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setRtdbDatasource(RtdbDatasource.ElasticSearch);
			ontology.setUser(getUserAdministrator());
			
			
			Ontology index1 = ontologyService.getOntologyByIdentification(TEST_INDEX, getUserAdministrator().getUserId());
			if (index1==null) {
				try {
					ontologyService.createOntology(ontology);
				} catch (Exception e) {}
				
				try {
					manageFacade.createTable4Ontology(TEST_INDEX, TYPE);
				} catch (Exception e) {}
				
				
			}
				
			String idES = basicOpsFacade.insert(TEST_INDEX, DATA);
			
			log.info("doBefore1 object with id "+idES);
	

		
		} catch (Exception e) {
			log.info("doBefore1 "+e);
		}
		
		
	}
	
	public  void doBefore3() throws Exception {	
		log.info("doBefore3 up process...");
		
		File in = new ClassPathResource("type_geo_point.json").getFile();
		String TYPE = FileUtils.readFileToString(in);
			
		File data = new ClassPathResource("type_geo_point_data.json").getFile();
		String DATA  = FileUtils.readFileToString(data);
			
		try {
			manageFacade.removeTable4Ontology(TEST_INDEX_PIN);	
		} catch (Exception e) {
			log.info("Issue deleting table4ontology "+e);
		}
		
		try {
			ontologyRepository.deleteById(TEST_INDEX_PIN);
		} catch (Exception e) {
			log.info("Issue deleting TEST_INDEX_ONLINE_ELASTIC "+e);
		}
		
		try {
			Ontology ontology = new Ontology();
			ontology.setJsonSchema(TYPE);
			ontology.setIdentification(TEST_INDEX_PIN);
			ontology.setDescription(TEST_INDEX_PIN);
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setRtdbDatasource(RtdbDatasource.ElasticSearch);
			ontology.setUser(getUserAdministrator());
			
			
			Ontology index1 = ontologyService.getOntologyByIdentification(TEST_INDEX_PIN, getUserAdministrator().getUserId());
			if (index1==null) {
				try {
					ontologyService.createOntology(ontology);
				} catch (Exception e) {}
				try {
					manageFacade.createTable4Ontology(TEST_INDEX_PIN, TYPE);
				} catch (Exception e) {}
				
			}
				
			String idES = basicOpsFacade.insert(TEST_INDEX_PIN, DATA);
			
			log.info("doBefore3 inserted object with id "+idES);
			
	

		
		
		} catch (Exception e) {
			log.info("doBefore3 "+e);
		}
		
		
	}
	
	public  void doBefore4() throws Exception {	
		log.info("doBefore4 up process...");
		
		File in = new ClassPathResource("type_geo_mongo.json").getFile();
		String TYPE = FileUtils.readFileToString(in);
			
		File data = new ClassPathResource("type_geo_point_data.json").getFile();
		String DATA  = FileUtils.readFileToString(data);
			
		
		try {
			Ontology ontology = new Ontology();
			ontology.setJsonSchema(TYPE);
			ontology.setIdentification(TEST_INDEX_MONGO_PIN);
			ontology.setDescription(TEST_INDEX_MONGO_PIN);
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setRtdbDatasource(RtdbDatasource.Mongo);
			ontology.setUser(getUserAdministrator());
			
			
			Ontology index1 = ontologyService.getOntologyByIdentification(TEST_INDEX_MONGO_PIN, getUserAdministrator().getUserId());
			if (index1==null) {
				try {
					ontologyService.createOntology(ontology);
				} catch (Exception e) {}
				
				try {
					manageFacade.createTable4Ontology(TEST_INDEX_MONGO_PIN, TYPE);
				} catch (Exception e) {}
				
			}
				
			
			String idES = basicOpsFacade.insert(TEST_INDEX_MONGO_PIN, DATA);
			
			log.info("doBefore4 inserted object with id "+idES);
	

		
		
		} catch (Exception e) {
			log.info("Issue creating table4ontology "+e);
		}
		
		
	}
	
	public  void doBefore2() throws Exception {	
		log.info("doBefore2 up process...");
		
		File in = new ClassPathResource("type_mongo.json").getFile();
		String TYPE = FileUtils.readFileToString(in);
			
		File data = new ClassPathResource("type_data.json").getFile();
		String DATA  = FileUtils.readFileToString(data);
		
		try {
			Ontology ontology = new Ontology();
			ontology.setJsonSchema(TYPE);
			ontology.setIdentification(TEST_INDEX_MONGO);
			ontology.setDescription(TEST_INDEX_MONGO);
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setRtdbToHdb(true);
			ontology.setPublic(true);
			ontology.setRtdbDatasource(RtdbDatasource.Mongo);
			ontology.setUser(getUserAdministrator());
			
			
			Ontology index1 = ontologyService.getOntologyByIdentification(TEST_INDEX_MONGO, getUserAdministrator().getUserId());
			if (index1==null) {
				try {
					ontologyService.createOntology(ontology);
				} catch (Exception e) {}
				
				try {
					manageFacade.createTable4Ontology(TEST_INDEX_MONGO, TYPE);
				} catch (Exception e) {}
				
				
			}
				
			String idES = basicOpsFacade.insert(TEST_INDEX_MONGO, DATA);
			log.info("doBefore2 inserted object with id "+idES);
			
			
				
		} catch (Exception e) {
			log.info("doBefore2 "+e);
		}
		
		
	}
	

	
	@Test
	public void testGeoServiceElasticWithin() {
		try {
			Thread.sleep(10000);
			log.info(">>>>>>>>>>>>> testGeoServiceElasticWithin");
			partial_polygon_agnostic = getString(partial_polygon_agnostic);
			
			log.info(basicOpsFacade.findAllAsJson(TEST_INDEX));
			
			log.info(basicOpsFacade.findAllAsJson(TEST_INDEX, 10));
			
			List<String> listQ = geoService.within(TEST_INDEX, partial_polygon_agnostic);
			
			log.info("result  "+listQ);
			Assert.assertTrue(listQ!=null);
		} catch (Exception e) {
			Assert.fail("testGeoServiceElasticWithin failure. " + e);
		}
	}
	@Test
	public void testGeoServiceMongoWithin() {
		try {
			log.info(">>>>>>>>>>>>> testGeoServiceMongoWithin");
			partial_polygon_agnostic = getString(partial_polygon_agnostic);
			
			List<String> listQ = geoService.within(TEST_INDEX_MONGO, partial_polygon_agnostic);
			
			log.info("result  "+listQ);
			Assert.assertTrue(listQ!=null);
		} catch (Exception e) {
			Assert.fail("testGeoServiceMongoWithin failure. " + e);
		}
	}
	
	
	@Test
	public void testGeoServiceElastic() {
		try {
			Thread.sleep(10000);
			log.info(">>>>>>>>>>>>> testGeoServiceElastic");
			partial_polygon_agnostic = getString(partial_polygon_agnostic);
			
			log.info(basicOpsFacade.findAllAsJson(TEST_INDEX));
			
			List<String> listQ = geoService.intersects(TEST_INDEX, partial_polygon_agnostic);
			
			log.info("result  "+listQ);
			Assert.assertTrue(listQ!=null);
		} catch (Exception e) {
			Assert.fail("testGeoServiceElastic failure. " + e);
		}
	}
	
	@Test
	public void testGeoServiceMongo() {
		try {
			log.info(">>>>>>>>>>>>> testGeoServiceMongo");
			partial_polygon_agnostic = getString(partial_polygon_agnostic);
			
			List<String> listQ = geoService.intersects(TEST_INDEX_MONGO, partial_polygon_agnostic);
			
			log.info("result  "+listQ);
			Assert.assertTrue(listQ!=null);
		} catch (Exception e) {
			Assert.fail("testGeoServiceMongo failure. " + e);
		}
	}
	
	@Test
	public void testGeoServiceNear() {
		try {
			Thread.sleep(10000);
			log.info(basicOpsFacade.findAllAsJson(TEST_INDEX_PIN));
			log.info(">>>>>>>>>>>>> testGeoServiceNear");
			String TWO_HUNDRED_KILOMETERS=""+(1000*200);
			List<String> listQ = geoService.near(TEST_INDEX_PIN, TWO_HUNDRED_KILOMETERS, "40", "-70");
			
			log.info("result  "+listQ);
			Assert.assertTrue(listQ!=null);
		} catch (Exception e) {
			Assert.fail("testGeoServiceNear failure. " + e);
		}
	}

	
	@Test
	public void testGeoServiceNearMongo() {
		try {
			log.info(">>>>>>>>>>>>> testGeoServiceNearMongo");
			
			String TWO_HUNDRED_KILOMETERS=""+(1000*200);
			List<String> listQ = geoService.near(TEST_INDEX_MONGO_PIN, TWO_HUNDRED_KILOMETERS, "40", "-70");
			
			log.info("result  "+listQ);
			Assert.assertTrue(listQ!=null);
		} catch (Exception e) {
			Assert.fail("testGeoServiceNearMongo failure. " + e);
		}
	}
	
	

}
