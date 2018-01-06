package com.indracompany.sofia2.plugin.iotbroker.security;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public interface SecurityPlugin {
	public String authenticate(SSAPMessage<? extends SSAPBodyMessage> message) throws AuthenticationException;

	public void closeSession(String sessionKey) throws AuthorizationException;

	public void checkSessionKeyActive(String sessionKey) throws AuthorizationException;

	public void checkAuthorization(SSAPMessageTypes messageType, String ontology, String sessionKey) throws AuthorizationException;
}
