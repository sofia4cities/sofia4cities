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
package com.indracompany.sofia2.flowengine.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.flowengine.api.rest.pojo.FlowEngineDomain;
import com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClient;
import com.indracompany.sofia2.flowengine.nodered.communication.dto.FlowEngineAdminResponse;
import com.indracompany.sofia2.flowengine.nodered.communication.dto.FlowEngineDomainStatus;

@RestController
@RequestMapping(value = "/flowengine/admin")
public class FlowEngineController {

	@Autowired
	private NodeRedAdminClient nodeRedClientAdmin;

	@RequestMapping(value = "/stop", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String stopFlowEngine() {
		return nodeRedClientAdmin.stopFlowEngine();
	}

	@RequestMapping(value = "/domain/stop", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody void stopFlowEngineDomain(@RequestParam String domain) {
		nodeRedClientAdmin.stopFlowEngineDomain(domain);
	}

	@RequestMapping(value = "/domain/start", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String startFlowEngineDomain(@RequestBody FlowEngineDomain domain) {
		return nodeRedClientAdmin.startFlowEngineDomain(domain);
	}

	@RequestMapping(value = "/domain", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String createFlowengineDomain(@RequestBody FlowEngineDomain domain) {
		return nodeRedClientAdmin.createFlowengineDomain(domain);
	}

	@RequestMapping(value = "/domain", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody void deleteFlowEngineDomain(@RequestParam String domainId) {
		nodeRedClientAdmin.deleteFlowEngineDomain(domainId);
	}

	@RequestMapping(value = "/domain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody FlowEngineDomain getFlowEngineDomain(@RequestParam String domainId) {
		return nodeRedClientAdmin.getFlowEngineDomain(domainId);
	}

	// Generic Flow Engine Requests
	@RequestMapping(value = "/domain/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<FlowEngineDomainStatus> getAllFlowEnginesDomains() {
		return nodeRedClientAdmin.getAllFlowEnginesDomains();
	}

	@RequestMapping(value = "/domain/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<FlowEngineDomainStatus> getFlowEngineDomainStatus(@RequestParam List<String> domainList) {
		return nodeRedClientAdmin.getFlowEngineDomainStatus(domainList);
	}

	// Synchronization of the active/inactive domains with CDB
	@RequestMapping(value = "/sync/reset", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody FlowEngineAdminResponse resetSynchronizedWithBDC() {
		nodeRedClientAdmin.resetSynchronizedWithBDC();
		return FlowEngineAdminResponse.builder().body("OK").returnCode(200).error("").ok(true).build();
	}

	@RequestMapping(value = "/sync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String synchronizeMF(@RequestBody List<FlowEngineDomainStatus> domainList) {
		return nodeRedClientAdmin.synchronizeMF(domainList);
	}
}
