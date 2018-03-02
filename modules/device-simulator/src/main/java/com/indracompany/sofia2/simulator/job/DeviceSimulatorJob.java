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
import java.util.Map;

import org.assertj.core.data.MapEntry;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.simulator.service.FieldRandomizerService;
import com.indracompany.sofia2.simulator.service.SibService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeviceSimulatorJob {

	@Autowired
	OntologyService ontologyService;
	@Autowired
	private FieldRandomizerService fieldRandomizerService;
	@Autowired
	private SibService sibService;

	public void execute(JobExecutionContext context) throws JsonProcessingException, IOException {

		String user = context.getJobDetail().getJobDataMap().getString("userId");
		String json = context.getJobDetail().getJobDataMap().getString("json");
		String id = context.getJobDetail().getJobDataMap().getString("id");
		this.generateInstance(user, json);

	}

	public void generateInstance(String user, String json) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsonInstance = mapper.readTree(json);

		JsonNode fieldAndValues = this.fieldRandomizerService.randomizeFields(jsonInstance.path("fields"));
		JsonNode instance = mapper.createObjectNode();
		
		String clientPlatform = jsonInstance.get("clientPlatform").asText();
		String clientPlatformInstance = jsonInstance.get("clientPlatformInstance").asText();
		String ontology = jsonInstance.get("ontology").asText();
		
		String context = mapper.readTree(this.ontologyService.getOntologyByIdentification(ontology).getJsonSchema()).get("properties").fields().next().getKey();

		((ObjectNode) instance).set(context, fieldAndValues);
		this.sibService.inserOntologyInstanceToMongo(instance.toString(), user, clientPlatform, clientPlatformInstance,
				ontology);
	}

}
