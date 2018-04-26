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
package com.indracompany.sofia2.router.controller;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.router.camel.CamelContextHandler;
import com.indracompany.sofia2.router.util.Utils;

import io.swagger.annotations.ApiOperation;

@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
@RequestMapping("management")
public class ManagementControllerImpl  {
	
	
	@Autowired
	CamelContextHandler camelContextHandler;
	
	public static String defaultPath="./src/main/resources/";
	
	
	@RequestMapping(value = "/camel/context", method = RequestMethod.GET)
	@ApiOperation(value = "camel-context")
	public Set<String> camelContext() throws Exception {
		Map<String,SpringCamelContext>  list = camelContextHandler.findCamelContexts();
		return list.keySet();
	}
	@RequestMapping(value = "/camel/context/status", method = RequestMethod.GET)
	@ApiOperation(value = "camel-context-status")
	public ServiceStatus camelContextStatus(String name) throws Exception {
		CamelContext context = camelContextHandler.getCamelContext(name);
		return context.getStatus();
	}
	
	@RequestMapping(value = "/camel/context/start", method = RequestMethod.PUT)
	@ApiOperation(value = "camel-context-start")
	public void camelContextStart( String login) throws Exception {
		camelContextHandler.getCamelContext(login).start();
	}
	
	@RequestMapping(value = "/camel/context/stop", method = RequestMethod.PUT)
	@ApiOperation(value = "camel-context-stop")
	public void camelContextStop( String login) throws Exception {
		camelContextHandler.getCamelContext(login).stop();
	}
	
	@RequestMapping(value = "/camel/routes/load/context", method = RequestMethod.POST)
	@ApiOperation(value = "camel-routes-load-into-context")
	public String camelRoutesLoadIntoContext( String name, String context) throws Exception {
		
		String TEST_PATH_QUALITY = defaultPath+context+".xml";
		loadRoutes(TEST_PATH_QUALITY,name);
		return TEST_PATH_QUALITY;
	}
	
	@RequestMapping(value = "/camel/context/load", method = RequestMethod.POST)
	@ApiOperation(value = "camel-context-load")
	public String cameContextLoad(String name) throws Exception {
		
		String TEST_PATH_QUALITY = defaultPath+name+".xml";
		loadRoutes(TEST_PATH_QUALITY,name);
		return TEST_PATH_QUALITY.toString();
	}
	
	public void loadRoutes(String routestr, String name) {
		RoutesDefinition routes=null;
		if (routestr != null && !routestr.isEmpty()) {
			try {
				InputStream is = Utils.getResourceFromFile(new File(routestr));
				routes = camelContextHandler.getCamelContext(name).loadRoutesDefinition(is);

				camelContextHandler.getCamelContext(name).addRouteDefinitions(routes.getRoutes());

			} catch (Exception e) {
		      // Log error
			}
			
		}
			
	}

}
