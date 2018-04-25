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
 * 2013 - 2014  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.commons.testing.IntegrationTest;
import com.indracompany.sofia2.config.model.GadgetMeasure;
import com.indracompany.sofia2.config.model.User;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
@Category(IntegrationTest.class)
public class GadgetMeasureRepositoryIntegrationTest {

	@Autowired
	GadgetMeasureRepository repository;
	@Autowired
	GadgetRepository grepository;

	@Autowired
	UserRepository userRepository;

	private User getUserCollaborator() {
		return this.userRepository.findByUserId("collaborator");
	}

	@Before
	public void setUp() {
		List<GadgetMeasure> gadgetMeasures = this.repository.findAll();
		if (gadgetMeasures.isEmpty()) {
			log.error("You must use InitConfigDB to populate master data. Only use this method for testing");
			Assert.fail("You must use InitConfigDB to populate master data. Only use this method for testing");
		}
	}

	@Test
	public void given_SomeGadgetMeasuresExist_When_ItIsSearchedByGadget_Then_ItIsObtainedTheCorrectObjects() {
		GadgetMeasure gadgetMeasure = this.repository.findAll().get(0);
		log.info("Loaded gadget measure with id: " + gadgetMeasure.getId());
		Assert.assertTrue(this.repository.findByGadget(gadgetMeasure.getGadget()).size() > 0);
	}

}
