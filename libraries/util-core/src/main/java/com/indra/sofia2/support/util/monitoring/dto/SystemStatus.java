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
package com.indra.sofia2.support.util.monitoring.dto;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.indra.sofia2.support.entity.utils.CalendarAdapter;
import com.indra.sofia2.support.util.monitoring.dto.sib.GatewayStatusDto;

public class SystemStatus {
	private DateTime currentTimestamp;
	private String systemHealthMessage;
	private SystemHealthCheckResult systemHealth;
	private Map<String, SystemHealthCheckResult> systemHealthChecks;
	private List<BatchSchedulerHealthCheckResult> batchSchedulersHealthChecks;
	private List<GatewayStatusDto> ssapGatewaysHealthChecks;
	private Integer activeSsapSessions;

	public String getCurrentTimestamp() {
		return currentTimestamp.toString();
	}

	public void setCurrentTimestamp(DateTime currentTimestamp) {
		this.currentTimestamp = currentTimestamp;
	}

	public String getSystemHealthMessage() {
		return systemHealthMessage;
	}

	public void setSystemHealthMessage(String systemHealthMessage) {
		this.systemHealthMessage = systemHealthMessage;
	}

	public SystemHealthCheckResult getSystemHealth() {
		return systemHealth;
	}

	public void setSystemHealth(SystemHealthCheckResult systemHealth) {
		this.systemHealth = systemHealth;
	}

	public Map<String, SystemHealthCheckResult> getSystemHealthChecks() {
		return systemHealthChecks;
	}

	public void setSystemHealthChecks(Map<String, SystemHealthCheckResult> systemHealthChecks) {
		this.systemHealthChecks = systemHealthChecks;
	}

	public List<BatchSchedulerHealthCheckResult> getBatchSchedulersHealthChecks() {
		return batchSchedulersHealthChecks;
	}

	public void setBatchSchedulersHealthChecks(List<BatchSchedulerHealthCheckResult> batchSchedulersHealthChecks) {
		this.batchSchedulersHealthChecks = batchSchedulersHealthChecks;
	}

	public List<GatewayStatusDto> getSsapGatewaysHealthChecks() {
		return ssapGatewaysHealthChecks;
	}

	public void setSsapGatewaysHealthChecks(List<GatewayStatusDto> ssapGatewaysHealthChecks) {
		this.ssapGatewaysHealthChecks = ssapGatewaysHealthChecks;
	}

	public Integer getActiveSsapSessions() {
		return activeSsapSessions;
	}

	public void setActiveSsapSessions(Integer activeSsapSessions) {
		this.activeSsapSessions = activeSsapSessions;
	}

	public static SystemStatus getHttpTimeoutInstance() {
		SystemStatus result = new SystemStatus();
		result.activeSsapSessions = 0;
		result.systemHealth = SystemHealthCheckResult.UNKNOWN;
		result.currentTimestamp = CalendarAdapter.getUtcDate();
		result.systemHealthMessage = "Unable to communicate with the Sofia2 core via HTTP within the specified timeout";
		return result;
	}
}
