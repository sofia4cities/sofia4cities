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

import com.indracompany.sofia2.config.model.GadgetDataModel;
import com.indracompany.sofia2.config.model.User;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class GadgetDataModelIntegrationTest {

	@Autowired 
	GadgetDataModelRepository repository;
	@Autowired
	UserRepository userRepository;	
	private User getUserCollaborator() {
		return this.userRepository.findByUserId("collaborator");
	}

	@Before
	public void setUp() {
		List<GadgetDataModel> gadgetDataModels= this.repository.findAll();
		if (gadgetDataModels.isEmpty()) {
			log.info("No gadget data models ...");
			GadgetDataModel gadgetDM=new GadgetDataModel();
			gadgetDM.setIdentification("1");
			gadgetDM.setImage("ea02 2293 e344 8e16 df15 86b6".getBytes());
			gadgetDM.setUserId(getUserCollaborator());
			gadgetDM.setPublic(true);			
			repository.save(gadgetDM);
		}
	}
	@Test
	public void test_findByIdentificationIsAndUserIdIsOrIdentificationIsAndPublicTrue() { 
		GadgetDataModel gadgetDM=this.repository.findAll().get(0);
		String identification= gadgetDM.getIdentification();
		String userId=getUserCollaborator().getUserId();
		log.info("GadgetDataModel with identification: "+identification+" ,userId: "+userId+" public: "+gadgetDM.isPublic());
		log.info("Identification: "+identification+" userId: "+userId+" should return true (size of list>0)" );
		Assert.assertTrue(this.repository.findByIdentificationAndUserIdOrIsPublicTrue(identification,userId).size()>0);
		log.info("Identification: 000 (harcoded) userId: "+userId+" should return false (size of list=0)" );
		Assert.assertTrue(this.repository.findByIdentificationAndUserIdOrIsPublicTrue("000",userId).size()==0);
		if(gadgetDM.isPublic()){
			log.info("Identification: "+identification+" userId: 000 (harcoded) should return true (size of list>0) because is public" );
			Assert.assertTrue(this.repository.findByIdentificationAndUserIdOrIsPublicTrue(identification,"000").size()>0);

		}else{
			log.info("Identification: "+identification+" userId: 000 (harcoded) should return false (size of list=0) because is not public" );
			Assert.assertTrue(this.repository.findByIdentificationAndUserIdOrIsPublicTrue(identification,"000").size()>0);

		}
		Assert.assertTrue(this.repository.findByIdentificationAndUserIdOrIsPublicTrue(identification,"000").size()>0);

	}

}
