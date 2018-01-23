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
package com.indra.sofia2.support.util.demomode.timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component("demoFunctionalityTimeControllerImpl")
public class DemoFunctionalityTimeControllerImpl implements DemoFunctionalityTimeController {
	
	private Map<String, TaskTimeDetails> taskTimes;
	
	private ScheduledExecutorService scheduledExecutorService;

	
	@PostConstruct
	public void init(){
		this.taskTimes=new ConcurrentHashMap<String, TaskTimeDetails>();
		this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
	}

	@Override
	public long getRemainingSecondsBeforeStopping(String idTask) {
		
		TaskTimeDetails taskDetails=taskTimes.get(idTask);
		if(null!=taskDetails){
			long startTime=taskDetails.getStartTime();
			int availableSeconds=taskDetails.getAvailableSeconds();
			
			long currentTime=System.currentTimeMillis();
			
			
			return (startTime+(availableSeconds*1000))-currentTime;
		}else{
			return 0;
		}
	}

	@Override
	public void scheduleTask(final String taskName, final Runnable task, int executionSeconds) {
		
		this.taskTimes.put(taskName, new TaskTimeDetails(System.currentTimeMillis(), executionSeconds));
		
		this.scheduledExecutorService.schedule(new Runnable() {
			
			@Override
			public void run() {
				task.run();
				removeTimeDetails(taskName);
			}
		} , executionSeconds, TimeUnit.SECONDS);
		
		
		
	}
	
	
	private void removeTimeDetails(String taskName){
		this.taskTimes.remove(taskName);
	}
	
	class TaskTimeDetails{
		
		private long startTime;
		private int availableSeconds;
		
		public TaskTimeDetails(long startTime, int availableSeconds) {
			super();
			this.startTime = startTime;
			this.availableSeconds = availableSeconds;
		}
		
		public long getStartTime() {
			return startTime;
		}
		
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		
		public int getAvailableSeconds() {
			return availableSeconds;
		}
		
		public void setAvailableSeconds(int availableSeconds) {
			this.availableSeconds = availableSeconds;
		}
		
		
	}
	
	

}
