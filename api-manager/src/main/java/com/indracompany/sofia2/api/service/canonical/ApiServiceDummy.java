package com.indracompany.sofia2.api.service.canonical;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;

@Service
public class ApiServiceDummy implements ApiServiceInterface {
	
	@Autowired
	RuleManager ruleManager;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Facts facts = new Facts();
		facts.put(RuleManager.REQUEST, request);
		facts.put(RuleManager.ACTION, "GET");
		ruleManager.fire(facts);

		

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

	public RuleManager getRuleManager() {
		return ruleManager;
	}

	public void setRuleManager(RuleManager ruleManager) {
		this.ruleManager = ruleManager;
	}
	
	

}
