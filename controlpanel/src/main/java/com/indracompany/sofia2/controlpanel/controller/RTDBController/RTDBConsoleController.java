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
package com.indracompany.sofia2.controlpanel.controller.RTDBController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.service.ontology.OntologyService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/databases")
@Slf4j
public class RTDBConsoleController {
	@Autowired
	private OntologyService ontologyService;

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping("show")
	public String show(Model model) {
		List<Ontology> ontologies = this.ontologyService.getAllOntologies();
		model.addAttribute("ontologies", ontologies);
		return "/databases/show";

	}

}
