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
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.security.jwt.ri.JWTService;

@Component
@Rule
public class UserAndAPIRule extends DefaultRuleBase {
	
	@Autowired
	private ApiManagerService apiManagerService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JWTService jwtService;
	
	

	@Priority
	public int getPriority() {
		return 2;
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
		facts.get(RuleManager.REQUEST);
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);

		String PATH_INFO = (String) data.get(ApiServiceInterface.PATH_INFO);
		String TOKEN = (String) data.get(ApiServiceInterface.AUTHENTICATION_HEADER);
		String JWT_TOKEN = (String) data.get(ApiServiceInterface.JWT_TOKEN);
		User user =null;
		try {
			user = userService.getUserByToken(TOKEN);
		} catch (Exception e) {}
		
		Api api=null;
		if (user==null) {
			if (JWT_TOKEN.length()>0) {
				String userid = jwtService.extractToken(JWT_TOKEN);
				if (userid!=null)
					user=userService.getUser(userid);
			}
		}
		if (user==null) {
			stopAllNextRules(facts, "User not Found by Token :"+TOKEN, DefaultRuleBase.ReasonType.GENERAL);
		}
		
		else {
			api = apiManagerService.getApi(PATH_INFO, user);
			if (api==null) stopAllNextRules(facts, "API not Found by Token :"+TOKEN +" and Path Info"+PATH_INFO,DefaultRuleBase.ReasonType.GENERAL );
		}

		data.put(ApiServiceInterface.USER, user);
		data.put(ApiServiceInterface.API, api);
	}

	
}