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

import java.io.IOException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.indracompany.sofia2.commons.security.PasswordEncoder;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.router.service.app.model.OperationModel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OntologyDataServiceImpl implements OntologyDataService {

	@Autowired
	private OntologyRepository ontologyRepository;

	final private ObjectMapper objectMapper = new ObjectMapper();

	final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

	final public static String ENCRYPT_PROPERTY = "encrypted";

	private void checkOntologySchemaCompliance(final String data, final Ontology ontology)
			throws DataSchemaValidationException {
		final String jsonSchema = ontology.getJsonSchema();
		checkJsonCompliantWithSchema(data, jsonSchema);
	}

	void checkJsonCompliantWithSchema(final JsonNode data, final JsonNode schemaJson)
			throws ProcessingException, DataSchemaValidationException {
		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		JsonSchema schema = factory.getJsonSchema(schemaJson);
		ProcessingReport report = schema.validate(data);

		if (report != null && !report.isSuccess()) {
			final Iterator<ProcessingMessage> it = report.iterator();
			final StringBuffer msgerror = new StringBuffer();
			while (it.hasNext()) {
				ProcessingMessage msg = it.next();
				if (msg.getLogLevel().equals(LogLevel.ERROR)) {
					msgerror.append(msg.asJson());
				}
			}

			throw new DataSchemaValidationException(msgerror.toString());
		}
	}

	void checkJsonCompliantWithSchema(final String dataString, final String schemaString)
			throws DataSchemaValidationException {
		JsonNode dataJson;
		JsonNode schemaJson;

		try {
			dataJson = JsonLoader.fromString(dataString);
			schemaJson = JsonLoader.fromString(schemaString);
			checkJsonCompliantWithSchema(dataJson, schemaJson);

		} catch (IOException e) {
			throw new DataSchemaValidationException("Error reading data for checking schema compliance", e);
		} catch (ProcessingException e) {
			throw new DataSchemaValidationException("Error checking data schema compliance", e);
		}
	}

	String addContextData(final OperationModel operationModel) throws JsonProcessingException, IOException {

		final String body = operationModel.getBody();
		final String user = operationModel.getUser();
		final String clientConnection = operationModel.getClientConnection();
		final String clientPlatformId = operationModel.getClientPlatformId();
		final String clientPlatoformInstance = operationModel.getClientPlatoformInstance();
		final String clientSession = operationModel.getClientSession();

		final String timezoneId = ZoneId.systemDefault().toString();
		final String timestamp = Calendar.getInstance(TimeZone.getTimeZone(timezoneId)).getTime().toString();
		final long timestampMillis = System.currentTimeMillis();
		final ContextData contextData = ContextData.builder(user, timezoneId, timestamp, timestampMillis)
				.clientConnection(clientConnection).clientPatform(clientPlatformId)
				.clientPatformInstance(clientPlatoformInstance).clientSession(clientSession).build();

		final JsonNode jsonBody = objectMapper.readTree(body);
		if (jsonBody.isObject()) {
			final ObjectNode nodeBody = (ObjectNode) jsonBody;
			nodeBody.set("contextData", objectMapper.valueToTree(contextData));
			return objectMapper.writeValueAsString(nodeBody);
		} else {
			throw new IllegalStateException("Body should have a valid json object");
		}

	}

	String encryptData(String data, Ontology ontology) throws IOException {

		if (ontology.isAllowsCypherFields()) {

			final JsonNode jsonSchema = objectMapper.readTree(ontology.getJsonSchema());
			final JsonNode jsonData = objectMapper.readTree(data);
			String path = "#";
			String schemaPointer = "";

			processProperties(jsonData, jsonSchema, jsonSchema, path, schemaPointer);

			return jsonData.toString();

		} else {
			return data;
		}

	}

	private void processProperties(JsonNode allData, JsonNode schema, JsonNode rootSchema, String path,
			String schemaPointer) {

		JsonNode properties = schema.path("properties");
		Iterator<Entry<String, JsonNode>> elements = properties.fields();

		while (elements.hasNext()) {
			Entry<String, JsonNode> element = elements.next();
			if (element != null) {
				processProperty(allData, element.getKey(), element.getValue(), rootSchema,
						path + "/" + element.getKey(), schemaPointer + "/" + "properties/" + element.getKey());
			}
		}
	}

	private void processProperty(JsonNode allData, String elementKey, JsonNode elementValue, JsonNode rootSchema,
			String path, String schemaPointer) {

		JsonNode ref = elementValue.path("$ref");
		if (!ref.isMissingNode()) {
			String refString = ref.asText();
			JsonNode referencedElement = getReferencedJsonNode(refString, rootSchema);
			String newSchemaPointer = refString.substring(refString.lastIndexOf("#/")).substring(1);
			processProperties(allData, referencedElement, rootSchema, path, newSchemaPointer);
		} else {
			JsonNode oneOf = elementValue.path("oneOf");
			if (!oneOf.isMissingNode()) {
				// only one of the schemas is valid for the property
				if (oneOf.isArray()) {
					Iterator<JsonNode> miniSchemas = oneOf.elements();
					JsonNode miniData = getReferencedJsonNode(path, allData);
					boolean notFound = true;
					while (notFound && miniSchemas.hasNext()) {
						try {
							JsonNode miniSchema = miniSchemas.next();
							JsonSchema schema = factory.getJsonSchema(rootSchema, schemaPointer);
							ProcessingReport report = schema.validate(miniData);
							if (report.isSuccess()) {
								notFound = false;

								processProperty(allData, elementKey, miniSchema, rootSchema, path, schemaPointer);
							}
						} catch (ProcessingException e) {
							// if it is not the valid schema it must be ignored
							log.trace("Mini Schema skipped", e);
						}
					}
				}
			} else {
				JsonNode allOf = elementValue.path("allOf");
				JsonNode anyOf = elementValue.path("anyOf");
				Iterator<JsonNode> miniSchemas = null;
				if (!anyOf.isMissingNode()) {
					if (anyOf.isArray()) {
						miniSchemas = anyOf.elements();
					}
				} else if (!allOf.isMissingNode()) {
					if (allOf.isArray()) {
						miniSchemas = allOf.elements();
					}
				}

				if (miniSchemas != null) {
					JsonNode miniData = getReferencedJsonNode(path, allData);
					while (miniSchemas.hasNext()) {
						try {
							JsonNode miniSchema = miniSchemas.next();
							JsonSchema schema = factory.getJsonSchema(rootSchema, schemaPointer);
							ProcessingReport report = schema.validate(miniData);
							if (report.isSuccess()) {
								processProperty(allData, elementKey, miniSchema, rootSchema, path, schemaPointer);
							}
						} catch (ProcessingException e) {
							// if it is not the valid schema it must be ignored
							log.trace("Mini Schema skipped", e);
						}
					}
				} else {
					JsonNode encrypt = elementValue.path(ENCRYPT_PROPERTY);
					if (encrypt.asBoolean()) {
						JsonNode data = getReferencedJsonNode(path, allData);
						String dataToEncrypt = data.asText();
						try {
							String encrypted = PasswordEncoder.getInstance().encodeSHA256(dataToEncrypt);
							String propertyPath = path.substring(0, path.lastIndexOf("/"));
							JsonNode originalData = getReferencedJsonNode(propertyPath, allData);
							((ObjectNode) originalData).put(elementKey, encrypted);
						} catch (final Exception e) {
							log.error("Error in encrypting data: " + e.getMessage());
							throw new RuntimeException(e);
						}
					} else {
						processProperties(allData, elementValue, rootSchema, path, schemaPointer);
					}
				}
			}
		}
	}

	private JsonNode getReferencedJsonNode(String ref, JsonNode root) {
		String[] path = ref.split("/");
		assert path[0].equals("#");
		JsonNode referecedElement = root;

		for (int i = 1; i < path.length; i++) {
			referecedElement = referecedElement.path(path[i]);
		}

		return referecedElement;
	}

	@Override
	public String preProcessInsertData(OperationModel operationModel) throws DataSchemaValidationException {
		final String data = operationModel.getBody();
		final String ontologyName = operationModel.getOntologyName();
		final Ontology ontology = ontologyRepository.findByIdentification(ontologyName);
		checkOntologySchemaCompliance(data, ontology);
		try {
			String bodyWithDataContext = addContextData(operationModel);
			String encryptedDataInBODY = encryptData(bodyWithDataContext, ontology);
			return encryptedDataInBODY;
		} catch (IOException e) {
			throw new RuntimeException("Error working with JSON data", e);
		}

	}

}
