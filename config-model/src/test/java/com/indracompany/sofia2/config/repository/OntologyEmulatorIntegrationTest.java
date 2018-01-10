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

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyEmulator;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class OntologyEmulatorIntegrationTest {

	@Autowired 
	OntologyRepository orepository;
	@Autowired
	OntologyEmulatorRepository repository;

	@Before
	public void setUp() {

		List<OntologyEmulator> oes=this.repository.findAll();
		if(oes.isEmpty())
		{
			log.info("No ontology emulators, adding...");
			OntologyEmulator oe=new OntologyEmulator();
			oe.setMeasures("2.5,3.4,4.5");
			oe.setIdentification("Id 1");
			oe.setUserId("1");
			oe.setInsertEvery(5);
			Ontology o=this.orepository.findAll().get(0);
			if(o==null)
			{
				o=new Ontology();
				o.setJsonSchema("{}");
				o.setIdentification("Id 1");
				o.setDescription("Description");
				o.setActive(true);
				o.setRtdbClean(true);
				o.setPublic(true);
				orepository.save(o);

			}
			oe.setOntologyId(o);
			this.repository.save(oe);



		}


	}
	@Test
	public void test_findByIdentificationAndUserId()
	{
		OntologyEmulator oe= this.repository.findAll().get(0);

		Assert.assertTrue(this.repository.findByIdentificationAndUserId(oe.getIdentification(), oe.getUserId()).size()>0);



	}



}
