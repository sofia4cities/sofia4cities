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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
import com.indracompany.sofia2.resources.service.IntegrationResourcesService;
import com.indracompany.sofia2.resources.service.IntegrationResourcesServiceImpl.Module;
import com.indracompany.sofia2.resources.service.IntegrationResourcesServiceImpl.ServiceUrl;

import lombok.extern.slf4j.Slf4j;

@Service
@EnableScheduling
@Slf4j
public class PersistenceServiceImpl implements PersistenceService {

	@Autowired
	private ClientPlatformService clientPlatformService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private IntegrationResourcesService intregationResourcesService;
	private static final String UNAUTHORIZED_ONTOLOGY = "Unauthorized ontology";
	private Map<String, String> sessionKeys;
	private List<String> deviceBlackList;

	private String iotbrokerUrl;

	@PostConstruct
	public void setUp() {
		this.sessionKeys = new HashMap<String, String>();
		this.iotbrokerUrl = this.intregationResourcesService.getUrl(Module.iotbroker, ServiceUrl.base);
		this.deviceBlackList = new ArrayList<String>();
	}

	public void connectDeviceRest(String clientPlatform, String clientPlatformInstance) {
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
		} catch (Exception e) {
			// log.error("IoT broker down");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {

			}

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
				this.connectDeviceRest(clientPlatform, clientPlatformInstance);
				this.insertOntologyInstance(instance, ontology, user, clientPlatform, clientPlatformInstance);
			}
			// Remove from black list, as it is still sending data
			if (this.deviceBlackList.contains(clientPlatform))
				this.deviceBlackList.remove(clientPlatform);
			log.debug("Device " + clientPlatformInstance);
		} else {
			this.connectDeviceRest(clientPlatform, clientPlatformInstance);
			this.insertOntologyInstance(instance, ontology, user, clientPlatform, clientPlatformInstance);
		}
	}

	@Override
	public void disconnectDeviceRest(String identification) {

		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", this.sessionKeys.get(identification));
		headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.exchange(iotbrokerUrl + "/rest/client/leave", HttpMethod.GET, new HttpEntity<String>(headers),
				String.class);
		this.sessionKeys.remove(identification);
		log.info("Closed session for device " + identification);
	}

	@Scheduled(fixedDelay = 60000)
	private void disconnectBlackListedDevices() {
		for (Iterator<String> iterator = this.deviceBlackList.iterator(); iterator.hasNext();) {
			String device = iterator.next();
			this.disconnectDeviceRest(device);
			iterator.remove();
		}
		for (Map.Entry<String, String> entry : this.sessionKeys.entrySet()) {
			this.deviceBlackList.add(entry.getKey());
		}
	}

}