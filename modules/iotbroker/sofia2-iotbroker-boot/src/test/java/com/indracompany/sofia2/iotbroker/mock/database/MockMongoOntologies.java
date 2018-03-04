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
package com.indracompany.sofia2.iotbroker.mock.database;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

@Component
public class MockMongoOntologies {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	ManageDBRepository manage;
	@Autowired
	BasicOpsDBRepository repository;

	public <T> boolean createOntology(Class<T> ontology) throws JsonGenerationException, JsonMappingException, IOException {
		final List<String> list = manage.getListOfTables();
		if(list.contains(ontology.getSimpleName())) {
			repository.delete(ontology.getSimpleName());
			return true;
		}

		final JsonSchemaGenerator generator = new JsonSchemaGenerator(objectMapper);
		final JsonSchema jsonSchema = generator.generateSchema(ontology);

		final StringWriter json = new StringWriter();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.writeValue(json, jsonSchema);

		System.out.println(json.toString());
		manage.createTable4Ontology(ontology.getSimpleName(), json.toString());

		return true;
	}


	public <T>void deleteOntology(Class<T> ontology) {
		repository.delete(ontology.getSimpleName());
		manage.removeTable4Ontology(ontology.getSimpleName());
	}


}
