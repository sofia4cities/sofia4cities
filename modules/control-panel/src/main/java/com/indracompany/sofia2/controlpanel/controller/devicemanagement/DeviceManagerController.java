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
package com.indracompany.sofia2.controlpanel.controller.devicemanagement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Device;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.device.DeviceService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/devices/management")
@Slf4j
public class DeviceManagerController {

	@Autowired
	private ClientPlatformService clientPlatformService;
	@Autowired
	private AppWebUtils utils;
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private UserService userService;

	@Autowired
	private GraphDeviceUtil graphDeviceUtil;

	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, @RequestParam(required = false) String identification,
			@RequestParam(required = false) String[] ontologies) {

		if (!this.utils.getRole().equals(Role.Type.ROLE_ADMINISTRATOR.name())) {
			List<Device> devices = new ArrayList<Device>();
			for (ClientPlatform client : this.clientPlatformService
					.getclientPlatformsByUser(this.userService.getUser(this.utils.getUserId()))) {
				devices.addAll(this.deviceService.getByClientPlatformId(client));
			}
			model.addAttribute("devices", devices);
		} else
			model.addAttribute("devices", this.deviceService.getAll());

		return "devices/management/list";

	}

	@GetMapping("/show")
	public String show(Model model, RedirectAttributes redirect) {
		return "devices/management/show";
	}

	@GetMapping("/show/{id}")
	public String info(Model model, RedirectAttributes redirect, @PathVariable String id) {
		Device device = this.deviceService.getById(id);
		if (null == device)
			return "redirect:/devices/management/list";
		model.addAttribute("device", device);
		return "devices/management/info";
	}

	@GetMapping("/getgraph")
	public @ResponseBody String getGraph(Model model) {

		List<GraphDeviceDTO> arrayLinks = new LinkedList<GraphDeviceDTO>();

		arrayLinks.addAll(graphDeviceUtil.constructGraphWithClientPlatformsForUser());

		return arrayLinks.toString();
	}

}
