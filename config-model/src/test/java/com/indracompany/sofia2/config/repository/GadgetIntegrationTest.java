/*******************************************************************************

 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.Calendar;
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
import com.indracompany.sofia2.config.model.Gadget;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class GadgetIntegrationTest {

	@Autowired
	GadgetRepository repository;

	@Before
	public void setUp() {
		List<Gadget> gadgets= this.repository.findAll();
		if (gadgets.isEmpty()) {
			log.info("No gadgets ...");
			Gadget gadget=new Gadget();
			gadget.setDbType("DBC");
			gadget.setUserId("6");
			gadget.setPublic(true);
			gadget.setName("Gadget1");
			gadget.setType("Tipo 1");

			repository.save(gadget);
		}
	}
	@Test
	public void test_findByUserIdAndType() { 
		Gadget gadget=this.repository.findAll().get(0);

		Assert.assertTrue(this.repository.findByUserIdAndType(gadget.getUserId(),gadget.getType()).size()>0);
	}



}
