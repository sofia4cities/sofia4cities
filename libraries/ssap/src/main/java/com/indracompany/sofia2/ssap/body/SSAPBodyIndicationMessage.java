package com.indracompany.sofia2.ssap.body;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyOntologyMessage;

public class SSAPBodyIndicationMessage extends SSAPBodyOntologyMessage {

	private String subsciptionId;
	private String query;
	private JsonNode data;

	public String getSubsciptionId() {
		return subsciptionId;
	}

	public void setSubsciptionId(String subsciptionId) {
		this.subsciptionId = subsciptionId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public JsonNode getData() {
		return data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	@Override
	public boolean isSessionKeyMandatory() {
		return true;
	}

	@Override
	public boolean isOntologyMandatory() {
		return true;
	}

}
