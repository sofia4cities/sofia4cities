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
package com.indracompany.sofia2.router.service.app.service.crud;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class DataSourceConfiguration {

	static private Logger logger = Logger.getLogger(DataSourceConfiguration.class);

	/**
	 * 
	 * Create a beanPostProcessor , @Bean for adding the dynamic beans.
	 */
	@Bean
	static BeanDefinitionRegistryPostProcessor beanPostProcessor(final ConfigurableEnvironment environment) {
		return new BeanDefinitionRegistryPostProcessor() {

			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
				// TODO Auto-generated method stub

			}

			@Override
			public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanRegistry) throws BeansException {
				// createDynamicBeans(environment,beanRegistry);

			}

		};
	}

	/*
	 * static private Map<String,DataSourceProperties>
	 * parseProperties(ConfigurableEnvironment environment) {
	 * Map<String,DataSourceProperties> propertyMap = new HashMap<>();
	 * for(PropertySource source : environment.getPropertySources()) { if(source
	 * instanceof EnumerablePropertySource ) { EnumerablePropertySource
	 * propertySource = (EnumerablePropertySource) source; for(String property :
	 * propertySource.getPropertyNames()) {
	 * if(DataSourceProperties.isUrlProperty(property)) { String prefix =
	 * extractPrefix(property); propertyMap.put(prefix, new
	 * DataSourceProperties(environment,prefix)); } } } } return propertyMap; }
	 * 
	 * static private void createDynamicBeans(ConfigurableEnvironment
	 * environment,BeanDefinitionRegistry beanRegistry) {
	 * Map<String,DataSourceProperties> propertyMap = parseProperties(environment);
	 * for(Map.Entry<String,DataSourceProperties> entry : propertyMap.entrySet()) {
	 * registerDynamicBean(entry.getKey(),entry.getValue(),beanRegistry); } }
	 * 
	 * 
	 * 
	 * static private void registerDynamicBean(String prefix, DataSourceProperties
	 * dsProps,BeanDefinitionRegistry beanRegistry) {
	 * logger.info("Registering beans for " + prefix); BeanDefinition
	 * dataSourceBeanDef =
	 * BeanDefinitionBuilder.genericBeanDefinition(BasicDataSource.class)
	 * .addPropertyValue("url",dsProps.getUrl()) .addPropertyValue("username",
	 * dsProps.getUsername()) .addPropertyValue("password", dsProps.getPassword())
	 * .addPropertyValue("driverClassName", dsProps.getDriver())
	 * .getBeanDefinition(); if(dsProps.getPrimary()) {
	 * dataSourceBeanDef.setPrimary(true); }
	 * beanRegistry.registerBeanDefinition("datasource_" + prefix,
	 * dataSourceBeanDef); if(dsProps.getPrimary()) {
	 * beanRegistry.registerAlias("datasource_" + prefix, "dataSource"); }
	 * BeanDefinition repositoryBeanDef =
	 * BeanDefinitionBuilder.genericBeanDefinition(Repository.class)
	 * .addConstructorArgReference("datasource_" + prefix) .getBeanDefinition();
	 * beanRegistry.registerBeanDefinition("repository_" + prefix,
	 * repositoryBeanDef);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * static private String extractPrefix(String property) { int idx =
	 * property.indexOf("."); return property.substring(0, idx); }
	 */
}
