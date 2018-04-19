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
package com.indracompany.sofia2.digitaltwin.logic.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.digitaltwin.event.manager.EventManager;
import com.indracompany.sofia2.digitaltwin.status.IDigitalTwinStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides Digital Device API to Javascript Logic
 * 
 * @author INDRA SISTEMAS
 *
 */
@Slf4j
@Component
public class DigitalTwinApi {

	@Autowired
	private EventManager eventManager;

	@Autowired
	private IDigitalTwinStatus digitalTwinStatus;

	private static DigitalTwinApi instance;

	@PostConstruct
	public void init() {
		instance = this;
	}

	public static DigitalTwinApi getInstance() {
		return instance;
	}

	public void log(String trace) {
		eventManager.log(trace);
	}

	public void setStatusValue(String property, Object value) {
		try {
			digitalTwinStatus.setProperty(property, value);
		} catch (Exception e) {
			log.error("Error setting status property {}", property, e);
		}
	}

	public Object getStatusValue(String property) {
		try {
			return digitalTwinStatus.getProperty(property);
		} catch (Exception e) {
			log.error("Error getting status property {}", property, e);
			return null;
		}
	}

	public void sendUpdateShadow() {
		eventManager.updateShadow(digitalTwinStatus.toMap());
	}

	public void sendCustomEvent(String eventName) {
		eventManager.sendCustomEvent(digitalTwinStatus.toMap(), eventName);
	}

}
