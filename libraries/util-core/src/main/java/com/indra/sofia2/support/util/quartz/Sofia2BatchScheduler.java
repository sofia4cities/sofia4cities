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

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import com.indra.sofia2.support.util.quartz.exceptions.Sofia2BatchSchedulerException;

/**
 * Interface common to all the quartz batch job schedulers.
 * 
 * @author lbarrios
 *
 */
public interface Sofia2BatchScheduler {
	
	/**
	 * Returns the name of the scheduler
	 * @return
	 */
	public String getSchedulerName();
	
	/**
	 * Returns the job group of the scheduler
	 * @return
	 */
	public String getJobGroup();

	/**
	 * Starts the scheduler
	 * 
	 * @throws Exception
	 */
	public void start() throws Sofia2BatchSchedulerException;

	/**
	 * Stops the scheduler
	 * 
	 * @throws Exception
	 */
	public void stop() throws Sofia2BatchSchedulerException;

	/**
	 * Deletes all the scheduled jobs.
	 * 
	 * @throws Exception
	 */
	public void clean() throws Sofia2BatchSchedulerException;

	/**
	 * Indicates if the scheduler is running.
	 * 
	 * @return
	 */
	public boolean isRunning();
	
	/**
	 * Returns the number of scheduled jobs.
	 * @return
	 */
	public int countScheduledJobs() throws Sofia2BatchSchedulerException;

	/**
	 * Schedules a job using the given specification
	 * 
	 * @param jobSpecification
	 */
	public void scheduleJob(Sofia2BatchJobSpecification jobSpecification) throws Sofia2BatchSchedulerException;

	/**
	 * Schedules several jobs using the given specifications
	 * 
	 * @param jobSpecifications
	 */
	public void scheduleJobs(List<Sofia2BatchJobSpecification> jobSpecifications) throws Sofia2BatchSchedulerException;

	/**
	 * Removes a quartz job
	 * 
	 * @param jobName
	 * @return A boolean value that indicates if the job was removed.
	 */
	public boolean unscheduleJob(String jobName) throws Sofia2BatchSchedulerException;

	/**
	 * Removes the quartz jobs whose names match the given regular expression
	 * 
	 * @param regex
	 * @return
	 * @throws Sofia2BatchSchedulerException
	 */
	public boolean unscheduleJobsByJobIdLike(String regex) throws Sofia2BatchSchedulerException;

	/**
	 * Deletes all the quartz jobs that are not listed in validJobNames
	 * 
	 * @param validJobIds
	 * @return A set containing the existing job names
	 */
	public Set<String> synchronizeQuartzJobs(Set<String> validJobNames) throws Sofia2BatchSchedulerException;

	/**
	 * Pauses all the Quartz triggers.
	 * 
	 * @warning This method won't pause the jobs that are currently running. It
	 *          will only prevent the execution of new jobs.
	 */
	public void pauseTriggers() throws Sofia2BatchSchedulerException;

	/**
	 * Resumes all the Quartz triggers.
	 * 
	 * @warning This method can start several executions of each job, according
	 *          to its triggers.
	 */
	public void resumeTriggers() throws Sofia2BatchSchedulerException;

	/**
	 * Indicates whether all the existing jobs will be deleted before the
	 * scheduler starts.
	 * 
	 * @return
	 */
	public boolean isCleanUpExistingJobs();

	/**
	 * Enables autowiring in the Quartz jobs
	 * 
	 * @param applicationContext
	 */
	public void enableAutowiringInQuartzJobs(ApplicationContext applicationContext);
	
	/**
	 * Registers a scheduler listener
	 * @param listener
	 */
	public void addSchedulerListener(Sofia2BatchSchedulerListener listener);
	
	/**
	 * Unregisters a scheduler listener
	 * @param listener
	 */
	public void removeSchedulerListener(Sofia2BatchSchedulerListener listener);
}
