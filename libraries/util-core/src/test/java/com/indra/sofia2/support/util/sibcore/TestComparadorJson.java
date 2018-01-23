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
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.sibcore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.indra.sofia2.support.util.json.JsonShemaValidator;
import com.indra.sofia2.support.util.json.exceptions.ParseOntoException;


@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations ={"classpath:/META-INF/spring/applicationContext.xml"})
public class TestComparadorJson {

	private JsonNode templateOnt;
	private JsonNode templateFeed;
	private JsonNode ontologyOK1;
	private JsonNode ontologyOK2;
	private JsonNode template;
	private JsonNode ontologyKO;
	private JsonNode ontologyKO1;
	private JsonNode ontologyFeedOK;
	private JsonNode ontologyFeedKO;
	private JsonNode templateAlert;
	private JsonNode ontologyAlertOK;
	private JsonNode ontologyAlertKO;
	private JsonNode templateD;
	private JsonNode ontologyTemplOK;
	private JsonNode ontologyTemplKO;
	
	JsonShemaValidator validation = new JsonShemaValidator(); 
	
	@Before
	public void init() throws Exception {
		templateOnt = JsonLoader.fromResource("/META-INF/json-schemas/OntologyOK.json");
		templateFeed = JsonLoader.fromResource("/META-INF/json-schemas/PlantillaBaseFeed-schema.json");
		ontologyFeedOK = JsonLoader.fromResource("/META-INF/json-schemas/OntologyFeedOK.json");
		ontologyFeedKO = JsonLoader.fromResource("/META-INF/json-schemas/OntologyFeedKO.json");
		templateAlert = JsonLoader.fromResource("/META-INF/json-schemas/PlantillaBaseAlert-schema.json");
		ontologyAlertOK = JsonLoader.fromResource("/META-INF/json-schemas/OntologyAlertOK.json");
		ontologyAlertKO = JsonLoader.fromResource("/META-INF/json-schemas/OntologyAlertKO.json");
		ontologyOK1 = JsonLoader.fromResource("/META-INF/json-schemas/OntologyOK1.json");
		ontologyOK2 = JsonLoader.fromResource("/META-INF/json-schemas/OntologyOK2.json");
		template =  JsonLoader.fromResource("/META-INF/json-schemas/PlantillaBase-schema.json");
		ontologyKO =  JsonLoader.fromResource("/META-INF/json-schemas/OntologyKO.json");
		ontologyKO1 = JsonLoader.fromResource("/META-INF/json-schemas/OntologyKO1.json");
		templateD = JsonLoader.fromResource("/META-INF/json-schemas/Plantilla.json");
		ontologyTemplOK = JsonLoader.fromResource("/META-INF/json-schemas/OntologyPOK.json");
		ontologyTemplKO = JsonLoader.fromResource("/META-INF/json-schemas/OntologyPKO.json");
	}
	
	@Test
	public void testComparadorTemplateOK(){
		try {
			Assert.assertTrue(validation.validatorSchemaOnto(templateD.toString(), ontologyTemplOK.toString()));
		} catch ( ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testComparadorTemplateKO(){
		try {
			Assert.assertTrue(validation.validatorSchemaOnto(templateD.toString(), ontologyTemplKO.toString()));
		} catch ( ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testComparadorFeedOK(){
		try {
			Assert.assertTrue(validation.validatorSchemaOnto(templateFeed.toString(), ontologyFeedOK.toString()));
		} catch ( ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testComparadorFeedKO(){
		try {
			Assert.assertFalse(validation.validatorSchemaOnto(templateFeed.toString(), ontologyFeedKO.toString()));
		} catch ( ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testComparadorAlertOK(){
		try {
			Assert.assertTrue(validation.validatorSchemaOnto(templateAlert.toString(), ontologyAlertOK.toString()));
		} catch ( ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testComparadorAlertKO(){
		try {
			Assert.assertFalse(validation.validatorSchemaOnto(templateAlert.toString(), ontologyAlertKO.toString()));
		} catch ( ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
    @Test
	public void testComparadorOK(){
		try {
			Assert.assertTrue(validation.validatorSchemaOnto(templateOnt.toString(), ontologyOK1.toString()));
		} catch ( ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
    
    @Test
	public void testComparadorOK1(){
		try {
			Assert.assertTrue(validation.validatorSchemaOnto(template.toString(), ontologyOK1.toString()));
		} catch (ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
    @Test
	public void testComparadorOK2(){
		try {
			Assert.assertTrue(validation.validatorSchemaOnto(template.toString(), ontologyOK2.toString()));
		} catch (ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
    
	@Test
	public void testComparadorKO(){
		try {
			Assert.assertFalse(validation.validatorSchemaOnto(template.toString(), ontologyKO.toString()));
		} catch (ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testComparadorKO1(){
		try {
			Assert.assertFalse(validation.validatorSchemaOnto(template.toString(), ontologyKO1.toString()));
		} catch (ParseOntoException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
}
