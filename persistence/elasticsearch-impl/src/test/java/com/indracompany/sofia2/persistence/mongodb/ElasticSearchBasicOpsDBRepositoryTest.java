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
package com.indracompany.sofia2.persistence.mongodb;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchBasicOpsDBRepository;
import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchManageDBRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ElasticSearchBasicOpsDBRepositoryTest {
	
	public final static String TEST_INDEX = "elasticsearch-test_index";
	public final static String TEST_INDEX_ONLINE = TEST_INDEX + "_online";
	
	@Value("${sofia2.database.elasticsearch.database:es_sofia2_s4c}")
	@Getter
	@Setter
	private String database;

	@Autowired
	ElasticSearchBasicOpsDBRepository repository;
	
	@Autowired
	ElasticSearchManageDBRepository manage;
	
	private String JSON_TEST = "{" +
             "\"name\":\"skyji\"," +
             "\"job\":\"Admin\"," +
             "\"location\":\"India\"" +
             "}";
	
	private String JSON_TEST_UPDATE = "{" +
            "\"name\":\"pepe\"," +
            "\"job\":\"pepe\"," +
            "\"location\":\"pepe\"" +
            "}";
	
	private String SQL_TEST = "select * from ";
	//private String SQL_TEST = "select * from "+database+"/"+TEST_INDEX_ONLINE;
	
	@Before
	public  void doBefore() throws Exception {	
		System.out.println("up process...");
		manage.createTable4Ontology(TEST_INDEX_ONLINE, "");
	}
	
	@After
	public  void tearDown() {
		System.out.println("teardown process...");
		try {
			manage.removeTable4Ontology(TEST_INDEX_ONLINE);
		} catch (Exception e) {
			log.info("Issue deleting table4ontology "+e);
		}
		
	}
	
	@Test
	public void testInsertAndGet() {
		try {
			log.info("testInsertAndGet");
			
			String id = repository.insert(TEST_INDEX_ONLINE, JSON_TEST);
			 
			log.info("Returned inserted object with id "+id);
			
			String resultById = repository.findById(TEST_INDEX_ONLINE, id);
			 
			log.info("Returned searched object with this data "+resultById);
			
			log.info("testInsertAndGet END ");
			
			Assert.assertTrue(!resultById.isEmpty());
		} catch (Exception e) {
			Assert.fail("testInsertAndGet failure. " + e);
		}
	}
	
	@Test
	public void testInsertCountDelete() {
		try {
			log.info("testInsertCountDelete");
			
			String id = repository.insert(TEST_INDEX_ONLINE, JSON_TEST);
			log.info("Returned inserted object with id "+id);
			 
			long many = repository.count(TEST_INDEX_ONLINE);
			log.info("Returned count object with type "+TEST_INDEX_ONLINE+" size: "+many );
			 
			long size = repository.deleteNativeById(TEST_INDEX_ONLINE, id);
			log.info("Returned delete object with type "+TEST_INDEX_ONLINE+" size: "+size +" id "+id );
			Thread.sleep(10000);
			 
			many = repository.count(TEST_INDEX_ONLINE);
			log.info("Returned count object after deleting with type "+TEST_INDEX_ONLINE+" size: "+many );
			
			log.info("testInsertCountDelete END ");
			
			Assert.assertTrue(!id.isEmpty());
		} catch (Exception e) {
			Assert.fail("testInsertCountDelete failure. " + e);
		}
	}
	
	@Test
	public void testInsertUpdate() {
		try {
			
			log.info("testInsertUpdate");
			
			String id = repository.insert(TEST_INDEX_ONLINE, JSON_TEST);
			log.info("Returned inserted object with id "+id);
			 
			long many = repository.updateNativeByObjectIdAndBodyData(TEST_INDEX_ONLINE, id, JSON_TEST_UPDATE);
			log.info("Returned count updateNativeByObjectIdAndBodyData "+TEST_INDEX_ONLINE+" id: "+id+" count:"+many );
			 
			String resultById = repository.findById(TEST_INDEX_ONLINE, id);
			 
			log.info("Returned searched object with this data "+resultById);
			
			log.info("testInsertUpdate END ");
			
			Assert.assertTrue(many==1);
		} catch (Exception e) {
			Assert.fail("testInsertCountDelete failure. " + e);
		}
	}
	
	@Test
	public void testSearchQuery() {
		try {
			
			log.info("testSearchQuery");
			
			String id = repository.insert(TEST_INDEX_ONLINE, JSON_TEST);
			log.info("Returned inserted object with id "+id);
			 
			List<String> listData = repository.findAll(TEST_INDEX_ONLINE);
			log.info("Returned list of found objects "+listData);
			
			String sql = SQL_TEST+" "+database+"/"+TEST_INDEX_ONLINE;
			
			String outpoutSQL = repository.querySQLAsJson(TEST_INDEX_ONLINE, sql);
			
			log.info("Returned SQL "+outpoutSQL);
			
			sql = SQL_TEST+" "+database;
			
			String outpoutSQL2 = repository.querySQLAsJson(TEST_INDEX_ONLINE, sql);
			
			log.info("testSearchQuery END ");
			
			Assert.assertTrue(outpoutSQL.equals(outpoutSQL2));
		} catch (Exception e) {
			Assert.fail("testInsertCountDelete failure. " + e);
		}
	}

	

}
