/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.ontologydata.OntologyDataService;

@Component
@Rule
public class OngologySchemaRule extends DefaultRuleBase {
	
	@Autowired
	OntologyDataService service;
	
	@Priority
	public int getPriority() {
		return 4;
	}
	
	@Condition
	public boolean dataHasSchemaCompliance(Facts facts) {
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);

		String body = (String) data.get(ApiServiceInterface.BODY);
		Ontology ontology = (Ontology) data.get(ApiServiceInterface.ONTOLOGY);
		String method = (String) data.get(ApiServiceInterface.METHOD);
		
		switch (method) {
		case "POST": 
			boolean valid = service.hasOntologySchemaCompliance(body, ontology);
			return !valid;
		default:
			return false;
		}
	}
	
	@Action
	public void invalidInsert(Facts facts) {
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);
		String body = (String) data.get(ApiServiceInterface.BODY);
		
		stopAllNextRules(facts, "Body is not copliant with the ontology schema: "+body, DefaultRuleBase.ReasonType.GENERAL);
	}
}
