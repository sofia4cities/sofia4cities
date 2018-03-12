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
package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.websocket;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

@ConditionalOnProperty(
		prefix="sofia2.iotbroker.plugbable.gateway.stomp",
		name="enable",
		havingValue="true"
		)
@Controller
public class StompWebSocketHandler {
	@Autowired
	GatewayNotifier subscriptor;

	@Autowired
	MessageProcessor processor;

	@Autowired
	SimpMessagingTemplate messagingTemplate;

	@PostConstruct
	public void init() {
		subscriptor.addSubscriptionListener("stomp_gateway",
				(s) -> {
					messagingTemplate.convertAndSend("/topic/subscription/" + s.getSessionKey(), s);
				});

		subscriptor.addCommandListener("stomp_gateway",
				(c) -> {
					messagingTemplate.convertAndSend("/topic/command/" + c.getSessionKey(), c);
					return new SSAPMessage<>();
				});
	}

	@MessageMapping("/message/{token}")
	public void handleConnect(@Payload SSAPMessage<SSAPBodyJoinMessage> message, @DestinationVariable("token") String token, MessageHeaders messageHeaders) throws MessagingException, JsonProcessingException {
		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(message);
		final ObjectMapper mapper = new ObjectMapper();
		messagingTemplate.convertAndSend("/topic/message/" + token, response);

	}


}
