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
package com.indracompany.sofia2.scheduler.scheduler.generator.impl;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.scheduler.scheduler.generator.TriggerGenerator;

@Service
public class TriggerGeneratorImpl implements TriggerGenerator{
	
	@Override
	public Trigger createTrigger(JobDetail jobDetail, TriggerKey triggerKey) {
		
		Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerKey)
                .startNow()
                .build();
        
        return trigger;
	}

	@Override
	public Trigger createCronTrigger(String cronExpression, JobDetail jobDetail, TriggerKey triggerKey) {
		
		CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression).
				   withMisfireHandlingInstructionDoNothing();
        
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(schedBuilder)
                .withIdentity(triggerKey)
                .build();
        
        return trigger;
    }
	
}
