/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
	
	private final String GOOD_JSON_SCHEMA = "{\n" + 
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
	
	private final String BAD_JSON_SCHEMA = "{\n" + 
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
			"            \"ERROR_ID_NAME\"\n" +   //Error in the id name
			"        ],\n" + 
			"        \"properties\": {\n" + 
			"            \"id\": {\n" + 
			"                \"type\": \"string\"\n" + 
			"            }\n" + 
			"        }\n" + 
			"    },\n" + 
			"    \"description\": \"Ontologia para comandos de TMS\",\n" + 
			"    \"additionalProperties\": true\n" + 
			"}";;
	
	private final String GOOD_JSON_DATA = "{\"Command\":{ \"id\":\"string\"}}";
	
	private final String NONVALID_JSON_DATA = "{\"Something\":{ \"id\":\"string\"}}"; //Something is not declared in the schema
	
	private final String BAD_JSON_DATA = "{Something\":{ \"id\":\"string\"}}"; //invalid JSON. Something should be surrounded by quotes.
	
	@Test
	public void given_OneValidJsonSchemaAndOneCompliantJson_When_TheJsonIsValidated_Then_ItReturnsTrue() {
		assertTrue("This Json data should pass the validation", service.isJsonCompliantWithSchema(GOOD_JSON_DATA, GOOD_JSON_SCHEMA));
	}
	
	@Test
	public void given_OneInvalidJsonSchemaAndOneCompliantJson_When_TheJsonIsValidated_Then_ItResturnsFalse() {
		assertFalse("The schema should not be validated", service.isJsonCompliantWithSchema(GOOD_JSON_DATA, BAD_JSON_SCHEMA));
	}
	
	@Test
	public void given_OneValidJsonSchemaAndOneNotCompliantJson_When_TheJsonIsValidated_Then_ItReturnsFalse() {
		assertFalse("The Json data should not pass the validation", service.isJsonCompliantWithSchema(NONVALID_JSON_DATA, GOOD_JSON_SCHEMA));
	}
	
	@Test
	public void given_OneValidJsonSchemaAndOneIncorrectJson_When_TheJsonIsValidated_Then_ItReturnsFalse() {
		assertFalse("The Json data is incorrect, it should not be validated", service.isJsonCompliantWithSchema(BAD_JSON_DATA, GOOD_JSON_SCHEMA));
	}
}
