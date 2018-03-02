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
package com.indracompany.sofia2.simulator.service;

import java.io.IOException;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SibServiceImpl implements SibService {

	@Autowired
	private BasicOpsDBRepository repository;
	@Autowired
	private MongoTemplate springDataMongoTemplate;
	
	@Override
	public void inserOntologyInstanceToMongo(String instance, String user, String clientPlatform, String clientPlatformInstance, String ontology) throws JsonProcessingException, IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(instance);
		
		
		
		
		if (!springDataMongoTemplate.collectionExists(ontology)) {
			springDataMongoTemplate.createCollection(ontology);
			log.debug("Ontology collection created");
		}

	
		

		final ContextData contextData = new ContextData();

		contextData.setClientConnection("");
		contextData.setClientPatform(clientPlatform);
		contextData.setClientPatformInstance(clientPlatformInstance);
		contextData.setTimezoneId(ZoneId.systemDefault().toString());
		contextData.setUser(user);

		((ObjectNode) rootNode).set("contextData", objectMapper.valueToTree(contextData));

		repository.insert(ontology,
				rootNode.toString());

	}

}
