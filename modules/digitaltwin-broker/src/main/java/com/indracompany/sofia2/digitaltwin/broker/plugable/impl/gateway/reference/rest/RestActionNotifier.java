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

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

	private final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);

	private RestTemplate restTemplate;

	@Autowired
	private DigitalTwinDeviceRepository deviceRepo;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(getRestTemplateRequestFactory());
	}

	@Override
	public void notifyActionMessage(JSONObject message) {
		try {
			if (message.has("id")) {
				String targetTwin = message.get("id").toString();

				DigitalTwinDevice device = deviceRepo.findByIdentification(targetTwin);
				if (null != device) {
					String deviceEndpoint = device.getUrlSchema() + "://" + device.getIp() + ":" + device.getPort()
							+ device.getContextPath() + "/actions";

					ActionMessage actionMessage = new ActionMessage();
					actionMessage.setName(message.get("name").toString());

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

	private ClientHttpRequestFactory getRestTemplateRequestFactory() {
		RequestConfig config = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
				.setConnectionRequestTimeout(TIMEOUT).build();

		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

		return new HttpComponentsClientHttpRequestFactory(client);
	}

}
