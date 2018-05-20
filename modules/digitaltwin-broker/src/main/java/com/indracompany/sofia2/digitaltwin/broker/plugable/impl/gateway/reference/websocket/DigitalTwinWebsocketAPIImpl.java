/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.digitaltwin.broker.plugable.impl.gateway.reference.websocket;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.indracompany.sofia2.digitaltwin.broker.processor.ActionProcessor;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DigitalTwinWebsocketAPIImpl implements DigitalTwinWebsocketAPI {

	@Autowired
	private ActionProcessor actionProcessor;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	/**
	 * Receives messages from Digital Twin Manager, to send to the real device
	 */
	@Override
	@MessageMapping("/sendAction")
	public void sendAction(String message, MessageHeaders messageHeaders) {
		try {

			String apiKey = ((List) (((Map) messageHeaders.get("nativeHeaders")).get("Authorization"))).get(0)
					.toString();
			JSONObject objMessage = new JSONObject(message);
			actionProcessor.action(apiKey, objMessage);
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	@Override
	public void notifyShadowMessage(JSONObject message) {
		try {
			String sourceTwin = message.get("id").toString();
			messagingTemplate.convertAndSend("/api/shadow/" + sourceTwin, message.toString());

		} catch (Exception e) {
			log.error("Error notifing message", e);
		}
	}

	@Override
	public void notifyCustomMessage(JSONObject message) {
		try {
			String sourceTwin = message.get("id").toString();
			messagingTemplate.convertAndSend("/api/custom/" + sourceTwin, message.toString());

		} catch (Exception e) {
			log.error("Error notifing message", e);
		}
	}

	@Override
	public void notifyActionMessage(JSONObject message) {
		try {
			String sourceTwin = message.get("id").toString();
			messagingTemplate.convertAndSend("/api/action/" + sourceTwin, message.toString());

		} catch (Exception e) {
			log.error("Error notifing message", e);
		}
	}

}
