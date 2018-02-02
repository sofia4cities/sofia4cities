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
package com.indracompany.sofia2.controlpanel.controller.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.service.ontology.OntologyService;

import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/ontologies")
@Slf4j
public class OntologyController {

	@Autowired
	private OntologyService ontologyService;

	@Autowired
	private AppWebUtils utils;
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/list",produces = "text/html")
	public String list(Model model, HttpServletRequest request, @RequestParam(required=false, name="identification")String identification,@RequestParam(required=false, name="description")String description) {
		
		//Scaping "" string values for parameters 
		if(identification!=null){if(identification.equals("")) identification=null;}
		if(description!=null){if(description.equals("")) description=null;}

		List<Ontology> ontologies=this.ontologyService.getOntolgiesWithDescriptionAndIdentification(utils.getUserId(), identification, description);
		
		model.addAttribute("ontologies", ontologies);
		return "/ontologies/list";
	}
	@RequestMapping(method = RequestMethod.POST, value="getNamesForAutocomplete")
	public @ResponseBody List<String> getNamesForAutocomplete(){
		return this.ontologyService.getAllIdentifications();
	}
	@GetMapping(value = "/create",produces = "text/html")
	public String create(Model model)
	{		
		model.addAttribute("ontology", new Ontology());
		return "/ontologies/create";
	}
	
	@GetMapping(value = "/createwizard",produces = "text/html")
	public String createWizard(Model model)
	{
		model.addAttribute("ontology", new Ontology());
		return "/ontologies/createwizard";
	}
	
	
}
