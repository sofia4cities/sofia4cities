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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.client.dto.DeviceDTO;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
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
	private GraphDeviceUtil graphDeviceUtil;

	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, @RequestParam(required = false) String identification,
			@RequestParam(required = false) String[] ontologies) {

		if (identification != null) {
			if (identification.equals(""))
				identification = null;
		}

		if (ontologies != null) {
			if (ontologies.length == 0)
				ontologies = null;
		}
		this.pupulateClientList(model, this.clientPlatformService.getAllClientPlatformByCriteria(utils.getUserId(),
				identification, ontologies));

		return "/devices/management/list";

	}

	private void pupulateClientList(Model model, List<ClientPlatform> clients) {

		List<DeviceDTO> devicesDTO = new ArrayList<DeviceDTO>();

		if (clients != null && clients.size() > 0) {
			for (ClientPlatform client : clients) {
				DeviceDTO deviceDTO = new DeviceDTO();
				deviceDTO.setUser(client.getUser().getUserId());
				deviceDTO.setDateCreated(client.getCreatedAt());
				deviceDTO.setDescription(client.getDescription());
				deviceDTO.setId(client.getId());
				deviceDTO.setIdentification(client.getIdentification());
				if (client.getClientPlatformOntologies() != null && client.getClientPlatformOntologies().size() > 0) {
					List<String> list = new ArrayList<String>();
					for (ClientPlatformOntology cpo : client.getClientPlatformOntologies()) {
						list.add(cpo.getOntology().getIdentification());
					}
					deviceDTO.setOntologies(StringUtils.arrayToDelimitedString(list.toArray(), ", "));
				}
				devicesDTO.add(deviceDTO);
			}
		}

		model.addAttribute("devices", devicesDTO);
		model.addAttribute("ontologies",
				ontologyService.getOntologiesWithDescriptionAndIdentification(utils.getUserId(), null, null));
		model.addAttribute("accessLevel", clientPlatformService.getClientPlatformOntologyAccessLevel());
	}

	@GetMapping("/show")
	public String show(Model model, RedirectAttributes redirect) {
		return "devices/management/show";
	}

	@GetMapping("/getgraph")
	public @ResponseBody String getGraph(Model model) {

		List<GraphDeviceDTO> arrayLinks = new LinkedList<GraphDeviceDTO>();

		arrayLinks.addAll(graphDeviceUtil.constructGraphWithClientPlatformsForUser());

		return arrayLinks.toString();
	}

}
