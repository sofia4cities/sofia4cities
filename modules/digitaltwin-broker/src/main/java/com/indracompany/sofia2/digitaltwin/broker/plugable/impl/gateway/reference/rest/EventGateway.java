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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



@RequestMapping(path="/event")
@CrossOrigin(origins = "*")
@Api(value="event", description="Sofia4Cities events for digital twins")
public interface EventGateway {
	
	@ApiOperation(value = "Event Register to register the endpoint of the Digital Twin")
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public ResponseEntity<?> register(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true) JsonNode data);
		
	
	@ApiOperation(value = "Event Ping")
	@RequestMapping(value="/ping", method=RequestMethod.POST)
	public ResponseEntity<?> ping(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true)  JsonNode data);
	
	@ApiOperation(value = "Event Log")
	@RequestMapping(value="/log", method=RequestMethod.POST)
	public ResponseEntity<?> log(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true) JsonNode data);
	
	@ApiOperation(value = "Event Shadow")
	@RequestMapping(value="/shadow", method=RequestMethod.POST)
	public ResponseEntity<?> shadow(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true) JsonNode data);
	
	@ApiOperation(value = "Event Notebook")
	@RequestMapping(value="/notebook", method=RequestMethod.POST)
	public ResponseEntity<?> notebook(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true) JsonNode data);
	
	@ApiOperation(value = "Event Flow")
	@RequestMapping(value="/flow", method=RequestMethod.POST)
	public ResponseEntity<?> flow(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true) JsonNode data);
	
	@ApiOperation(value = "Event Rule")
	@RequestMapping(value="/rule", method=RequestMethod.POST)
	public ResponseEntity<?> rule(
			@ApiParam(value = "ApiKey provided from digital twin", required = true) String apiKey,
			@ApiParam(value = "Json data need to execute the event", required = true) JsonNode data);
}
