package com.indracompany.sofia2.plugin.iotbroker.security.impl;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPlugin;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

//TODO: Delete this class
@Profile({"dummy-dev"})
@Component
public class DummyTokenSecurityPlugin implements SecurityPlugin {
	
	@Override
	public String authenticate(SSAPMessage<? extends SSAPBodyMessage> message) throws AuthenticationException {
		return UUID.randomUUID().toString();
	}

	@Override
	public void closeSession(String sessionKey) throws AuthorizationException {
		
	}

	@Override
	public void checkSessionKeyActive(String sessionKey) throws AuthorizationException {
		
	}

}
