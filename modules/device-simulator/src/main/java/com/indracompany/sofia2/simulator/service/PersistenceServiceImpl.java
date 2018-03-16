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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.token.TokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersistenceServiceImpl implements PersistenceService {

	@Autowired
	private ClientPlatformService clientPlatformService;
	@Autowired
	private TokenService tokenService;

	private static final String UNAUTHORIZED_ONTOLOGY = "Unauthorized ontology";
	private Map<String, String> sessionKeys;
	@Value("${sofia2.iotbroker.server}")
	private String iotbrokerUrl;

	@PostConstruct
	public void setUp() {
		this.sessionKeys = new HashMap<String, String>();
	}

	public void connectIotBrokerRest(String clientPlatform, String clientPlatformInstance) {
		final Token token = this.tokenService.getToken(this.clientPlatformService.getByIdentification(clientPlatform));
		final RestTemplate restTemplate = new RestTemplate();
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(iotbrokerUrl + "/rest/client/join")
				.queryParam("token", token.getToken()).queryParam("clientPlatform", clientPlatform)
				.queryParam("clientPlatformId", clientPlatformInstance);
		try {
		final JsonNode response = restTemplate.getForObject(builder.build().encode().toUri(), JsonNode.class);
		String sessionKey = response.get("sessionKey").asText();
		log.info("Session Key :" + sessionKey);
		if (sessionKey != null)
			this.sessionKeys.put(clientPlatform, sessionKey);
		}catch(Exception e)
		{
			log.error("IoT broker down");
		}
		

	}

	@Override
	public void insertOntologyInstance(String instance, String ontology, String user, String clientPlatform,
			String clientPlatformInstance) throws Exception {

		if (this.sessionKeys.get(clientPlatform) != null) {

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.add("Authorization", this.sessionKeys.get(clientPlatform));
			headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode ontologyData = mapper.readTree(instance);

			final RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			HttpEntity<JsonNode> request = new HttpEntity<JsonNode>(ontologyData, headers);

			JsonNode response = restTemplate.postForObject(this.iotbrokerUrl + "/rest/ontology/" + ontology, request,
					JsonNode.class);
			log.debug("Response from Rest Service: " + response.asText());

			if (response.path("id").isMissingNode()
					&& response.asText().equals(PersistenceServiceImpl.UNAUTHORIZED_ONTOLOGY)) {
				log.debug("Attemping to renew session key");
				this.connectIotBrokerRest(clientPlatform, clientPlatformInstance);
				this.insertOntologyInstance(instance, ontology, user, clientPlatform, clientPlatformInstance);
			}

			log.debug("Device " + clientPlatformInstance);
		} else {
			this.connectIotBrokerRest(clientPlatform, clientPlatformInstance);
			this.insertOntologyInstance(instance, ontology, user, clientPlatform, clientPlatformInstance);
		}
	}

}
