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


import lombok.extern.slf4j.Slf4j;

/**
*
* @author Javier Gomez-Cornejo
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ClientPlatformIntegrationTest {
	@Autowired 
	ClientPlatformRepository repository;
	@Before
	public void setUp() {
		List<ClientPlatform> clients= this.repository.findAll();
		if (clients.isEmpty()) {
			log.info("No clients ...");
			ClientPlatform client= new ClientPlatform();
			client.setUserId("1");
			client.setIdentification("ScadaTags_Alarms_kp");
			client.setEncryptionKey("b37bf11c-631e-4bc4-ae44-910e58525952");
			client.setDescription("Kp para la insercion de alarmas de scada");
			repository.save(client);			
			client= new ClientPlatform();
			client.setUserId("6");
			client.setIdentification("GTKP-fjgcornejo");
			client.setEncryptionKey("f9dfe72e-7082-4fe8-ba37-3f569b30a691");
			repository.save(client);
			
		}
	}
	
	@Test
	public void test1_FindByType() { 
		List<ClientPlatform> client=this.repository.findByIdentificationAndDescription("GTKP-fjgcornejo", null);
		Assert.assertTrue(client!=null);		
	}

}
