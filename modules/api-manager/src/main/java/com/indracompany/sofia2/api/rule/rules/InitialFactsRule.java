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
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;

@Component
@Rule
public class InitialFactsRule {

	@Priority
	public int getPriority() {
		return 1;
	}

	@Condition
	public boolean existsRequest(Facts facts) {
		HttpServletRequest request = (HttpServletRequest) facts.get(RuleManager.REQUEST);
		if (request != null)
			return true;
		else
			return false;
	}

	@Action
	public void setFirstDerivedData(Facts facts) {
		HttpServletRequest request = (HttpServletRequest) facts.get(RuleManager.REQUEST);
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);

		String query = Optional.ofNullable(request.getParameter(ApiServiceInterface.QUERY)).orElse("");
		String queryType = Optional.ofNullable(request.getParameter(ApiServiceInterface.QUERY_TYPE)).orElse("");

		String headerToken = request.getHeader(ApiServiceInterface.AUTHENTICATION_HEADER);
		if (headerToken == null) {
			headerToken = request.getParameter(ApiServiceInterface.AUTHENTICATION_HEADER);
		}

		headerToken = Optional.ofNullable(headerToken).orElse("");
		
		String method = request.getMethod();
		String pathInfo = request.getPathInfo();

		String queryDb = Optional.ofNullable(request.getParameter(ApiServiceInterface.FILTER_PARAM)).orElse("");
		String targetDb = Optional.ofNullable(request.getParameter(ApiServiceInterface.TARGET_DB_PARAM)).orElse("");
		String formatResult = Optional.ofNullable(request.getParameter(ApiServiceInterface.FORMAT_RESULT)).orElse("");

		data.put(ApiServiceInterface.QUERY, query);
		data.put(ApiServiceInterface.QUERY_TYPE, queryType);
		data.put(ApiServiceInterface.AUTHENTICATION_HEADER, headerToken);
		data.put(ApiServiceInterface.PATH_INFO, pathInfo);
		data.put(ApiServiceInterface.FILTER_PARAM, queryDb);
		data.put(ApiServiceInterface.TARGET_DB_PARAM, targetDb);
		data.put(ApiServiceInterface.FORMAT_RESULT, formatResult);
		data.put(ApiServiceInterface.METHOD, method);

	}
}