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

import com.indracompany.sofia2.config.model.GeneratorType;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class GeneratorTypeIntegrationTest {

	@Autowired 
	GeneratorTypeRepository repository;

	@Before
	public void setUp()
	{
		List<GeneratorType> types=this.repository.findAll();
		if(types.isEmpty())
		{

			log.info("No generator types found..adding");
			GeneratorType type=new GeneratorType();
			type.setId(1);
			type.setIdentification("Random Number");
			type.setKeyType("desde,number;hasta,number;numdecimal,number");
			type.setKeyValueDef("desde,100;hasta,10000;numdecimal,0");
			this.repository.save(type);

		}

	}

	@Test
	public void test_findById()
	{
		GeneratorType type=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findById(type.getId())!=null);

	}

}
