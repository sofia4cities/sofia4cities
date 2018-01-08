/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.Dashboard;

import lombok.extern.slf4j.Slf4j;

/**
*
* @author Javier Gomez-Cornejo
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class DashboardIntegrationTest {
	
	@Autowired
	DashboardRepository repository;
	
	@Before
	public void setUp() {
		List<Dashboard> dashboards=this.repository.findAll();
		if(dashboards.isEmpty())
		{
			log.info("No dashboards...adding");
			Dashboard dashboard= new Dashboard();
			dashboard.setModel("Modelo 1");
			dashboard.setUserId("1");
			dashboard.setName("Nombre del modelo 1");
			dashboard.setDashboardTypeId("9");
			repository.save(dashboard);
			
		
		}
	
	}
	@Test
	public void test_countByName(){
		
		Dashboard d=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.countByName(d.getName())==1L);
		
		
	}
	@Test
	public void test_findByNameAndDashboardTypeId(){
		Dashboard d=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findByNameAndDashboardTypeId(d.getName(), d.getDashboardTypeId()).size()>0);
		
	}
	

}
