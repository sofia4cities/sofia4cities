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
package com.indracompany.sofia2.router.service;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.boot.CamelConfigurationProperties;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class CamelContextHandler implements BeanFactoryAware {

  private BeanFactory beanFactory;
  
  @Autowired
  CamelContext camelContextReference;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private CamelConfigurationProperties camelConfigurationProperties;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public boolean camelContextExist(String name) {
    return applicationContext.containsBean(name);
  }
  
  public Map<String,SpringCamelContext> findCamelContexts() {
	 return (Map<String,SpringCamelContext>)applicationContext.getBeansOfType(SpringCamelContext.class);
  }
  
  public Resource loadCamelContextResource(String resourceLocation) {
	  Resource resource =
			  applicationContext.getResource(resourceLocation); 
	  return resource;
  }

  public CamelContext getCamelContext(String id) {
    CamelContext camelContext = null;
    String name = "" + id;
    if (applicationContext.containsBean(name)) {    
       camelContext = applicationContext.getBean(name, SpringCamelContext.class);
    } else {
       camelContext = camelContext(name);
    }
    return camelContext;
  }

  @SuppressWarnings("deprecation")
  private CamelContext camelContext(String contextName) {
    CamelContext camelContext = new SpringCamelContext(applicationContext);
    SpringCamelContext.setNoStart(true);
    if (!camelConfigurationProperties.isJmxEnabled()) {
      camelContext.disableJMX();
    }

    if (contextName != null) {
      ((SpringCamelContext) camelContext).setName(contextName);
    }

    if (camelConfigurationProperties.getLogDebugMaxChars() > 0) {
     camelContext.getProperties().put( Exchange.LOG_DEBUG_BODY_MAX_CHARS, "" + camelConfigurationProperties.getLogDebugMaxChars());

    }

    camelContext.setStreamCaching( camelConfigurationProperties.isStreamCaching());
    camelContext.setTracing( camelConfigurationProperties.isTracing());
    camelContext.setMessageHistory( camelConfigurationProperties.isMessageHistory());
    camelContext.setHandleFault( camelConfigurationProperties.isHandleFault());
    camelContext.setAutoStartup( camelConfigurationProperties.isAutoStartup());
    camelContext.setAllowUseOriginalMessage(camelConfigurationProperties.isAllowUseOriginalMessage());
    
    ErrorHandlerBuilder ehBuilder= camelContextReference.getErrorHandlerBuilder();

    if (camelContext.getManagementStrategy().getManagementAgent() != null) {
    	camelContext.getManagementStrategy().getManagementAgent().setEndpointRuntimeStatisticsEnabled(camelConfigurationProperties.isEndpointRuntimeStatisticsEnabled());
    	camelContext.getManagementStrategy().getManagementAgent().setStatisticsLevel(camelConfigurationProperties.getJmxManagementStatisticsLevel());
    	camelContext.getManagementStrategy().getManagementAgent().setManagementNamePattern(camelConfigurationProperties.getJmxManagementNamePattern());
    	camelContext.getManagementStrategy().getManagementAgent().setCreateConnector(camelConfigurationProperties.isJmxCreateConnector());

    }

    ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    configurableBeanFactory.registerSingleton(contextName, camelContext);

   /* try {
      camelContext.start();
    } catch (Exception e) {
      // Log error
    }*/
    return camelContext;
  }

}