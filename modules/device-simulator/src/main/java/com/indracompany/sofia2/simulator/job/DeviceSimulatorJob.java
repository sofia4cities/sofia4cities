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

import com.fasterxml.jackson.core.JsonProcessingException;
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

	private JsonNode schema;

	public void execute(JobExecutionContext context) throws JsonProcessingException, IOException {

		String user = context.getJobDetail().getJobDataMap().getString("userId");
		String json = context.getJobDetail().getJobDataMap().getString("json");
		String id = context.getJobDetail().getJobDataMap().getString("id");
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

		JsonNode ontologySchema = this.generateJsonSchema(ontology, user);
		JsonNode fieldAndValues = this.fieldRandomizerService.randomizeFields(jsonInstance.path("fields"),
				ontologySchema);

		log.debug("Inserted ontology: " + fieldAndValues.toString());
		this.persistenceService.insertOntologyInstance(fieldAndValues.toString(), ontology, user, clientPlatform,
				clientPlatformInstance);
		return fieldAndValues;
	}

	private JsonNode generateJsonSchema(String ontology, String user) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode ontologySchema = mapper
				.readTree(this.ontologyService.getOntologyByIdentification(ontology, user).getJsonSchema());
		this.schema = ontologySchema;
		JsonNode fieldsSchema = mapper.createObjectNode();

		if (!ontologySchema.path("datos").isMissingNode()) {

			Iterator<String> fields = ontologySchema.path("datos").path("properties").fieldNames();
			ontologySchema = ontologySchema.path("datos").path("properties");
			while (fields.hasNext()) {
				String field = fields.next();
				if (ontologySchema.path(field).get("type").asText().equals("string"))
					if (!ontologySchema.path(field).path("enum").isMissingNode())
						((ObjectNode) fieldsSchema).put(field, ontologySchema.path(field).get("enum").get(0).asText());
					else {
						if (field.equals("$date"))
							((ObjectNode) fieldsSchema).put(field, this.getCurrentDate());
						else
							((ObjectNode) fieldsSchema).put(field, "");
					}
				else if (ontologySchema.path(field).get("type").asText().equals("number"))
					((ObjectNode) fieldsSchema).put(field, 0);
				else if (ontologySchema.path(field).get("type").asText().equals("object")) {
					JsonNode object = this.createObjectNode(ontologySchema.path(field));
					((ObjectNode) fieldsSchema).set(field, object);
				} else if (ontologySchema.path(field).get("type").asText().equals("array")) {
					JsonNode object = this.createArrayNode(ontologySchema.path(field));
					JsonNode arrayNode = mapper.createArrayNode();
					((ArrayNode) arrayNode).add(object);
					((ObjectNode) fieldsSchema).set(field, arrayNode);
				}

			}
		}
		String context = mapper
				.readTree(this.ontologyService.getOntologyByIdentification(ontology, user).getJsonSchema())
				.get("properties").fields().next().getKey();
		return mapper.createObjectNode().set(context, fieldsSchema);

	}

	private JsonNode createObjectNode(JsonNode fieldNode) {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode objectNode = mapper.createObjectNode();

		if (!fieldNode.path("properties").isMissingNode()) {
			fieldNode = fieldNode.path("properties");
			Iterator<String> fields = fieldNode.fieldNames();

			while (fields.hasNext()) {
				String field = fields.next();
				if (fieldNode.path(field).get("type").asText().equals("string"))
					if (!fieldNode.path(field).path("enum").isMissingNode())
						((ObjectNode) objectNode).put(field, fieldNode.path(field).get("enum").get(0).asText());
					else {
						if (field.equals("$date"))
							((ObjectNode) objectNode).put(field, this.getCurrentDate());
						else
							((ObjectNode) objectNode).put(field, "");
					}
				else if (fieldNode.path(field).get("type").asText().equals("number"))
					((ObjectNode) objectNode).put(field, 0);
				else if (fieldNode.path(field).get("type").asText().equals("object")) {
					JsonNode object = this.createObjectNode(fieldNode.path(field));
					((ObjectNode) objectNode).set(field, object);
				} else if (fieldNode.path(field).get("type").asText().equals("array")) {
					JsonNode object = this.createArrayNode(fieldNode.path(field));
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
					objectNode = this.createObjectNode(this.schema.path(property));
				}
			}

		}

		return objectNode;
	}

	private JsonNode createArrayNode(JsonNode fieldNode) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode objectNode = mapper.createObjectNode();

		if (!fieldNode.path("properties").isMissingNode()) {
			fieldNode = fieldNode.path("properties");
			Iterator<String> fields = fieldNode.fieldNames();

			while (fields.hasNext()) {
				String field = fields.next();
				if (fieldNode.path(field).get("type").asText().equals("string"))
					if (!fieldNode.path(field).path("enum").isMissingNode())
						((ObjectNode) objectNode).put(field, fieldNode.path(field).get("enum").get(0).asText());
					else {
						if (field.equals("$date"))
							((ObjectNode) objectNode).put(field, this.getCurrentDate());
						else
							((ObjectNode) objectNode).put(field, "");
					}
				else if (fieldNode.path(field).get("type").asText().equals("number"))
					((ObjectNode) objectNode).put(field, 0);
				else if (fieldNode.path(field).get("type").asText().equals("object")) {
					JsonNode object = this.createObjectNode(fieldNode.path(field));
					return object;
				}
				// ((ObjectNode) objectNode).set(field, object);
				// else if (fieldNode.path(field).get("type").asText().equals("array")) {
				// JsonNode object = this.createArrayNode(fieldNode.path(field));
				// JsonNode arrayNode = mapper.createArrayNode();
				// ((ArrayNode) arrayNode).add(object);
				// ((ObjectNode) objectNode).set(field, arrayNode);
				// }

			}

		} else if (!fieldNode.path("items").isMissingNode()) {

			fieldNode = fieldNode.path("items");
			int size = fieldNode.size();
			ArrayNode nodeArray = mapper.createArrayNode();

			for (int i = 0; i < size; i++) {
				String type;
				String fieldName;
				boolean isEnum;

				if (fieldNode.isArray()) {
					type = fieldNode.get(i).get("type").asText();
					for (int j = 0; j < size; j++) {
						JsonNode nodeAux = mapper.createObjectNode();
						if (type.equals("string")) {

							// ((ObjectNode) nodeAux).put(String.valueOf(j), "");
							((ObjectNode) nodeAux).put(String.valueOf(j), "");
							((ArrayNode) nodeArray).add("");
						} else if (type.equals("number")) {
							// ((ObjectNode) nodeAux).put(String.valueOf(j), 0);
							((ObjectNode) nodeAux).put(String.valueOf(j), 0);
							((ArrayNode) nodeArray).add(0);

						} else if (type.equals("object")) {
							JsonNode object = this.createObjectNode(fieldNode);
							return object;
						}
						// else if (type.equals("array")) {
						// JsonNode object = this.createArrayNode(fieldNode.path(i));
						// JsonNode arrayNode = mapper.createArrayNode();
						// ((ArrayNode) arrayNode).add(object);
						// ((ObjectNode) objectNode).set(String.valueOf(i), arrayNode);
						// }
					}
					if (!nodeArray.isNull())
						return nodeArray;

				} else {
					type = fieldNode.get("type").asText();

				}
				if (type.equals("string")) {

					((ArrayNode) nodeArray).add("");

				} else if (type.equals("number")) {
					((ArrayNode) nodeArray).add(0);

				} else if (type.equals("object")) {
					JsonNode object = this.createObjectNode(fieldNode);
					return object;
				} else if (type.equals("array")) {
					JsonNode object = this.createArrayNode(fieldNode.path(i));
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
	// TODO jsonnode para fecha

	private String getCurrentDate() {
		DateFormat dfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		return dfr.format(new Date());
	}
}
