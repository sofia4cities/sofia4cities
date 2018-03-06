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
import com.indracompany.sofia2.api.util.RequestDumpUtil;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;

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

		String query = Optional.ofNullable(RequestDumpUtil.getValueFromRequest(ApiServiceInterface.QUERY, request)).orElse("");
		String queryType = Optional.ofNullable(RequestDumpUtil.getValueFromRequest(ApiServiceInterface.QUERY_TYPE, request)).orElse(QueryType.NONE.name());
		String contentTypeInput= RequestDumpUtil.getContentType(request);

		String headerToken = request.getHeader(ApiServiceInterface.AUTHENTICATION_HEADER);
		if (headerToken == null) {
			headerToken = request.getParameter(ApiServiceInterface.AUTHENTICATION_HEADER);
		}

		headerToken = Optional.ofNullable(headerToken).orElse("");
		
		String method = request.getMethod();
		String pathInfo = request.getPathInfo();

		String queryDb = Optional.ofNullable(RequestDumpUtil.getValueFromRequest(ApiServiceInterface.FILTER_PARAM, request)).orElse("");
		String targetDb = Optional.ofNullable(RequestDumpUtil.getValueFromRequest(ApiServiceInterface.TARGET_DB_PARAM, request)).orElse("");
		String formatResult = Optional.ofNullable(RequestDumpUtil.getValueFromRequest(ApiServiceInterface.FORMAT_RESULT, request)).orElse("");

		data.put(ApiServiceInterface.QUERY, query);
		data.put(ApiServiceInterface.QUERY_TYPE, queryType);
		data.put(ApiServiceInterface.AUTHENTICATION_HEADER, headerToken);
		data.put(ApiServiceInterface.PATH_INFO, pathInfo);
		data.put(ApiServiceInterface.FILTER_PARAM, queryDb);
		data.put(ApiServiceInterface.TARGET_DB_PARAM, targetDb);
		data.put(ApiServiceInterface.FORMAT_RESULT, formatResult);
		data.put(ApiServiceInterface.METHOD, method);
		data.put(ApiServiceInterface.CONTENT_TYPE_INPUT, contentTypeInput);
		
		facts.put(RuleManager.ACTION, method);
		
		

	}
}