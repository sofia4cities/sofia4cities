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
package com.indracompany.sofia2.api.camel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class CamelLoadRoutesEventListener {
	
	@Autowired
	ApiCamelContextHandler camelContextHandler;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public String context() {
		  Resource resource =  applicationContext.getResource("classpath:api-camel-routes-only.xml"); 
		  try {
			  if (camelContextHandler.camelContextExist("camel-context-reference")) {
				  loadRoutes(resource.getInputStream(),"camel-context-reference");
			  }
			  else {
				  loadRoutes(resource.getInputStream(),"camel-context");
			  }
			  
			  
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "OK";
		
	 }
	
	
	public String loadRoutes(InputStream is, String name) {
		RoutesDefinition routes=null;
			try {
				
			CamelContext context=	camelContextHandler.getCamelContext(name);
			routes = context.loadRoutesDefinition(is);
			List<RouteDefinition> list = routes.getRoutes();
			context.addRouteDefinitions(list);
			context.startAllRoutes();
			
			ServiceStatus  status = context.getStatus();
			System.out.println(status);
			} catch (Exception e) {
		      // Log error
			}
			
		return routes.toString();
	}

    @EventListener({ApplicationReadyEvent.class})
    void contextRefreshedEvent() {
        System.out.println("ApplicationReadyEvent happened");
        context();
    }
}