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
package com.indracompany.sofia2.controlpanel.controller.management.device;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.services.apimanager.ApiManagerService;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.controlpanel.controller.management.ApiOpsRestServices;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Api(value = "Device Management")
@RestController
@Slf4j
public class DeviceManagementController extends ApiOpsRestServices {

	@Autowired
	ClientPlatformService clientPlatformService;
	@Autowired
	ApiManagerService apiManagerService;

	@ApiOperation(value = "validate clientPlatform id with token")
	@RequestMapping(value = "/validate/device/{clientPlatformId}/token/{token}", method = RequestMethod.GET)
	public ResponseEntity<?> validate(
			@ApiParam(value = "ClientPlatform Id  ", required = true) @PathVariable("clientPlatformId") String clientPlatformId,
			@ApiParam(value = "Token", required = true) @PathVariable(name = "token") String token) {

		List<Token> tokens = clientPlatformService.getTokensByClientPlatformId(clientPlatformId);

		if (tokens == null || tokens.size() == 0)
			return new ResponseEntity<>("NOT_VALID", HttpStatus.OK);

		Token result = tokens.stream().filter(x -> token.equals(x.getToken())).findAny().orElse(null);

		if (result == null)
			return new ResponseEntity<>("NOT_VALID", HttpStatus.OK);
		else
			return new ResponseEntity<>("VALID", HttpStatus.OK);

	}

}
