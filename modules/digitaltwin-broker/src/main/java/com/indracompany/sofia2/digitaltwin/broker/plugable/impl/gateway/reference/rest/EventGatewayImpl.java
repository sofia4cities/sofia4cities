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

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.digitaltwin.broker.processor.EventProcessor;
import com.indracompany.sofia2.digitaltwin.broker.processor.model.EventResponseMessage;

import lombok.extern.slf4j.Slf4j;

@RestController
@ConditionalOnProperty(prefix = "sofia2.digitaltwin.broker.rest", name = "enable", havingValue = "true")
@EnableAutoConfiguration
@Slf4j
public class EventGatewayImpl implements EventGateway {

//	@Autowired
//	private DigitalTwinDeviceRepository deviceRepo;
//
//	@Autowired
//	@Qualifier("routerDigitalTwinServiceImpl")
//	private RouterDigitalTwinService routerDigitalTwinService;
	
	@Autowired
	private EventProcessor eventProcessor;
	

	@Override
	public ResponseEntity<?> register(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		if(log.isDebugEnabled()) {
			log.debug("Received register event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.register(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in register event", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	
		
//		// Validation apikey
//		if (data.get("id") == null || data.get("endpoint") == null) {
//			return new ResponseEntity<>("id and endpoint are required", HttpStatus.BAD_REQUEST);
//		}
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//			// Set endpoint
//			String deviceUrl = data.get("endpoint").asText();
//
//			String urlSchema = deviceUrl.split("://")[0];
//			String ip = deviceUrl.split("://")[1].split("/")[0].split(":")[0];
//			String port = deviceUrl.split("://")[1].split("/")[0].split(":")[1];
//			String contextPath;
//			if (deviceUrl.split("://")[1].split("/").length > 2) {
//				contextPath = deviceUrl.split("://")[1].split("/")[2];
//			} else {
//				contextPath = deviceUrl.split("://")[1].split("/")[1];
//			}
//
//			device.setUrlSchema(urlSchema);
//			device.setIp(ip);
//			device.setPort(Integer.parseInt(port));
//			if (!contextPath.startsWith("/")) {
//				device.setContextPath("/" + contextPath);
//			} else {
//				device.setContextPath(contextPath);
//			}
//
//			deviceRepo.save(device);
//
//			// insert the register event
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.REGISTER);
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//			model.setEndpoint(deviceUrl);
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.insertLog(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
//		return new ResponseEntity<>("Device Registered", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> ping(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		
		if(log.isDebugEnabled()) {
			log.debug("Received ping event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.ping(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in ping event", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

//		// Validation apikey
//		if (data.get("id") == null) {
//			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
//		}
//
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//			// Set last updated
//			device.setUpdatedAt(new Date());
//			deviceRepo.save(device);
//
//			// insert the ping event
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.PING);
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.insertLog(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
//		return new ResponseEntity<>("Ping Successful", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> log(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		
		if(log.isDebugEnabled()) {
			log.debug("Received log event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.log(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in log event", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

//		if (data.get("id") == null || data.get("log") == null) {
//			return new ResponseEntity<>("id and log are required", HttpStatus.BAD_REQUEST);
//		}
//
//		// Validation apikey
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//			// Set last updated
//			device.setUpdatedAt(new Date());
//			deviceRepo.save(device);
//			// insert trace of log
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.LOG);
//			model.setLog(data.get("log").asText());
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.insertLog(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//			return new ResponseEntity<>(result.getResult(), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
	}

	@Override
	public ResponseEntity<?> shadow(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		
		if(log.isDebugEnabled()) {
			log.debug("Received shadow event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.shadow(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in shadow event", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

//		if (data.get("id") == null) {
//			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
//		}
//
//		// Validation apikey
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//
//			// Set last updated
//			device.setUpdatedAt(new Date());
//			deviceRepo.save(device);
//
//			// insert shadow
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.SHADOW);
//			model.setStatus(data.get("status").toString());
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.updateShadow(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//			// TODO update
//			return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
	}

	@Override
	public ResponseEntity<?> notebook(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		
		if(log.isDebugEnabled()) {
			log.debug("Received notebook event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.notebook(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in notebook event", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

//		if (data.get("id") == null) {
//			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
//		}
//
//		// Validation apikey
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//
//			// Set last updated
//			device.setUpdatedAt(new Date());
//			deviceRepo.save(device);
//
//			// insert event
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.NOTEBOOK);
//			model.setStatus(data.get("status").toString());
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.insertEvent(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//			return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
	}

	@Override
	public ResponseEntity<?> flow(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {

		if(log.isDebugEnabled()) {
			log.debug("Received flow event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.flow(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in flow event", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
//		if (data.get("id") == null) {
//			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
//		}
//
//		// Validation apikey
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//
//			// Set last updated
//			device.setUpdatedAt(new Date());
//			deviceRepo.save(device);
//
//			// insert event
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.FLOW);
//			model.setStatus(data.get("status").toString());
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.insertEvent(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//			return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
	}

	@Override
	public ResponseEntity<?> rule(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		
		if(log.isDebugEnabled()) {
			log.debug("Received rule event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.rule(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in rule event", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
//		if (data.get("id") == null) {
//			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
//		}
//
//		// Validation apikey
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//
//			// Set last updated
//			device.setUpdatedAt(new Date());
//			deviceRepo.save(device);
//
//			// insert event
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.RULE);
//			model.setStatus(data.get("status").toString());
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.insertEvent(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//			return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
	}

	@Override
	public ResponseEntity<?> custom(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		if(log.isDebugEnabled()) {
			log.debug("Received custom event: {}", data.toString());
		}
		try {
			EventResponseMessage eventMessage=eventProcessor.custom(apiKey, new JSONObject(data.toString()));
			return new ResponseEntity<>(eventMessage.getMessage(), eventMessage.getCode());
			
		}catch(Exception e) {
			log.error("Error in rule custom", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
//		if (data.get("id") == null) {
//			return new ResponseEntity<>("id is required", HttpStatus.BAD_REQUEST);
//		}
//
//		// Validation apikey
//		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());
//
//		if (null == device) {
//			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
//		}
//
//		if (apiKey.equals(device.getDigitalKey())) {
//
//			// Set last updated
//			device.setUpdatedAt(new Date());
//			deviceRepo.save(device);
//
//			// insert event
//			DigitalTwinModel model = new DigitalTwinModel();
//			DigitalTwinCompositeModel compositeModel = new DigitalTwinCompositeModel();
//
//			model.setEvent(EventType.CUSTOM);
//			model.setStatus(data.get("status").toString());
//			model.setDeviceId(device.getId());
//			model.setDeviceName(device.getIdentification());
//			model.setType(device.getTypeId().getName());
//			model.setEventName(data.get("event").asText());
//
//			compositeModel.setDigitalTwinModel(model);
//			compositeModel.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//			OperationResultModel result = routerDigitalTwinService.insertEvent(compositeModel);
//			if (!result.isStatus()) {
//				return new ResponseEntity<>(result.getMessage(), HttpStatus.valueOf(result.getErrorCode()));
//			}
//			return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
//		}
	}
}
