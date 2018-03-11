package com.indracompany.sofia2.ssap.body;

import com.indracompany.sofia2.ssap.body.parent.SSAPBodyOntologyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

public class SSAPBodySubscribeMessage extends SSAPBodyOntologyMessage {

	private String query;
	private SSAPQueryType queryType;

	@Override
	public boolean isSessionKeyMandatory() {
		return true;
	}

	@Override
	public boolean isOntologyMandatory() {
		return true;
	}

	public SSAPQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(SSAPQueryType queryType) {
		this.queryType = queryType;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
