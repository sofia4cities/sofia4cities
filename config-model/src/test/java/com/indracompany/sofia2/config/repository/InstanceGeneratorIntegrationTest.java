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
import com.indracompany.sofia2.config.model.InstanceGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class InstanceGeneratorIntegrationTest {

	@Autowired
	InstanceGeneratorRepository repository;
	@Autowired
	GeneratorTypeRepository gtrepository;

	@Before
	public void setUp()
	{
		List<InstanceGenerator> generators=this.repository.findAll();
		if(generators.isEmpty())
		{
			log.info("No instance generators found...adding");
			InstanceGenerator generator=new InstanceGenerator();
			generator.setId(1);
			generator.setValues("desde,0;hasta,400");
			generator.setIdentification("Integer 0 a 400");
			GeneratorType type=this.gtrepository.findById(4);
			if(type==null)
			{
				type=new GeneratorType();
				type.setId(1);
				type.setIdentification("Random Number");
				type.setKeyType("desde,number;hasta,number;numdecimal,number");
				type.setKeyValueDef("desde,100;hasta,10000;numdecimal,0");
				this.gtrepository.save(type);
			}
			generator.setGeneratorTypeId(type);
			this.repository.save(generator);

		}
	}

	@Test
	public void test_findById()
	{
		InstanceGenerator generator=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findById(generator.getId())!=null);

	}

}
