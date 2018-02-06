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
package com.indracompany.sofia2.api.rule.rules;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rule.DefaultRuleBase;
import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.api.service.api.ApiManagerService;
import com.indracompany.sofia2.api.service.api.ApiSecurityService;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;

@Component
@Rule
public class OntologyRule extends DefaultRuleBase {
	
	@Autowired
	private ApiSecurityService apiSecurityService;
	@Autowired
	private ApiManagerService apiManagerService;

	@Priority
	public int getPriority() {
		return 4;
	}

	@Condition
	public boolean existsRequest(Facts facts) {
		HttpServletRequest request = (HttpServletRequest) facts.get(RuleManager.REQUEST);
		if ((request != null) && canExecuteRule(facts))
			return true;
		else
			return false;
	}

	@Action
	public void setFirstDerivedData(Facts facts) {
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);

		User user = (User) data.get(ApiServiceInterface.USER);
		Api api = (Api) data.get(ApiServiceInterface.API);
		String pathInfo = (String) data.get(ApiServiceInterface.PATH_INFO);
		String method = (String) data.get(ApiServiceInterface.METHOD);
		String body = (String) data.get(ApiServiceInterface.BODY);
		
		Ontology ontology = api.getOntology();
		if (ontology!=null) {
			ApiOperation customSQL = apiManagerService.getCustomSQL(pathInfo, api,method);
			Boolean isPathQuery = apiManagerService.isPathQuery(pathInfo);
			
			// Si la invocacion es un GET con ID
			if (isPathQuery && customSQL==null) {
				System.out.println("GET WITH ID");
			}
			// Si es una invocacion sin ID y no CUSTOM 
			else if (customSQL==null){
				System.out.println("GET WITH NO ID AND NO CUSTOM"); 
			 }
			// Si es un metodo CUSTOMSQL
			else {
				System.out.println("CUSTOM"); 
			 }
			
			
			System.out.println(customSQL);
			
			//Guess type of operation!!!
			
		}
		
		
	}

	
}