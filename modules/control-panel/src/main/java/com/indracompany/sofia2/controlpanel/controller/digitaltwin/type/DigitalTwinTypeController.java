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
package com.indracompany.sofia2.controlpanel.controller.digitaltwin.type;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

import com.indracompany.sofia2.config.model.DigitalTwinType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.service.digitaltwin.device.DigitalTwinDeviceService;
import com.indracompany.sofia2.config.service.digitaltwin.type.DigitalTwinTypeService;
import com.indracompany.sofia2.config.services.exceptions.DigitalTwinServiceException;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

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

	@Autowired
	private DigitalTwinDeviceService digitalTwinDeviceService;

	@Autowired
	@Qualifier("MongoManageDBRepository")
	private ManageDBRepository mongoManageRepo;

	@PostMapping("/getNamesForAutocomplete")
	public @ResponseBody List<String> getNamesForAutocomplete() {
		return this.digitalTwinTypeService.getAllIdentifications();
	}

	@GetMapping(value = "/create")
	public String create(Model model) {
		model.addAttribute("digitaltwintype", new DigitalTwinType());
		return "digitaltwintypes/create";
	}

	@GetMapping(value = "/update/{id}", produces = "text/html")
	public String update(Model model, @PathVariable("id") String id) {
		digitalTwinTypeService.getDigitalTwinToUpdate(model, id);
		return "digitaltwintypes/create";
	}

	@PostMapping(value = "/create")
	@Transactional
	public String createDigitalTwinType(Model model, @Valid DigitalTwinType digitalTwinType,
			BindingResult bindingResult, RedirectAttributes redirect, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			log.debug("Some digital twin type properties missing");
			utils.addRedirectMessage("digitaltwintype.validation.error", redirect);
			return "redirect:/digitaltwintypes/create";
		}
		try {
			User user = userService.getUser(utils.getUserId());
			digitalTwinType.setUser(user);
			digitalTwinTypeService.createDigitalTwinType(digitalTwinType, httpServletRequest);
			// Create collections on mongo for properties and actions
			mongoManageRepo.createTable4Ontology("TwinProperties"
					+ digitalTwinType.getName().substring(0, 1).toUpperCase() + digitalTwinType.getName().substring(1),
					"{}");
			mongoManageRepo.createTable4Ontology("TwinActions" + digitalTwinType.getName().substring(0, 1).toUpperCase()
					+ digitalTwinType.getName().substring(1), "{}");
		} catch (DigitalTwinServiceException e) {
			log.error("Cannot create digital twin type because of:" + e.getMessage());
			utils.addRedirectException(e, redirect);
			return "redirect:/digitaltwintypes/create";
		}
		return "redirect:/digitaltwintypes/list";
	}

	@GetMapping(value = "/list")
	public String list(Model model) {
		model.addAttribute("digitalTwinTypes", digitalTwinTypeService.getAll());
		return "digitaltwintypes/list";
	}

	@GetMapping(value = "/show/{id}")
	public String show(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {
		DigitalTwinType type = digitalTwinTypeService.getDigitalTwinTypeById(id);
		if (type != null) {
			model.addAttribute("digitaltwintype", type);
			model.addAttribute("properties", digitalTwinTypeService.getPropertiesByDigitalId(id));
			model.addAttribute("actions", digitalTwinTypeService.getActionsByDigitalId(id));
			model.addAttribute("events", digitalTwinTypeService.getEventsByDigitalId(id));
			model.addAttribute("logic", digitalTwinTypeService.getLogicByDigitalId(id));

			return "digitaltwintypes/show";
		} else {
			utils.addRedirectMessage("digitaltwintype.notfound.error", redirect);
			return "redirect:/digitaltwintypes/list";
		}
	}

	@PutMapping(value = "/update/{id}", produces = "text/html")
	public String updateDigitalTwinType(Model model, @PathVariable("id") String id,
			@Valid DigitalTwinType digitalTwinType, BindingResult bindingResult, RedirectAttributes redirect,
			HttpServletRequest httpServletRequest) {

		if (bindingResult.hasErrors()) {
			log.debug("Some digital twin type properties missing");
			utils.addRedirectMessage("digitaltwintype.validation.error", redirect);
			return "redirect:/digitaltwintypes/update/" + id;
		}

		try {
			User user = userService.getUser(utils.getUserId());
			digitalTwinType.setUser(user);
			this.digitalTwinTypeService.updateDigitalTwinType(digitalTwinType, httpServletRequest);
		} catch (DigitalTwinServiceException e) {
			log.debug("Cannot update Digital Twin Type");
			utils.addRedirectMessage("digitaltwintype.update.error", redirect);
			return "redirect:/digitaltwintypes/create";
		}
		return "redirect:/digitaltwintypes/list";

	}

	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {

		DigitalTwinType digitalTwinType = digitalTwinTypeService.getDigitalTwinTypeById(id);
		if (digitalTwinType != null) {
			try {
				this.digitalTwinTypeService.deleteDigitalTwinType(digitalTwinType);
			} catch (DigitalTwinServiceException e) {
				utils.addRedirectMessage("digitaltwintype.delete.error", redirect);
				return "redirect:/digitaltwintypes/list";
			}
			return "redirect:/digitaltwintypes/list";
		} else {
			return "redirect:/digitaltwintypes/list";
		}
	}

	@GetMapping("/getNumOfDevices/{type}")
	public @ResponseBody Integer getNumOfDevices(@PathVariable("type") String type) {
		return this.digitalTwinDeviceService.getNumOfDevicesByTypeId(type);
	}

}
