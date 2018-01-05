package com.indracompany.sofia2.ssap.body.parent;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class SSAPBodyMessage {
	
	protected JsonNode data;
	protected String thinKp;
	protected String thinkpInstance;
	
	public JsonNode getData() {
		return data;
	}
	public void setData(JsonNode data) {
		this.data = data;
	}
	public String getThinKp() {
		return thinKp;
	}
	public void setThinKp(String thinKp) {
		this.thinKp = thinKp;
	}
	public String getThinkpInstance() {
		return thinkpInstance;
	}
	public void setThinkpInstance(String thinkpInstance) {
		this.thinkpInstance = thinkpInstance;
	}
	
	public abstract boolean isThinKpMandatory();

	public abstract boolean isSessionKeyMandatory();
	
	public abstract boolean isAutorizationMandatory();

}
