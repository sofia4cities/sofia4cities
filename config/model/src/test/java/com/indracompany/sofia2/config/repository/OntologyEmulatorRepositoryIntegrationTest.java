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

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyEmulator;
import com.indracompany.sofia2.config.model.User;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class OntologyEmulatorRepositoryIntegrationTest {

	@Autowired
	OntologyRepository orepository;
	@Autowired
	OntologyEmulatorRepository repository;

	@Autowired
	UserRepository userRepository;

	private User getUserCollaborator() {
		return this.userRepository.findByUserId("collaborator");
	}

	@Before
	public void setUp() {
		List<OntologyEmulator> oes = this.repository.findAll();
		if (oes.isEmpty()) {
			log.info("No ontology emulators, adding...");
			OntologyEmulator oe = new OntologyEmulator();
			oe.setMeasures("2.5,3.4,4.5");
			oe.setIdentification("Id 1");
			oe.setUser(getUserCollaborator());
			oe.setInsertEvery(5);
			Ontology o = this.orepository.findAll().get(0);
			if (o == null) {
				o = new Ontology();
				o.setJsonSchema("{}");
				o.setIdentification("Id 1");
				o.setDescription("Description");
				o.setActive(true);
				o.setRtdbClean(true);
				o.setPublic(true);
				orepository.save(o);

			}
			oe.setOntology(o);
			this.repository.save(oe);

		}

	}

	@Test
	public void given_SomeOntologyEmulatorsExist_When_ItIsSearchedByIdentificationAndUserId_Then_TheCorrectObjectIsObtained() {
		OntologyEmulator oe = this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findByIdentificationAndUser(oe.getIdentification(), oe.getUser()).size() > 0);
	}

}
