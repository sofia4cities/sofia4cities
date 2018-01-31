package com.indracompany.sofia2.controlpanel.controller.twitter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.service.ontology.OntologyService;
import com.indracompany.sofia2.service.twitter.TwitterService;

@Controller
@RequestMapping("/twitter")
public class TwitterListenerController {
	
	@Autowired
	AppWebUtils utils;
	@Autowired
	TwitterService twitterService;
	@Autowired
	OntologyService ontologyService;
	public static final String ROLE_ADMINISTRATOR="ROLE_ADMINISTRATOR";
	
	@GetMapping("/scheduledsearch/list")
	public String list(Model model)
	{
		model.addAttribute("twitterListens", this.twitterService.getAllListensByUserId(utils.getUserId()));
		return "redirect:/main";
	}
	@GetMapping("/scheduledsearch/create")
	public String createForm(Model model)
	{
		List<Configuration> configurations;
		List<Ontology> ontologies;
		if(this.utils.getRole().equals(ROLE_ADMINISTRATOR))
		{
			configurations=this.twitterService.getAllConfigurations();
			ontologies=this.ontologyService.getAllOntologies();
		}else
		{
			configurations=this.twitterService.getConfigurationsByUserId(this.utils.getUserId());
			ontologies=this.ontologyService.getOntologiesByUserId(this.utils.getUserId());
			
		}
		model.addAttribute("configurations",configurations);
		model.addAttribute("ontologies",ontologies);
		return "redirect:/main";
	}

}
