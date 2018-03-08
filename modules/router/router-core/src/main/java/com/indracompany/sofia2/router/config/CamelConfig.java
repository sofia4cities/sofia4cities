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
package com.indracompany.sofia2.router.config;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.camel.spring.CamelBeanPostProcessor;
import org.apache.camel.spring.boot.CamelConfigurationProperties;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.spring.boot.RoutesCollector;
import org.apache.camel.spring.boot.TypeConversionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableConfigurationProperties(CamelConfigurationProperties.class)
@Import(TypeConversionConfiguration.class)
@ImportResource({"classpath:camel-context-reference.xml"})
public class CamelConfig {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	
	@Bean
	@ConditionalOnMissingBean(RoutesCollector.class)
	RoutesCollector routesCollector(ApplicationContext applicationContext, CamelConfigurationProperties config) {

		Collection<CamelContextConfiguration> configurations = applicationContext
				.getBeansOfType(CamelContextConfiguration.class).values();

		return new RoutesCollector(applicationContext, new ArrayList<CamelContextConfiguration>(configurations),
				config);
	}

	@Bean
	CamelBeanPostProcessor camelBeanPostProcessor(ApplicationContext applicationContext) {
		CamelBeanPostProcessor processor = new CamelBeanPostProcessor();
		processor.setApplicationContext(applicationContext);
		return processor;
	}

}
