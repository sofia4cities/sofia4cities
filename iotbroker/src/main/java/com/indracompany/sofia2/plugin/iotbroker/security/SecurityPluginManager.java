package com.indracompany.sofia2.plugin.iotbroker.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.common.exception.AuthorizationException;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

@Component
public class SecurityPluginManager {

	
	@Autowired
	private List<SecurityPlugin> plugins;
	
	//TODO: Calls with hystrix ... or camel ...
	public List<String> authenticate(SSAPMessage<? extends SSAPBodyMessage> message) throws AuthenticationException {
		List<String> ks = new ArrayList<>();
		for(SecurityPlugin p : plugins) {
			ks.add(p.authenticate(message));
		}
		
		return ks;
	}

	public void closeSession(String sessionKey) throws AuthorizationException {
		for(SecurityPlugin p : plugins) {
			p.closeSession(sessionKey);
		}
		
	}

	public void checkSessionKeyActive(String sessionKey) throws AuthorizationException {
		for(SecurityPlugin p : plugins) {
			p.checkSessionKeyActive(sessionKey);
		}
		
	}
	
	
}
