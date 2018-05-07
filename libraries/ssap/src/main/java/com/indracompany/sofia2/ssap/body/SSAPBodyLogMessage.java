package com.indracompany.sofia2.ssap.body;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPLogLevel;
import com.indracompany.sofia2.ssap.enums.SSAPStatusType;

import javafx.geometry.Point2D;

public class SSAPBodyLogMessage extends SSAPBodyMessage {

	public SSAPLogLevel level;
	public String message;
	public JsonNode extraData;
	private Point2D coordinates;
	private SSAPStatusType status;

	public Point2D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point2D coordinates) {
		this.coordinates = coordinates;
	}

	public SSAPLogLevel getLevel() {
		return level;
	}

	public void setLevel(SSAPLogLevel level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JsonNode getExtraData() {
		return extraData;
	}

	public void setExtraData(JsonNode extraData) {
		this.extraData = extraData;
	}

	@Override
	public boolean isSessionKeyMandatory() {
		return true;
	}

	@Override
	public boolean isOntologyMandatory() {
		return false;
	}

	public SSAPStatusType getStatus() {
		return status;
	}

	public void setStatus(SSAPStatusType status) {
		this.status = status;
	}

}
