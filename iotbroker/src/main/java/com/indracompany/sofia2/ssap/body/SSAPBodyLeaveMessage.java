package com.indracompany.sofia2.ssap.body;

import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPBodyLeaveMessage extends SSAPBodyMessage {

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

}
