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

import com.indracompany.sofia2.config.model.OntologyUserAccess;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class OntologyUserAccessIntegrationTest {

	@Autowired
	OntologyUserAccessTypeRepository ouatrepository;
	@Autowired
	OntologyUserAccessRepository repository;

	@Before
	public void setUp()
	{
		List<OntologyUserAccess> users=this.repository.findAll();
		if(users.isEmpty())
		{
			log.info("No users found...adding");
			OntologyUserAccess user=new OntologyUserAccess();
			user.setUserId("6");
			this.repository.save(user);




		}


	}

	@Test
	public void findByUserId()
	{

		OntologyUserAccess user=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findByUserId(user.getUserId()).size()>0);

	}
}
