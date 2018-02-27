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
package com.indracompany.sofia2.router.service.proxy;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.router.service.RouterService;

@Service("routerServiceProxy")
public class RouterServiceProxy implements RouterService {
	
	 @Autowired
	 CamelContext camelContext;
	
	public Object nodeRed(Object input) throws Exception {
		
		ProducerTemplate t = camelContext.createProducerTemplate();
		String result = (String)t.requestBody("direct:node-red-proxy", input);
		
		return result;
		
	}
	
	public Object scriptingEngine(Object input) throws Exception {
		
		ProducerTemplate t = camelContext.createProducerTemplate();
		String result = (String)t.requestBody("direct:scripting-engine-proxy", input);
		
		return result;
		
	}
	


}