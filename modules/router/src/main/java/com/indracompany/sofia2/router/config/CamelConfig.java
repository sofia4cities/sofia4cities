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

import org.apache.camel.component.hystrix.metrics.servlet.HystrixEventStreamServlet;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.CamelBeanPostProcessor;
import org.apache.camel.spring.boot.CamelConfigurationProperties;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.spring.boot.RoutesCollector;
import org.apache.camel.spring.boot.TypeConversionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import io.hawt.config.ConfigFacade;
import io.hawt.web.AuthenticationFilter;

@Configuration
@EnableConfigurationProperties(CamelConfigurationProperties.class)
@Import(TypeConversionConfiguration.class)
@ImportResource({"classpath:router-camel-context.xml"})
public class CamelConfig {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	public HystrixEventStreamServlet hystrixServlet() {
		return new HystrixEventStreamServlet();
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		return new ServletRegistrationBean(new HystrixEventStreamServlet(), "/hystrix.stream");
	}


	@Bean
	public ServletRegistrationBean camelServletRegistrationBean() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
		registration.setName("RouterServlet");
		registration.setAsyncSupported(true);
		registration.setLoadOnStartup(1);
		return registration;
	}

	@Bean
	public String init() {
		System.setProperty(AuthenticationFilter.HAWTIO_AUTHENTICATION_ENABLED, "false");
		System.out.println(AuthenticationFilter.HAWTIO_AUTHENTICATION_ENABLED);
		return AuthenticationFilter.HAWTIO_AUTHENTICATION_ENABLED;
	}

	@Bean
	public ConfigFacade configFacade() {
		System.setProperty("hawtio.offline", "true");
		return ConfigFacade.getSingleton();
	}
	
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
