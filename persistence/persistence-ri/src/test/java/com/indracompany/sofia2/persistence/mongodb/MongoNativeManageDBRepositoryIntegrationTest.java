package com.indracompany.sofia2.persistence.mongodb;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class MongoNativeManageDBRepositoryIntegrationTest {
	
	@Autowired
	ManageDBRepository repository;	
		
	/*
	public void createIndex(String ontology, String attribute) throws DBPersistenceException;

	public List<String> createIndex(String sentence) throws DBPersistenceException;

	public void dropIndex(String ontology, String indexName) throws DBPersistenceException;

	public List<String> getIndexes(String ontology) throws DBPersistenceException;
	
	public void validateIndexes(String ontology, String schema) throws DBPersistenceException;
	*/
	
	@Test
	public void test_getStatusDatabase() {
		try {
			Assert.assertEquals(repository.getStatusDatabase().get("sofia"),Boolean.TRUE);			
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}	
	@Test
	public void test_getListOfTables() {
		try {
			Assert.assertTrue(repository.getListOfTables().size()>0);			
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}	

	@Test
	public void test_getListOfTables4Ontology() {
		try {
			Assert.assertEquals(repository.getListOfTables4Ontology("MensajesPlataforma").size(),1);			
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}	
	
	@Test
	public void test_createCollection() {
		try {
			int size1= repository.getListOfTables().size();
			repository.createTable4Ontology("ONT_TODELETE_1", 
			"{\"$schema\": \"http://json-schema.org/draft-04/schema#\",\"title\": \"Test Schema\"}");
			int size2=repository.getListOfTables().size();
			Assert.assertEquals(size2,size1+1);
			repository.removeTable4Ontology("ONT_TODELETE_1");
			size2=repository.getListOfTables().size();
			Assert.assertEquals(size2,size1);
			
		} 
		catch (Exception e) {
			Assert.fail("test_createCollection:"+e.getMessage());
		}
	}			

}
