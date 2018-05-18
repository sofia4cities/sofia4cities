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
package com.indracompany.sofia2.digitaltwin.broker.plugable.impl.gateway.reference.rest;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.repository.DigitalTwinDeviceRepository;
import com.indracompany.sofia2.digitaltwin.broker.plugable.impl.gateway.reference.ActionNotifier;
import com.indracompany.sofia2.digitaltwin.broker.processor.model.ActionMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestActionNotifier implements ActionNotifier {

	private RestTemplate restTemplate;

	@Autowired
	private DigitalTwinDeviceRepository deviceRepo;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
	}

	@Override
	public void notifyShadowMessage(JSONObject message) {

	}

	@Override
	public void notifyCustomMessage(JSONObject message) {
		try {
			if (message.has("target")) {
				String targetTwin = message.get("target").toString();

				DigitalTwinDevice device = deviceRepo.findByIdentification(targetTwin);
				if (null != device) {
					String deviceEndpoint = device.getUrlSchema() + "://" + device.getIp() + ":" + device.getPort()
							+ device.getContextPath() + "/actions";

					ActionMessage actionMessage = new ActionMessage();
					actionMessage.setName(message.get("event").toString());

					HttpEntity<ActionMessage> shadowEntity = new HttpEntity<ActionMessage>(actionMessage);

					log.info("Attemp to notify custom message to device {}", deviceEndpoint);
					ResponseEntity<String> resp = restTemplate.exchange(deviceEndpoint, HttpMethod.POST, shadowEntity,
							String.class);

					if (resp.getStatusCode() == HttpStatus.OK) {
						log.info("Notified custom message to device {}", deviceEndpoint);
					} else {
						log.warn("HTTP code {} notifing custom message to device {}", resp.getStatusCode(),
								deviceEndpoint);
						log.warn("Broker message {}", resp.getBody());
					}
				}
			}
		} catch (Exception e) {
			log.error("Error notifing shadow message", e);
		}

	}

}
