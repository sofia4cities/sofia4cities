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
package com.indracompany.sofia2.scheduler.core.scheduler.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.scheduler.core.scheduler.service.BatchSchedulerFactory;
import com.indracompany.sofia2.scheduler.library.BasicSchedulerType;
import com.indracompany.sofia2.scheduler.library.SchedulerType;
import com.indracompany.sofia2.scheduler.library.config.scheduler.BatchScheduler;

@Service
public class BatchFactoryImpl implements BatchSchedulerFactory{
	
	@Autowired
	List<BatchScheduler> schedulers;
	
	Map<String, BatchScheduler> schedulerMap;

	@Autowired
	public BatchFactoryImpl(List<BatchScheduler> schedulers) {
		
		this.schedulers = schedulers;
		
		schedulerMap = schedulers.stream().collect(
	            Collectors.toMap(x -> {
					try {
						return x.getSchedulerName();
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
					return null;
				}, x -> x));
			
	}

	@Override
	public BatchScheduler getScheduler(SchedulerType schedulerType) {
	
		return schedulerMap.get(schedulerType.getSchedulerName());

	}

	@Override
	public List<BatchScheduler> getSchedulers() {
		return schedulers;
	}

	@Override
	public BatchScheduler getScheduler(String schedulerName) {		
		return getScheduler(BasicSchedulerType.schedulerTypeFor(schedulerName));
	}

}
