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
package com.indracompany.sofia2.controlpanel.controller.querytool;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.persistence.services.QueryToolService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/querytool")
@Slf4j
public class QueryToolController {
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private QueryToolService queryToolService;
	@Autowired
	private AppWebUtils utils;
	
	public static final String QUERY_SQL="SQL";
	public static final String QUERY_NATIVE="NATIVE";

	@GetMapping("show")
	public String show(Model model) {
		List<Ontology> ontologies = null;
		if (utils.isAdministrator()) {
			ontologies = this.ontologyService.getAllOntologies();
		} else {
			ontologies = this.ontologyService.getOntologiesByUserId(utils.getUserId());
		}
		model.addAttribute("ontologies", ontologies);
		return "/querytool/show";

	}
	
	@PostMapping("query")
	public @ResponseBody String runQuery(Model model, @RequestParam String queryType,
			@RequestParam String query, @RequestParam String ontologyIdentification)
	{
		boolean hasUserPermission = ontologyService.hasUserPermissionForQuery(utils.getUserId(), ontologyIdentification);
		
		if(hasUserPermission) {
			if(queryType.toUpperCase().equals(QUERY_SQL))
			{
				return queryToolService.querySQLAsJson(ontologyIdentification, query, 0);
				
			}else if(queryType.toUpperCase().equals(QUERY_NATIVE))
			{
				int limit=0;
				//Limit from query EX: db.Ontology1.find().limit(3);
				if(query.contains("limit")){
					String[] splitLimit = query.split("limit");
					limit=Integer.parseInt(splitLimit[1].replaceAll("[();]", ""));
				}
				return queryToolService.queryNativeAsJson(ontologyIdentification, query, 0, limit).replace("\\\"", "'").replace("\"","");
			}else{
				return utils.getMessage("querytool.querytype.notselected", "{'message' : 'Please select queryType Native or SQL'}");				
			}
		}else
			return utils.getMessage("querytool.ontology.access.denied.json", "{'message' : 'You don't have permissions for this ontology'}");
	
	}

}
