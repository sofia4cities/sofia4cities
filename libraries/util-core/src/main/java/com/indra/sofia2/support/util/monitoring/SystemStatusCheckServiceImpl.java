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
package com.indra.sofia2.support.util.monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.entity.utils.CalendarAdapter;
import com.indra.sofia2.support.util.monitoring.dto.BatchSchedulerHealthCheckResult;
import com.indra.sofia2.support.util.monitoring.dto.SystemHealthCheckResult;
import com.indra.sofia2.support.util.monitoring.dto.SystemStatus;
import com.indra.sofia2.support.util.monitoring.dto.sib.GatewayStatusDto;
import com.indra.sofia2.support.util.monitoring.healthchecks.SystemHealthChecksManager;
import com.indra.sofia2.support.util.monitoring.sib.SsapSessionsCounter;
import com.indra.sofia2.support.util.sib.gateway.GatewayState;

@Component
public class SystemStatusCheckServiceImpl implements SystemStatusCheckService {

	private static final Logger logger = LoggerFactory.getLogger(SystemStatusCheckServiceImpl.class);

	@Autowired
	private SystemHealthChecksManager healthChecksManager;

	@Autowired
	private ApplicationContext applicationContext;

	private SsapSessionsCounter ssapSessionsCounter;

	@PostConstruct
	public void init() {
		logger.info("Initializing system health checks service...");
		Map<String, SsapSessionsCounter> ssapSessionCounterBeans = applicationContext
				.getBeansOfType(SsapSessionsCounter.class);
		if (!ssapSessionCounterBeans.isEmpty()) {
			logger.info("Registering SSAP sessions counter...");
			ssapSessionsCounter = ssapSessionCounterBeans.values().iterator().next();
		}
	}

	@Override
	public SystemStatus getSystemStatus() {
		/*
		 * Perform the basic system health checks
		 */
		Map<String, SystemHealthCheckResult> systemHealthChecks = new HashMap<String, SystemHealthCheckResult>();
		Map<String, Boolean> mRtdbHealth = healthChecksManager.rtdbHealthCheck();
		//Se mete el estado de todas las BDTRs
		for(Entry<String,Boolean> entry:mRtdbHealth.entrySet()){
			if(entry.getValue()){
				systemHealthChecks.put(entry.getKey(), SystemHealthCheckResult.OK);
			}else{
				systemHealthChecks.put(entry.getKey(), SystemHealthCheckResult.FAIL);
			}
		}
	//	systemHealthChecks.put("realTimeDatabase", healthChecksManager.rtdbHealthCheck());
		systemHealthChecks.put("realTimeHistoricalDatabase",
				healthChecksManager.realTimeHdbHealthCheck());
		systemHealthChecks.put("batchHistoricalDatabase", healthChecksManager.batchHdbHealthCheck());
		systemHealthChecks.put("hdfs", healthChecksManager.hdfsHealthCheck());
		systemHealthChecks.put("configurationDatabase", healthChecksManager.cdbHealthCheck());
		systemHealthChecks.put("distributedCache", healthChecksManager.cacheHealthCheck());
		systemHealthChecks.put("consoleHttpConnectivity", healthChecksManager.consoleHttpConnectivityCheck());
		systemHealthChecks.put("apiManagerHttpConnectivity", healthChecksManager.apiManagerHttpConnectivityCheck());
		systemHealthChecks.put("sibHttpConnectivity", healthChecksManager.sibHttpConnectivityCheck());
		systemHealthChecks.put("batchContainerHttpConnectivity", healthChecksManager.processHttpConnectivityCheck());
		systemHealthChecks.put("scriptingContainerHttpConnectivity",
				healthChecksManager.scriptHttpConnectivityCheck());

		/*
		 * Perform the Quartz schedulers health checks (if there are Quartz
		 * schedulers)
		 */
		List<BatchSchedulerHealthCheckResult> schedulerHealthChecks = healthChecksManager.batchSchedulersHealthCheck();
		SystemHealthCheckResult quartzHealthCheck = SystemHealthCheckResult.DISABLED;
		for (BatchSchedulerHealthCheckResult schedulerHealthCheck : schedulerHealthChecks) {
			if (!schedulerHealthCheck.isRunning() && schedulerHealthCheck.getScheduledJobs() > 0) {
				quartzHealthCheck = SystemHealthCheckResult.FAIL;
				break;
			} else {
				quartzHealthCheck = SystemHealthCheckResult.OK;
			}
		}
		systemHealthChecks.put("quartzSchedulers", quartzHealthCheck);

		/*
		 * Perform the SSAP gateways health checks (if there are SSAP gateways)
		 */
		List<GatewayStatusDto> ssapGatewaysHealthChecks = healthChecksManager.sibGatewaysHealthCheck();
		SystemHealthCheckResult sibSsapGatewaysHealthCheck = SystemHealthCheckResult.DISABLED;
		for (GatewayStatusDto gatewayHealthCheck : ssapGatewaysHealthChecks) {
			if (gatewayHealthCheck.getState() != GatewayState.STARTED) {
				sibSsapGatewaysHealthCheck = SystemHealthCheckResult.FAIL;
				break;
			}
			if (gatewayHealthCheck.getInternalHealthChecks() != null) {
				for (SystemHealthCheckResult internalHealthCheck : gatewayHealthCheck.getInternalHealthChecks().values()) {
					if (!internalHealthCheck.toBoolean())
						sibSsapGatewaysHealthCheck = SystemHealthCheckResult.FAIL;
					break;
				}
				if (sibSsapGatewaysHealthCheck == SystemHealthCheckResult.FAIL)
					break;
			}
		}
		systemHealthChecks.put("ssapGateways", sibSsapGatewaysHealthCheck);

		/*
		 * Filter out disabled health checks and compute the overall system
		 * status
		 */
		boolean systemStatusFlag = true;
		Map<String, SystemHealthCheckResult> filteredSystemHealthChecks = new HashMap<String, SystemHealthCheckResult>();
		for (String key : systemHealthChecks.keySet()) {
			SystemHealthCheckResult healthCheckResult = systemHealthChecks.get(key);
			if (healthCheckResult != SystemHealthCheckResult.DISABLED)
				filteredSystemHealthChecks.put(key, systemHealthChecks.get(key));
			systemStatusFlag = systemStatusFlag && healthCheckResult.toBoolean();
		}

		SystemStatus result = new SystemStatus();
		result.setCurrentTimestamp(CalendarAdapter.getUtcDate());
		if (!schedulerHealthChecks.isEmpty())
			result.setBatchSchedulersHealthChecks(schedulerHealthChecks);
		if (!ssapGatewaysHealthChecks.isEmpty()) {
			result.setSsapGatewaysHealthChecks(ssapGatewaysHealthChecks);
		}

		/*
		 * Count the active SSAP sessions (if we're on a SIB)
		 */
		if (ssapSessionsCounter != null)
			result.setActiveSsapSessions(ssapSessionsCounter.countSsapSessions());

		result.setSystemHealthChecks(filteredSystemHealthChecks);
		if (systemStatusFlag) {
			result.setSystemHealth(SystemHealthCheckResult.OK);
			result.setSystemHealthMessage("All the components are operative");
		} else {
			result.setSystemHealth(SystemHealthCheckResult.FAIL);
			result.setSystemHealthMessage("At least one component is not operative");
		}
		return result;
	}
}
