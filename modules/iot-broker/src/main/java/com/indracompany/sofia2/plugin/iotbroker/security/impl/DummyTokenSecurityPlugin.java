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
package com.indracompany.sofia2.plugin.iotbroker.security.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.plugin.iotbroker.security.IoTSession;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPlugin;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

//TODO: Delete this class
@Component
public class DummyTokenSecurityPlugin implements SecurityPlugin {

	@Override
	public Optional<IoTSession> authenticate(String token, String clientPlatform, String clientPlatformInstance) throws AuthenticationException {
		final IoTSession session = new IoTSession();
		session.setClientPlatform(clientPlatform);
		session.setClientPlatformInstance(clientPlatformInstance);

		return Optional.of(session);

		//		return UUID.randomUUID().toString();
	}

	@Override
	public boolean closeSession(String sessionKey) throws AuthorizationException {
		return true;
	}

	@Override
	public boolean checkSessionKeyActive(String sessionKey) throws AuthorizationException {
		return true;
	}

	@Override
	public boolean checkAuthorization(SSAPMessageTypes messageType, String ontology, String sessionKey)
			throws AuthorizationException {
		return true;
	}

	@Override
	public Optional<IoTSession> getSession(String sessionKey) {

		final IoTSession session = new IoTSession();
		session.setClientPlatform(UUID.randomUUID().toString());
		session.setClientPlatform(UUID.randomUUID().toString());
		session.setClientPlatformInstance(UUID.randomUUID().toString());

		return Optional.of(session);
	}

}
