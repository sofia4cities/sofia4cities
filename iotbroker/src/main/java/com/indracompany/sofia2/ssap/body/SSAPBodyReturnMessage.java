package com.indracompany.sofia2.ssap.body;

import com.indracompany.sofia2.ssap.SSAPErrorCode;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPBodyReturnMessage extends SSAPBodyMessage{
	
	private boolean ok = true;
	private String error;
	private SSAPErrorCode errorCode;
	
	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public SSAPErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(SSAPErrorCode errorCode) {
		this.errorCode = errorCode;
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

}
