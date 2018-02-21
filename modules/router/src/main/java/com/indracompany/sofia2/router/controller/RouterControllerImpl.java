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

import org.apache.camel.CamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.router.service.CamelContextHandler;
import com.indracompany.sofia2.router.service.RouterService;
import com.indracompany.sofia2.router.util.Utils;

import io.swagger.annotations.ApiOperation;

@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
@RequestMapping("router")
public class RouterControllerImpl implements RouterControllerInterface {
	
	@Autowired
	CamelContext camelContext;
	
	@Autowired
	CamelContextHandler camelContextHandler;
	
	@Autowired
	@Qualifier("routerServiceProxy")
	private RouterService routerService;
	
	@RequestMapping(value = "/node-red", method = RequestMethod.POST)
	@ApiOperation(value = "node-red")
	public String nodeRed(@RequestBody String login) throws Exception {
		return (String)routerService.nodeRed(login);
	}
	
	@RequestMapping(value = "/scripting-engine", method = RequestMethod.POST)
	@ApiOperation(value = "scripting-engine")
	public String scriptingEngine(@RequestBody String login) throws Exception {
		return (String)routerService.scriptingEngine(login);
	}
	
	@RequestMapping(value = "/camel", method = RequestMethod.GET)
	@ApiOperation(value = "camel")
	public String camel( String login) throws Exception {
		//camelContext.stop();
		
		String TEST_PATH_QUALITY = "./src/main/resources/router-camel-context2.xml";
		
		InputStream is = Utils.getResourceFromFile(new File(TEST_PATH_QUALITY));
		RoutesDefinition routes = camelContext.loadRoutesDefinition(is);
		
		camelContext.addRouteDefinitions(routes.getRoutes());
		
		return routes.toString();
	}
	
	@RequestMapping(value = "/pepe", method = RequestMethod.GET)
	@ApiOperation(value = "pepe")
	public String camel2( String login) throws Exception {
		
		String TEST_PATH_QUALITY = "./src/main/resources/imaging-camel-context.xml";
		
		loadRoutes(TEST_PATH_QUALITY);
		return TEST_PATH_QUALITY.toString();
	}
	
	public void loadRoutes(String routestr) {
		  if (routestr != null && !routestr.isEmpty()) {
		    try {
		      RoutesDefinition routes = camelContextHandler.getCamelContext(0).loadRoutesDefinition(IOUtils.toInputStream(routestr, "UTF-8"));

		      camelContextHandler.getCamelContext(0).addRouteDefinitions(routes.getRoutes());

		    } catch (Exception e) {
		      // Log error
		    }
		  }
		}

}
