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
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.router.service.app.model.OperationModel;

@Service
public class OntologyDataServiceImpl implements OntologyDataService {
	
	@Autowired
	private OntologyRepository ontologyRepository;
	
	@Override
	public void checkOntologySchemaCompliance(final String data, final Ontology ontology) throws DataSchemaValidationException {
		final String jsonSchema = ontology.getJsonSchema();
		checkJsonCompliantWithSchema(data, jsonSchema);
	}
	
	@Override
	public void checkOntologySchemaCompliance(final String data, final String ontologyName) throws DataSchemaValidationException {
		final Ontology ontology = ontologyRepository.findByIdentification(ontologyName);
		checkOntologySchemaCompliance(data, ontology);
	}
		
	void checkJsonCompliantWithSchema(final String dataString, final String schemaString) throws DataSchemaValidationException{
		JsonNode dataJson;
		JsonNode schemaJson;
		JsonSchema schema;
		ProcessingReport report;
		try {
			dataJson = JsonLoader.fromString(dataString);
			schemaJson = JsonLoader.fromString(schemaString);
			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			schema = factory.getJsonSchema(schemaJson);
			report = schema.validate(dataJson);
		} catch (IOException e) {
			throw new DataSchemaValidationException ("Error reading data for cheaking schema compliance", e);
		} catch (ProcessingException e) {
			throw new DataSchemaValidationException ("Error checking data schema compliance", e);
			
		}
		
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

	@Override
	public String addContextData(final OperationModel operationModel) throws JsonProcessingException, IOException {

		final String body = operationModel.getBody();
		final String user = operationModel.getUser();
		final String clientConnection = operationModel.getClientConnection();
		final String clientPlatformId = operationModel.getClientPlatformId();
		final String clientPlatoformInstance = operationModel.getClientPlatoformInstance();
		final String clientSession = operationModel.getClientSession();
		
		
		final String timezoneId = ZoneId.systemDefault().toString();
		final String timestamp = Calendar.getInstance(TimeZone.getTimeZone(timezoneId)).getTime().toString();
		final ContextData contextData = ContextData.builder(user, timezoneId, timestamp)
				.clientConnection(clientConnection)
				.clientPatform(clientPlatformId)
				.clientPatformInstance(clientPlatoformInstance)
				.clientSession(clientSession)
				.build();
		
		final ObjectMapper objectMapper = new ObjectMapper();
		final JsonNode jsonBody = objectMapper.readTree(body);
		if (jsonBody.isObject()) {
			final ObjectNode nodeBody = (ObjectNode) jsonBody;
			nodeBody.set("contextData", objectMapper.valueToTree(contextData));
			return objectMapper.writeValueAsString(nodeBody);
		} else {
			throw new IllegalStateException("Body should have a valid json object");
		}

	}

}
