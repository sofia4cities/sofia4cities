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
package com.indra.sofia2.support.util.monitoring.healthchecks;

import java.util.List;
import java.util.Map;
import com.indra.sofia2.support.util.monitoring.dto.BatchSchedulerHealthCheckResult;
import com.indra.sofia2.support.util.monitoring.dto.SystemHealthCheckResult;
import com.indra.sofia2.support.util.monitoring.dto.sib.GatewayStatusDto;

public interface SystemHealthChecksManager {

	/**
	 * Checks if the system is connected to the real-time database.
	 */
	public Map<String, Boolean> rtdbHealthCheck();

	/**
	 * Checks if the system is connected to the configuration database.
	 */
	public SystemHealthCheckResult cdbHealthCheck();	

	/**
	 * Checks if the system is connected to the real-time historical database (usually, Impala)
	 */
	public SystemHealthCheckResult realTimeHdbHealthCheck();
	
	/**
	 * Checks if the system is connected to the batch historical database (usually, Hive) 
	 */
	public SystemHealthCheckResult batchHdbHealthCheck();
	
	/**
	 * Checks if the system is connected to HDFS
	 * @return
	 */
	public SystemHealthCheckResult hdfsHealthCheck();

	/**
	 * Checks if the cache of the system is operating normally
	 */
	public SystemHealthCheckResult cacheHealthCheck();

	/**
	 * Checks if the system is connected to a Sofia2 Script core via HTTP
	 */
	public SystemHealthCheckResult scriptHttpConnectivityCheck();

	/**
	 * Checks if the system is connected to a Sofia2 Process core via HTTP
	 */
	public SystemHealthCheckResult processHttpConnectivityCheck();

	/**
	 * Checks if the system is connected to a Sofia2 SIB core via HTTP
	 */
	public SystemHealthCheckResult sibHttpConnectivityCheck();

	/**
	 * Checks if the system is connected to a Sofia2 API Manager core via HTTP
	 */
	public SystemHealthCheckResult apiManagerHttpConnectivityCheck();

	/**
	 * Checks if the system is connected to a Sofia2 Console core via HTTP
	 */
	public SystemHealthCheckResult consoleHttpConnectivityCheck();
	
	/**
	 * Returns the status of the Quartz schedulers
	 * @return
	 */
	public List<BatchSchedulerHealthCheckResult> batchSchedulersHealthCheck();
	
	/**
	 * Returns the status of the SSAP gateways
	 * @return
	 */
	public List<GatewayStatusDto> sibGatewaysHealthCheck();

}
