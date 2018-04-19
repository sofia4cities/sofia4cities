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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
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
import com.indracompany.sofia2.persistence.elasticsearch.api.ESBaseApi;
import com.indracompany.sofia2.persistence.services.BasicOpsPersistenceServiceFacade;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;
import com.indracompany.sofia2.persistence.services.util.MustacheUtil;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
@Slf4j
public class GeoSpatialRepositoryTest {
	
	public final static String TEST_INDEX = "doc";
	public final static String TEST_INDEX_MONGO = TEST_INDEX+System.currentTimeMillis() ;
	
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
	
	@Autowired
	private ESBaseApi connector;
	
	ObjectMapper mapper = new ObjectMapper();
	
	private User getUserAdministrator() {
		if (userAdministrator == null)
			userAdministrator = this.userCDBRepository.findByUserId("administrator");
		return userAdministrator;
	}
			
	private String SQL_TEST = "select * from ";
	private String partial_envelope =  "partial_envelope.json";
	private String partial_polygon =   "partial_polygon.json";
	private String partial_polygon_mongo =   "partial_polygon_mongo.json";

	
	public static String getString(String file) throws IOException {
		File in = new ClassPathResource(file).getFile();
		return FileUtils.readFileToString(in);
	}
	
	@Before
	public  void doBefore() throws Exception {	
		log.info("up process...");
		
		doBefore1();
		doBefore2();
		
	}
	
	public  void doBefore1() throws Exception {	
		log.info("doBefore1 up process...");
		
		File in = new ClassPathResource("type.json").getFile();
		String TYPE = FileUtils.readFileToString(in);
			
		File data = new ClassPathResource("type_data.json").getFile();
		String DATA  = FileUtils.readFileToString(data);
			
		connector.deleteIndex(TEST_INDEX);
		
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
			if (index1==null)
				ontologyService.createOntology(ontology);
			
			
			manageFacade.createTable4Ontology(TEST_INDEX, TYPE);
		
			String idES = basicOpsFacade.insert(TEST_INDEX, DATA);
		
			log.info("Returned ES inserted object with id "+idES);
	

			Thread.sleep(10000);
		
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
				ontologyService.createOntology(ontology);
				
				
				manageFacade.createTable4Ontology(TEST_INDEX_MONGO, TYPE);
				manageFacade.createIndex(TEST_INDEX_MONGO,"geometry","2dsphere");
			
				String idES = basicOpsFacade.insert(TEST_INDEX_MONGO, DATA);
				log.info("Returned ES inserted object with id "+idES);
			}
				
		} catch (Exception e) {
			log.info("Issue creating table4ontology "+e);
		}
		
		
	}
	
	@After
	public  void tearDown() {
		log.info("teardown process...");
	
		
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
		
		/*try {
			manageFacade.removeTable4Ontology(TEST_INDEX_MONGO);	
		} catch (Exception e) {
			log.info("Issue deleting table4ontology "+e);
		}
		
		try {
			ontologyRepository.deleteById(TEST_INDEX_MONGO);
		} catch (Exception e) {
			log.info("Issue deleting TEST_INDEX_ONLINE_ELASTIC "+e);
		}*/
	}
	
	@Test
	public void testSearchQueryFindAllElasticSearch() {
		try {
			
			partial_envelope = getString(partial_envelope);
			partial_polygon = getString(partial_polygon);
			
			log.info("testSearchQueryFindAll");
		
			List<String> listDataES = basicOpsFacade.findAll(TEST_INDEX);
			log.info("Returned list of found objects "+listDataES);
			
			Map<String, Object> context = new HashMap<>();
			context.put("ontology", TEST_INDEX);
			context.put("field", "geometry");
			context.put("partial",partial_envelope);
			
			
			String simple =  MustacheUtil.executeTemplate("todo.mustache", context);
			List<String> list = basicOpsFacade.queryNative(TEST_INDEX, simple);
			
			log.info("result  "+list);
			
			String output =  MustacheUtil.executeTemplate("geo.intersects.elasticsearch.mustache", context);
			
			log.info("matched with template  "+output);
		
			List<String> listQ = basicOpsFacade.queryNative(TEST_INDEX, output);
			
			context.put("partial",partial_polygon);
			output =  MustacheUtil.executeTemplate("geo.intersects.elasticsearch.mustache", context);
			
			log.info("matched with template  "+output);
			
			 listQ = basicOpsFacade.queryNative(TEST_INDEX, output);
			
			log.info("result  "+listQ);
			
			Assert.assertTrue(listDataES!=null);
		} catch (Exception e) {
			Assert.fail("testInsertCountDelete failure. " + e);
		}
	}
	
	@Test
	public void testSearchQueryFindAllMongo() {
		try {
			
			partial_polygon_mongo = getString(partial_polygon_mongo);
			
			log.info("testSearchQueryFindAll");
		
			List<String> listDataES = basicOpsFacade.findAll(TEST_INDEX_MONGO);
			log.info("Returned list of found objects "+listDataES);
			
			Map<String, Object> context = new HashMap<>();
			context.put("ontology", TEST_INDEX_MONGO);
			context.put("field", "geometry");
			context.put("partial",partial_polygon_mongo);
			
			
			String output =  MustacheUtil.executeTemplate("geo.intersects.mongo.mustache", context);
			
			log.info("matched with template  "+output);
		
			List<String> listQ = basicOpsFacade.queryNative(TEST_INDEX_MONGO, output);
			
			
			log.info("result  "+listQ);
			
			Assert.assertTrue(listDataES!=null);
		} catch (Exception e) {
			Assert.fail("testInsertCountDelete failure. " + e);
		}
	}
	

	
	

}
