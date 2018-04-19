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
import com.indracompany.sofia2.router.service.app.service.RouterDigitalTwinService;

@RestController
@ConditionalOnProperty(prefix = "sofia2.digitaltwin.broker.rest", name = "enable", havingValue = "true")
@EnableAutoConfiguration
public class ConfigGatewayImpl implements ConfigGateway {

	@Autowired
	private DigitalTwinDeviceRepository deviceRepo;

	@Autowired
	@Qualifier("routerDigitalTwinServiceImpl")
	private RouterDigitalTwinService routerDigitalTwinService;

	@Override
	public ResponseEntity<?> getWot(@RequestHeader(value = "Authorization") String apiKey, @RequestBody JsonNode data) {
		// Validation apikey
		if (data.get("id") == null) {
			return new ResponseEntity<>("id are required", HttpStatus.BAD_REQUEST);
		}
		DigitalTwinDevice device = deviceRepo.findByIdentification(data.get("id").asText());

		if (null == device) {
			return new ResponseEntity<>("Digital Twin not found", HttpStatus.NOT_FOUND);
		}

		if (apiKey.equals(device.getDigitalKey())) {

			return new ResponseEntity<>(device.getTypeId().getJson(), HttpStatus.OK);

		} else {
			return new ResponseEntity<>("Token not valid", HttpStatus.UNAUTHORIZED);
		}
	}

}
