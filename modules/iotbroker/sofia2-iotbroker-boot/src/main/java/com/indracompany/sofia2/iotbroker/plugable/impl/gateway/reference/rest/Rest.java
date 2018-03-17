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
package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.rest;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLeaveMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyQueryMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.enums.SSAPQueryResultFormat;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@ConditionalOnProperty(
		prefix="sofia2.iotbroker.plugable.gateway.rest",
		name="enable",
		havingValue="true"
		)
@RestController
@RequestMapping(path="/rest"
//produces= MediaType.APPLICATION_JSON_UTF8_VALUE,
//consumes=MediaType.APPLICATION_JSON_UTF8_VALUE
		)
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
@Api(value="rest", description="Sofia4Cities operations for devices")
public class Rest {

	@Autowired
	MessageProcessor processor;

	@Autowired
	GatewayNotifier subscriptor;

	@PostConstruct
	private void init() {
		subscriptor.addSubscriptionListener("rest_gateway",  (s) -> System.out.println("rest_gateway fake processing") );
	}

	@ApiOperation(value = "Logs a client device into Sofia4Cities with token.\nReturns a sessionKey to use in further operations")
	@RequestMapping(value="/client/join", method=RequestMethod.GET)
	public ResponseEntity<?> join(
			@ApiParam(value = "Token asociated to client platform", required = true) @RequestParam(name="token") String token,
			@ApiParam(value = "Client Platform asociated to token", required = true) @RequestParam(name="clientPlatform") String clientPlatform,
			@ApiParam(value = "Desired ClientPlatform id. the value is chosen from user", required = true) @RequestParam(name="clientPlatformId") String clientPlatformId) {

		final SSAPMessage<SSAPBodyJoinMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyJoinMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.JOIN);
		//		request.setSessionKey();
		request.getBody().setToken(token);
		request.getBody().setClientPlatform(clientPlatform);
		request.getBody().setClientPlatformInstance(clientPlatformId);


		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}

	@ApiOperation(value = "Logs out a client device into Sofia4Cities with token")
	@RequestMapping(value="/client/leave", method=RequestMethod.GET)
	public ResponseEntity<?> leave(
			@ApiParam(value = "SessionKey provided from join operation", required = true) @RequestHeader(value="Authorization") String sessionKey) {

		final SSAPMessage<SSAPBodyLeaveMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyLeaveMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.LEAVE);
		request.setSessionKey(sessionKey);

		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}

	@ApiOperation(value = "Get a list of instances of a ontology data")
	@RequestMapping(value="/ontology/{ontology}", method=RequestMethod.GET)
	public ResponseEntity<?> list(
			@ApiParam(value = "SessionKey provided from join operation", required = true) @RequestHeader(value="Authorization") String sessionKey,
			@ApiParam(value = "Ontology to perform operation. Client platform must have granted permissions ", required = true) @PathVariable("ontology") @RequestParam(name="ontology") String ontology,
			@ApiParam(value = "Examples:\n\tNATIVE: db.temperature.find({})\n\tSQL: select * from temperature; ", required = true) @RequestParam(name="query") String query,
			@ApiParam(value = "OPTIONS: NATIVE or SQL", required=true) @RequestParam(name="queryType") SSAPQueryType queryType) {

		final SSAPMessage<SSAPBodyQueryMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyQueryMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.QUERY);
		request.setSessionKey(sessionKey);
		request.getBody().setCacheTime(0);
		request.getBody().setOntology(ontology);
		request.getBody().setQuery(query);
		request.getBody().setQueryType(queryType);
		request.getBody().setResultFormat(SSAPQueryResultFormat.JSON);

		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}

	@ApiOperation(value = "Inserts a instance of a ontology expresed in json format")
	@RequestMapping(value="/ontology/{ontology}", method=RequestMethod.POST)
	public ResponseEntity<?> create(
			@ApiParam(value = "SessionKey provided from join operation", required = true) @RequestHeader(value="Authorization") String sessionKey,
			@ApiParam(value = "Ontology to perform operation. Client platform must have granted permissions ", required = true)  @PathVariable("ontology")  String ontology,
			@ApiParam(value = "Json data representing ontology instance", required = true) @RequestBody JsonNode data) {

		final SSAPMessage<SSAPBodyInsertMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyInsertMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.INSERT);
		request.setSessionKey(sessionKey);
		request.getBody().setOntology(ontology);
		request.getBody().setData(data);

		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}

	@ApiOperation(value = "Updates a instance of a ontology expresed in json format")
	@RequestMapping(value="/ontology/{ontology}/{id}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateById(
			@ApiParam(value = "SessionKey provided from join operation", required = true) @RequestHeader(value="Authorization") String sessionKey,
			@ApiParam(value = "Ontology to perform operation. Client platform must have granted permissions ", required = true)  @PathVariable("ontology") String ontology,
			@ApiParam(value = "Ontology identification to perform operation", required=true) @PathVariable("id") String id,
			@ApiParam(value = "Json data representing ontology instance", required = true)  @RequestBody JsonNode data) {

		final SSAPMessage<SSAPBodyUpdateByIdMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyUpdateByIdMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.UPDATE_BY_ID);
		request.setSessionKey(sessionKey);
		request.getBody().setId(id);
		request.getBody().setOntology(ontology);
		request.getBody().setData(data);

		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}

	@ApiOperation(value = "Updates a instance or instances of a ontology with a mongoDB update query")
	@RequestMapping(value="/ontology/{ontology}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateByQuery(
			@ApiParam(value = "SessionKey provided from join operation", required = true) @RequestHeader(value="Authorization") String sessionKey,
			@ApiParam(value = "Ontology to perform operation. Client platform must have granted permissions ", required = true)  @PathVariable("ontology") String ontology,
			@ApiParam(value = "Examples: NATIVE: db.temperature.update({\"location\":\"Helsinki\"}, { $set:{\"value\":15}})", required = true) @RequestParam(name="query") String query) {

		final SSAPMessage<SSAPBodyUpdateMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyUpdateMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.UPDATE);
		request.setSessionKey(sessionKey);
		request.getBody().setOntology(ontology);
		request.getBody().setQuery(query);

		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}

	@ApiOperation(value = "Delete a instance or instances of a ontology with a mongoDB remove query")
	@RequestMapping(value="/ontology/{ontology}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteByQuery(
			@ApiParam(value = "SessionKey provided from join operation", required = true) @RequestHeader(value="Authorization") String sessionKey,
			@ApiParam(value = "Ontology to perform operation. Client platform must have granted permissions ", required = true)  @PathVariable("ontology") String ontology,
			@ApiParam(value = "Examples: NATIVE: db.temperature.update({\"value\":15})", required=true) @RequestParam(name="query") String query) {

		final SSAPMessage<SSAPBodyDeleteMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyDeleteMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.DELETE);
		request.setSessionKey(sessionKey);
		request.getBody().setOntology(ontology);
		request.getBody().setQuery(query);

		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}

	@ApiOperation(value = "Delete a instance of a ontology")
	@RequestMapping(value="/ontology/{ontology}/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteById(
			@ApiParam(value = "SessionKey provided from join operation", required = true) @RequestHeader(value="Authorization") String sessionKey,
			@ApiParam(value = "Ontology to perform operation. Client platform must have granted permissions ", required = true)  @PathVariable("ontology") String ontology,
			@ApiParam(value = "Ontology identification to perform operation", required=true) @PathVariable("id") String id) {

		final SSAPMessage<SSAPBodyDeleteByIdMessage> request = new SSAPMessage<>();
		request.setBody(new SSAPBodyDeleteByIdMessage());

		request.setDirection(SSAPMessageDirection.REQUEST);
		request.setMessageType(SSAPMessageTypes.DELETE_BY_ID);
		request.setSessionKey(sessionKey);
		request.getBody().setId(id);
		request.getBody().setOntology(ontology);

		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request, getGatewayInfo());
		if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
			return new ResponseEntity<>(response.getBody().getData(), HttpStatus.OK);
		}
		else {
			return formResponseError(response);
		}
	}



	private ResponseEntity<?> formResponseError(SSAPMessage<SSAPBodyReturnMessage> response) {
		final SSAPErrorCode code = response.getBody().getErrorCode();
		HttpStatus status;

		switch (code) {
		case AUTENTICATION:
			status = HttpStatus.FORBIDDEN;
			break;

		case AUTHORIZATION:
			status = HttpStatus.UNAUTHORIZED;
			break;

		case PROCESSOR:
			status = HttpStatus.BAD_REQUEST;
			break;

		default:
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			break;
		}

		return new ResponseEntity<>(response.getBody().getError(), status);
	}

	private GatewayInfo getGatewayInfo() {
		final GatewayInfo info = new GatewayInfo();
		info.setName("rest_gateway");
		info.setProtocol("REST");

		return info;
	}


}
