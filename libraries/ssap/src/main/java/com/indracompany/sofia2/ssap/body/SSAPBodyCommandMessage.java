package com.indracompany.sofia2.ssap.body;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPBodyCommandMessage extends SSAPBodyMessage {

	private String commandId;
	private String command;
	private JsonNode params;

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public JsonNode getParams() {
		return params;
	}

	public void setParams(JsonNode params) {
		this.params = params;
	}

	@Override
	public boolean isSessionKeyMandatory() {
		return false;
	}

	@Override
	public boolean isOntologyMandatory() {
		return false;
	}
}
