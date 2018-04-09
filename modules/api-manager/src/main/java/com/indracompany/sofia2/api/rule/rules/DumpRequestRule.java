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
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.util.RequestDumpUtil;

@Component
@Rule
public class DumpRequestRule {

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

		StringBuilder sb = new StringBuilder();

		RequestDumpUtil.dumpRequest(sb, request);
		RequestDumpUtil.dumpRequestHeader(sb, request);
		RequestDumpUtil.dumpRequestParameter(sb, request);
		RequestDumpUtil.dumpRequestSessionAttribute(sb, request);

		System.out.println(sb.toString());

		// data.put(ApiServiceInterface.DUMP, sb.toString());

	}
}