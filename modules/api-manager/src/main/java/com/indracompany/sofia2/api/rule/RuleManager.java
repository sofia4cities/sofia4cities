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
package com.indracompany.sofia2.api.rule;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineListener;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


@Service
public class RuleManager implements ApplicationContextAware{

	public static final String FACTS="facts";
	public static final String REQUEST = "request";
	public static final String ACTION = "action";
	public static final String REASON = "reason";
	
	public static final String STOP_STATE = "STOP_STATE";
	
	@Autowired
	private ApplicationContext applicationContext;

	private Rules rules;
	private RulesEngine rulesEngine;
	
	@Autowired(required=false)
	private RulesEngineListener listener=null;
	
	
	@PostConstruct
	public void initIt() throws Exception {
		register();
	}
	public Rules register() {
		rulesEngine = new DefaultRulesEngine();
		rules = new Rules();
		
		/*if (listener!=null) {
			rulesEngine.getRulesEngineListeners().add(listener);
		}*/

		Map<String, Object> beansOfTypeRule = (Map<String, Object>) applicationContext.getBeansWithAnnotation(org.jeasy.rules.annotation.Rule.class);

		Iterator<Entry<String, Object>> iterator = beansOfTypeRule.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> next = iterator.next();
			Object objeto = next.getValue();
			rules.register(objeto);
		}
		return rules;
	}
	
	public void fire(Facts facts) {
		rulesEngine.fire(rules, facts);
		
	}

	
	public RulesEngineListener getListener() {
		return listener;
	}
	public void setListener(RulesEngineListener listener) {
		this.listener = listener;
	}
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
	}

}
