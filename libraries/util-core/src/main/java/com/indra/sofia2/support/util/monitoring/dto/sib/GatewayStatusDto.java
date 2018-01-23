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
package com.indra.sofia2.support.util.monitoring.dto.sib;

import java.util.Map;

import com.indra.sofia2.support.util.monitoring.dto.SystemHealthCheckResult;
import com.indra.sofia2.support.util.sib.gateway.GatewayState;
import com.indra.sofia2.support.entity.sib.GatewayProtocol;

public class GatewayStatusDto {
	private GatewayProtocol protocol;
	private GatewayState state;
	private Integer activeConnections;
	private Map<String, SystemHealthCheckResult> internalHealthChecks;

	public GatewayProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(GatewayProtocol protocol) {
		this.protocol = protocol;
	}

	public GatewayState getState() {
		return state;
	}

	public void setState(GatewayState state) {
		this.state = state;
	}

	public Integer getActiveConnections() {
		return activeConnections;
	}

	public void setActiveConnections(Integer activeConnections) {
		this.activeConnections = activeConnections;
	}

	public Map<String, SystemHealthCheckResult> getInternalHealthChecks() {
		return internalHealthChecks;
	}

	public void setInternalHealthChecks(Map<String, SystemHealthCheckResult> internalHealthChecks) {
		this.internalHealthChecks = internalHealthChecks;
	}

}
