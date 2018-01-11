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

import com.indracompany.sofia2.config.model.OntologyCategory;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class OntologyUserAccessTypeIntegrationTest {

	@Autowired
	OntologyUserAccessTypeRepository repository;

	@Before
	public void setUp()
	{
		List<OntologyUserAccessType> types=this.repository.findAll();
		if(types.isEmpty()){
			log.info("No user access types found...adding");
			OntologyUserAccessType type=new OntologyUserAccessType();
			type.setId(1);
			type.setName("ALL");
			type.setDescription("Todos los permisos");
			this.repository.save(type);
		}
	}

	@Test
	public void test_findById()
	{
		OntologyUserAccessType type=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findById(type.getId())!=null);
	}


}
