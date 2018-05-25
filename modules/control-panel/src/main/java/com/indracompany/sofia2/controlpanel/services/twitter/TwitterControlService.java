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
package com.indracompany.sofia2.controlpanel.services.twitter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.TwitterListening;
import com.indracompany.sofia2.config.services.twitter.TwitterListeningService;
import com.indracompany.sofia2.scheduler.SchedulerType;
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskInfo;
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskOperation;
import com.indracompany.sofia2.scheduler.scheduler.bean.response.ScheduleResponseInfo;
import com.indracompany.sofia2.scheduler.scheduler.service.TaskService;

@Service
public class TwitterControlService {

	@Autowired
	private TaskService taskService;

	@Autowired
	private TwitterListeningService twitterListeningService;

	public void scheduleTwitterListening(TwitterListening twitterListening) {

		TaskInfo task = new TaskInfo();
		task.setJobName(twitterListening.getId());
		task.setSchedulerType(SchedulerType.Twitter);

		Map<String, Object> jobContext = new HashMap<String, Object>();
		jobContext.put("id", twitterListening.getId());
		jobContext.put("ontology", twitterListening.getOntology().getIdentification());
		jobContext.put("clientPlatform", twitterListening.getToken().getClientPlatform().getIdentification());
		jobContext.put("token", twitterListening.getToken().getToken());
		jobContext.put("topics", twitterListening.getTopics());
		jobContext.put("geolocation", false);
		jobContext.put("userId", twitterListening.getUser().getUserId());
		jobContext.put("timeout", 2);
		if (twitterListening.getConfiguration() != null) {
			jobContext.put("configurationId", twitterListening.getConfiguration().getId());
		} else {
			jobContext.put("configurationId", null);
		}

		task.setUsername(twitterListening.getUser().getUserId());
		task.setData(jobContext);
		task.setSingleton(false);
		task.setCronExpression("0/20 * * ? * * *");

		// Calendar end = Calendar.getInstance();
		// end.add(Calendar.MINUTE, 2);

		task.setStartAt(twitterListening.getDateFrom());// twitterListening.getDateFrom());
		task.setEndAt(twitterListening.getDateTo());// witterListening.getDateTo());
		ScheduleResponseInfo response = taskService.addJob(task);
		twitterListening.setJobName(response.getJobName());
		this.twitterListeningService.updateListening(twitterListening);

	}

	public void unscheduleTwitterListening(TwitterListening twitterListening) {
		TaskOperation operation = new TaskOperation();
		operation.setJobName(twitterListening.getJobName());
		if (operation.getJobName() != null) {
			this.taskService.unscheduled(operation);
			// twitterListening.setJobName(null);
			// twitterListening.setDateTo(new Date());
			// this.twitterListeningService.updateListening(twitterListening);
		}

	}

	public void updateTwitterListening(TwitterListening twitterListening) {
		this.twitterListeningService.updateListening(twitterListening);
		twitterListening = this.twitterListeningService.getListenById(twitterListening.getId());
		this.unscheduleTwitterListening(twitterListening);
		if (twitterListening.getDateTo().getTime() > System.currentTimeMillis())
			this.scheduleTwitterListening(twitterListening);
	}

}
