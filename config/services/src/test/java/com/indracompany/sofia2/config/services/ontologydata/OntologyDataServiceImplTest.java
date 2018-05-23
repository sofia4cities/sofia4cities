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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.services.ontologydata.OntologyDataServiceImpl.EncryptionOperations;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.Source;

@RunWith(MockitoJUnitRunner.class)
public class OntologyDataServiceImplTest {

	@InjectMocks
	OntologyDataServiceImpl service;

	@Mock
	private OntologyRepository ontologyRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void given_OneValidJsonSchemaAndOneCompliantJson_When_TheJsonIsValidated_Then_ExceptionIsNotLauched()
			throws DataSchemaValidationException {
		service.checkJsonCompliantWithSchema(TestResources.DATA_FOR_GOOD_JSON, TestResources.GOOD_JSON_SCHEMA);
	}

	@Test(expected = DataSchemaValidationException.class)
	public void given_OneInvalidJsonSchemaAndOneCompliantJson_When_TheJsonIsValidated_Then_ExceptionIsLauched()
			throws DataSchemaValidationException {
		service.checkJsonCompliantWithSchema(TestResources.DATA_FOR_GOOD_JSON, TestResources.BAD_JSON_SCHEMA);
	}

	@Test(expected = DataSchemaValidationException.class)
	public void given_OneValidJsonSchemaAndOneNotCompliantJson_When_TheJsonIsValidated_Then_ItReturnsFalse()
			throws DataSchemaValidationException {
		service.checkJsonCompliantWithSchema(TestResources.DATA_FOR_NONVALID_JSON, TestResources.GOOD_JSON_SCHEMA);
	}

	@Test(expected = DataSchemaValidationException.class)
	public void given_OneValidJsonSchemaAndOneIncorrectJson_When_TheJsonIsValidated_Then_ItReturnsFalse()
			throws DataSchemaValidationException {
		service.checkJsonCompliantWithSchema(TestResources.DATA_FOR_BAD_JSON, TestResources.GOOD_JSON_SCHEMA);
	}

	@Test
	public void given_OneValidOperationModel_When_TheDataContextHasToBeCreated_Then_TheCompleteDataWithTheDataContextIsGenerated()
			throws JsonProcessingException, IOException {
		final String ontologyName = "Commands";
		final OperationType operationType = OperationType.INSERT;
		final String user = "developer";
		final Source source = Source.INTERNAL_ROUTER;
		final String clientConnection = "connection1";
		final String deviceTemplate = "platform1";
		final String device = "instance1";
		final String clientSession = "session1";
		OperationModel om = OperationModel.builder(ontologyName, operationType, user, source)
				.clientConnection(clientConnection).deviceTemplate(deviceTemplate).device(device)
				.clientSession(clientSession).body(TestResources.DATA_FOR_GOOD_JSON).cacheable(false)
				.queryType(QueryType.NATIVE).build();

		String completeBody = service.addContextData(om, null);
		assertTrue("The body should be created", completeBody != null);
		final JsonNode jsonBody = objectMapper.readTree(completeBody);

		JsonNode contextData = jsonBody.findValue("contextData");
		assertTrue("The contextData should be created", contextData != null);

		JsonNode timestampJSON = contextData.findValue("timestamp");
		assertTrue("The timestamp should be created", timestampJSON != null);

		JsonNode timezoneIdJSON = contextData.findValue("timezoneId");
		assertTrue("The timezoneId should be created", timezoneIdJSON != null);

		JsonNode userJSON = contextData.findValue("user");
		assertTrue("The user should be created", userJSON.asText().equals(user));

		JsonNode deviceTemplateJSON = contextData.findValue("deviceTemplate");
		assertTrue("The deviceTemplate should be created", deviceTemplateJSON.asText().equals(deviceTemplate));

		JsonNode deviceJSON = contextData.findValue("device");
		assertTrue("The device should be created", deviceJSON.asText().equals(device));

		JsonNode clientConnectionJSON = contextData.findValue("clientConnection");
		assertTrue("The clientConnection should be created", clientConnectionJSON.asText().equals(clientConnection));

		JsonNode clientSessionJSON = contextData.findValue("clientSession");
		assertTrue("The clientSession should be created", clientSessionJSON.asText().equals(clientSession));
	}

	@Test
	public void given_OneOntologyThatDoesNotAllowEncryption_When_AnInsertIsPerformed_Then_NothingIsDone()
			throws JsonProcessingException, IOException {

		final String ontologyName = "one";
		final Ontology ontology = new Ontology();
		ontology.setId("1");
		ontology.setIdentification(ontologyName);
		ontology.setJsonSchema(TestResources.GOOD_JSON_SCHEMA);

		String encryptedData = service.encryptionOperation(TestResources.DATA_FOR_GOOD_JSON, ontology,
				EncryptionOperations.encrypt);

		assertTrue("If ontology does not allow encryption, data should not be encrypted",
				TestResources.DATA_FOR_GOOD_JSON.equals(encryptedData));
	}

