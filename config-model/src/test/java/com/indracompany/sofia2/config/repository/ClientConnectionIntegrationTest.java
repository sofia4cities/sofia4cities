/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import java.util.Calendar;
import java.util.Date;
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

import com.indracompany.sofia2.config.model.ClientConnection;
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

public class ClientConnectionIntegrationTest {

	@Autowired 
	ClientConnectionRepository repository;

	@Before
	public void setUp() {
		List<ClientConnection> clients= this.repository.findAll();
		if (clients.isEmpty()) {
			log.info("No clients ...");
			ClientConnection con= new ClientConnection();
			ClientPlatform client= new ClientPlatform();
			client.setId("06be1962-aa27-429c-960c-d8a324eef6d4");
			con.setClientPlatformId(client);			
			con.setIdentification("1");
			con.setIpStrict(true);
			con.setStaticIp(false);
			con.setLastIp("192.168.1.89");
			Calendar date = Calendar.getInstance();
			con.setLastConnection(date);
			repository.save(con);
		}
	}
	@Test
	public void test_CountByClientPlatformId() { 
		ClientConnection con=this.repository.findAll().get(0);

		Assert.assertTrue(this.repository.countByClientPlatformId(con.getClientPlatformId())>0);
	}

	@Test
	public void test_FindByUserId(){ 

		ClientConnection con=this.repository.findAll().get(0);

		Assert.assertTrue(this.repository.findByUserId(con.getClientPlatformId().getUserId()).size()>0);
	}

	@Test
	public void test_FindByClientPlatformId(){ 
		ClientConnection con=this.repository.findAll().get(0);

		Assert.assertTrue(this.repository.findByClientPlatformId(con.getClientPlatformId()).size()>0);
	}

	@Test
	public void test_FindByClientPlatformIdAndIdentification(){ 
		ClientConnection con=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findByClientPlatformIdAndIdentification(con.getClientPlatformId(),con.getIdentification()).size()>0);
	}

}




