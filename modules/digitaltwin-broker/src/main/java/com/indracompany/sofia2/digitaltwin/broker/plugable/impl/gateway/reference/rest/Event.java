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
package com.indracompany.sofia2.digitaltwin.broker.plugable.impl.gateway.reference.rest;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.repository.DigitalTwinDeviceRepository;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinCompositeModel;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel.EventType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterDigitalTwinService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@ConditionalOnProperty(
		prefix="sofia2.digitaltwin.rest",
		name="enable",
		havingValue="true"
		)
@RestController
@RequestMapping(path="/event")
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
@Api(value="event", description="Sofia4Cities events for digital twins")
public class Event {
	
	@Autowired
	private DigitalTwinDeviceRepository deviceRepo;
	
	@Autowired
	@Qualifier("routerDigitalTwinServiceImpl")
	private RouterDigitalTwinService routerDigitalTwinService;
	
	@ApiOperation(value = "Event Register to register the endpoint of the Digital Twin")
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public ResponseEntity<?> register(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) @RequestHeader(value="Authorization") String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  @RequestBody JsonNode data){
		
		//Validation apikey
		if(data.get("id")==null || data.get("endpoint")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		DigitalTwinDevice device = deviceRepo.findById(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//Set endpoint
			device.setUrl(data.get("endpoint").asText());
			deviceRepo.save(device);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Event Ping")
	@RequestMapping(value="/ping", method=RequestMethod.POST)
	public ResponseEntity<?> ping(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) @RequestHeader(value="Authorization") String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  @RequestBody JsonNode data){
		
		//Validation apikey
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		DigitalTwinDevice device = deviceRepo.findById(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//Set last updated
			device.setUpdatedAt(new Date());
			deviceRepo.save(device);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Event Log")
	@RequestMapping(value="/log", method=RequestMethod.POST)
	public ResponseEntity<?> log(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) @RequestHeader(value="Authorization") String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  @RequestBody JsonNode data){
		
		if(data.get("id")==null || data.get("log")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		DigitalTwinModel model = new DigitalTwinModel();
		DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
		
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findById(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//Set last updated
			device.setUpdatedAt(new Date());
			deviceRepo.save(device);
			//insert trace of log
			model.setEvent(EventType.LOG);
			model.setLog(data.get("log").asText());
			model.setId(data.get("id").asText());
			model.setType(data.get("type").asText());
			
			compositeModel.setDigitalTwinModel(model);
			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
			
			OperationResultModel result = routerDigitalTwinService.insertLog(compositeModel);
			if(!result.isStatus()) {
				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
			}
			return new ResponseEntity<>(result.getResult(), HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@ApiOperation(value = "Event Shadow")
	@RequestMapping(value="/shadow", method=RequestMethod.POST)
	public ResponseEntity<?> shadow(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) @RequestHeader(value="Authorization") String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  @RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findById(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			
			//update device with shadow info
			JsonNode status = data.get("status");
			//TODO update
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@ApiOperation(value = "Event Notebook")
	@RequestMapping(value="/notebook", method=RequestMethod.POST)
	public ResponseEntity<?> notebook(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) @RequestHeader(value="Authorization") String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  @RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findById(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//TODO
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@ApiOperation(value = "Event Flow")
	@RequestMapping(value="/flow", method=RequestMethod.POST)
	public ResponseEntity<?> flow(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) @RequestHeader(value="Authorization") String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  @RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findById(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//TODO
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@ApiOperation(value = "Event Rule")
	@RequestMapping(value="/rule", method=RequestMethod.POST)
	public ResponseEntity<?> rule(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) @RequestHeader(value="Authorization") String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  @RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findById(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//TODO
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
}
