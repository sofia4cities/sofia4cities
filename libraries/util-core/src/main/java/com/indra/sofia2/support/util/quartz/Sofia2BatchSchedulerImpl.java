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
 * 2013 - 2015  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.quartz;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import com.indra.sofia2.support.util.quartz.exceptions.Sofia2BatchSchedulerException;

public final class Sofia2BatchSchedulerImpl implements Sofia2BatchScheduler {

	private static final Logger log = LoggerFactory.getLogger(Sofia2BatchSchedulerImpl.class);
	public static final String TRIGGER_SUFFIX = "_Trigger";

	private SchedulerFactoryBean sfb;
	private SpringBeanJobFactory beanJobFactory;
	private Sofia2BatchSchedulerConfiguration configuration;
	private QuartzSchedulerListener quartzListener;
	
	@Autowired
	private ApplicationContext applicationContext;

	public Sofia2BatchSchedulerConfiguration getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(Sofia2BatchSchedulerConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public boolean isCleanUpExistingJobs() {
		return configuration.isCleanUpExistingJobs();
	}

	@Override
	public String getSchedulerName() {
		return configuration.getSchedulerName();
	}

	@Override
	public String getJobGroup() {
		return configuration.getJobGroup();
	}

	@Override
	public void enableAutowiringInQuartzJobs(ApplicationContext applicationContext) {
		log.info("Enabling Spring autowiring in Quartz jobs...");
		AutowiringSpringBeanJobFactory beanJobFactory = new AutowiringSpringBeanJobFactory();
		beanJobFactory.setApplicationContext(applicationContext);
		this.beanJobFactory = beanJobFactory;
	}
	
	@PostConstruct
	public void init() throws Sofia2BatchSchedulerException {
		log.info("Initializing Quartz scheduler. SchedulerName = {}.", configuration.getSchedulerName());
		try {
			log.info("Loading Quartz configuration of the scheduler. SchedulerName = {}.", this.configuration.getSchedulerName());
			this.quartzListener = new QuartzSchedulerListener();
			this.sfb = new SchedulerFactoryBean();
			if (this.beanJobFactory != null) {
				this.sfb.setJobFactory(this.beanJobFactory);
			}
			this.sfb.setBeanName(configuration.getSchedulerName());
			this.sfb.setApplicationContext(applicationContext);
			sfb.setQuartzProperties(configuration.getQuartzProperties());
			sfb.setStartupDelay(configuration.getStartupDelay());
			sfb.setSchedulerListeners(this.quartzListener);
			sfb.afterPropertiesSet();
			if (configuration.isCleanUpExistingJobs())
				unscheduleExistingQuartzJobs();
		} catch (Exception e) {
			log.error("Unable to initialize Quartz scheduler. SchedulerName = {}, cause = {}, errorMessage = {}.",
					configuration.getSchedulerName(), e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("Unable to initialize Quartz scheduler", e);
		}
	}
	
	public void setSchedulerListeners(SchedulerListener... schedulerListeners) {
		this.sfb.setSchedulerListeners(schedulerListeners);
	}

	@PreDestroy
	public void destroy() {
		try {
			log.info("Destroying Quartz scheduler. SchedulerName = {}.", configuration.getSchedulerName());
			if (!sfb.getScheduler().isShutdown()) {
				// If the underlying Quartz scheduler is shared, we must
				// stop it once.
				sfb.getScheduler().shutdown(false);
			}
			log.info("The Quartz scheduler has been destroyed successfully. SchedulerName = {}.", configuration.getSchedulerName());
		} catch (Exception e) {
			log.error("Unable to destroy Quartz scheduler. SchedulerName = {}, cause = {}, errorMessage = {}.",
					configuration.getSchedulerName(), e.getCause(), e.getMessage());
		}
	}

	@Override
	public void start() throws Sofia2BatchSchedulerException {
		if (!sfb.isRunning()) {
			try {
				log.info("Starting Quartz scheduler. SchedulerName = {}.", configuration.getSchedulerName());
				sfb.start();
			} catch (Exception e) {
				log.error("Unable to start Quartz scheduler. SchedulerName = {}, cause = {}, errorMessage = {}.",
						configuration.getSchedulerName(), e.getCause(), e.getMessage());
				throw new Sofia2BatchSchedulerException("Unable to start Quartz scheduler", e);
			}
		}
	}

	@Override
	public void stop() throws Sofia2BatchSchedulerException {
		if (sfb.isRunning()) {
			log.info("Stopping Quartz scheduler. SchedulerName = {}.", configuration.getSchedulerName());
			try {
				sfb.stop();
			} catch (SchedulingException e) {
				log.error("Unable to stop Quartz scheduler. SchedulerName = {}, cause = {}, errorMessage = {}.",
						configuration.getSchedulerName(), e.getCause(), e.getMessage());
				throw new Sofia2BatchSchedulerException("Unable to stop Quartz scheduler", e);
			}
		}
	}

	@Override
	public boolean isRunning() {
		try {
			return this.sfb.isRunning();
		} catch (SchedulingException e) {
			log.error(
					"Unable to check the status of the Quartz scheduler. SchedulerName = {}, cause = {}, errorMessage = {}.",
					configuration.getSchedulerName(), e.getCause(), e.getMessage());
			return false;
		}
	}
	
	@Override
	public int countScheduledJobs() throws Sofia2BatchSchedulerException {
		Scheduler scheduler = sfb.getObject();
		try {
			log.info("Counting scheduled jobs. SchedulerName = {}, jobGroup = {}.", this.configuration.getSchedulerName(), configuration.getJobGroup());
			return scheduler.getJobKeys(GroupMatcher.jobGroupEquals(configuration.getJobGroup())).size();
		} catch (SchedulerException e) {
			log.error(
					"Unable to count scheduled jobs. SchedulerName = {}, jobGroup = {}, cause = {}, errorMessage = {}.",
					this.configuration.getSchedulerName(), configuration.getJobGroup(), e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("Unable to count scheduled jobs", e);
		}
	}

	private void unscheduleExistingQuartzJobs() throws Sofia2BatchSchedulerException {
		log.info("Unscheduling existing Quartz jobs. SchedulerName = {}.", this.configuration.getSchedulerName());
		Scheduler scheduler = sfb.getObject();
		Set<JobKey> jobKeys = null;
		try {
			jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(configuration.getJobGroup()));
		} catch (SchedulerException e) {
			log.error("Unable to retrieve job keys. SchedulerName = {}, jobGroup = {}, cause = {}, errorMessage = {}.",
					this.configuration.getSchedulerName(), configuration.getJobGroup(), e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("The job keys couldn't be retrieved");
		}
		for (JobKey jobKey : jobKeys) {
			if (jobKey.getGroup().equals(configuration.getJobGroup())) {
				List<? extends Trigger> triggers = null;
				try {
					triggers = scheduler.getTriggersOfJob(jobKey);
				} catch (SchedulerException e) {
					log.error(
							"Unable to retrieve triggers. SchedulerName = {}, jobGroup = {}, cause = {}, errorMessage = {}.",
							this.configuration.getSchedulerName(), configuration.getJobGroup(), e.getCause(), e.getMessage());
					throw new Sofia2BatchSchedulerException("The job keys couldn't be retrieved");
				}
				try {
					for (Trigger trigger : triggers) {
						scheduler.unscheduleJob(trigger.getKey());
					}
					scheduler.deleteJob(jobKey);
				} catch (SchedulerException e) {
					log.error(
							"Unable to remove Quartz job. SchedulerName = {}, jobGroup = {}, cause = {}, errorMessage = {}.",
							this.configuration.getSchedulerName(), configuration.getJobGroup(), e.getCause(), e.getMessage());
					throw new Sofia2BatchSchedulerException("The jobs couln't be removed");
				}
			}
		}
	}

	private JobDetailImpl buildQuartzJob(Sofia2BatchJobSpecification jobSpecification) {
		log.debug("Creating Quartz job. SchedulerName = {}, jobName = {}.", this.configuration.getSchedulerName(),
				jobSpecification.getJobName());
		JobDetailImpl jd = new JobDetailImpl();
		jd.setJobClass(jobSpecification.getJobClass());
		jd.setDurability(configuration.isUseDurableJobs());
		jd.setName(jobSpecification.getJobName());
		jd.setGroup(configuration.getJobGroup());
		jd.setRequestsRecovery(configuration.isRequestRecovery());
		/* Do not change this unless you know what you're doing */
		JobDataMap binding = new JobDataMap();
		binding.putAll(jobSpecification.getJobContext());
		jd.setJobDataMap(binding);
		return jd;
	}

	@Override
	public void scheduleJob(Sofia2BatchJobSpecification jobSpecification) throws Sofia2BatchSchedulerException {
		List<Sofia2BatchJobSpecification> jobSpecifications = new ArrayList<Sofia2BatchJobSpecification>();
		jobSpecifications.add(jobSpecification);
		scheduleJobs(jobSpecifications);
	}

	@Override
	public void scheduleJobs(List<Sofia2BatchJobSpecification> jobSpecifications) throws Sofia2BatchSchedulerException {
		List<String> jobNames = new ArrayList<String>(jobSpecifications.size());
		for (Sofia2BatchJobSpecification jobSpecification : jobSpecifications) {
			jobNames.add(jobSpecification.getJobName());
		}

		log.info("Building Quartz triggers and job details. SchedulerName = {}, jobNames = {}.", this.configuration.getSchedulerName(),
				jobNames);
		Scheduler scheduler = sfb.getObject();
		Map<JobDetail, Set<? extends Trigger>> triggersAndJobs = new HashMap<JobDetail, Set<? extends Trigger>>();

		for (Sofia2BatchJobSpecification jobSpecification : jobSpecifications) {
			JobDetailImpl jobDetail = buildQuartzJob(jobSpecification);
			triggersAndJobs.put(jobDetail, buildTriggers(jobSpecification, jobDetail));
		}

		log.info("Scheduling Quartz jobs. SchedulerName = {}, jobNames = {}.", this.configuration.getSchedulerName(), jobNames);
		try {
			scheduler.scheduleJobs(triggersAndJobs, true);
		} catch (SchedulerException e) {
			log.error(
					"Unable to schedule Quartz jobs. SchedulerName = {}, jobNames = {}, cause = {}, errorMessage = {}.",
					configuration.getSchedulerName(), jobNames, e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("Unable to schedule Quartz jobs", e);
		}

		log.info("The Quartz jobs have been scheduled successfully. SchedulerName = {}, jobNames = {}.",
				this.configuration.getSchedulerName(), jobNames);
	}

	@Override
	public boolean unscheduleJob(String jobName) throws Sofia2BatchSchedulerException {
		return unscheduleJobsByJobIdLike(jobName);
	}

	@Override
	public boolean unscheduleJobsByJobIdLike(String regex) throws Sofia2BatchSchedulerException {
		log.info("Removing Quartz job. SchedulerName = {}, jobNameRegex = {}.", this.configuration.getSchedulerName(), regex);
		Scheduler scheduler = sfb.getObject();
		Pattern compiledRegex = Pattern.compile(regex);
		List<JobKey> matchingJobKeys = new ArrayList<JobKey>();
		try {
			Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(configuration.getJobGroup()));
			for (JobKey key : jobKeys) {
				if (compiledRegex.matcher(key.getName()).matches()) {
					matchingJobKeys.add(key);
				}
			}
		} catch (SchedulerException e) {
			log.error("Unable to retrieve job keys. SchedulerName = {}, regex = {}, cause = {}, errorMessage = {}.",
					this.configuration.getSchedulerName(), regex, e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("The job keys couldn't be retrieved");
		}
		if (!matchingJobKeys.isEmpty())
			return unscheduleJobs(matchingJobKeys);
		else
			return false;
	}

	private boolean unscheduleJobs(List<JobKey> jobKeys) throws Sofia2BatchSchedulerException {
		Scheduler scheduler = sfb.getObject();
		List<String> jobNames = new ArrayList<String>(jobKeys.size());
		for (JobKey jobKey : jobKeys) {
			jobNames.add(jobKey.getName());
		}
		try {
			scheduler.deleteJobs(jobKeys);
			log.info("The Quartz jobs have been removed. SchedulerName = {}, jobNames = {}.", this.configuration.getSchedulerName(),
					jobNames);
			return true;
		} catch (SchedulerException e) {
			log.error(
					"The Quartz job couldn't be removed.  SchedulerName = {}, jobNames = {}, cause = {}, errorMessage = {}.",
					this.configuration.getSchedulerName(), jobNames, e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("The Quartz job couldn't be removed");
		}
	}

	private Set<Trigger> buildTriggers(Sofia2BatchJobSpecification jobSpecification, JobDetail jobDetail)
			throws Sofia2BatchSchedulerException {
		Set<Trigger> result = new HashSet<Trigger>(1);
		if (jobSpecification.isCronBased()) {
			CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
			triggerFactory.setBeanName(jobSpecification.getJobName() + TRIGGER_SUFFIX);
			triggerFactory.setCronExpression(jobSpecification.getCronExpression());
			if (jobSpecification.getTimezone() != null) {
				triggerFactory.setTimeZone(jobSpecification.getTimezone());
			}
			triggerFactory.setJobDetail(jobDetail);
			try {
				triggerFactory.afterPropertiesSet();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.add(triggerFactory.getObject());
		} else {
			SimpleTriggerFactoryBean triggerFactory = new SimpleTriggerFactoryBean();
			triggerFactory.setBeanName(jobSpecification.getJobName() + TRIGGER_SUFFIX);
			triggerFactory.setJobDetail(jobDetail);
			if(jobSpecification.getStartDelay()<0){
				triggerFactory.setStartDelay(0);
			}else{
				triggerFactory.setStartDelay(jobSpecification.getStartDelay());
			}
			triggerFactory.setRepeatInterval(jobSpecification.getRepeatInterval());
			triggerFactory.setRepeatCount(jobSpecification.getRepeatCount());
			try {
				triggerFactory.afterPropertiesSet();
			} catch (Throwable e) {
				log.error(
						"Unable to build periodic Quartz trigger. SchedulerName = {}, jobName = {}, cause = {}, errorMessage = {}.",
						configuration.getSchedulerName(), jobSpecification.getJobName(), e.getCause(), e.getMessage());
				throw new Sofia2BatchSchedulerException("Unable to build periodic Quartz trigger.", e);
			}
			result.add(triggerFactory.getObject());
		}
		return result;
	}

	@Override
	public void clean() throws Sofia2BatchSchedulerException {
		this.unscheduleExistingQuartzJobs();
	}

	@Override
	public Set<String> synchronizeQuartzJobs(Set<String> validJobNames) throws Sofia2BatchSchedulerException {
		log.info("Synchronizing Quartz jobs of the group. SchedulerName = {}, jobGroup = {}.", this.configuration.getSchedulerName(),
				configuration.getJobGroup());
		List<JobKey> invalidJobKeys = new ArrayList<JobKey>();
		Set<String> existingJobNames = new HashSet<String>();
		try {
			for (JobKey jobKey : sfb.getObject().getJobKeys(GroupMatcher.jobGroupEquals(configuration.getJobGroup()))) {
				if (validJobNames.contains(jobKey.getName())) {
					existingJobNames.add(jobKey.getName());
				} else {
					invalidJobKeys.add(jobKey);
				}
			}
			unscheduleJobs(invalidJobKeys);
		} catch (SchedulerException e) {
			log.error("Unable to retrieve job keys. SchedulerName = {}, jobGroup = {}, cause = {}, errorMessage = {}.",
					this.configuration.getSchedulerName(), configuration.getJobGroup(), e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("The job keys couldn't be retrieved");
		}
		return existingJobNames;
	}

	@Override
	public void pauseTriggers() throws Sofia2BatchSchedulerException {
		log.info("Pausing all the jobs of the group. SchedulerName = {}, jobGroup = {}.", this.configuration.getSchedulerName(),
				configuration.getJobGroup());
		GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEquals(configuration.getJobGroup());
		try {
			sfb.getScheduler().pauseJobs(groupMatcher);
		} catch (SchedulerException e) {
			log.error("Unable to pause Quartz jobs. Cause = {}, errorMessage = {}.", e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("Unable to pause Quartz triggers", e);
		}
	}

	@Override
	public void resumeTriggers() throws Sofia2BatchSchedulerException {
		log.info("Resuming all the jobs of the group. SchedulerName = {}, jobGroup = {}.", this.configuration.getSchedulerName(),
				configuration.getJobGroup());
		GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEquals(configuration.getJobGroup());
		try {
			sfb.getScheduler().resumeJobs(groupMatcher);
		} catch (SchedulerException e) {
			log.error("Unable to resume Quartz jobs. Cause = {}, errorMessage = {}.", e.getCause(), e.getMessage());
			throw new Sofia2BatchSchedulerException("Unable to resume Quartz triggers", e);
		}
	}
	
	@Override
	public void addSchedulerListener(Sofia2BatchSchedulerListener listener) {
		this.quartzListener.addListener(listener);
	}

	@Override
	public void removeSchedulerListener(Sofia2BatchSchedulerListener listener) {
		this.quartzListener.removeListener(listener);
	}
}