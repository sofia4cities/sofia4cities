/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
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
package com.indracompany.sofia2.router.service.app.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class DigitalTwinModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static enum EventType {
		PING, LOG, NOTEBOOK, FLOW, RULE, SHADOW, REGISTER, CUSTOM, PIPELINE;
	}

	@Getter
	@Setter
	private String deviceId;
	
	@Getter
	@Setter
	private String deviceName;
	
	@Getter
	@Setter
	private String type;
	
	@Getter
	@Setter
	private  EventType event;
	
	@Getter
	@Setter
	private  String eventName;
	
	@Getter
	@Setter
	private  String status;
	
	@Getter
	@Setter
	private  String processId;
	
	@Getter
	@Setter
	private  String log;
	
	@Getter
	@Setter
	private String endpoint;

	@Override
	public String toString() {
		return "DigitalTwinModel [deviceId=" + deviceId + ", type=" + type + ", event=" + event + ", status=" + status
				+ ", processId=" + processId + ", log=" + log + ", endpoint=" + endpoint + ", eventName" + eventName+"]";
	}
	
	
}
