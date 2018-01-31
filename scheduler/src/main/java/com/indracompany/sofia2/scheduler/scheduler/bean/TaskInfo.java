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
package com.indracompany.sofia2.scheduler.scheduler.bean;

import java.util.Map;

import com.indracompany.sofia2.scheduler.SchedulerType;

public class TaskInfo {
		
	private String jobName;
	private String username;
	private String cronExpression;	
	private SchedulerType schedulerType;
	private Map data;
	private boolean isSinglenton;
	
	public TaskInfo () {
		
	}
	
	public TaskInfo(String jobName, String username, String cronExpression, SchedulerType schedulerType, Map data,
			boolean isSinglenton) {
		super();
		this.jobName = jobName;
		this.username = username;
		this.cronExpression = cronExpression;
		this.schedulerType = schedulerType;
		this.data = data;
		this.isSinglenton = isSinglenton;
	}
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public SchedulerType getSchedulerType() {
		return schedulerType;
	}

	public void setSchedulerType(SchedulerType schedulerType) {
		this.schedulerType = schedulerType;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Map getData() {
		return data;
	}

	public void setData(Map data) {
		this.data = data;
	}

	public boolean isSinglenton() {
		return isSinglenton;
	}

	public void setSinglenton(boolean isSinglenton) {
		this.isSinglenton = isSinglenton;
	}

}
