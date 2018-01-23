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
package com.indracompany.sofia2.scheduler.core.scheduler.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.scheduler.core.common.ResponseInfo;
import com.indracompany.sofia2.scheduler.core.scheduler.domain.TaskInfo;
import com.indracompany.sofia2.scheduler.core.scheduler.service.TaskService;
import com.indracompany.sofia2.scheduler.core.scheduler.service.impl.TaskServiceTwitter;

import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/scheduler")
public class DefaultController {
	
	private final Logger logger = LoggerFactory.getLogger(DefaultController.class);
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	List<Scheduler> schedulers;
	
	@Autowired
	TaskServiceTwitter taskServiceTwitter;
	
	@RequestMapping (method = RequestMethod.GET, value="/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<TaskInfo> list() {	
		List <Scheduler> schedulers2 = schedulers;
		
		for (Scheduler s: schedulers2) {
			
			try {
				logger.info(""+ s.getSchedulerInstanceId());
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		Map<String, Scheduler> schedulerMap = schedulers2.stream().collect(
                Collectors.toMap(x -> {
					try {
						return x.getSchedulerName();
					} catch (SchedulerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}, x -> x));
		
		*/
		
		return taskService.list();
	}
		
	@RequestMapping(method = RequestMethod.POST, value="/schedule", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseInfo schedule(@RequestBody TaskInfo taskInfo) {
		
		boolean scheduled = taskService.addJob(taskInfo);	
		return new ResponseInfo(scheduled, "");
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/scheduleTwitter", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseInfo scheduleTwitter(@RequestBody TaskInfo taskInfo) {
		
		boolean scheduled = taskServiceTwitter.addJob(taskInfo);	
		return new ResponseInfo(scheduled, "");
	}
	
	@RequestMapping (method = RequestMethod.GET, value="/listTwitter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<TaskInfo> listTwitter() {		
		return taskServiceTwitter.list();	
	}
	
	@RequestMapping (method = RequestMethod.GET, value="/unschedule/{jobName}/{jobGroup}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseInfo unschedule(@PathVariable String jobName, @PathVariable String jobGroup) {
		
		boolean unscheduled = taskService.unscheduled(jobName, jobGroup);
		return new ResponseInfo(unscheduled, "");
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/pause/{jobName}/{jobGroup}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseInfo pause(@PathVariable String jobName, @PathVariable String jobGroup){
		
		boolean resumed = taskService.pause(jobName, jobGroup);		
		return new ResponseInfo(resumed, "");
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/resume/{jobName}/{jobGroup}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseInfo resume(@PathVariable String jobName, @PathVariable String jobGroup){
		
		boolean resumed = taskService.resume(jobName, jobGroup);		
		return new ResponseInfo(resumed, "");
	}
	
}
