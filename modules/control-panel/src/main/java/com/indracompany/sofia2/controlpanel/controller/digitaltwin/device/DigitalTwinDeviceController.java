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
package com.indracompany.sofia2.controlpanel.controller.digitaltwin.device;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.service.digitaltwin.device.DigitalTwinDeviceService;
import com.indracompany.sofia2.config.services.exceptions.DigitalTwinServiceException;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/digitaltwindevices")
public class DigitalTwinDeviceController {
	
	@Autowired
	private AppWebUtils utils;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DigitalTwinDeviceService digitalTwinDeviceService;
	
	@PostMapping("/getNamesForAutocomplete")
	public @ResponseBody List<String> getNamesForAutocomplete() {
		return this.digitalTwinDeviceService.getAllIdentifications();
	}
	
	@GetMapping(value = "/create")
	public String create(Model model) {
		model.addAttribute("digitaltwindevice", new DigitalTwinDevice());
		model.addAttribute("typesDigitalTwin",this.digitalTwinDeviceService.getAllDigitalTwinTypeNames());
		return "digitaltwindevices/create";
	}
	
	@GetMapping(value = "/list")
	public String list(Model model) {
		model.addAttribute("digitalTwinDevices",digitalTwinDeviceService.getAll());
		return "digitaltwindevices/list";
	}
	
	@GetMapping(value = "/generateToken")
	public @ResponseBody String generateToken() {
		return digitalTwinDeviceService.generateToken();	
	}
	
	@GetMapping(value = "/getLogicFromType/{type}")
	public @ResponseBody String getLogicFromType(@PathVariable("type") String type) {
		return digitalTwinDeviceService.getLogicFromType(type);	
	}
	
	@PostMapping(value = "/create")
	@Transactional
	public String createDigitalTwinDevice(Model model, @Valid DigitalTwinDevice digitalTwinDevice, BindingResult bindingResult,
			RedirectAttributes redirect, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			log.debug("Some digital twin device properties missing");
			utils.addRedirectMessage("digitaltwindevice.create.error", redirect);
			return "redirect:/digitaltwindevices/create";
		}
		try {
			User user = userService.getUser(utils.getUserId());
			digitalTwinDevice.setUser(user);
			digitalTwinDeviceService.createDigitalTwinDevice(digitalTwinDevice, httpServletRequest);

		} catch (DigitalTwinServiceException e) {
			log.error("Cannot create digital twin device because of:" + e.getMessage());
			utils.addRedirectException(e, redirect);
			return "redirect:/digitaltwindevices/create";
		}
		return "redirect:/digitaltwindevices/list";
	}
	
	@GetMapping(value = "/show/{id}")
	public String show(Model model,@PathVariable("id") String id, RedirectAttributes redirect) {
		DigitalTwinDevice device = digitalTwinDeviceService.getDigitalTwinDeviceById(id);
		if(device!=null) {
			model.addAttribute("digitaltwindevice", device);
			return "digitaltwindevices/show";
		}else {
			utils.addRedirectMessage("digitaltwindevice.notfound.error", redirect);
			return "redirect:/digitaltwindevices/list";
		}
	}
	
	@GetMapping(value = "/update/{id}", produces = "text/html")
	public String update(Model model, @PathVariable("id") String id) {
		digitalTwinDeviceService.getDigitalTwinToUpdate(model, id);
		model.addAttribute("typesDigitalTwin",this.digitalTwinDeviceService.getAllDigitalTwinTypeNames());
		return "digitaltwindevices/create";
	}
	
	@PutMapping(value = "/update/{id}", produces = "text/html")
	public String updateDigitalTwinDevice(Model model, @PathVariable("id") String id, @Valid DigitalTwinDevice digitalTwinDevice,
			BindingResult bindingResult, RedirectAttributes redirect, HttpServletRequest httpServletRequest) {

		if (bindingResult.hasErrors()) {
			log.debug("Some digital twin device properties missing");
			utils.addRedirectMessage("digitaltwindevice.validation.error", redirect);
			return "redirect:/digitaltwindevices/update/" + id;
		}
		
		try {
			User user = userService.getUser(utils.getUserId());
			digitalTwinDevice.setUser(user);
			this.digitalTwinDeviceService.updateDigitalTwinDevice(digitalTwinDevice, httpServletRequest);
		} catch (DigitalTwinServiceException e) {
			log.debug("Cannot update Digital Twin Device");
			utils.addRedirectMessage("digitaltwindevice.update.error", redirect);
			return "redirect:/digitaltwindevices/create";
		}
		return "redirect:/digitaltwindevices/list";

	}
	
	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {

		DigitalTwinDevice digitalTwinDevice = digitalTwinDeviceService.getDigitalTwinDeviceById(id);
		if (digitalTwinDevice != null) {
			try {
				this.digitalTwinDeviceService.deleteDigitalTwinDevice(digitalTwinDevice);
			} catch (Exception e) {
				utils.addRedirectMessage("digitaltwindevice.delete.error", redirect);
				return "redirect:/digitaltwindevices/list";
			}
			return "redirect:/digitaltwindevices/list";
		} else {
			return "redirect:/digitaltwindevices/list";
		}
	}
	
}
