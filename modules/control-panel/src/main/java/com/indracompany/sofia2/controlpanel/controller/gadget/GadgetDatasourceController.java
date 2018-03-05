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
package com.indracompany.sofia2.controlpanel.controller.gadget;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.GadgetDatasource;
import com.indracompany.sofia2.config.services.exceptions.GadgetDatasourceServiceException;
import com.indracompany.sofia2.config.services.gadget.GadgetDatasourceService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.persistence.services.QueryToolService;
import org.springframework.security.access.AccessDeniedException;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/datasources")
@Controller
@Slf4j
public class GadgetDatasourceController {

		@Autowired
		private GadgetDatasourceService gadgetDatasourceService;
		
		@Autowired
		private OntologyService ontologyService; 
		
		@Autowired
		private UserService userService; 
		
		@Autowired
		private QueryToolService queryToolService;
		
		@Autowired
		private AppWebUtils utils;
		
		@RequestMapping(value = "/list", produces = "text/html")
		public String list (Model uiModel, HttpServletRequest request) {
					
			String identification = request.getParameter("identification");
			String description = request.getParameter("description");
			
			if(identification!=null){if(identification.equals("")) identification=null;}
			if(description!=null){if(description.equals("")) description=null;}

			List<GadgetDatasource> datasource=this.gadgetDatasourceService.findGadgetDatasourceWithIdentificationAndDescription( identification, description, utils.getUserId());
					
			uiModel.addAttribute("datasources", datasource);
			return "/datasources/list";
					
		}
			
		@PostMapping( value="getNamesForAutocomplete")
		public @ResponseBody List<String> getNamesForAutocomplete(){
			return this.gadgetDatasourceService.getAllIdentifications();
		}
		
		public void showDatasources(Model model) {
			model.addAttribute("datasources", this.gadgetDatasourceService.getAllIdentifications());
			
		}
		
		@GetMapping(value = "/create", produces = "text/html")
		public String createGadget(Model model) {
			model.addAttribute("datasource",new GadgetDatasource());
			model.addAttribute("ontologies", ontologyService.getOntologiesByUserId(utils.getUserId()));
			return "/datasources/create";

		}
		
		@PostMapping(value = {"/create"})
		public String createOntology(Model model,
				@Valid GadgetDatasource gadgetDatasource, BindingResult bindingResult,
				RedirectAttributes redirect) {
			if(bindingResult.hasErrors())
			{
				log.debug("Some gadget datasource properties missing");
				utils.addRedirectMessage("gadgetDatasource.validation.error", redirect);
				return "redirect:/datasources/create";
			}
			try{
				gadgetDatasource.setUser(this.userService.getUser(this.utils.getUserId()));
				this.gadgetDatasourceService.createGadgetDatasource(gadgetDatasource);
			}catch (GadgetDatasourceServiceException e)
			{
				log.debug("Cannot create gadget datasource");
				utils.addRedirectMessage("gadgetDatasource.create.error", redirect);
				return "redirect:/datasources/create";
			}
			utils.addRedirectMessage("gadgetDatasource.create.success", redirect);
			return "redirect:/datasources/list";
		}
		
		@GetMapping(value = "/update/{id}", produces = "text/html")
		public String update(Model model, @PathVariable ("id") String id) {
			GadgetDatasource gadgetDatasource = this.gadgetDatasourceService.getGadgetDatasourceById(id);
			if(gadgetDatasource!=null){
				if (!gadgetDatasourceService.hasUserPermission(id, this.utils.getUserId()))
					return "/error/403";
				model.addAttribute("datasource", gadgetDatasource);
				return "/datasources/create";
			}else
				return "/error/404";
			
			
		}

		@PutMapping(value = "/update/{id}", produces = "text/html")
		public String updateGadgetDatasource(Model model, @PathVariable ("id") String id,
				@Valid GadgetDatasource gadgetDatasource, BindingResult bindingResult,
				RedirectAttributes redirect) {
			
			if(bindingResult.hasErrors())
			{
				log.debug("Some Gadget Datasource properties missing");
				utils.addRedirectMessage("gadgetDatasource.validation.error", redirect);
				return "redirect:/datasources/update/"+id;
			}
			if (!gadgetDatasourceService.hasUserPermission(id, this.utils.getUserId()))
				return "/error/403";
			try {
				this.gadgetDatasourceService.updateGadgetDatasource(gadgetDatasource);
			}catch (GadgetDatasourceServiceException e)
			{
				log.debug("Cannot update gadget datasource");
				utils.addRedirectMessage("gadgetDatasource.update.error", redirect);
				return "redirect:/datasources/create";
			}
			
			utils.addRedirectMessage("gadgetDatasource.update.success", redirect);
			return "redirect:/datasources/list";
		}
		
		@DeleteMapping("/{id}")
		public String delete(Model model, @PathVariable("id") String id) {

			this.gadgetDatasourceService.deleteGadgetDatasource(id);
			return "redirect:/datasources/list";
		}
		
		@GetMapping(value = "/getUserGadgetDatasources", produces="application/json")
		public @ResponseBody List<GadgetDatasource> getUserGadgetDatasources(){
			return this.gadgetDatasourceService.getUserGadgetDatasources(utils.getUserId());
		}
		
		@GetMapping(value = "/getSampleDatasource/{id}", produces="application/json")
		public @ResponseBody String getSampleDatasource(@PathVariable("id") String datasourceId){
			String sampleQuery = this.gadgetDatasourceService.getSampleQueryGadgetDatasourceById(datasourceId);
			return queryToolService.querySQLAsJson(this.utils.getUserId(),"", sampleQuery, 0);
		}
	}