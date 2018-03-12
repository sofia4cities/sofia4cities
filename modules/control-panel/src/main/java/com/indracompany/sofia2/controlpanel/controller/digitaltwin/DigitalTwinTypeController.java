/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.controlpanel.controller.digitaltwin;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.DigitalTwinType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.service.digitaltwin.DigitalTwinTypeService;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/digitaltwintypes")
@Slf4j
public class DigitalTwinTypeController {
	
	@Autowired
	private AppWebUtils utils;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DigitalTwinTypeService digitalTwinTypeService;
	
	@GetMapping(value = "/create")
	public String create(Model model) {
		model.addAttribute("digitaltwintype", new DigitalTwinType());
		return "digitaltwintypes/create";
	}
	
	@PostMapping(value = "/create")
	@Transactional
	public String createDigitalTwinType(Model model, @Valid DigitalTwinType digitalTwinType, BindingResult bindingResult,
			RedirectAttributes redirect, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			log.debug("Some digital twin type properties missing");
			utils.addRedirectMessage("ontology.validation.error", redirect);
			return "redirect:/digitaltwintypes/create";
		}
		try {
			User user = userService.getUser(utils.getUserId());
			digitalTwinType.setUser(user);
			digitalTwinTypeService.createDigitalTwinType(digitalTwinType, httpServletRequest);

		} catch (OntologyServiceException e) {
			log.error("Cannot create digital twin type because of:" + e.getMessage());
			utils.addRedirectException(e, redirect);
			return "redirect:/digitaltwintypes/create";
		}
		return "redirect:/digitaltwintypes/list";
	}

}
