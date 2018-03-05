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

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
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

	public static final String QUERY_SQL = "SQL";
	public static final String QUERY_NATIVE = "NATIVE";

	@GetMapping("show")
	public String show(Model model) {
		List<Ontology> ontologies = null;
		
		ontologies = this.ontologyService.getOntologiesByUserId(utils.getUserId());
		
		model.addAttribute("ontologies", ontologies);

		return "querytool/show";

	}

	@PostMapping("query")
	public String runQuery(Model model, @RequestParam String queryType, @RequestParam String query,
			@RequestParam String ontologyIdentification) throws JsonProcessingException {
		String queryResult = null;
		
		Ontology ontology = ontologyService.getOntologyByIdentification(ontologyIdentification, utils.getUserId());
		
		try {
			if (ontologyService.hasUserPermissionForQuery(utils.getUserId(), ontology)) {
				if (queryType.toUpperCase().equals(QUERY_SQL)) {
					queryResult = queryToolService.querySQLAsJson(ontologyIdentification, query, 0);
					model.addAttribute("queryResult", queryResult);
					return "querytool/show :: query";

				} else if (queryType.toUpperCase().equals(QUERY_NATIVE)) {
					queryResult = queryToolService.queryNativeAsJson(ontologyIdentification, query);
					model.addAttribute("queryResult", utils.getAsObject(queryResult));
					return "querytool/show :: query";
				} else {
					return utils.getMessage("querytool.querytype.notselected", "Please select queryType Native or SQL");
				}
			} else
				return utils.getMessage("querytool.ontology.access.denied.json",
						"You don't have permissions for this ontology");

		} catch (DBPersistenceException e) {
			log.error("Error in runQuery", e);
			model.addAttribute("queryResult", e.getMessage());
			return "querytool/show :: query";
		} catch (Exception e) {
			log.error("Error in runQuery", e);
			model.addAttribute("queryResult",
					utils.getMessage("querytool.query.native.error", "Error malformed query"));
			return "querytool/show :: query";
		}

	}

	@PostMapping("ontologyfields")
	public String getOntologyFields(Model model, @RequestParam String ontologyIdentification)
			throws JsonProcessingException, IOException {

		model.addAttribute("fields", this.ontologyService.getOntologyFields(ontologyIdentification, utils.getUserId()));
		return "querytool/show :: fields";

	}

}
