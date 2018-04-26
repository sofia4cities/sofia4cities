/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.rtdbmaintainer.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.Configuration.Type;
import com.indracompany.sofia2.config.services.configuration.ConfigurationService;
import com.indracompany.sofia2.scheduler.SchedulerType;
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskInfo;
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskOperation;
import com.indracompany.sofia2.scheduler.scheduler.service.TaskService;

@Component
public class RtdbMaintainerConfiguration implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	ConfigurationService configurationService;
	@Autowired
	TaskService taskService;

	private static final String JOB_NAME = "RtdbMaintainerJob";

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		Configuration configuration = this.configurationService.getConfiguration(Type.SchedulingConfiguration,
				"default", null);
		@SuppressWarnings("unchecked")
		Map<String, Object> ymlConfig = (Map<String, Object>) this.configurationService
				.fromYaml(configuration.getYmlConfig()).get("RtdbMaintainer");
		String cron = (String) ymlConfig.get("cron");
		TimeUnit timeUnit = TimeUnit.valueOf((String) ymlConfig.get("timeUnit"));
		long timeout = ((Integer) ymlConfig.get("timeout")).longValue();

		TaskOperation taskOperation = new TaskOperation();
		taskOperation.setJobName(JOB_NAME + "-" + SchedulerType.Batch.toString());
		if (!this.taskService.checkExists(taskOperation)) {
			TaskInfo task = new TaskInfo();
			task.setSchedulerType(SchedulerType.Batch);
			task.setCronExpression(cron);
			task.setSingleton(true);
			task.setJobName(JOB_NAME);
			task.setUsername("administrator");
			Map<String, Object> jobContext = new HashMap<String, Object>();
			jobContext.put("timeout", timeout);
			jobContext.put("timeUnit", timeUnit);
			task.setData(jobContext);
			this.taskService.addJob(task);
		}

	}

}
