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
package com.indracompany.sofia2.api.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.api.service.api.ApiManagerService;

@Service
public class ApiServiceImpl extends ApiManagerService implements ApiServiceInterface {
	
	@Autowired
	RuleManager ruleManager;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Facts facts = new Facts();
		facts.put(RuleManager.REQUEST, request);
		facts.put(RuleManager.ACTION, "GET");
		Map<String,Object> dataFact=new HashMap<String,Object>();
		facts.put(RuleManager.FACTS, dataFact);
		ruleManager.fire(facts);

		Map<String,Object> data = (Map<String,Object>)facts.get(RuleManager.FACTS);
		
		System.out.println((String)data.get(ApiServiceInterface.QUERY));

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

	}

	

}
