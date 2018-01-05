package com.indracompany.sofia2.persistence.mongodb;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class MongoNativeBasicOpsDBRepositoryIntegrationTest {
	
	@Autowired
	BasicOpsDBRepository repository;	

	
	@Test
	public void test_count() {
		try {
			Assert.assertTrue(repository.count("AuditGeneral")>0);			
		} catch (Exception e) {
			Assert.fail("Error test_count"+e.getMessage());
		}
	}	
	@Test
	public void test_getById() {
		try {
			String data = repository.findById("AuditGeneral","5a1f6e89d870ee6c40d89e4b");			
			Assert.assertTrue(data!=null && data.indexOf("time")!=-1);			
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}	
	@Test
	public void test_getAll() {
		try {
			String data = repository.findAllAsOneJSON("AuditGeneral");		
			List<String> asList= repository.findAll("AuditGeneral");
			Assert.assertTrue(asList.size()==108);		
			Assert.assertTrue(data.indexOf("kp")>0);				
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB");
		}
	}	


}
