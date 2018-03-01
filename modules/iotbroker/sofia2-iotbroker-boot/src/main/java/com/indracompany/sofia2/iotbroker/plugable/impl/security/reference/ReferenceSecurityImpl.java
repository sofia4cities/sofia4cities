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
package com.indracompany.sofia2.iotbroker.plugable.impl.security.reference;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.token.TokenService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.SecurityPlugin;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@EnableScheduling
@Component
public class ReferenceSecurityImpl implements SecurityPlugin {

	@Autowired
	TokenService tokenService;
	@Autowired
	ClientPlatformService clientPlatformService;
	@Autowired
	UserService userService;
	@Autowired
	OntologyService ontologyService;

	ConcurrentHashMap<String, IoTSession> sessionList = new ConcurrentHashMap<>(200);

	@Override
	public Optional<IoTSession> authenticate(String token, String clientPlatform, String clientPlatformInstance) {
		final Token retrivedToken = tokenService.getTokenByToken(token);
		if(retrivedToken == null) {
			return Optional.empty();
		}

		final ClientPlatform clientPlatformDB = retrivedToken.getClientPlatform();
		if(clientPlatform.equals(clientPlatformDB.getIdentification())) {
			final IoTSession session = new IoTSession();
			session.setClientPlatform(clientPlatform);
			//TODO: What if the instance it is not provied
			session.setClientPlatformInstance(clientPlatformInstance);
			session.setExpiration(60*1000*1000);
			session.setLastAccess(ZonedDateTime.now());
			session.setSessionKey(UUID.randomUUID().toString());
			session.setToken(token);
			session.setClientPlatformID(clientPlatformDB.getId());

			session.setUserID(retrivedToken.getClientPlatform().getUser().getUserId());
			session.setUserName(retrivedToken.getClientPlatform().getUser().getFullName());

			sessionList.put(session.getSessionKey(), session);

			return Optional.of(session);
		}

		return Optional.empty();

	}

	@Override
	public boolean closeSession(String sessionKey) {
		sessionList.remove(sessionKey);
		return true;
	}

	@Override
	public boolean checkSessionKeyActive(String sessionKey) {
		final IoTSession session = sessionList.get(sessionKey);

		if(session == null) {
			return false;
		}

		final ZonedDateTime now = ZonedDateTime.now();
		final ZonedDateTime lastAccess = session.getLastAccess();

		final long time = ChronoUnit.MILLIS.between(now, lastAccess);

		if(time > session.getExpiration()) {
			sessionList.remove(sessionKey);
			return false;
		}

		return true;

	}

	@Override
	public boolean checkAuthorization(SSAPMessageTypes messageType, String ontology, String sessionKey) {

		if(!checkSessionKeyActive(sessionKey)) {
			return false;
		}

		final IoTSession session = sessionList.get(sessionKey);
		final Ontology ontologyDB = ontologyService.getOntologyByIdentification(ontology);
		final ClientPlatform clientPlatformDB = clientPlatformService.getByIdentification(session.getClientPlatform());

		return clientPlatformService.haveAuthorityOverOntology(clientPlatformDB, ontologyDB);
	}

	@Override
	public Optional<IoTSession> getSession(String sessionKey) {
		final IoTSession session = sessionList.get(sessionKey);
		if(session == null) {
			return Optional.empty();
		}

		return Optional.of(session);

	}

	@Scheduled(fixedDelay=60000)
	private void invalidateExpiredSessions() {
		final long now = System.currentTimeMillis();
		final Predicate<IoTSession> delete = s -> (now - s.getLastAccess().toInstant().toEpochMilli()) >= s.getExpiration();
		sessionList.values().removeIf(delete);
	}

}
