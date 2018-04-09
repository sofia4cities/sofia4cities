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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.repository.DigitalTwinDeviceRepository;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinCompositeModel;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel.EventType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterDigitalTwinService;


@RestController
@ConditionalOnProperty(
		prefix="sofia2.digitaltwin.broker.rest",
		name="enable",
		havingValue="true"
		)
@EnableAutoConfiguration
public class EventGatewayImpl implements EventGateway {
	
	@Autowired
	private DigitalTwinDeviceRepository deviceRepo;
	
	@Autowired
	@Qualifier("routerDigitalTwinServiceImpl")
	private RouterDigitalTwinService routerDigitalTwinService;
	
	@Override
	public ResponseEntity<?> register(
			@RequestHeader(value="Authorization") String apiKey,
			@RequestBody JsonNode data){
		
		//Validation apikey
		if(data.get("id")==null || data.get("endpoint")==null) {
			return new ResponseEntity<>("id and endpoint are required", HttpStatus.BAD_REQUEST);
		}
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
	
		if(null==device) {
			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
		}
		
		if(apiKey.equals(device.getApiKey())) {
			//Set endpoint
			String deviceUrl=data.get("endpoint").asText();
			
			device.setUrl(deviceUrl);
			deviceRepo.save(device);
			
			//insert the register event
			DigitalTwinModel model = new DigitalTwinModel();
			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
			
			model.setEvent(EventType.REGISTER);
			model.setDeviceId(data.get("id").asText());
			model.setDeviceName(this.deviceRepo.findById(data.get("id").asText()).getIdentification());
			model.setType(device.getTypeId().getName());
			model.setEndpoint(deviceUrl);
			
			compositeModel.setDigitalTwinModel(model);
			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
			
			OperationResultModel result = routerDigitalTwinService.insertLog(compositeModel);
			if(!result.isStatus()) {
				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
			}
			
		}else {
			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>("Device Registered", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> ping(
			@RequestHeader(value="Authorization") String apiKey,
			@RequestBody  JsonNode data){
		
		//Validation apikey
		if(data.get("id")==null) {
			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
		}
		
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
		
		if(null==device) {
			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
		}
		
		if(apiKey.equals(device.getApiKey())) {
			//Set last updated
			device.setUpdatedAt(new Date());
			deviceRepo.save(device);
			
			//insert the ping event
			DigitalTwinModel model = new DigitalTwinModel();
			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
			
			model.setEvent(EventType.PING);
			model.setDeviceId(data.get("id").asText());
			model.setDeviceName(this.deviceRepo.findById(data.get("id").asText()).getIdentification());
			model.setType(device.getTypeId().getName());
			
			compositeModel.setDigitalTwinModel(model);
			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
			
			OperationResultModel result = routerDigitalTwinService.insertLog(compositeModel);
			if(!result.isStatus()) {
				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
			}
			
		}else {
			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>("Ping Successful", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> log(
			@RequestHeader(value="Authorization") String apiKey,
			@RequestBody JsonNode data){
		
		if(data.get("id")==null || data.get("log")==null) {
			return new ResponseEntity<>("id and log are required", HttpStatus.BAD_REQUEST);
		}

		
		
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
		
		if(null==device) {
			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
		}
		
		if(apiKey.equals(device.getApiKey())) {
			//Set last updated
			device.setUpdatedAt(new Date());
			deviceRepo.save(device);
			//insert trace of log
			DigitalTwinModel model = new DigitalTwinModel();
			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
			
			model.setEvent(EventType.LOG);
			model.setLog(data.get("log").asText());
			model.setDeviceId(data.get("id").asText());
			model.setDeviceName(this.deviceRepo.findById(data.get("id").asText()).getIdentification());
			model.setType(device.getTypeId().getName());
			
			compositeModel.setDigitalTwinModel(model);
			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
			
			OperationResultModel result = routerDigitalTwinService.insertLog(compositeModel);
			if(!result.isStatus()) {
				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
			}
			return new ResponseEntity<>(result.getResult(), HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
		}
	}
	
	@Override
	public ResponseEntity<?> shadow(
			@RequestHeader(value="Authorization") String apiKey,
			@RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
		}
		
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
		
		if(null==device) {
			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
		}
		
		if(apiKey.equals(device.getApiKey())) {
			
			//Set last updated
			device.setUpdatedAt(new Date());
			deviceRepo.save(device);
			
			//insert shadow
			DigitalTwinModel model = new DigitalTwinModel();
			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
			
			model.setEvent(EventType.SHADOW);
			model.setStatus(data.get("status").toString());
			model.setDeviceId(data.get("id").asText());
			model.setDeviceName(this.deviceRepo.findById(data.get("id").asText()).getIdentification());
			model.setType(device.getTypeId().getName());
			
			compositeModel.setDigitalTwinModel(model);
			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
			
			OperationResultModel result = routerDigitalTwinService.updateShadow(compositeModel);
			if(!result.isStatus()) {
				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
			}
			//TODO update
			return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
		}
	}
	
	@Override
	public ResponseEntity<?> notebook(
			@RequestHeader(value="Authorization") String apiKey,
			@RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//TODO
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@Override
	public ResponseEntity<?> flow(
			@RequestHeader(value="Authorization") String apiKey,
			@RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//TODO
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@Override
	public ResponseEntity<?> rule(
			@RequestHeader(value="Authorization") String apiKey,
			@RequestBody JsonNode data){
		
		if(data.get("id")==null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		//Validation apikey
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
		if(apiKey.equals(device.getApiKey())) {
			//TODO
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}
}