	@Test
	public void given_OneOntologyThatDoesAllowEncryptionWithRefsInTheSchema_When_AnInsertIsPerformed_Then_CorrectDataIsEncripted()
			throws JsonProcessingException, IOException {

		final String ontologyName = "one";
		final Ontology ontology = new Ontology();
		ontology.setId("1");
		ontology.setIdentification(ontologyName);
		ontology.setJsonSchema(TestResources.SMALL_SCHEMA_WITH_ENCRYPTION);
		ontology.setAllowsCypherFields(true);

		String encryptedData = service.encryptionOperation(TestResources.DATA_FOR_GOOD_JSON, ontology,
				EncryptionOperations.encrypt);

		JsonNode jsonData = objectMapper.readTree(TestResources.DATA_FOR_GOOD_JSON);
		JsonNode id = jsonData.findPath("id");
		JsonNode jsonEncryptedData = objectMapper.readTree(encryptedData);
		JsonNode encryptedId = jsonEncryptedData.findPath("id");

		assertFalse("Data should be encrypted", id.toString().equals(encryptedId.toString()));
	}

	@Test
	public void given_OneOntologyThatDoesAllowEncryptionWithSchemaWithArraysAndObjects_When_AnInsertIsPerformed_Then_CorrectDataIsEncripted()
			throws JsonProcessingException, IOException {
		final String ontologyName = "one";
		final Ontology ontology = new Ontology();
		ontology.setId("1");
		ontology.setIdentification(ontologyName);
		ontology.setJsonSchema(TestResources.LONG_SCHEMA_WITH_ENCRYPTED);
		ontology.setAllowsCypherFields(true);

		String encryptedData = service.encryptionOperation(TestResources.DATA_FOR_LONG_SCHEMA_TO_ENCRYPT, ontology,
				EncryptionOperations.encrypt);

		JsonNode jsonData = objectMapper.readTree(TestResources.DATA_FOR_LONG_SCHEMA_TO_ENCRYPT);
		JsonNode name = jsonData.findPath("image").path("media").path("name");
		JsonNode mime = jsonData.findPath("image").path("media").path("mime");
		JsonNode coordinates = jsonData.findPath("geometry").path("coordinates");
		JsonNode measures = jsonData.findPath("measures");

		JsonNode jsonEncryptedData = objectMapper.readTree(encryptedData);
		JsonNode encryptedName = jsonEncryptedData.findPath("image").path("media").path("name");
		JsonNode encryptedMime = jsonEncryptedData.findPath("image").path("media").path("mime");
		JsonNode encryptedCoordinates = jsonEncryptedData.findPath("geometry").path("coordinates");
		JsonNode encryptedMeasures = jsonEncryptedData.findPath("measures");

		assertFalse("Feed.image.media.name should be encrypted", name.toString().equals(encryptedName.toString()));
		assertFalse("Feed.measures[0].name should be encrypted",
				measures.toString().equals(encryptedMeasures.toString()));
		assertFalse("Feed.geometry.coordinates shoulb be encrypted",
				coordinates.toString().equals(encryptedCoordinates.toString()));
		assertTrue("Feed.image.media.mime should not be encrypted", mime.toString().equals(encryptedMime.toString()));

	}

	@Test
	public void given_OneOntologyThatAllowsEncryptedDataAndOneEncryptedEntity_When_DecryptionOfDataIsRequested_Then_TheClearEntityIsReturned()
			throws IOException {
		final String ontologyName = "one";
		final Ontology ontology = new Ontology();
		ontology.setId("1");
		ontology.setIdentification(ontologyName);
		ontology.setJsonSchema(TestResources.SMALL_SCHEMA_WITH_ENCRYPTION);
		ontology.setAllowsCypherFields(true);

		String encryptedData = service.encryptionOperation(TestResources.DATA_FOR_GOOD_JSON, ontology,
				EncryptionOperations.encrypt);

		JsonNode jsonData = objectMapper.readTree(TestResources.DATA_FOR_GOOD_JSON);
		JsonNode id = jsonData.findPath("id");
		JsonNode jsonEncryptedData = objectMapper.readTree(encryptedData);
		JsonNode encryptedId = jsonEncryptedData.findPath("id");

		assertFalse("Data should be encrypted", id.toString().equals(encryptedId.toString()));

		String clearData = service.encryptionOperation(encryptedData, ontology, EncryptionOperations.decrypt);

		assertTrue("Data should be equal after encrytion and decryption process",
				clearData.equals(jsonData.toString()));

	}
}
