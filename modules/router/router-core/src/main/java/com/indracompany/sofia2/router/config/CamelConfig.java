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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.camel.spring.CamelBeanPostProcessor;
import org.apache.camel.spring.boot.CamelConfigurationProperties;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.spring.boot.RoutesCollector;
import org.apache.camel.spring.boot.TypeConversionConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import com.indracompany.sofia2.commons.ssl.SSLUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(CamelConfigurationProperties.class)
@Import(TypeConversionConfiguration.class)
@ImportResource({"classpath:camel-context-reference.xml"})
public class CamelConfig {
	
	@Bean
	@ConditionalOnMissingBean(RoutesCollector.class)
	RoutesCollector routesCollector(ApplicationContext applicationContext, CamelConfigurationProperties config) {
		Collection<CamelContextConfiguration> configurations = applicationContext.getBeansOfType(CamelContextConfiguration.class).values();
		return new RoutesCollector(applicationContext, new ArrayList<CamelContextConfiguration>(configurations),config);
	}

	@Bean
	CamelBeanPostProcessor camelBeanPostProcessor(ApplicationContext applicationContext) {
		CamelBeanPostProcessor processor = new CamelBeanPostProcessor();
		processor.setApplicationContext(applicationContext);
		return processor;
	}
	
	private static final String MSJ_SSL_ERROR = "Error configuring SSL verification in Router";
	
	@Bean
	@Conditional(RouterAvoidSSLVerificationCondition.class)
	String sslUtil() {
        try {
        	SSLUtil.turnOffSslChecking();
        } catch (KeyManagementException e) {
               log.error(MSJ_SSL_ERROR, e);
               throw new RuntimeException(MSJ_SSL_ERROR, e);
        } catch (NoSuchAlgorithmException e) {
               log.error(MSJ_SSL_ERROR, e);
               throw new RuntimeException(MSJ_SSL_ERROR, e);
        }
        
        return "OK";
	}

}
