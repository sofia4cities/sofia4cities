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

import com.indracompany.sofia2.api.rest.api.fiql.ApiFIQL;
import com.indracompany.sofia2.api.rule.DefaultRuleBase;
import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.api.service.api.ApiManagerService;
import com.indracompany.sofia2.api.service.api.ApiSecurityService;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.user.UserService;

@Component
@Rule
public class SecurityRule extends DefaultRuleBase {
	
	@Autowired
	private ApiSecurityService apiSecurityService;

	@Priority
	public int getPriority() {
		return 3;
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
		
		boolean checkLimit=false;
		boolean checkUser = apiSecurityService.checkUserApiPermission(api, user);
		if (checkUser==false) 
			stopAllNextRules(facts, "User has no permission to use API");
		
		else {
			checkLimit =  apiSecurityService.checkApiLimit(api);
			if (checkLimit==false)
				stopAllNextRules(facts, "User API Limit Reached");
		}
		
		
	}

	
}