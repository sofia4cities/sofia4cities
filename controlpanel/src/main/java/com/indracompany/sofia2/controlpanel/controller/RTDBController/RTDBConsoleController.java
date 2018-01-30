package com.indracompany.sofia2.controlpanel.controller.RTDBController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.controlpanel.controller.ontology.OntologyController;
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
	public String show(Model model)
	{
		List<Ontology> ontologies=this.ontologyService.findAllOntologies();
		model.addAttribute("ontologies", ontologies);
		return "/databases/show";
		
	}

}
