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
package com.indracompany.sofia2.router.camel;

import java.io.InputStream;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CamelLoadRoutesEventListener {

	@Autowired
	CamelContextHandler camelContextHandler;

	@Autowired
	private ApplicationContext applicationContext;

	public String context() {

		int routes = camelContextHandler.getDefaultCamelContext().getRoutes().size();
		if (routes == 0) {
			try {
				Resource[] resource = applicationContext.getResources("classpath*:camel-routes/*.xml");
				if (resource != null) {
					for (Resource r : resource) {
						log.info("Loading Camel Set of Routes :" + r.getFilename() + " " + r.getURL());
						loadRoutes(r.getInputStream());
					}
				}
			} catch (Exception e) {
			}
			log.info("Router Event Loader -> Camel Context Status is: " + camelContextHandler.getDefaultCamelContext().getStatus());
			log.info("Router Event Loader -> Camel Total Number of Routes Loaded :"
					+ camelContextHandler.getDefaultCamelContext().getRoutes().size());

		}

			return "OK";
	}

	public void loadRoutes(InputStream is) {
		RoutesDefinition routes = null;
		try {
			CamelContext context = camelContextHandler.getDefaultCamelContext();
			routes = context.loadRoutesDefinition(is);
			List<RouteDefinition> list = routes.getRoutes();
			context.addRouteDefinitions(list);
			context.startAllRoutes();
			log.info("Route Definition Loaded: "+ routes);
		} catch (Exception e) {
			log.error("Something happens loading routes :"+e.getMessage());
		}
	}

	@EventListener({ ApplicationReadyEvent.class })
	void contextRefreshedEvent() {
		log.info("Router Event Loader -> ApplicationReadyEvent happened, loading Camel Routes");
		context();
	}
}