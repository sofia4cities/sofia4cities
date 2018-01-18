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

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformContainer;
import com.indracompany.sofia2.config.model.ClientPlatformContainerType;
import com.indracompany.sofia2.config.model.Token;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ClientPlatformComtainerIntegrationTest {

	@Autowired
	ClientPlatformContainerRepository repository;
	@Autowired
	ClientPlatformRepository cprepository;
	@Autowired
	ClientPlatformContainerTypeRepository cpctrepository;
	@Autowired
	TokenRepository trepository;

	@Before
	public void setUp() {
		List<ClientPlatformContainer> pcs= this.repository.findAll();
		if(pcs.isEmpty())
		{

			ClientPlatformContainer cpc=new ClientPlatformContainer();
			cpc.setClientConnection("6930722a-a9fa-4206-a3cf-1441a0318b39");
			cpc.setProgramName("Program 1");
			cpc.setMaxExecutionTime(5);
			cpc.setMessageFilesPrefix("msg_");
			cpc.setLogFilesPrefix("log_");
			cpc.setState("Up");
			ClientPlatform cp;
			if(!this.cprepository.findAll().isEmpty()){
				cp=this.cprepository.findAll().get(0);
			}else{			
				cp=new ClientPlatform();
				cp.setUserId("1");
				cp.setIdentification("ScadaTags_Alarms_kp");
				cp.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
				cp.setDescription("Kp para la insercion de alarmas de scada");
				cprepository.save(cp);

			}
			cpc.setClientPlatformId(cp);
			Token token;
			if(!this.trepository.findAll().isEmpty()){
				token=this.trepository.findAll().get(0);
			}else{
				token=new Token();
				token.setClientPlatformId(cp);
				token.setToken("Token1");
				token.setActive(true);

			}
			cpc.setAuthenticationTokenId(token);
			ClientPlatformContainerType type;

			if(!this.cpctrepository.findAll().isEmpty()){
				type=this.cpctrepository.findAll().get(0);
			}else{
				type=new ClientPlatformContainerType();
				type.setId(1);
				type.setType("Python");
				cpctrepository.save(type);


			}
			cpc.setClientPlatformContainerTypeId(type);
			this.repository.save(cpc);
		}


	}
	@Test
	public void test_findByIdentificationLikeAndClientConnectionLike()
	{

		ClientPlatformContainer cpc=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findByIdentificationLikeAndClientConnectionLike(cpc.getClientPlatformId().getIdentification(), cpc.getClientConnection()).size()>0);
	}
}
