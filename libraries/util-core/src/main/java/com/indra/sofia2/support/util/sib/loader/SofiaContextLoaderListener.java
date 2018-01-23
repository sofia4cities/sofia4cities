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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.sib.loader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;
import com.indra.sofia2.grid.context.HazelcastUtils;
import com.indra.sofia2.support.util.sib.loader.events.ContextLoaderEventListenersRegistry;
import com.netflix.hystrix.Hystrix;

/**
 * Se sobreescribe ContextLoaderListener para soporte de sobrescritura de Spring
 * @author Indra
 *
 */
public class SofiaContextLoaderListener extends SofiaContextLoader implements ServletContextListener {
	
	private static final Logger log = LoggerFactory.getLogger(SofiaContextLoaderListener.class);
	private SofiaContextLoader contextLoader;
	private ClassLoaderLeakPreventor leakPreventor;

	/**
	 * Initialize the root web application context.
	 */
	public void contextInitialized(ServletContextEvent event)  {
		log.info("Handling context initialization event...");
		if (this.contextLoader == null) {
			this.contextLoader = (SofiaContextLoader) this;
		}
		this.contextLoader.initWebApplicationContext(event.getServletContext());
		log.info("Initializing class loader leak preventor...");
		leakPreventor = new ClassLoaderLeakPreventor();
		leakPreventor.contextInitialized(event);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.info("Notifying context destruction event...");
		ContextLoaderEventListenersRegistry registry = 
				(ContextLoaderEventListenersRegistry) ArqSpringContext.getBean(ContextLoaderEventListenersRegistry.class);
		registry.notifyContextDestroyedEvent();
		log.info("Shutting down embedded Hazelcast instances (gracefully)...");
		HazelcastUtils.shutdownHazelcastInstances();
		if (this.contextLoader != null) {
			log.info("Closing Spring ApplicationContext...");
			this.contextLoader.closeWebApplicationContext(event.getServletContext());
		}
		log.info("Shutting down Hystrix thread pools...");
		Hystrix.reset();
		SofiaContextCleanupListener.cleanupAttributes(event.getServletContext());
		log.info("Forcing release of orphan threads and resources...");
		leakPreventor.contextDestroyed(event);		
	}
}
