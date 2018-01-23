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
package com.indra.sofia2.support.util.quartz;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Sofia2BatchSchedulerConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Sofia2BatchSchedulerConfiguration.class);
	private static final String QUARTZ_INSTANCE_NAME_PROPERTY = "org.quartz.scheduler.instanceName";
	private static final String QUARTZ_INSTANCE_ID_PROPERTY = "org.quartz.scheduler.instanceId";
	private static final String QUARTZ_THREAD_POOL_CLASS_PROPERTY = "org.quartz.threadPool.class";
	private static final String QUARTZ_THREAD_POOL_SIZE_PROPERTY = "org.quartz.threadPool.threadCount";
	private static final String QUARTZ_THREAD_PRIORITY_PROPERTY = "org.quartz.threadPool.threadPriority";
	private static final String QUARTZ_JOBSTORE_MISFIRE_THRESHOLD_PROPERTY = "org.quartz.jobStore.misfireThreshold";
	private static final String QUARTZ_JOBSTORE_CLASS_PROPERTY = "org.quartz.jobStore.class";
	private static final String QUARTZ_JOBSTORE_DRIVER_DELEGATE_CLASS_PROPERTY = "org.quartz.jobStore.driverDelegateClass";
	private static final String QUARTZ_JOBSTORE_USE_PROPERTIES_PROPERTY = "org.quartz.jobStore.useProperties";
	private static final String QUARTZ_JOBSTORE_DATASOURCE_NAME_PROPERTY = "org.quartz.jobStore.dataSource";
	private static final String QUARTZ_JOBSTORE_TABLE_PREFIX_PROPERTY = "org.quartz.jobStore.tablePrefix";
	private static final String QUARTZ_JOBSTORE_IS_CLUSTERED_PROPERTY = "org.quartz.jobStore.isClustered";
	private static final String QUARTZ_JOBSTORE_CHECKIN_INTERVAL_PROPERTY = "org.quartz.jobStore.clusterCheckinInterval";
	private static final String QUARTZ_DATASOURCE_DRIVER_CLASS_PROPERTY = "org.quartz.dataSource.quartzDs.driver";
	private static final String QUARTZ_DATASOURCE_JNDI_URL_PROPERTY = "org.quartz.dataSource.quartzdb.jndiURL";
	private static final String QUARTZ_DATASOURCE_JDBC_URL_PROPERTY = "org.quartz.dataSource.quartzDs.URL";
	private static final String QUARTZ_DATASOURCE_USERNAME_PROPERTY = "org.quartz.dataSource.quartzDs.user";
	private static final String QUARTZ_DATASOURCE_PASSWORD_PROPERTY = "org.quartz.dataSource.quartzDs.password";
	private static final String QUARTZ_DATASOURCE_MAX_CONENCTIONS_PROPERTY = "org.quartz.dataSource.quartzDs.maxConnections";
	private Properties quartzProperties;

	public Sofia2BatchSchedulerConfiguration() {
		quartzProperties = new Properties();
		quartzProperties.setProperty(QUARTZ_INSTANCE_ID_PROPERTY, "AUTO");
		quartzProperties.setProperty(QUARTZ_THREAD_POOL_CLASS_PROPERTY, "org.quartz.simpl.SimpleThreadPool");
		quartzProperties.setProperty(QUARTZ_THREAD_POOL_SIZE_PROPERTY, "3");
		quartzProperties.setProperty(QUARTZ_THREAD_PRIORITY_PROPERTY, "5");
		quartzProperties.setProperty(QUARTZ_JOBSTORE_MISFIRE_THRESHOLD_PROPERTY, "60000");
		quartzProperties.setProperty(QUARTZ_JOBSTORE_CLASS_PROPERTY, "org.quartz.impl.jdbcjobstore.JobStoreTX");
		quartzProperties.setProperty(QUARTZ_JOBSTORE_USE_PROPERTIES_PROPERTY, "false");
		quartzProperties.setProperty(QUARTZ_JOBSTORE_DATASOURCE_NAME_PROPERTY, "quartzDs");
		quartzProperties.setProperty(QUARTZ_JOBSTORE_TABLE_PREFIX_PROPERTY, "QRTZ_");
		quartzProperties.setProperty(QUARTZ_JOBSTORE_IS_CLUSTERED_PROPERTY, "true");
		quartzProperties.setProperty(QUARTZ_JOBSTORE_CHECKIN_INTERVAL_PROPERTY, "20000");
	}

	@Value("${database.quartz.jndi.url:#{null}}")
	private String jndiUrl;

	@Value("${database.driverClassName}")
	private String jdbcDriverClass;
	@Value("${database.quartz.jdbc.url:#{null}}")
	private String jdbcUrl;
	@Value("${database.quartz.jdbc.username:#{null}}")
	private String jdbcUsername;
	@Value("${database.quartz.jdbc.password:#{null}}")
	private String jdbcPassword;
	@Value("${database.quartz.maxConnections:5}")
	private int maxConnections;
	
	private String schedulerName;
	private String jobGroup;
	private boolean cleanUpExistingJobs;
	private boolean useDurableJobs;
	private boolean requestRecovery;
	private int startupDelay;

	@PostConstruct
	public void init() {
		logger.info("Loading configuration common to all Sofia2 batch schedulers...");
		if (jndiUrl != null) {
			logger.info("Configuring batch scheduler in JNDI mode...");
			configureJndiDatasource(jdbcDriverClass, jndiUrl, maxConnections);
		} else {
			if (jdbcUrl == null || jdbcUsername == null || jdbcPassword == null) {
				throw new IllegalArgumentException("The JDBC URL, the username and the password are required");
			}
			configureJdbcDatasource(jdbcDriverClass, jdbcUrl, jdbcUsername, jdbcPassword, maxConnections);
		}
		cleanUpExistingJobs = false;
		useDurableJobs = true;
		requestRecovery = true;
		startupDelay = 60;
	}

	Properties getQuartzProperties() {
		return quartzProperties;
	}

	public void setInstanceName(String instanceName) {
		quartzProperties.setProperty(QUARTZ_INSTANCE_NAME_PROPERTY, instanceName);
	}

	public void setThreadPoolSize(int size) {
		quartzProperties.setProperty(QUARTZ_THREAD_POOL_SIZE_PROPERTY, Integer.toString(size));
	}

	public void configureJdbcDatasource(String driverClass, String jdbcUrl, String username, String password,
			int maxConnections) {
		configureJobStoreDelegate(driverClass);
		quartzProperties.put(QUARTZ_DATASOURCE_DRIVER_CLASS_PROPERTY, driverClass);
		quartzProperties.put(QUARTZ_DATASOURCE_JDBC_URL_PROPERTY, jdbcUrl);
		quartzProperties.put(QUARTZ_DATASOURCE_USERNAME_PROPERTY, username);
		quartzProperties.put(QUARTZ_DATASOURCE_PASSWORD_PROPERTY, password);
		quartzProperties.put(QUARTZ_DATASOURCE_MAX_CONENCTIONS_PROPERTY, maxConnections);
	}

	public void configureJndiDatasource(String driverClass, String jndiUrl, int maxConnections) {
		configureJobStoreDelegate(driverClass);
		quartzProperties.put(QUARTZ_DATASOURCE_DRIVER_CLASS_PROPERTY, driverClass);
		quartzProperties.put(QUARTZ_DATASOURCE_JNDI_URL_PROPERTY, jndiUrl);
		quartzProperties.put(QUARTZ_DATASOURCE_MAX_CONENCTIONS_PROPERTY, maxConnections);
	}
	
	private void configureJobStoreDelegate(String driverClass) {
		if (driverClass.toLowerCase().contains("postgre"))
			quartzProperties.setProperty(QUARTZ_JOBSTORE_DRIVER_DELEGATE_CLASS_PROPERTY,
					"org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
		else
			quartzProperties.setProperty(QUARTZ_JOBSTORE_DRIVER_DELEGATE_CLASS_PROPERTY,
					"org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public boolean isCleanUpExistingJobs() {
		return cleanUpExistingJobs;
	}

	public void setCleanUpExistingJobs(boolean cleanUpExistingJobs) {
		this.cleanUpExistingJobs = cleanUpExistingJobs;
	}

	public boolean isUseDurableJobs() {
		return useDurableJobs;
	}

	public void setUseDurableJobs(boolean useDurableJobs) {
		this.useDurableJobs = useDurableJobs;
	}

	public boolean isRequestRecovery() {
		return requestRecovery;
	}

	public void setRequestRecovery(boolean requestRecovery) {
		this.requestRecovery = requestRecovery;
	}

	public int getStartupDelay() {
		return startupDelay;
	}

	public void setStartupDelay(int startupDelay) {
		this.startupDelay = startupDelay;
	}

}
