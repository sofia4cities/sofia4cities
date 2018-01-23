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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.monitoring.healthchecks;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.indra.sofia2.grid.client.cache.CacheService;
import com.indra.sofia2.support.bbdd.hdfs.HdfsStatusChecker;
import com.indra.sofia2.support.bbdd.sib.persistence.DAOBDHPersistence;
import com.indra.sofia2.support.bbdd.sib.persistence.DAOTRPersistence;
import com.indra.sofia2.support.bbdd.test.DatabaseTester;
import com.indra.sofia2.support.util.monitoring.dto.BatchSchedulerHealthCheckResult;
import com.indra.sofia2.support.util.monitoring.dto.SystemHealthCheckResult;
import com.indra.sofia2.support.util.monitoring.dto.sib.GatewayStatusDto;
import com.indra.sofia2.support.util.quartz.Sofia2BatchScheduler;
import com.indra.sofia2.support.util.quartz.exceptions.Sofia2BatchSchedulerException;
import com.indra.sofia2.support.util.rest.RestTemplateFactory;
import com.indra.sofia2.support.util.sib.gateway.MonitoredGateway;

@Component
public class SystemHealthChecksManagerImpl implements SystemHealthChecksManager {

	private static final Logger logger = LoggerFactory.getLogger(SystemHealthChecksManagerImpl.class);
	
	private final static String SIB="IoT Broker";
	private final static String API_MANAGER="API Manager";
	private final static String SCRIPT="Streaming Engine";
	private final static String PROCESS="Batch Container";
	private final static String WEB_CONSOLE="Control Panel";

	@Autowired(required=false)
	@Qualifier("DAOImpalaBDH")
	private DAOBDHPersistence impalaDao;
	
	@Autowired(required=false)
	@Qualifier("DAOHiveBDH")
	private DAOBDHPersistence hiveDao;

	@Autowired
	@Qualifier("DAODiskDBTR")
	private DAOTRPersistence rtdbDao;

	@Autowired
	@Qualifier("bdcTester")
	private DatabaseTester cdbTester;

	@Autowired
	private CacheService cacheService;

	private HdfsStatusChecker hdfsStatusChecker;
	
	@Autowired
	private ApplicationContext applicationContext;

	@Value("${monitoring.realTimeHdb.enabled:false}")
	private boolean realTimeHdbMonitoringEnabled;
	@Value("${monitoring.batchHdb.enabled:false}")
	private boolean batchHdbMonitoringEnabled;
	@Value("${monitoring.hdfs.enabled:false}")
	private boolean hdfsMonitoringEnabled;

	@Value("${monitoring.iotbroker.enabled:false}")
	private boolean sibMonitoringEnabled;
	@Value("${iotbroker.services.baseUrl:null}")
	private String sibBaseUrl;
	@Value("${iotbroker.services.monitoring.path:null}")
	private String sibMonitoringServicePath;

	@Value("${monitoring.apimanager.enabled:false}")
	private boolean apiManagerMonitoringEnabled;
	@Value("${apimanager.services.baseUrl:null}")
	private String apiManagerBaseUrl;
	@Value("${apimanager.services.monitoring.path:null}")
	private String apiManagerMonitoringServicePath;

	@Value("${monitoring.streamingengine.enabled:false}")
	private boolean scriptMonitoringEnabled;
	@Value("${streamingengine.services.baseUrl:null}")
	private String scriptBaseUrl;
	@Value("${streamingengine.services.monitoring.path:null}")
	private String scriptMonitoringServicePath;

	@Value("${monitoring.batchcontainer.enabled:false}")
	private boolean processMonitoringEnabled;
	@Value("${batchcontainer.services.baseUrl:null}")
	private String processBaseUrl;
	@Value("${batchcontainer.services.monitoring.path:null}")
	private String processMonitoringServicePath;

	@Value("${monitoring.controlpanel.enabled:false}")
	private boolean consoleMonitoringEnabled;
	@Value("${controlpanel.services.baseUrl:null}")
	private String consoleBaseUrl;
	@Value("${controlpanel.services.monitoring.path:null}")
	private String consoleMonitoringServicePath;

	@Value("${monitoring.services.connectTimeout:10000}")
	private int connectTimeout;
	@Value("${monitoring.services.readTimeout:10000}")
	private int readTimeout;

