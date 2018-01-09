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

import lombok.extern.slf4j.Slf4j;

/**
*
* @author Javier Gomez-Cornejo
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class OntologyIntegrationTest {
	
	@Autowired 
	OntologyRepository repository;
	
	@Before
	public void setUp() {
		List<Ontology> ontologies=this.repository.findAll();
		if(ontologies.isEmpty())
		{
			log.info("No ontologies..adding");
			Ontology ontology=new Ontology();
			ontology.setJsonSchema("{}");
			ontology.setIdentification("Id 1");
			ontology.setDescription("Description");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setPublic(true);
			repository.save(ontology);
			ontology=new Ontology();
			ontology.setJsonSchema("{Data:,Temperature:}");
			ontology.setDescription("Description");
			ontology.setIdentification("Id 2");
			ontology.setActive(true);
			ontology.setRtdbClean(true);
			ontology.setPublic(true);
			repository.save(ontology);

		}
	}
	@Test
	public void test_findByIdentificationLikeAndDescriptionLike()
	{
		Ontology o=this.repository.findAll().get(0);
		o.isActive();
		Assert.assertTrue(this.repository.findByIdentificationLikeAndDescriptionLike(o.getIdentification(), o.getDescription()).size()>0);
		
		
	}
	
	public void countByIsActiveTrueAndIsPublicTrue()
	{
		Assert.assertTrue(this.repository.countByActiveTrueAndIsPublicTrue()==1L);

		
		
	}
}
