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

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Ontology;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class ClientPlatformOntologyIntegrationTest {


	@Autowired 
	ClientPlatformOntologyRepository repository;
	@Autowired 
	ClientPlatformRepository cprepository;
	@Autowired 
	OntologyRepository orepository;

	@Before
	public void setUp() {

		List<ClientPlatformOntology> cpos=this.repository.findAll();
		if(cpos.isEmpty())
		{
			log.info("No Client Platform Ontologies");
			ClientPlatformOntology cpo=new ClientPlatformOntology();
			ClientPlatform cp=this.cprepository.findAll().get(0);
			Ontology o=this.orepository.findAll().get(0);
			if(cp==null)
			{
				cp=new ClientPlatform();
				cp.setUserId("1");
				cp.setIdentification("ScadaTags_Alarms_kp");
				cp.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
				cp.setDescription("Kp para la insercion de alarmas de scada");
				cprepository.save(cp);			
			}
			if(o==null)
			{
				o=new Ontology();
				o.setJsonSchema("{}");
				o.setIdentification("Id 1");
				o.setDescription("Description");
				o.setActive(true);
				o.setRtdbClean(true);
				o.setPublic(true);
				orepository.save(o);
			}
			cpo.setClientPlatformId(cp);
			cpo.setOntologyId(o);
			this.repository.save(cpo);

		}
	}

	@Test
	public void test_findByOntologyIdAndClientPlatformId()
	{
		ClientPlatformOntology cpo=this.repository.findAll().get(0);

		Assert.assertTrue(this.repository.findByOntologyIdAndClientPlatformId(cpo.getOntologyId(), cpo.getClientPlatformId()).size()>0);

	}


}