	@Autowired
	private RestTemplateFactory restTemplateFactory;
	
	private RestTemplate restTemplate;

	private URI sibMonitoringUrl, apiManagerMonitoringUrl, scriptMonitoringUrl, processMonitoringUrl,
			consoleMonitoringUrl;

	@PostConstruct
	public void init() throws Exception {
		logger.info("Initializing health checks manager...");
		restTemplate = restTemplateFactory.getRestTemplate(connectTimeout, readTimeout);
		logger.info("Parsing monitoring service URLs...");
		
		if(sibMonitoringEnabled){
			sibMonitoringUrl = getMonitoringServiceUrl(SIB, sibBaseUrl, sibMonitoringServicePath);
		}
		
		if(apiManagerMonitoringEnabled){
			apiManagerMonitoringUrl = getMonitoringServiceUrl(API_MANAGER, apiManagerBaseUrl,
					apiManagerMonitoringServicePath);
		}
		
		if(scriptMonitoringEnabled){
			scriptMonitoringUrl = getMonitoringServiceUrl(SCRIPT, scriptBaseUrl,
					scriptMonitoringServicePath);
		}
		
		if(processMonitoringEnabled){
			processMonitoringUrl = getMonitoringServiceUrl(PROCESS, processBaseUrl,
				processMonitoringServicePath);
		}
		
		if(consoleMonitoringEnabled){
			consoleMonitoringUrl = getMonitoringServiceUrl(WEB_CONSOLE, consoleBaseUrl,
				consoleMonitoringServicePath);
		}
		
		if (hdfsMonitoringEnabled) {
			logger.info("Configuring HDFS status checker...");
			hdfsStatusChecker = applicationContext.getBean(HdfsStatusChecker.class);
		}

		logger.info("The health checks manager has been initialized.");
	}

	private URI getMonitoringServiceUrl(String moduleName, String baseUrl, String servicePath)
			throws Exception {
		if (baseUrl == null || baseUrl.isEmpty() || servicePath == null || servicePath.isEmpty()) {
			String errorMessage = String.format(
					"The module '%s' must be monitored, but its base URL, its monitoring service path or both are missing.",
					moduleName);
			logger.error(errorMessage);
			throw new Exception(errorMessage);
		}
		return UriComponentsBuilder.fromHttpUrl(baseUrl).path(servicePath).build().toUri();
	}

	@Override
	public Map<String, Boolean> rtdbHealthCheck() {
		logger.info("Performing RTDBs health check...");
		return rtdbDao.rtdbHealthOk();
	}

	@Override
	public SystemHealthCheckResult cdbHealthCheck() {
		try {
			logger.info("Performing CDB health check...");
			if (cdbTester.testBDC())
				return SystemHealthCheckResult.OK;
		} catch (Throwable e) {
			logger.warn("The CDB health check test has failed. Cause = {}, errorMessage = {}.", e.getCause(),
					e.getMessage());
		}
		return SystemHealthCheckResult.FAIL;
	}

