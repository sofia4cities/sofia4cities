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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.services.datamodel.DataModelService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/datamodels")
@Slf4j
public class DataModelController {

	@Autowired //TODO use final and use constructor injection.
	private DataModelService dataModelService;
	
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
	
	@GetMapping("/show/{id}")
	public String show(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {
		try {
			DataModel dataModel = dataModelService.getDataModelById(id);
			if (dataModel != null) {

				model.addAttribute("dataModel", dataModel);
				return "datamodels/show";
				
			} else {
				utils.addRedirectMessage("datamodel.notfound.error", redirect);
				return "redirect:/datamodels/list";
			}
		} catch(RuntimeException e) {
			return "redirect:/datamodels/list";
		}
			
	}
	

	
}
