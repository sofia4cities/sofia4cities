package com.indracompany.sofia2.controlpanel.controller.jsontool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.datamodel.DataModelService;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/jsontool")
@Slf4j
public class JsonToolController {

	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private DataModelService dataModelService;
	@Autowired
	private AppWebUtils utils;
	@Autowired
	private UserService userService;

	private static final String DATAMODEL_DEFAULT_NAME = "EmptyBase";

	@GetMapping("show")
	public String show() {
		return "json2ontologytool/import";
	}

	@PostMapping("createontology")
	public @ResponseBody String createOntology(Model model, @RequestParam String ontologyIdentification,
			@RequestParam String schema, @RequestParam String instance) {
		Ontology ontology = new Ontology();
		ontology.setJsonSchema(schema);
		ontology.setIdentification(ontologyIdentification);
		ontology.setActive(true);
		ontology.setDataModel(this.dataModelService.getDataModelByName(DATAMODEL_DEFAULT_NAME));
		ontology.setDescription(ontologyIdentification + " created from schema");
		ontology.setUser(this.userService.getUser(this.utils.getUserId()));
		try {
			this.ontologyService.createOntology(ontology);
		} catch (OntologyServiceException e) {
			return "ko";
		}

		return "ok";
	}
}
