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
package com.indracompany.sofia2.simulator.job;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.simulator.service.FieldRandomizerService;
import com.indracompany.sofia2.simulator.service.PersistenceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeviceSimulatorJob {

	@Autowired
	OntologyService ontologyService;
	@Autowired
	private FieldRandomizerService fieldRandomizerService;
	@Autowired
	private PersistenceService persistenceService;
	private final static String PATH_DATA = "datos";
	private final static String PATH_PROPERTIES = "properties";
	private final static String DATE_VAR = "$date";
	private final static String NUMBER_VAR = "number";
	private final static String STRING_VAR = "string";
	private final static String OBJECT_VAR = "object";
	private final static String ARRAY_VAR = "array";
	private JsonNode schema;

	public void execute(JobExecutionContext context) throws IOException {

		String user = context.getJobDetail().getJobDataMap().getString("userId");
		String json = context.getJobDetail().getJobDataMap().getString("json");

		try {
			this.generateInstance(user, json);
		} catch (Exception e) {
			log.error("Error generating the ontology instance");
		}

	}

	public JsonNode generateInstance(String user, String json) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsonInstance = mapper.readTree(json);

		String clientPlatform = jsonInstance.get("clientPlatform").asText();
		String clientPlatformInstance = jsonInstance.get("clientPlatformInstance").asText();
		String ontology = jsonInstance.get("ontology").asText();

		JsonNode ontologySchema = this.generateJson(ontology, user);
		JsonNode fieldAndValues = this.fieldRandomizerService.randomizeFields(jsonInstance.path("fields"),
				ontologySchema);

		log.debug("Inserted ontology: " + fieldAndValues.toString());
		this.persistenceService.insertOntologyInstance(fieldAndValues.toString(), ontology, user, clientPlatform,
				clientPlatformInstance);
		return fieldAndValues;
	}

	private JsonNode generateJson(String ontology, String user) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode ontologySchema = mapper
				.readTree(this.ontologyService.getOntologyByIdentification(ontology, user).getJsonSchema());
		this.schema = ontologySchema;
		JsonNode fieldsSchema = mapper.createObjectNode();

		String pathToProperties = (!ontologySchema.path(PATH_DATA).isMissingNode() ? PATH_DATA : PATH_PROPERTIES);

		Iterator<String> fields = !ontologySchema.path(PATH_DATA).isMissingNode()
				? ontologySchema.path(pathToProperties).path(PATH_PROPERTIES).fieldNames()
				: ontologySchema.path(PATH_PROPERTIES).fieldNames();
		ontologySchema = !ontologySchema.path(PATH_DATA).isMissingNode()
				? ontologySchema.path(PATH_DATA).path(PATH_PROPERTIES)
				: ontologySchema.path(PATH_PROPERTIES);
		while (fields.hasNext()) {
			String field = fields.next();
			if (ontologySchema.path(field).get("type").asText().equals(STRING_VAR))
				if (!ontologySchema.path(field).path("enum").isMissingNode())
					((ObjectNode) fieldsSchema).put(field, ontologySchema.path(field).get("enum").get(0).asText());
				else {
					if (field.equals(DATE_VAR) || ontologySchema.path(field).get("format") != null)
						((ObjectNode) fieldsSchema).put(field, this.getCurrentDate());
					else
						((ObjectNode) fieldsSchema).put(field, "");
				}
			else if (ontologySchema.path(field).get("type").asText().equals(NUMBER_VAR))
				((ObjectNode) fieldsSchema).put(field, 0);
			else if (ontologySchema.path(field).get("type").asText().equals(OBJECT_VAR)) {
				JsonNode object = this.createObject(ontologySchema.path(field));
				((ObjectNode) fieldsSchema).set(field, object);
			} else if (ontologySchema.path(field).get("type").asText().equals(ARRAY_VAR)) {
				JsonNode object = this.createArray(ontologySchema.path(field));
				JsonNode arrayNode = mapper.createArrayNode();
				((ArrayNode) arrayNode).add(object);
				((ObjectNode) fieldsSchema).set(field, arrayNode);
			}

		}

		if (schema.path(PATH_PROPERTIES).size() == 1) {
			String context = schema.get(PATH_PROPERTIES).fields().next().getKey();
			return mapper.createObjectNode().set(context, fieldsSchema);
		} else {
			return fieldsSchema;

		}

	}

	private JsonNode createObject(JsonNode fieldNode) {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode objectNode = mapper.createObjectNode();

		if (!fieldNode.path(PATH_PROPERTIES).isMissingNode()) {
			fieldNode = fieldNode.path(PATH_PROPERTIES);
			Iterator<String> fields = fieldNode.fieldNames();

			while (fields.hasNext()) {
				String field = fields.next();
				if (fieldNode.path(field).get("type").asText().equals(STRING_VAR))
					if (!fieldNode.path(field).path("enum").isMissingNode())
						((ObjectNode) objectNode).put(field, fieldNode.path(field).get("enum").get(0).asText());
					else {
						if (field.equals(DATE_VAR) || fieldNode.path(field).get("format") != null)
							((ObjectNode) objectNode).put(field, this.getCurrentDate());
						else
							((ObjectNode) objectNode).put(field, "");
					}
				else if (fieldNode.path(field).get("type").asText().equals(NUMBER_VAR))
					((ObjectNode) objectNode).put(field, 0);
				else if (fieldNode.path(field).get("type").asText().equals(OBJECT_VAR)) {
					JsonNode object = this.createObject(fieldNode.path(field));
					((ObjectNode) objectNode).set(field, object);
				} else if (fieldNode.path(field).get("type").asText().equals(ARRAY_VAR)) {
					JsonNode object = this.createArray(fieldNode.path(field));
					if (!object.isArray()) {
						JsonNode arrayNode = mapper.createArrayNode();
						((ArrayNode) arrayNode).add(object);
						((ObjectNode) objectNode).set(field, arrayNode);
					} else
						((ObjectNode) objectNode).set(field, object);

				}

			}
		} else {
			Iterator<String> fields = fieldNode.fieldNames();
			while (fields.hasNext()) {
				String objectType = fields.next();
				if (objectType.equals("oneOf") || objectType.equals("anyOf")) {
					String property = fieldNode.get(objectType).get(0).get("$ref").asText().replace("#/", "");
					objectNode = this.createObject(this.schema.path(property));
				}
			}

		}

		return objectNode;
	}

	private JsonNode createArray(JsonNode fieldNode) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode objectNode = mapper.createObjectNode();

		if (!fieldNode.path(PATH_PROPERTIES).isMissingNode()) {
			fieldNode = fieldNode.path(PATH_PROPERTIES);
			Iterator<String> fields = fieldNode.fieldNames();

			while (fields.hasNext()) {
				String field = fields.next();
				if (fieldNode.path(field).get("type").asText().equals(STRING_VAR))
					if (!fieldNode.path(field).path("enum").isMissingNode())
						((ObjectNode) objectNode).put(field, fieldNode.path(field).get("enum").get(0).asText());
					else {
						if (field.equals(DATE_VAR) || fieldNode.path(field).get("format") != null)
							((ObjectNode) objectNode).put(field, this.getCurrentDate());
						else
							((ObjectNode) objectNode).put(field, "");
					}
				else if (fieldNode.path(field).get("type").asText().equals(NUMBER_VAR))
					((ObjectNode) objectNode).put(field, 0);
				else if (fieldNode.path(field).get("type").asText().equals(OBJECT_VAR)) {
					return this.createObject(fieldNode.path(field));
				}

			}

		} else if (!fieldNode.path("items").isMissingNode()) {

			fieldNode = fieldNode.path("items");
			int size = fieldNode.size();
			ArrayNode nodeArray = mapper.createArrayNode();

			for (int i = 0; i < size; i++) {
				String type;

				if (fieldNode.isArray()) {
					type = fieldNode.get(i).get("type").asText();
					for (int j = 0; j < size; j++) {
						JsonNode nodeAux = mapper.createObjectNode();
						if (type.equals(STRING_VAR)) {

							((ObjectNode) nodeAux).put(String.valueOf(j), "");
							nodeArray.add("");
						} else if (type.equals(NUMBER_VAR)) {

							((ObjectNode) nodeAux).put(String.valueOf(j), 0);
							nodeArray.add(0);

						} else if (type.equals(OBJECT_VAR)) {
							JsonNode object = this.createObject(fieldNode);
							return object;
						}

					}
					if (!nodeArray.isNull())
						return nodeArray;

				} else {
					type = fieldNode.get("type").asText();

				}
				if (type.equals(STRING_VAR)) {

					nodeArray.add("");

				} else if (type.equals(NUMBER_VAR)) {
					nodeArray.add(0);

				} else if (type.equals(OBJECT_VAR)) {
					JsonNode object = this.createObject(fieldNode);
					return object;
				} else if (type.equals(ARRAY_VAR)) {
					JsonNode object = this.createArray(fieldNode.path(i));
					JsonNode arrayNode = mapper.createArrayNode();
					((ArrayNode) arrayNode).add(object);
					((ObjectNode) objectNode).set(String.valueOf(i), arrayNode);
				}

			}
			if (!nodeArray.isNull())
				return nodeArray;

		}

		return objectNode;
	}

	private String getCurrentDate() {
		DateFormat dfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		return dfr.format(new Date());
	}
}
