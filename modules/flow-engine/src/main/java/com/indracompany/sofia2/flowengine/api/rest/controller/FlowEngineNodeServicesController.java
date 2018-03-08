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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.flowengine.api.rest.pojo.FlowEngineInsertRequest;
import com.indracompany.sofia2.flowengine.api.rest.pojo.FlowEngineQueryRequest;
import com.indracompany.sofia2.flowengine.api.rest.pojo.UserDomainValidationRequest;
import com.indracompany.sofia2.flowengine.api.rest.service.FlowEngineNodeService;
import com.indracompany.sofia2.flowengine.exception.NotAllowedException;
import com.indracompany.sofia2.flowengine.exception.NotAuthorizedException;
import com.indracompany.sofia2.flowengine.exception.ResourceNotFoundException;

import javassist.NotFoundException;

@RestController
@RequestMapping(value = "/flowengine/node/services")
public class FlowEngineNodeServicesController {

	@Autowired
	private FlowEngineNodeService flowEngineNodeService;
	ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/deployment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String deploymentNotification(@RequestBody String json) {
		return flowEngineNodeService.deploymentNotification(json);

	}

	@RequestMapping(value = "/user/ontologies", method = RequestMethod.GET, produces = { "application/javascript",
			"application/json" })
	public @ResponseBody String getOntologiesByUser(@RequestParam String authentication,
			@RequestParam("callback") String callbackName)
			throws ResourceNotFoundException, NotAuthorizedException, JsonProcessingException {
		String response = mapper.writeValueAsString(flowEngineNodeService.getOntologyByUser(authentication));
		return callbackName + "(" + response + ")";
	}

	@RequestMapping(value = "/user/client_platforms", method = RequestMethod.GET, produces = { "application/javascript",
			"application/json" })
	public @ResponseBody String getClientPlatformsByUser(@RequestParam String authentication)
			throws ResourceNotFoundException, NotAuthorizedException, JsonProcessingException {
		String response = mapper.writeValueAsString(flowEngineNodeService.getClientPlatformByUser(authentication));
		return "kpUser(" + response + ")";
	}

	@RequestMapping(value = "/user/validate", method = RequestMethod.POST, produces = { "application/javascript",
			"application/json" })
	public @ResponseBody String getClientPlatformsByUser(@RequestBody UserDomainValidationRequest request)
			throws ResourceNotFoundException, NotAuthorizedException, NotAllowedException {
		return flowEngineNodeService.validateUserDomain(request);
	}

	@RequestMapping(value = "/user/all_data", method = RequestMethod.GET, produces = { "application/javascript" })
	public @ResponseBody String getOntologiesAndClientPlatformsByUser(@RequestParam String authentication)
			throws ResourceNotFoundException, NotAuthorizedException, NotAllowedException, JsonProcessingException {
		String ontologies = mapper.writeValueAsString(flowEngineNodeService.getOntologyByUser(authentication));
		String clientPlatforms = mapper
				.writeValueAsString(flowEngineNodeService.getClientPlatformByUser(authentication));
		StringBuilder response = new StringBuilder();
		response.append("dataAllUser([").append(ontologies.substring(1, ontologies.length() - 1)).append(",\"##$$##\",")
				.append(clientPlatforms.substring(1, clientPlatforms.length() - 1)).append("])");

		return response.toString();
	}

	@RequestMapping(value = "/user/query", method = RequestMethod.POST, produces = { "application/javascript",
			"application/json" })
	public @ResponseBody String submitQuery(@RequestBody FlowEngineQueryRequest queryRequest)
			throws ResourceNotFoundException, NotAuthorizedException, JsonProcessingException, NotFoundException {
		return flowEngineNodeService.submitQuery(queryRequest.getOntology(), queryRequest.getQueryType(),
				queryRequest.getQuery(), queryRequest.getAuthentication());
	}

	@RequestMapping(value = "/user/insert", method = RequestMethod.POST, produces = { "application/javascript",
			"application/json" })
	public @ResponseBody String submitInsert(@RequestBody FlowEngineInsertRequest insertRequest)
			throws ResourceNotFoundException, NotAuthorizedException, JsonProcessingException, NotFoundException {
		return flowEngineNodeService.submitInsert(insertRequest.getOntology(), insertRequest.getData(),
				insertRequest.getAuthentication());
	}

}
