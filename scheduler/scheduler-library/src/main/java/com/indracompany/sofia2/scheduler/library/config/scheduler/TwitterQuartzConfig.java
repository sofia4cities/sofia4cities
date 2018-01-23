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
package com.indracompany.sofia2.scheduler.library.config.scheduler;

import static com.indracompany.sofia2.scheduler.library.PropertyNames.SCHEDULER_PROPERTIES_LOCATION;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnResource(resources = SCHEDULER_PROPERTIES_LOCATION)
public class TwitterQuartzConfig extends GenericQuartzConfig {
	
	private final String SCHEDULER_BEAN_NAME = "twitterScheduler";
	private final String SCHEDULER_BEAN_FACTORY_NAME = "twitter-scheduler-factory";
	private final String SCHEDULER_NAME = "twitter-scheduler";
	
	@Bean(SCHEDULER_BEAN_FACTORY_NAME)
	public SchedulerFactoryBean twitterSchedulerFactoryBean(JobFactory jobFactory, PlatformTransactionManager transactionManager) throws SchedulerException {
		
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		
		schedulerFactoryBean.setTransactionManager(transactionManager);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setSchedulerName(SCHEDULER_NAME);
		schedulerFactoryBean.setBeanName(SCHEDULER_NAME);

		// custom job factory of spring with DI support for @Autowired!
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setAutoStartup(checksIfAutoStartup());
		
		schedulerFactoryBean.setDataSource(dataSource);
		
		schedulerFactoryBean.setJobFactory(jobFactory);
		schedulerFactoryBean.setQuartzProperties(quartzProperties);
		
		return schedulerFactoryBean;
	}
	
	@Bean(SCHEDULER_BEAN_NAME)
	public Scheduler twitterScheduler (@Autowired @Qualifier(SCHEDULER_BEAN_FACTORY_NAME) SchedulerFactoryBean schedulerFactoryBean){
		return schedulerFactoryBean.getScheduler();
	}
	
	@Override
	public String getSchedulerBeanName() {
		return SCHEDULER_BEAN_NAME;
	}
	
}
