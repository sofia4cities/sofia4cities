package com.indracompany.sofia2.api.camel;

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

@Component
public class ApiCamelLoadRoutesEventListener {

	@Autowired
	ApiCamelContextHandler camelContextHandler;

	@Autowired
	private ApplicationContext applicationContext;
	
	public String context() {
		
		int routes = camelContextHandler.getDefaultCamelContext().getRoutes().size();
		if (routes==0) {
			try {
				Resource[] resource = applicationContext.getResources("classpath*:camel-routes/*.xml");
				if (resource != null) {
					for (Resource r : resource) {
						System.out.println("Loading Camel Set of Routes :"+r.getFilename()+" "+r.getURL());
						loadRoutes(r.getInputStream());
					}
				}
			} catch (Exception e) {
			}
			
			System.out.println("API Manager Event Loader -> Camel Context Status is: " + camelContextHandler.getDefaultCamelContext().getStatus());
			System.out.println("API Manager Event Loader -> Camel Total Number of Routes Loaded :"
					+ camelContextHandler.getDefaultCamelContext().getRoutes().size());
			
		}
		
			return "OK";
	}

	public String loadRoutes(InputStream is) {
		RoutesDefinition routes = null;
		try {
			CamelContext context = camelContextHandler.getDefaultCamelContext();
			routes = context.loadRoutesDefinition(is);
			List<RouteDefinition> list = routes.getRoutes();
			context.addRouteDefinitions(list);
			context.startAllRoutes();
		} catch (Exception e) {
			// Log error
		}
		return routes.toString();
	}

	@EventListener({ ApplicationReadyEvent.class })
	void contextRefreshedEvent() {
		System.out.println("API Manager Event Loader -> ApplicationReadyEvent happened, loading Camel Routes");
		context();
	}
}