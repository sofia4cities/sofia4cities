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
package com.indracompany.sofia2.scheduler.library.scheduler.service.impl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.scheduler.library.config.scheduler.BatchScheduler;
import com.indracompany.sofia2.scheduler.library.job.BatchGenericExecutor;
import com.indracompany.sofia2.scheduler.library.job.BatchGenericJob;
import com.indracompany.sofia2.scheduler.library.scheduler.domain.TaskInfo;
import com.indracompany.sofia2.scheduler.library.scheduler.domain.TaskOperation;
import com.indracompany.sofia2.scheduler.library.scheduler.service.BatchSchedulerFactory;
import com.indracompany.sofia2.scheduler.library.scheduler.service.JobGenerator;
import com.indracompany.sofia2.scheduler.library.scheduler.service.TaskService;
import com.indracompany.sofia2.scheduler.library.scheduler.service.TriggerGenerator;

@Service
public class TaskServiceImpl implements TaskService {
	
	private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
	
	@Autowired
	private BatchSchedulerFactory batchSchedulerFactory;
	
	@Autowired
	private TriggerGenerator triggerGenerator;
	
	@Autowired
	private JobGenerator jobGenerator;
	
	
	@Override
	public List<TaskInfo> list(String username) {
		
		List<TaskInfo> list = new ArrayList<>();
		
		try {
			
			for (BatchScheduler scheduler: batchSchedulerFactory.getSchedulers()) {
							
				for(String groupJob: scheduler.getJobGroupNames()){
					
					for(JobKey jobKey: scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupJob))){
						
						List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
						
						for (Trigger trigger: triggers) {
							
							TriggerState triggerState = scheduler.getTriggerState(trigger.getKey()); 
							JobDetail jobDetail = scheduler.getJobDetail(jobKey);
							
							String cronExpression = "";
							
							if (trigger instanceof CronTrigger) {
					            CronTrigger cronTrigger = (CronTrigger) trigger;
					            cronExpression = cronTrigger.getCronExpression();
					        }
							
							logger.info(triggerState.name() + " " +  triggerState.toString());
							TaskInfo info = new TaskInfo(jobKey.getName(), jobKey.getGroup(), jobDetail.getDescription(), 
														 triggerState.name(), scheduler.getSchedulerName(), cronExpression, parseDate (trigger.getStartTime()), 
														 parseDate (trigger.getNextFireTime()), parseDate(trigger.getPreviousFireTime()), null);
							
							
							list.add(info);
						}					
					}
				}	
			}
			
		} catch (SchedulerException e) {
			logger.error("Error listing task", e);
		}
		
		return list;
	}
	
	private String parseDate (Date date) {
		String parsedDate = "";
		
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			parsedDate = format.format(date);
		}
		
		return parsedDate;
	}
	
	@Override
	public boolean addJob(TaskInfo info) {
		
		boolean added = true;
		logger.info("add job [jobname: " + info.getJobName() + ", groupName: " + info.getJobGroup() + "]");
		
		String jobName = info.getJobName();		
		String jobGroup = info.getJobGroup();
		String cronExpression = info.getCronExpression();
		String jobDescription = info.getJobDescription();
		
		BatchScheduler scheduler = batchSchedulerFactory.getScheduler(info.getSchedulerType());
		
		try {
			
			if (checkExists(new TaskOperation(jobName, jobGroup, info.getSchedulerType()))) {
		        logger.info("AddJob fails, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
		        //throw new ServiceException(String.format("Job, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
		    }
			
			JobDataMap jobDataMap = new JobDataMap();
			
			if (info.getData() != null) {
				jobDataMap.putAll(info.getData());
			}
			
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
			
			JobDetail jobDetail = jobGenerator.createJobDetail(jobKey, jobDataMap, BatchGenericJob.class, jobDescription);

			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			Trigger trigger = triggerGenerator.createCronTrigger(cronExpression, jobDetail, triggerKey);
			
			scheduler.scheduleJob(jobDetail, trigger);
			
			//add job to user list (username, jobname, groupname, schedulername)
			
		} catch (SchedulerException e) {
			added = false;
			logger.error("Error adding task", e);
		}
		
		return added;
	}
	
	@Override
	public boolean unscheduled(TaskOperation operation){
		
		boolean unscheduled = false;
		logger.info("unschedule job [jobname: " + operation.getJobName() + ", groupName: " + operation.getJobGroup()+ "]");
		
        try {
        	
        	BatchScheduler scheduler = batchSchedulerFactory.getScheduler(operation.getSchedulerType());
    		       	
			if (checkExists(operation)) {
				TriggerKey triggerKey = TriggerKey.triggerKey(operation.getJobName(), operation.getJobGroup());
				scheduler.pauseTrigger(triggerKey);
			    unscheduled = scheduler.unscheduleJob(triggerKey);
			    logger.info("unschedule, triggerKey:{}", triggerKey);
			}
		} catch (SchedulerException e) {
			logger.error("Error unscheduled task", e);
		}
        
        return unscheduled;
	}
	
	@Override
	public boolean pause(TaskOperation operation){
		
		boolean paused = false;
		logger.info("pause job [jobname: " + operation.getJobName() + ", groupName: " + operation.getJobGroup()+ "]");
		
		try {
			
			BatchScheduler scheduler = batchSchedulerFactory.getScheduler(operation.getSchedulerType());		
			
			if (checkExists(operation)) {
				TriggerKey triggerKey = TriggerKey.triggerKey(operation.getJobName(), operation.getJobGroup());
				scheduler.pauseTrigger(triggerKey);
			    logger.info("Pause success, triggerKey:{}", triggerKey);
			    paused = true;
			}
			
		} catch (SchedulerException e) {
			logger.error("Error pause task", e);
		}
		
		return paused;
	}
	
	@Override
	public boolean resume(TaskOperation operation){
		
		boolean resumed = false;
		logger.info("resume job [jobname: " + operation.getJobName() + ", groupName: " + operation.getJobGroup()+ "]");
		
        try {
        	
        	BatchScheduler scheduler = batchSchedulerFactory.getScheduler(operation.getSchedulerType());
    		       	
			if (checkExists(operation)) {
				
				TriggerKey triggerKey = TriggerKey.triggerKey(operation.getJobName(), operation.getJobGroup());	        
				scheduler.resumeTrigger(triggerKey);
			    logger.info("Resume success, triggerKey:{}", triggerKey);
			    resumed = true;
			}
			
		} catch (SchedulerException e) {
			logger.error("Error resume task", e);
		}
        
        return resumed;
	}
	
	@Override
	public boolean checkExists(TaskOperation operation) {
		
		boolean exists = false;
		logger.info("check if exists job [jobname: " + operation.getJobName() + ", groupName: " + operation.getJobGroup()+ "]");
		
		try {
			
			BatchScheduler scheduler = batchSchedulerFactory.getScheduler(operation.getSchedulerType());
			
			TriggerKey triggerKey = TriggerKey.triggerKey(operation.getJobName(), operation.getJobGroup());
			exists = scheduler.checkExists(triggerKey);
			
		} catch (SchedulerException e) {
			logger.error("Error checking if a job exists ", e);
		}
		
		return exists;
	}
	
}
