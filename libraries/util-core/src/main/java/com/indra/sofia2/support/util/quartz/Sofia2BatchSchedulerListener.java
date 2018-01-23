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

public abstract class Sofia2BatchSchedulerListener {
	
	/**
	 * Returns the unique identifier of this listener.
	 * @return
	 */
	public abstract String getId();

	/**
	 * This method will be invoked whenever a job is scheduled
	 * @param jobId
	 */
	public void onJobScheduled(String jobId) {}

	/**
	 * This method will be invoked whenever a job is unscheduled
	 * @param jobId
	 */
	public void onJobUnscheduled(String jobId) {}

	/**
	 * This method will be invoked whenever a trigger is finalized.
	 * @param jobId
	 */
	public void onTriggerFinalized(String jobId) {}

	/**
	 * This method will be invoked whenever a trigger is paused.
	 * @param jobId
	 */
	public void onTriggerPaused(String jobId) {}

	/**
	 * This method will be invoked when all triggers are paused
	 * @param jobId
	 */
	public void onTriggersPaused() {}

	/**
	 * This method will be invoked whenever a trigger is resumed.
	 * @param jobId
	 */
	public void onTriggerResumed(String jobId) {}

	/**
	 * This method will be invoked when all triggers are resumed
	 * @param jobId
	 */
	public void onTriggersResumed() {}

	/**
	 * This method will be invoked whenever a job is added
	 * @param jobId
	 */
	public void onJobAdded(String jobId) {}

	/**
	 * This method will be invoked whenever a job is deleted
	 * @param jobId
	 */
	public void onJobDeleted(String jobId) {}

	/**
	 * This method will be invoked whenever a job is paused
	 * @param jobId
	 */
	public void onJobPaused(String jobId) {}

	/**
	 * This method will be invoked when all jobs are paused
	 * @param jobId
	 */
	public void onJobsPaused() {}

	/**
	 * This method will be invoked whenever a job is resumed
	 * @param jobId
	 */
	public void onJobResumed(String jobId) {}

	/**
	 * This method will be invoked when all jobs are resumed
	 * @param jobId
	 */
	public void onJobsResumed() {}
}
