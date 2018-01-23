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
package com.indra.sofia2.support.util.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(AutowiringSpringBeanJobFactory.class);

	private transient AutowireCapableBeanFactory beanFactory;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		logger.debug("Storing autowire-capable bean factory...");
		beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		logger.debug("Creating autowire-capable Quartz job...");
		final Object job = super.createJobInstance(bundle);
		beanFactory.autowireBean(job);
		return job;
	}
}
