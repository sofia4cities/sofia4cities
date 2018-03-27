package com.indracompany.sofia2.api.camel;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.boot.CamelConfigurationProperties;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCamelContextHandler implements BeanFactoryAware {

	private BeanFactory beanFactory;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private CamelConfigurationProperties camelConfigurationProperties;

	private static String DEFAULT_CONTEXT_CAMEL = "camel-context-reference";

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public boolean camelContextExist(String name) {
		return applicationContext.containsBean(name);
	}

	public Map<String, SpringCamelContext> findCamelContexts() {
		return (Map<String, SpringCamelContext>) applicationContext.getBeansOfType(SpringCamelContext.class);
	}

	public Resource loadCamelContextResource(String resourceLocation) {
		Resource resource = applicationContext.getResource(resourceLocation);
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

	public CamelContext getDefaultCamelContext() {
		return getCamelContext(DEFAULT_CONTEXT_CAMEL);
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
			camelContext.getProperties().put(Exchange.LOG_DEBUG_BODY_MAX_CHARS,
					"" + camelConfigurationProperties.getLogDebugMaxChars());

		}

		camelContext.setStreamCaching(camelConfigurationProperties.isStreamCaching());
		camelContext.setTracing(camelConfigurationProperties.isTracing());
		camelContext.setMessageHistory(camelConfigurationProperties.isMessageHistory());
		camelContext.setHandleFault(camelConfigurationProperties.isHandleFault());
		camelContext.setAutoStartup(camelConfigurationProperties.isAutoStartup());
		camelContext.setAllowUseOriginalMessage(camelConfigurationProperties.isAllowUseOriginalMessage());

		if (camelContext.getManagementStrategy().getManagementAgent() != null) {
			camelContext.getManagementStrategy().getManagementAgent().setEndpointRuntimeStatisticsEnabled(
					camelConfigurationProperties.isEndpointRuntimeStatisticsEnabled());
			camelContext.getManagementStrategy().getManagementAgent()
					.setStatisticsLevel(camelConfigurationProperties.getJmxManagementStatisticsLevel());
			camelContext.getManagementStrategy().getManagementAgent()
					.setManagementNamePattern(camelConfigurationProperties.getJmxManagementNamePattern());
			camelContext.getManagementStrategy().getManagementAgent()
					.setCreateConnector(camelConfigurationProperties.isJmxCreateConnector());
		}

		ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
		configurableBeanFactory.registerSingleton(contextName, camelContext);

		/*
		 * try { camelContext.start(); } catch (Exception e) { // Log error }
		 */
		return camelContext;
	}

}