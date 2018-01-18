package com.indracompany.sofia2.controlpanel.controller.ontologies;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.indracompany.sofia2.config.model.Ontology;

import com.indracompany.sofia2.config.repository.OntologyRepository;



@RequestMapping("/ontologies")
@Controller
public class OntologyController {
	
	@Autowired
	private OntologyRepository ontologyRepository;
	
	
	@RequestMapping(value = "/list" , produces = "text/html")
	public String listOntologies(Model uiModel,HttpServletRequest request)
	{
		
		List<Ontology> ontologies;
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		String userRole=authentication.getAuthorities().toArray()[0].toString();
		if(userRole.equals("ROLE_ADMINISTRATOR"))
		{
			ontologies=this.ontologyRepository.findAll();
		}else
		{
			ontologies=this.ontologyRepository.findByUserId(authentication.getName());
		}
		uiModel.addAttribute("ontologies",ontologies);
		return "/ontologies/list";
	}
	
	

}
