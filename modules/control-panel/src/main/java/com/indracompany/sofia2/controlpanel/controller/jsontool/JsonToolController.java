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
package com.indracompany.sofia2.controlpanel.controller.jsontool;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.datamodel.DataModelService;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.Source;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/jsontool")
@Slf4j
public class JsonToolController {

	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private DataModelService dataModelService;
	@Autowired
	private AppWebUtils utils;
	@Autowired
	private UserService userService;
	@Autowired
	private RouterService routerService;

	private ObjectMapper mapper = new ObjectMapper();

	private static final String DATAMODEL_DEFAULT_NAME = "EmptyBase";
	private static final String SCHEMA_DRAFT_VERSION = "http://json-schema.org/draft-04/schema#";
	private static final String PATH_PROPERTIES = "properties";

	@GetMapping("tools")
	public String show(Model model) {
		model.addAttribute("ontologies", this.ontologyService.getOntologiesByUserId(this.utils.getUserId()));
		return "json2ontologytool/import";
	}

	@PostMapping("createontology")
	public @ResponseBody String createOntology(Model model, @RequestParam String ontologyIdentification,
			@RequestParam String ontologyDescription, @RequestParam String schema) throws IOException {
		Ontology ontology = new Ontology();
		ontology.setJsonSchema(this.completeSchema(schema, ontologyIdentification, ontologyDescription).toString());
		ontology.setIdentification(ontologyIdentification);
		ontology.setActive(true);
		ontology.setDataModel(this.dataModelService.getDataModelByName(DATAMODEL_DEFAULT_NAME));
		ontology.setDescription(ontologyDescription);
		ontology.setUser(this.userService.getUser(this.utils.getUserId()));
		try {
			this.ontologyService.createOntology(ontology);
		} catch (OntologyServiceException e) {
			return "ko";
		}

		return "ok";
	}

	@PostMapping("importbulkdata")
	public @ResponseBody String importBulkData(Model model, @RequestParam String data,
			@RequestParam String ontologyIdentification) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(data);
			final OperationModel operation;

			operation = new OperationModel.Builder(ontologyIdentification, OperationType.INSERT, this.utils.getUserId(),
					Source.INTERNAL_ROUTER).body(node.toString()).queryType(QueryType.NATIVE).build();
			final NotificationModel modelNotification = new NotificationModel();
			modelNotification.setOperationModel(operation);

			try {
				final OperationResultModel response = routerService.insert(modelNotification);
				if (response.getMessage().equals("OK"))
					return response.getResult();
			} catch (Exception e) {
				return "Could not insert data";
			}

		} catch (IOException e) {
			return "Not valid JSON";
		}

		return "Error";
	}

	@PostMapping("/getParentNodeOfSchema")
	public @ResponseBody String parentNode(@RequestParam String id) throws IOException {
		final Ontology ontology = this.ontologyService.getOntologyByIdentification(id, this.utils.getUserId());
		if (ontology != null) {
			final String jsonSchema = ontology.getJsonSchema();

			final JsonNode schema = this.mapper.readTree(jsonSchema);
			if (schema.path(PATH_PROPERTIES).size() == 1) {
				return schema.path(PATH_PROPERTIES).fieldNames().next();
			}
		}
		return "";
	}

	public JsonNode completeSchema(String schema, String identification, String description) throws IOException {
		JsonNode schemaSubTree = mapper.readTree(schema);
		((ObjectNode) schemaSubTree).put("type", "object");
		((ObjectNode) schemaSubTree).put("description", "Info " + identification);

		((ObjectNode) schemaSubTree).put("$schema", SCHEMA_DRAFT_VERSION);
		((ObjectNode) schemaSubTree).put("title", identification);

		((ObjectNode) schemaSubTree).put("additionalProperties", true);
		return schemaSubTree;
	}
}
