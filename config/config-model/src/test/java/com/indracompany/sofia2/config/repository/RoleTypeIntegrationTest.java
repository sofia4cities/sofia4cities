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

import com.indracompany.sofia2.config.model.RoleType;
import com.indracompany.sofia2.config.repository.RoleTypeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class RoleTypeIntegrationTest {

	@Autowired
	RoleTypeRepository repository;


	@Before
	public void setUp() {
		List<RoleType> types = this.repository.findAll();
		if (types.isEmpty()) {
			//			log.info("No types en tabla.Adding...");
			throw new RuntimeException("No types en tabla.Adding...");
		}
	}


	@Test
	public void test1_Count() { 
		Assert.assertTrue(this.repository.count()==7);		
	}

	@Test
	public void test2_GetAll() {
		Assert.assertTrue(this.repository.findAll().size()==7);
	}

	@Test
	public void test3_CountByName() { 
		Assert.assertTrue(this.repository.countByName("ROLE_ADMINISTRATOR")==1L);		
	}

	@Test
	public void test4_FindByName() { 
		//		Assert.assertTrue(this.repository.findByName("ROLE_ADMINISTRATOR").get(0).getId()==1L);		
	}


}
