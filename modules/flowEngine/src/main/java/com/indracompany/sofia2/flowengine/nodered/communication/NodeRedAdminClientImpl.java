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
package com.indracompany.sofia2.flowengine.nodered.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.flowengine.nodered.communication.dto.FlowEngineAdminResponse;
import com.indracompany.sofia2.flowengine.nodered.communication.dto.FlowEngineDomainStatus;
import com.indracompany.sofia2.flowengine.nodered.communication.dto.SynchronizeDomainStatusRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NodeRedAdminClientImpl implements NodeRedAdminClient {

	@Value("${sofia2.flowengine.admin.url}")
	private String flowengineUrl;

	// Services
	@Value("${sofia2.flowengine.services.request.timeout.ms:5000}")
	private int restRequestTimeout;
	@Value("${sofia2.flowengine.services.stop.admin}")
	private String stopflowEngine;
	@Value("${sofia2.flowengine.services.domain.status}")
	private String flowEngineDomainStatus;
	@Value("${sofia2.flowengine.services.domain.getall}")
	private String flowEngineDomainGetAll;
	@Value("${sofia2.flowengine.services.domain.get}")
	private String flowEngineDomainGet;
	@Value("${sofia2.flowengine.services.domain.create}")
	private String flowEngineDomainCreate;
	@Value("${sofia2.flowengine.services.domain.delete}")
	private String flowEngineDomainDelete;
	@Value("${sofia2.flowengine.services.domain.start}")
	private String flowEngineDomainStart;
	@Value("${sofia2.flowengine.services.domain.stop}")
	private String flowEngineDomainStop;
	@Value("${sofia2.flowengine.services.sync}")
	private String syncFlowEngineDomains;

	private HttpComponentsClientHttpRequestFactory httpRequestFactory;

	private ObjectMapper mapper;

	private boolean isSynchronizedWithBDC;

	@PostConstruct
	public void init() {
		httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectTimeout(restRequestTimeout);

		this.mapper = new ObjectMapper();

		// TODO Set as unsynchronized until a call to synchronization is made.
		// The rest of the functions must be "disabled" till then
		// resetSynchronizedWithBDC();
		this.isSynchronizedWithBDC = true;// DELETEME
	}

	@Override
	public FlowEngineAdminResponse stopFlowEngine() {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(flowengineUrl + stopflowEngine,
					HttpMethod.POST, null, String.class);
			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Not able to stop the flow engine. Cause={}, message={}", e.getCause(), e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error stopping flow engine.").build();
		}
		return response;
	}

	@Override
	public void resetSynchronizedWithBDC() {
		this.isSynchronizedWithBDC = false;
	}

	@Override
	public FlowEngineAdminResponse stopFlowEngineDomain(String domain) {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			ResponseEntity<String> responseEntity = restTemplate
					.exchange(flowengineUrl + flowEngineDomainStop + "/" + domain, HttpMethod.PUT, null, String.class);
			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Not able to stop the flow engine Domain={}. Cause={}, message={}", domain, e.getCause(),
					e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error stopping domain " + domain + ".").build();
		}
		return response;
	}

	@Override
	public FlowEngineAdminResponse startFlowEngineDomain(String domain, int port, String home, int servicePort) {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		StringBuilder data = new StringBuilder();
		data.append("{").append("\"domain\": ").append("\"").append(domain).append("\",").append("\"port\": ")
				.append("\"").append(port).append("\",").append("\"home\": ").append("\"").append(home).append("\",")
				.append("\"servicePort\": ").append("\"").append(servicePort).append("\"").append("}");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> domainToStart = new HttpEntity<String>(data.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(flowengineUrl + flowEngineDomainStart,
					HttpMethod.POST, domainToStart, String.class);
			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Unable to create Domain={}. Cause={}, message={}", domain, e.getCause(), e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error Starting domain " + domain + ".").build();
		}
		return response;
	}

	@Override
	public FlowEngineAdminResponse createFlowengineDomain(String domain, int port, String home, int servicePort) {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		StringBuilder data = new StringBuilder();
		data.append("{").append("\"domain\": ").append("\"").append(domain).append("\",").append("\"port\": ")
				.append("\"").append(port).append("\",").append("\"home\": ").append("\"").append(home).append("\",")
				.append("\"servicePort\": ").append("\"").append(servicePort).append("\"").append("}");

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> newDomain = new HttpEntity<String>(data.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(flowengineUrl + flowEngineDomainCreate,
					HttpMethod.POST, newDomain, String.class);
			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Unable to create Domain={}. Cause={}, message={}", domain, e.getCause(), e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error creating domain " + domain + ".").build();
		}
		return response;
	}

	@Override
	public FlowEngineAdminResponse deleteFlowEngineDomain(String domainId) {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					flowengineUrl + flowEngineDomainDelete + "/" + domainId, HttpMethod.DELETE, null, String.class);

			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Unable to Delete Domain={}. Cause={}, message={}", domainId, e.getCause(), e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error deleting domain " + domainId + ".").build();
		}
		return null;
	}

	@Override
	public FlowEngineAdminResponse getFlowEngineDomain(String domainId) {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(flowengineUrl + flowEngineDomainGet)
					.queryParam("domain", domainId);

			ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(),
					HttpMethod.GET, null, String.class);

			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Unable to retrieve Domain={}. Cause={}, message={}", domainId, e.getCause(), e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error retrieving domain " + domainId + ".").build();
		}
		return response;
	}

	@Override
	public FlowEngineAdminResponse getAllFlowEnginesDomains() {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(flowengineUrl + flowEngineDomainGetAll,
					HttpMethod.GET, null, String.class);

			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Not able to retrieve all flow engine domains. Cause={}, message={}", e.getCause(),
					e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error getting All domains status list.").build();
		}

		return response;
	}

	@Override
	public FlowEngineAdminResponse getFlowEngineDomainStatus(List<String> domainList) {

		FlowEngineAdminResponse response = notSynchronicedResponse();
		if (!this.isSynchronizedWithBDC)
			return response;

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(flowengineUrl + flowEngineDomainStatus)
					.queryParam("domains", mapper.writeValueAsString(domainList));
			Map<String, String> uriVars = new HashMap<>();
			uriVars.put("domains", mapper.writeValueAsString(domainList));

			ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(),
					HttpMethod.GET, null, String.class);
			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
		} catch (Exception e) {
			log.warn("Not able to parse Node Red Admin response. Cause = {}, message = {}", e.getCause(),
					e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error getting domain status list.").build();
		}
		return response;

	}

	@Override
	public FlowEngineAdminResponse synchronizeMF(List<FlowEngineDomainStatus> domainList) {
		// TODO Auto-generated method stub
		FlowEngineAdminResponse response = null;
		SynchronizeDomainStatusRequest synchronizeDomainStatusRequest = new SynchronizeDomainStatusRequest();
		synchronizeDomainStatusRequest.setListDomain(domainList);
		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> domainsToSync = new HttpEntity<String>(synchronizeDomainStatusRequest.toJson(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(flowengineUrl + syncFlowEngineDomains,
					HttpMethod.POST, domainsToSync, String.class);
			response = FlowEngineAdminResponse.builder().ok(true).error("").body(responseEntity.getBody())
					.returnCode(responseEntity.getStatusCode().value()).build();
			this.isSynchronizedWithBDC = true;
		} catch (Exception e) {
			log.warn("Unable to synchronize domains with CDB={}. Cause={}, message={}", e.getCause(), e.getMessage());
			response = FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
					.error("Error synchronizing CDB domains list to flow engine.").build();
		}

		return response;
	}

	private FlowEngineAdminResponse notSynchronicedResponse() {
		return FlowEngineAdminResponse.builder().ok(false).body("").returnCode(500)
				.error("NodeRed Admin Client not synchronized with CDB.").build();
	}
}
