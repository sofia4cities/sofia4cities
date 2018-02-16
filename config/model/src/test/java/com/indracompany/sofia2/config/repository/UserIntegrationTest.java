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

import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class UserIntegrationTest {

	@Autowired
	UserRepository repository;

	@Autowired
	RoleRepository roleRepository;

	@Before
	public void setUp() {
		List<User> types = this.repository.findAll();
		if (types.isEmpty()) {
			// log.info("No types en tabla.Adding...");
			throw new RuntimeException("No types en Users...");
		}
	}

	@Test
	public void test1_Count() {
		Assert.assertTrue(this.repository.count() > 6);
	}

	@Test
	public void test3_FindUserNoAdmin() {
		Assert.assertTrue(this.repository.findUsersNoAdmin().size() > 5);
	}

	@Test
	public void test4_FindByEmail() {
		Assert.assertTrue(this.repository.findByEmail("administrator@sofia2.com").size() == 1);
	}

	@Test
	public void test5_createAndDeleteUser() {
		long count = this.repository.count();
		User type = new User();
		type.setUserId("lmgracia");
		type.setPassword("changeIt!");
		type.setFullName("Luis Miguel Gracia");
		type.setEmail("lmgracia@sofia2.com");
		type.setActive(true);
		type.setRole(this.roleRepository.findById(Role.Type.ROLE_DEVELOPER.toString()));
		repository.save(type);
		Assert.assertTrue(this.repository.count() == count + 1);
		repository.delete(type);
		Assert.assertTrue(this.repository.count() == count);

	}

}
