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
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.quartz.CronExpression;
import org.quartz.Job;

import com.indra.sofia2.support.util.quartz.exceptions.Sofia2BatchSchedulerException;

public class Sofia2BatchJobSpecification {
	private String jobName;
	private Class<? extends Job> jobClass;
	private CronExpression cronExpression;
	private TimeZone timezone;
	private long startDelay;
	private long repeatInterval;
	private int repeatCount;
	private Map<String, Serializable> jobContext;
	private boolean cronBased;

	private Sofia2BatchJobSpecification(String jobName, Class<? extends Job> jobClass,
			Map<String, Serializable> jobContext) {
		this.jobName = jobName;
		this.jobClass = jobClass;
		this.jobContext = jobContext;
	}

	/**
	 * Creates the specification of a periodic job.
	 * 
	 * @param jobName
	 * @param jobClass
	 * @param startDelay
	 * @param repeatInterval
	 * @param repeatCount
	 * @param jobContext
	 */
	public Sofia2BatchJobSpecification(String jobName, Class<? extends Job> jobClass, long startDelay,
			long repeatInterval, int repeatCount, Map<String, Serializable> jobContext) {
		this(jobName, jobClass, jobContext);
		this.cronBased = false;
		this.startDelay = startDelay;
		this.repeatInterval = repeatInterval;
		this.repeatCount = repeatCount;
	}

	/**
	 * Creates the specification of a CRON-based job using the given timezone.
	 * 
	 * @param jobName
	 * @param jobClass
	 * @param cronExpression
	 * @param timezone
	 * @param jobContext
	 * @throws Sofia2BatchSchedulerException
	 */
	public Sofia2BatchJobSpecification(String jobName, Class<? extends Job> jobClass, String cronExpression,
			TimeZone timezone, Map<String, Serializable> jobContext) throws Sofia2BatchSchedulerException {
		this(jobName, jobClass, jobContext);
		this.cronBased = true;
		try {
			
			this.cronExpression = new CronExpression(cronExpression);
			this.cronExpression.setTimeZone(timezone);
		} catch (ParseException e) {
			throw new Sofia2BatchSchedulerException(
					String.format("The CRON expression '%s' is invalid", cronExpression), e);
		}
		/*
		 * We must get the next fire date considering the timezone. If we don't, we will get
		 * false positives!
		 */
		if (this.cronExpression.getNextValidTimeAfter(new Date()) == null)
			throw new Sofia2BatchSchedulerException(
					String.format("The CRON expression '%s' will never fire", cronExpression));
		this.timezone = timezone;
	}

	/**
	 * Creates the specification of a CRON-based job using the timezone of the
	 * server.
	 * 
	 * @param jobName
	 * @param jobClass
	 * @param cronExpression
	 * @param jobContext
	 * @throws Sofia2BatchSchedulerException
	 */
	public Sofia2BatchJobSpecification(String jobName, Class<? extends Job> jobClass, String cronExpression,
			Map<String, Serializable> jobContext) throws Sofia2BatchSchedulerException {
		this(jobName, jobClass, cronExpression, null, jobContext);
	}

	public String getJobName() {
		return jobName;
	}

	public Class<? extends Job> getJobClass() {
		return jobClass;
	}

	public String getCronExpression() {
		return cronExpression.getCronExpression();
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public long getStartDelay() {
		return startDelay;
	}

	public long getRepeatInterval() {
		return repeatInterval;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public Map<String, Serializable> getJobContext() {
		return jobContext;
	}

	public boolean isCronBased() {
		return cronBased;
	}
}
