package com.indracompany.sofia2.iotbroker.processor.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyCommandMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path="/")
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
public class CommandProcessor {

	@Autowired
	GatewayNotifier notifier;
	@Autowired
	ObjectMapper mapper;

	@RequestMapping(value="/commandAsync/{command}", method=RequestMethod.POST)
	public boolean sendAsync(@PathVariable(name="command") String command, String sessionKey, @RequestBody JsonNode params) {
		final SSAPMessage<SSAPBodyCommandMessage> cmd = new SSAPMessage<>();
		cmd.setBody(new SSAPBodyCommandMessage());
		cmd.setDirection(SSAPMessageDirection.REQUEST);
		cmd.setMessageType(SSAPMessageTypes.COMMAND);
		cmd.setSessionKey(sessionKey);
		cmd.getBody().setCommandId(UUID.randomUUID().toString());
		cmd.getBody().setCommand(command);
		cmd.getBody().setParams(params);

		notifier.sendCommandAsync(cmd);

		return true;
	}

	//	@RequestMapping(value="/commandSync/{command}", method=RequestMethod.POST)
	//	public JsonNode sendSync(@PathVariable(name="command") String command, String sessionKey, @RequestBody JsonNode params) {
	//
	//		final SSAPMessage<SSAPBodyCommandMessage> cmd = new SSAPMessage<>();
	//		cmd.setBody(new SSAPBodyCommandMessage());
	//		cmd.setDirection(SSAPMessageDirection.REQUEST);
	//		cmd.setMessageType(SSAPMessageTypes.COMMAND);
	//		cmd.setSessionKey(sessionKey);
	//		cmd.getBody().setCommand(UUID.randomUUID().toString());
	//		cmd.getBody().setCommand(command);
	//		cmd.getBody().setParams(params);
	//
	//		notifier.sendCommandSync(cmd);
	//
	//		return JsonNodeFactory.instance.nullNode();
	//	}

}
