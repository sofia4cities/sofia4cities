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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.client.dto.DeviceCreateDTO;
import com.indracompany.sofia2.config.services.client.dto.DeviceDTO;
import com.indracompany.sofia2.config.services.client.dto.GenerateTokensResponse;
import com.indracompany.sofia2.config.services.client.dto.TokenActivationRequest;
import com.indracompany.sofia2.config.services.client.dto.TokenActivationResponse;
import com.indracompany.sofia2.config.services.client.dto.TokenSelectedRequest;
import com.indracompany.sofia2.config.services.client.dto.TokensRequest;
import com.indracompany.sofia2.config.services.deletion.EntityDeletionService;
import com.indracompany.sofia2.config.services.exceptions.ClientPlatformServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.token.TokenService;
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
	@Autowired
	private TokenService tokenService;

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

		return "devices/list";

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

	@DeleteMapping("/{id}")
	public @ResponseBody String delete(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {

		try {
			ClientPlatform device = clientPlatformService.getByIdentification(id);
			if (!this.utils.getUserId().equals(device.getUser().getUserId()) && !utils.isAdministrator()) {
				utils.addRedirectMessage("device.delete.error", redirect);
				return "/controlpanel/devices/list";
			}
			this.entityDeletionService.deleteClient(id);
		} catch (Exception e) {
			utils.addRedirectMessage("device.delete.error", redirect);
			return "/controlpanel/devices/list";
		}

		return "/controlpanel/devices/list";
	}

	@GetMapping(value = "/create")
	public String create(Model model) {
		model.addAttribute("device", new DeviceCreateDTO());
		model.addAttribute("ontologies",
				ontologyService.getOntologiesWithDescriptionAndIdentification(utils.getUserId(), null, null));
		model.addAttribute("accessLevel", clientPlatformService.getClientPlatformOntologyAccessLevel());
		return "devices/create";
	}

	@PostMapping(value = { "/create" })
	public String createDevice(Model model, @Valid DeviceCreateDTO device, BindingResult bindingResult,
			RedirectAttributes redirect) {

		try {

			ClientPlatform ndevice = new ClientPlatform();
			ndevice.setIdentification(device.getIdentification());
			ndevice.setMetadata(device.getMetadata());
			ndevice.setDescription(device.getDescription());
			ObjectMapper mapper = new ObjectMapper();
			ndevice.setClientPlatformOntologies(new HashSet<ClientPlatformOntology>(mapper
					.readValue(device.getClientPlatformOntologies(), new TypeReference<List<ClientPlatformOntology>>() {
					})));
			ndevice.setUser(this.userService.getUser(this.utils.getUserId()));
			this.clientPlatformService.createClientPlatform(ndevice);

		} catch (ClientPlatformServiceException e) {
			log.debug("Cannot create clientPlatform");
			utils.addRedirectException(e, redirect);
			return "redirect:/devices/create";
		} catch (JsonParseException e) {
			log.debug("Cannot create clientPlatform");
			utils.addRedirectException(e, redirect);
			return "redirect:/devices/create";
		} catch (JsonMappingException e) {
			log.debug("Cannot create clientPlatform");
			utils.addRedirectException(e, redirect);
			return "redirect:/devices/create";
		} catch (IOException e) {
			log.debug("Cannot create clientPlatform");
			utils.addRedirectException(e, redirect);
			return "redirect:/devices/create";
		}
		return "redirect:/devices/list";
	}

	@GetMapping(value = "/update/{id}", produces = "text/html")
	public String update(Model model, @PathVariable("id") String id) {
		ClientPlatform device = this.clientPlatformService.getByIdentification(id);

		if (device != null) {
			if (!this.utils.getUserId().equals(device.getUser().getUserId()) && !utils.isAdministrator()) {
				return "/error/403";
			}
			DeviceCreateDTO deviceDTO = new DeviceCreateDTO();
			deviceDTO.setId(device.getId());
			deviceDTO.setDescription(device.getDescription());
			deviceDTO.setIdentification(device.getIdentification());
			deviceDTO.setMetadata(device.getMetadata());
			deviceDTO.setUserId(device.getUser().getUserId());
			mapOntologiesToJson(model, device, deviceDTO);
			mapTokensToJson(model, device, deviceDTO);
			model.addAttribute("device", deviceDTO);
			model.addAttribute("accessLevel", clientPlatformService.getClientPlatformOntologyAccessLevel());
			return "devices/create";
		} else {
			return "redirect:/devices/list";
		}
	}

	private void mapOntologiesToJson(Model model, ClientPlatform device, DeviceCreateDTO deviceDTO) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		List<Ontology> ontologies = ontologyService.getOntologiesByUserId(utils.getUserId());

		for (ClientPlatformOntology cpo : device.getClientPlatformOntologies()) {
			ObjectNode on = mapper.createObjectNode();
			on.put("id", cpo.getOntology().getIdentification());
			on.put("access", cpo.getAccess());

			for (Iterator<Ontology> iterator = ontologies.iterator(); iterator.hasNext();) {
				Ontology ontology = (Ontology) iterator.next();
				if (ontology.getIdentification().equals(cpo.getOntology().getIdentification())) {
					iterator.remove();
					break;
				}
			}
			arrayNode.add(on);
		}

		try {
			deviceDTO.setClientPlatformOntologies(mapper.writer().writeValueAsString(arrayNode));
			model.addAttribute("ontologies", ontologies);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private void mapTokensToJson(Model model, ClientPlatform device, DeviceCreateDTO deviceDTO) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		for (Token token : device.getTokens()) {
			ObjectNode on = mapper.createObjectNode();
			on.put("id", token.getId());
			on.put("token", token.getToken());
			on.put("active", token.isActive());
			arrayNode.add(on);
		}

		try {
			deviceDTO.setTokens(mapper.writer().writeValueAsString(arrayNode));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@PutMapping(value = "/update/{id}", produces = "text/html")
	public String updateDevice(Model model, @PathVariable("id") String id, @Valid DeviceCreateDTO uDevice,
			BindingResult bindingResult, RedirectAttributes redirect) {

		if (bindingResult.hasErrors()) {
			log.debug("Some device properties missing");
			utils.addRedirectMessage("device.validation.error", redirect);
			return "redirect:/devices/update/" + id;
		}

		if (!this.utils.getUserId().equals(uDevice.getUserId()) && !utils.isAdministrator()) {
			return "/error/403";
		}
		try {
			this.clientPlatformService.updateDevice(uDevice);
		} catch (ClientPlatformServiceException e) {
			log.debug("Cannot update device");
			utils.addRedirectMessage("device.update.error", redirect);
			return "redirect:/devices/create";
		}

		return "redirect:/devices/list";
	}

	@GetMapping("/show/{id}")
	public String show(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {

		ClientPlatform device = this.clientPlatformService.getByIdentification(id);

		if (device != null) {
			if (!this.utils.getUserId().equals(device.getUser().getUserId()) && !utils.isAdministrator()) {
				return "/error/403";
			}
			DeviceCreateDTO deviceDTO = new DeviceCreateDTO();
			deviceDTO.setId(device.getId());
			deviceDTO.setDescription(device.getDescription());
			deviceDTO.setIdentification(device.getIdentification());
			deviceDTO.setMetadata(device.getMetadata());
			deviceDTO.setUserId(device.getUser().getUserId());
			mapOntologiesToJson(model, device, deviceDTO);
			mapTokensToJson(model, device, deviceDTO);
			model.addAttribute("device", deviceDTO);
			model.addAttribute("accessLevel", clientPlatformService.getClientPlatformOntologyAccessLevel());
			return "devices/show";
		} else {
			return "redirect:/devices/list";
		}

	}

	@PostMapping(value = "/desactivateToken")
	public @ResponseBody TokenActivationResponse desactivateToken(@RequestBody TokenActivationRequest request) {
		TokenActivationResponse response = new TokenActivationResponse();
		response.setRequestedActive(request.isActive());
		response.setToken(request.getToken());
		try {
			Token token = tokenService.getTokenByID(request.getToken());
			tokenService.deactivateToken(token, request.isActive());
			response.setOk(true);
		} catch (Exception e) {
			response.setOk(false);
		}
		return response;
	}

	@PostMapping(value = "/deleteToken")
	public @ResponseBody TokenActivationResponse deleteToken(@RequestBody TokenSelectedRequest request) {
		TokenActivationResponse response = new TokenActivationResponse();
		response.setToken(request.getToken());
		try {
			Token token = tokenService.getTokenByID(request.getToken());
			if (!this.utils.getUserId().equals(token.getClientPlatform().getUser().getUserId())
					&& !utils.isAdministrator()) {
				response.setOk(false);
			} else {
				entityDeletionService.deleteToken(token.getId());
				response.setOk(true);
			}
		} catch (Exception e) {
			response.setOk(false);
		}
		return response;
	}

	@PostMapping(value = "/generateToken")
	public @ResponseBody GenerateTokensResponse generateTokens(@RequestBody TokensRequest request) {

		GenerateTokensResponse response = new GenerateTokensResponse();
		if (request == null || request.getDeviceIdentification() == null
				|| request.getDeviceIdentification().equals("")) {
			response.setOk(false);
		}
		if (tokenService.generateTokenForClient(
				this.clientPlatformService.getByIdentification(request.getDeviceIdentification())) != null) {
			response.setOk(true);
		} else {
			response.setOk(false);
		}
		return response;
	}

	@PostMapping(value = "/loadDeviceTokens")
	public @ResponseBody String loadDeviceTokens(@RequestBody TokensRequest request) {

		ClientPlatform clientPlatform = clientPlatformService.getByIdentification(request.getDeviceIdentification());
		if (!this.utils.getUserId().equals(clientPlatform.getUser().getUserId()) && !utils.isAdministrator()) {
			return "/error/403";
		} else {
			List<Token> tokens = tokenService.getTokens(clientPlatform);
			if (tokens != null && tokens.size() > 0) {
				ObjectMapper mapper = new ObjectMapper();
				ArrayNode arrayNode = mapper.createArrayNode();

				for (Token token : tokens) {
					ObjectNode on = mapper.createObjectNode();
					on.put("id", token.getId());
					on.put("token", token.getToken());
					on.put("active", token.isActive());
					arrayNode.add(on);
				}

				try {
					return mapper.writer().writeValueAsString(arrayNode);
				} catch (JsonProcessingException e) {
					return "/error/403";
				}
			}
			return "[]";
		}

	}

}
