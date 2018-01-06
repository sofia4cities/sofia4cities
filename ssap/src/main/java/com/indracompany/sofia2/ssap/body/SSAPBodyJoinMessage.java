package com.indracompany.sofia2.ssap.body;

import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPBodyJoinMessage extends SSAPBodyMessage {
	
	private String token;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	@Override
	public boolean isThinKpMandatory() {
		return false;
	}

	@Override
	public boolean isSessionKeyMandatory() {
		return false;
	}

	@Override
	public boolean isAutorizationMandatory() {
		return false;
	}

	@Override
	public boolean isOntologyMandatory() {
		return false;
	}
	
}
