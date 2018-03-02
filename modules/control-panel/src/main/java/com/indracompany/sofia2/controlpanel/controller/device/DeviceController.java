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
package com.indracompany.sofia2.controlpanel.controller.device;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.deletion.EntityDeletionService;
import com.indracompany.sofia2.config.services.exceptions.ClientPlatformServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/devices")
@Slf4j
public class DeviceController {

	@Autowired
	private ClientPlatformService clientPlatformService;
	@Autowired
	private AppWebUtils utils;
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private EntityDeletionService entityDeletionService;
	@Autowired
	private UserService userService;

	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, @RequestParam(required = false) String identification,
			@RequestParam(required = false) String[] ontologies

	) {

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

		return "/devices/list";

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
					deviceDTO.setOntologies(StringUtils.arrayToCommaDelimitedString(list.toArray()));
				}
				devicesDTO.add(deviceDTO);
			}
		}

		model.addAttribute("devices", devicesDTO);
		populateForm(model);
	}

	@DeleteMapping("/{id}")
	public @ResponseBody String delete(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {
		User user = this.clientPlatformService.getByIdentification(id).getUser();

		try {
			this.entityDeletionService.deleteClient(id);
			// TODO ON DELETE CASCADE
		} catch (Exception e) {
			utils.addRedirectMessage("device.delete.error", redirect);
			return "list";
		}

		return "list";
	}

	@GetMapping(value = "/create")
	public String create(Model model) {
		model.addAttribute("device", new ClientPlatform());
		this.populateForm(model);
		return "/devices/create";
	}

	private void populateForm(Model model) {
		model.addAttribute("ontologies",
				ontologyService.getOntologiesWithDescriptionAndIdentification(utils.getUserId(), null, null));
		List<String> accessLevel = new ArrayList<String>();
		accessLevel.add("Query");
		accessLevel.add("write");
		model.addAttribute("accessLevel", accessLevel);
	}

	@RequestMapping(value = {
			"/create" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String createDevice(@RequestBody ClientPlatform device) {

		try {

			// TODO:Guardar toda la información
			device.setUser(this.userService.getUser(this.utils.getUserId()));

			// this.clientPlatformService.c(ontology);

		} catch (ClientPlatformServiceException e) {
			log.debug("Cannot create clientPlatform");
			return "/controlpanel/devices/create";
		}
		return "/controlpanel/devices/list";
	}

}
