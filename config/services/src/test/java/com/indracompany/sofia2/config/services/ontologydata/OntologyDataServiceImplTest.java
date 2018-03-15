package com.indracompany.sofia2.config.services.ontologydata;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OntologyDataServiceImplTest {
	
	@InjectMocks
	OntologyDataServiceImpl service;
	
	private String GOOD_JSON_SCHEMA = "{\n" + 
			"    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" + 
			"    \"title\": \"TMS-Commands\",\n" + 
			"    \"type\": \"object\",\n" + 
			"    \"required\": [\n" + 
			"        \"Command\"\n" + 
			"    ],\n" + 
			"    \"properties\": {\n" + 
			"        \"Command\": {\n" + 
			"            \"type\": \"string\",\n" + 
			"            \"$ref\": \"#/datos\"\n" + 
			"        }\n" + 
			"    },\n" + 
			"    \"datos\": {\n" + 
			"        \"description\": \"Info EmptyBase\",\n" + 
			"        \"type\": \"object\",\n" + 
			"        \"required\": [\n" + 
			"            \"id\"\n" + 
			"        ],\n" + 
			"        \"properties\": {\n" + 
			"            \"id\": {\n" + 
			"                \"type\": \"string\"\n" + 
			"            }\n" + 
			"        }\n" + 
			"    },\n" + 
			"    \"description\": \"Ontologia para comandos de TMS\",\n" + 
			"    \"additionalProperties\": true\n" + 
			"}";
	
	private String GOOD_JSON_DATA = "{\"Command\":{ \"id\":\"string\"}}";
	
	@Test
	public void given_OneValidJsonSchemaAndOneCompliantJson_When_TheJsonIsValidated_Then_ItReturnsTrue() {
		assertTrue("This Json data should pass the validation",service.isJsonCompliantWithSchema(GOOD_JSON_DATA, GOOD_JSON_SCHEMA));
	}
}
