package com.indracompany.sofia2.plugin.iotbroker.security.impl.reference;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.token.TokenService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.plugin.iotbroker.security.IoTSession;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPlugin;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

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
	@Autowired
	OntologyUserAccessRepository ontologyUserAccessRepository;


	ConcurrentHashMap<String, IoTSession> sessionList = new ConcurrentHashMap<>(200);

	@Override
	public Optional<IoTSession> authenticate(String token, String clientPlatform, String clientPlatformInstance) throws AuthenticationException {
		final Token retrivedToken = tokenService.getTokenByToken(token);
		if(retrivedToken == null) {
			return Optional.empty();
		}
		if(clientPlatform.equals(retrivedToken.getClientPlatform().getIdentification())) {
			final IoTSession session = new IoTSession();
			session.setClientPlatform(clientPlatform);
			//TODO: What if the instance it is not provied
			session.setClientPlatformInstance(clientPlatformInstance);
			session.setExpiration(60*1000*1000);
			session.setLastAccess(ZonedDateTime.now());
			session.setSessionKey(UUID.randomUUID().toString());
			session.setToken(token);

			session.setUserID(retrivedToken.getClientPlatform().getUser().getUserId());
			session.setUserName(retrivedToken.getClientPlatform().getUser().getFullName());

			sessionList.put(session.getSessionKey(), session);

			return Optional.of(session);
		}

		return Optional.empty();

	}

	@Override
	public boolean closeSession(String sessionKey) throws AuthorizationException {
		sessionList.remove(sessionKey);
		return true;
	}

	@Override
	public boolean checkSessionKeyActive(String sessionKey) throws AuthorizationException {
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
	public boolean checkAuthorization(SSAPMessageTypes messageType, String ontology, String sessionKey)
			throws AuthorizationException {

		final boolean ret = false;
		if(!checkSessionKeyActive(sessionKey)) {
			return false;
		}

		final IoTSession session = sessionList.get(sessionKey);
		final User userDB = userService.getUserByIdentification(session.getUserID());
		final Ontology ontologyDB = ontologyService.getOntologyByIdentification(ontology);

		final List<OntologyUserAccess> access = ontologyUserAccessRepository.findByOntologyIdAndUser(ontologyDB, userDB);
		final Iterator<OntologyUserAccess> it = access.iterator();
		while(it.hasNext()) {
			final OntologyUserAccess aa = it.next();
			aa.getOntologyUserAccessType();
			//TODO: Check permissions
		}

		return ret;

	}

	@Override
	public Optional<IoTSession> getSession(String sessionKey) {
		final IoTSession session = sessionList.get(sessionKey);
		if(session == null) {
			return Optional.empty();
		}

		return Optional.of(session);

	}

}
