package com.indracompany.sofia2.api.rule;

import java.util.HashMap;
import java.util.Map;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;

public class RulesEngineListener implements org.jeasy.rules.api.RulesEngineListener {

	@Override
	public void beforeEvaluate(Rules rules, Facts facts) {
		Map<String,Object> dataFact=new HashMap<String,Object>();
		facts.put(RuleManager.FACTS, dataFact);

	}

	@Override
	public void afterExecute(Rules rules, Facts facts) {
		
		
	}

}
