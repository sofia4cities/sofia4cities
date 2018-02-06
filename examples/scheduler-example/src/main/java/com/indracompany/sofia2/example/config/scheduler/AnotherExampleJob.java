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
package com.indracompany.sofia2.example.config.scheduler;


import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.scheduler.job.JobParamNames;
import com.indracompany.sofia2.scheduler.SchedulerType;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AnotherExampleJob {
	
	public void execute (JobExecutionContext context) {
		
		JobDataMap data = context.getMergedJobDataMap();
		String username = data.getString(JobParamNames.USERNAME);
		SchedulerType schedulerName = (SchedulerType)data.get(JobParamNames.SCHEDULER_TYPE);
		log.info("executing job test" + username + " " + schedulerName);
	}
	

}
