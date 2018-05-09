/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.ssap.body;

import java.awt.geom.Point2D;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPLogLevel;
import com.indracompany.sofia2.ssap.enums.SSAPStatusType;

public class SSAPBodyLogMessage extends SSAPBodyMessage {

	public SSAPLogLevel level;
	public String message;
	public JsonNode extraData;
	public Point2D.Double coordinates;

	public Point2D.Double getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point2D.Double coordinates) {
		this.coordinates = coordinates;
	}

	public SSAPStatusType status;

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
