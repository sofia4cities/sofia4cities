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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rule.DefaultRuleBase;
import com.indracompany.sofia2.api.rule.RuleManager;

@Component
@Rule
public class XFrameOptionsRule extends DefaultRuleBase {

	@Priority
	public int getPriority() {
		return 5;
	}

	@Condition
	public boolean existsRequest(Facts facts) {
		HttpServletResponse response = (HttpServletResponse) facts.get(RuleManager.RESPONSE);

		if ((response != null) && canExecuteRule(facts))
			return true;
		else
			return false;
	
	}

	@Action
	public void setFirstDerivedData(Facts facts) throws IOException, ParseException {
		HttpServletResponse response = (HttpServletResponse) facts.get(RuleManager.RESPONSE);
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
		//response.addHeader("X-Frame-Options", "SAMEORIGIN");
	
	}
	
	
}