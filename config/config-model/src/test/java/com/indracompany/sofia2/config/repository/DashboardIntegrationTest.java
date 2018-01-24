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
	
	@Autowired
	UserRepository userRep;

	@Before
	public void setUp() {
		List<Dashboard> dashboards=this.repository.findAll();
		if(dashboards.isEmpty())	{
			log.info("No dashboards...adding");
			Dashboard dashboard= new Dashboard();
			dashboard.setModel("Modelo 1");
			dashboard.setUserId(this.userRep.findById("1"));
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
