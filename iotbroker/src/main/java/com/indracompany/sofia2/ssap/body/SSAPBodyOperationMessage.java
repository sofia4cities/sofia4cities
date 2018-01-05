package com.indracompany.sofia2.ssap.body;

import com.indracompany.sofia2.ssap.SSAPQueryResultFormat;
import com.indracompany.sofia2.ssap.SSAPQueryType;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPBodyOperationMessage extends SSAPBodyMessage {

	private SSAPQueryType queryType;
	private SSAPQueryResultFormat resultFormat;

	public SSAPQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(SSAPQueryType queryType) {
		this.queryType = queryType;
	}

	public SSAPQueryResultFormat getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(SSAPQueryResultFormat resultFormat) {
		this.resultFormat = resultFormat;
	}

	@Override
	public boolean isThinKpMandatory() {
		return true;
	}

	@Override
	public boolean isSessionKeyMandatory() {
		return true;
	}

	@Override
	public boolean isAutorizationMandatory() {
		return true;
	}

}