	@Override
	public SystemHealthCheckResult realTimeHdbHealthCheck() {
		if (!realTimeHdbMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		try {
			logger.info("Performing real-time HDB health check...");
			if (impalaDao == null || impalaDao.test())
				return SystemHealthCheckResult.OK;
		} catch (Throwable e) {
			logger.warn("The real-time HDB health check test has failed. Cause = {}, errorMessage = {}.", e.getCause(),
					e.getMessage());
		}
		return SystemHealthCheckResult.FAIL;
	}

	@Override
	public SystemHealthCheckResult batchHdbHealthCheck() {
		if (!batchHdbMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		try {
			logger.info("Performing batch HDB health check...");
			if (hiveDao == null || hiveDao.test())
				return SystemHealthCheckResult.OK;
		} catch (Throwable e) {
			logger.warn("The batch HDB health check test has failed. Cause = {}, errorMessage = {}.", e.getCause(),
					e.getMessage());
		}
		return SystemHealthCheckResult.FAIL;
	}

	@Override
	public SystemHealthCheckResult hdfsHealthCheck() {
		if (!hdfsMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		try {
			logger.info("Performing HDFS health check...");
			if (hdfsStatusChecker.healthCheck())
				return SystemHealthCheckResult.OK;
		} catch (Throwable e) {
			logger.warn("The HDFS health check test has failed. Cause = {}, errorMessage = {}.", e.getCause(),
					e.getMessage());
		}
		return SystemHealthCheckResult.FAIL;
	}

	@Override
	public SystemHealthCheckResult cacheHealthCheck() {
		try {
			logger.info("Performing cache health check...");
			if (cacheService.performHealthCheck())
				return SystemHealthCheckResult.OK;
		} catch (Throwable e) {
			logger.warn("The cache health check test has failed. Cause = {}, errorMessage = {}.", e.getCause(),
					e.getMessage());
		}
		return SystemHealthCheckResult.FAIL;
	}

	private SystemHealthCheckResult invokeMonitoringService(URI serviceUrl) {
		logger.info("Invoking monitoring service. URL = {}.", serviceUrl);
		try {
			restTemplate.getForObject(serviceUrl, String.class);
			logger.info("The monitoring service has been invoked. URL = {}.", serviceUrl);
			return SystemHealthCheckResult.OK;
		} catch (RestClientException e) {
			logger.info("Unable to invoke monitoring service. URL = {}, cause = {}, errorMessage = {}.", serviceUrl,
					e.getCause(), e.getMessage());
			return SystemHealthCheckResult.FAIL;
		}
	}

	@Override
	public SystemHealthCheckResult scriptHttpConnectivityCheck() {
		if (!scriptMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		else
			return invokeMonitoringService(scriptMonitoringUrl);
	}

	@Override
	public SystemHealthCheckResult processHttpConnectivityCheck() {
		if (!processMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		else
			return invokeMonitoringService(processMonitoringUrl);
	}

	@Override
	public SystemHealthCheckResult sibHttpConnectivityCheck() {
		if (!sibMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		else
			return invokeMonitoringService(sibMonitoringUrl);
	}

	@Override
	public SystemHealthCheckResult apiManagerHttpConnectivityCheck() {
		if (!apiManagerMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		else
			return invokeMonitoringService(apiManagerMonitoringUrl);
	}

	@Override
	public SystemHealthCheckResult consoleHttpConnectivityCheck() {
		if (!consoleMonitoringEnabled)
			return SystemHealthCheckResult.DISABLED;
		else
			return invokeMonitoringService(consoleMonitoringUrl);
	}

	@Override
	public List<BatchSchedulerHealthCheckResult> batchSchedulersHealthCheck() {
		logger.info("Checking status of Quartz batch schedulers...");
		List<BatchSchedulerHealthCheckResult> result = new ArrayList<BatchSchedulerHealthCheckResult>();
		Map<String, Sofia2BatchScheduler> batchSchedulers = applicationContext
				.getBeansOfType(Sofia2BatchScheduler.class);
		for (Sofia2BatchScheduler scheduler : batchSchedulers.values()) {
			BatchSchedulerHealthCheckResult healthCheckResult = new BatchSchedulerHealthCheckResult();
			healthCheckResult.setSchedulerName(scheduler.getSchedulerName());
			healthCheckResult.setJobGroup(scheduler.getJobGroup());
			healthCheckResult.setRunning(scheduler.isRunning());
			try {
				healthCheckResult.setScheduledJobs(scheduler.countScheduledJobs());
			} catch (Sofia2BatchSchedulerException e) {
				logger.info(
						"Unable to count scheduled jobs. SchedulerName = {}, jobGroup = {}, cause = {}, errorMessage = {}.",
						scheduler.getSchedulerName(), scheduler.getJobGroup(), e.getCause(), e.getMessage());
			}
			result.add(healthCheckResult);
		}
		return result;
	}
	
	@Override
	public List<GatewayStatusDto> sibGatewaysHealthCheck() {
		List<GatewayStatusDto> result = new ArrayList<GatewayStatusDto>();
		Map<String, MonitoredGateway> monitoredGateways = applicationContext.getBeansOfType(MonitoredGateway.class);
		logger.info("Checking status of monitored gateways...");
		for (MonitoredGateway monitoredGateway : monitoredGateways.values()) {
			result.add(monitoredGateway.getStatus());
		}
		return result;
	}
}