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
import com.indracompany.sofia2.config.model.DashboardType;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j


public class DashboardTypeIntegrationTest {

	@Autowired
	DashboardTypeRepository repository;

	@Before
	public void setUp() {
		List<DashboardType> dashboardTypes=this.repository.findAll();
		if(dashboardTypes.isEmpty())
		{
			log.info("No dashboards...adding");
			DashboardType dashboardType= new DashboardType();
			dashboardType.setId(1);
			dashboardType.setModel("Modelo 1");
			dashboardType.setUserId("1");
			dashboardType.setPublic(true);
			dashboardType.setType("Tipo de modelo 1");
			repository.save(dashboardType);


		}

	}

	@Test
	public void test_countByDashboardType(){

		DashboardType dt=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.countByType(dt.getType())==1L);


	}

}
