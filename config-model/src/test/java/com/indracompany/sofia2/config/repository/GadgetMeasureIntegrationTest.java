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
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetMeasure;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class GadgetMeasureIntegrationTest {

	@Autowired
	GadgetMeasureRepository repository;
	@Autowired
	GadgetRepository grepository;

	@Before
	public void setUp() {
		List<GadgetMeasure> gadgetMeasures= this.repository.findAll();
		if (gadgetMeasures.isEmpty()) {
			log.info("No gadget measures ...");
			GadgetMeasure gadgetMeasure=new GadgetMeasure();
			gadgetMeasure.setAttribute("Attr1");
			List<Gadget> gadgets= this.grepository.findAll();
			Gadget gadget;
			if(gadgets.isEmpty()){
				log.info("No gadgets ...");
				gadget=new Gadget();
				gadget.setDbType("DBC");
				gadget.setUserId("6");
				gadget.setPublic(true);
				gadget.setName("Gadget1");
				gadget.setType("Tipo 1");

				grepository.save(gadget);
			}else{
				gadget=grepository.findAll().get(0);
			}
			gadgetMeasure.setGadgetId(gadget);
			repository.save(gadgetMeasure);
		}
	}

	@Test
	public void test_findByGadgetId() { 
		GadgetMeasure gadgetMeasure=this.repository.findAll().get(0);
		log.info("Loaded gadget measure with gadget id: "+gadgetMeasure.getGadgetId().getId());
		Assert.assertTrue(this.repository.findByGadgetId(gadgetMeasure.getGadgetId()).size()>0);
	}


}
