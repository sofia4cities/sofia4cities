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
package com.indracompany.sofia2.controlpanel.controller.datamodel;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.WebProject;
import com.indracompany.sofia2.config.services.datamodel.DataModelService;
import com.indracompany.sofia2.config.services.exceptions.DataModelServiceException;
import com.indracompany.sofia2.config.services.exceptions.UserServiceException;
import com.indracompany.sofia2.config.services.exceptions.WebProjectServiceException;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/datamodels")
@Slf4j
public class DataModelController {

	@Autowired //TODO use final and use constructor injection.
	private DataModelService dataModelService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AppWebUtils utils;
	

	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, 
			@RequestParam(required = false) String dataModelId,
			@RequestParam(required = false) String name, 
			@RequestParam(required = false) String description) {
		
		if ("".equals(dataModelId)) { 
			dataModelId = null;
		}
		
		if ("".equals(name)) {
			name = null;
		}
		
		if ("".equals(description)) {
			description = null;
		}
		

		if ((dataModelId == null) && (name == null) && (description == null)) {
			log.debug("No params for filtering, loading all Data Models");
			model.addAttribute("dataModels", this.dataModelService.getAllDataModels());

		} else {
			log.debug("Params detected, filtering Data Models...");
			model.addAttribute("dataModels",
					this.dataModelService.getDataModelsByCriteria(dataModelId, name, description));
		}

		return "datamodels/list";
	}	
	
	private void populateFormData(Model model) {
		model.addAttribute("dataModelTypes", this.dataModelService.getAllDataModelsTypes());
	}

	@GetMapping(value = "/show/{id}", produces = "text/html")
	public String show(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {
		DataModel dataModel= null;	
		if (id != null) {
			dataModel = this.dataModelService.getDataModelById(id);
		}
		if (dataModel == null)
			return "error/404";
		if (!this.utils.getUserId().equals(	dataModel.getUser().getUserId()) && !utils.isAdministrator())
			return "error/403";
		model.addAttribute("dataModel", dataModel);
		return "datamodels/show";
	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/create", produces = "text/html")
	public String create(Model model, @Valid DataModel dataModel, BindingResult bindingResult) {

		this.populateFormData(model);
		if (bindingResult.hasErrors())
			model.addAttribute("datamodel", new DataModel()); 
		return "/datamodels/create";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PostMapping(value = "/create", produces = "text/html")
	public String createDataModel(Model model, @ModelAttribute DataModel datamodel, BindingResult bindingResult, RedirectAttributes redirect, HttpServletRequest request) {

		if(dataModelService.dataModelExists(datamodel)) {
			log.error("This DataModel already exist" );
			utils.addRedirectMessage("datamodel.error.exist", redirect);
			return "redirect:/datamodels/list";
		}
		try {
			datamodel.setUser(userService.getUserByIdentification(utils.getUserId()));
			dataModelService.createDataModel(datamodel);
			log.error("DataModel created correctly");
			return "redirect:/datamodels/list"; // show/"+ datamodel.getId(); //show/"+ datamodel.getId();
			
		}catch (DataModelServiceException e) {
			log.error("Cannot create datamodel " );
			utils.addRedirectMessage("datamodel.error.created", redirect);
			return "redirect:/datamodels/create";
		}
	}
	 
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/update/{id}")
	public String update(@PathVariable String id, Model model, HttpServletRequest request) {
		
		try {
			this.populateFormData(model);
			DataModel datamodel = this.dataModelService.getDataModelById(id);
			if(datamodel != null) {
				log.debug("Update Data Model");
				model.addAttribute("datamodel", datamodel);
				return "datamodels/create";
			}else
				return "datamodels/create";
		}catch (DataModelServiceException e) {
			return "datamodels/create";
		}	
	}

	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PutMapping(value="/update/{id}" , produces = "text/html")
	public String updateDataModel(@PathVariable String id, Model model, @ModelAttribute DataModel datamodel, RedirectAttributes redirect, HttpServletRequest request) {
				
		if(datamodel != null) {
			datamodel.setUser(userService.getUserByIdentification(utils.getUserId()));
			if(!this.utils.getUserId().equals(datamodel.getUser().getUserId()) && !utils.isAdministrator())
				return "error/403";
			try {
				this.dataModelService.updateDataModel(datamodel);
			}catch(DataModelServiceException e) {
				log.debug("Could not update the Data Model");
				utils.addRedirectMessage("datamodel.error.update", redirect);
				return "datamodels/create";
			}
		}else {
			return "redirect:/update/" + datamodel.getId();
		}
			model.addAttribute("Datamodel", datamodel);
			log.debug("Data Mode has been update succesfully");
			return "redirect:/datamodels/show/" + datamodel.getId();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@DeleteMapping("/delete/{id}")
	public String deleteDataModel(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {
		
		DataModel dataModel = dataModelService.getDataModelById(id);
		if (dataModel != null) {
			try {
				this.dataModelService.deleteDataModel(id);
			}catch (DataModelServiceException e) {
				log.debug("Could not delete the Data Model");
				utils.addRedirectMessage("datamodel.error.delete", redirect);
				return "redirect:/datamodels/list";
			}
			log.debug("The Data Model has been created correctly");
			return "redirect:/datamodels/list";
		}else {
			log.debug("The Data Model does not exist");
			return "redirect:/datamodels/list";
		}
	}
	
}

