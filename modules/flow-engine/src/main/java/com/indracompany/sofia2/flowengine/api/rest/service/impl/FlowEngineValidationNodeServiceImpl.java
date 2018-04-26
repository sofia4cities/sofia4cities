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
package com.indracompany.sofia2.flowengine.api.rest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.flowengine.api.rest.pojo.DecodedAuthentication;
import com.indracompany.sofia2.flowengine.api.rest.service.FlowEngineValidationNodeService;
import com.indracompany.sofia2.flowengine.exception.NotAuthorizedException;
import com.indracompany.sofia2.flowengine.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlowEngineValidationNodeServiceImpl implements FlowEngineValidationNodeService {

	@Autowired
	private UserService userService;

	public User validateUserCredentials(String userId, String password)
			throws ResourceNotFoundException, NotAuthorizedException {
		if (userId == null || password == null || userId.isEmpty() || password.isEmpty()) {
			log.error("User or password cannot be empty.");
			throw new IllegalArgumentException("User or password cannot be empty.");
		}

		final User sofia2User = userService.getUser(userId);
		if (sofia2User == null) {
			log.error("Requested user does not exist");
			throw new ResourceNotFoundException("Requested user does not exist");
		}
		if (!sofia2User.getPassword().equals(password)) {
			log.error("Password for user " + userId + " does not match.");
			throw new NotAuthorizedException("Password for user " + userId + " does not match.");
		}
		return sofia2User;
	}

	public DecodedAuthentication decodeAuth(String authentication) throws IllegalArgumentException {
		try {
			return new DecodedAuthentication(authentication);
		} catch (final Exception e) {
			throw new IllegalArgumentException("Authentication is null or cannot be decoded.");
		}
	}

}
