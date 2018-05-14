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
package com.indracompany.sofia2.controlpanel.controller.dashboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DashboardUserAccess;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.dashboard.DashboardService;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardAccessDTO;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardCreateDTO;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardDTO;
import com.indracompany.sofia2.config.services.exceptions.DashboardServiceException;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import groovy.util.logging.Slf4j;

@RequestMapping("/dashboards")
@Controller
@Slf4j

public class DashboardController {

	@Autowired
	private DashboardService dashboardService;
	@Autowired
	private UserService userService;
	@Autowired
	private AppWebUtils utils;

	@RequestMapping(value = "/list", produces = "text/html")
	public String list(Model uiModel, HttpServletRequest request,
			@RequestParam(required = false, name = "identification") String identification,
			@RequestParam(required = false, name = "description") String description) {

		// Scaping "" string values for parameters
		if (identification != null) {
			if (identification.equals(""))
				identification = null;
		}
		if (description != null) {
			if (description.equals(""))
				description = null;
		}

		List<DashboardDTO> dashboard = this.dashboardService
				.findDashboardWithIdentificationAndDescription(identification, description, utils.getUserId());

		uiModel.addAttribute("dashboards", dashboard);

		return "dashboards/list";

	}

	@GetMapping(value = "/create")
	public String create(Model model) {
		model.addAttribute("dashboard", new DashboardCreateDTO());
		List<User> users = userService.getAllUsers();
		model.addAttribute("users", users);
		return "dashboards/create";
	}

	@GetMapping(value = "/update/{id}", produces = "text/html")
	public String update(Model model, @PathVariable("id") String id) {

		model.addAttribute("dashboard", dashboardService.getDashboardById(id, utils.getUserId()));

		return "dashboards/create";

	}

	@PostMapping(value = { "/create" })
	public String createDashboard(Model model, @Valid DashboardCreateDTO dashboard, BindingResult bindingResult,
			RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			utils.addRedirectMessage("dashboard.validation.error", redirect);
			return "redirect:/dashboards/create";
		}

		try {

			String dashboardId = dashboardService.createNewDashboard(dashboard, utils.getUserId());
			return "redirect:/dashboards/editfull/" + dashboardId;

		} catch (DashboardServiceException e) {
			utils.addRedirectException(e, redirect);
			return "redirect:/dashboards/create";
		}
	}

	@PutMapping(value = { "/dashboardconf/{id}" })
	public String saveUpdateDashboard(Model model, @Valid DashboardCreateDTO dashboard, @PathVariable("id") String id,
			BindingResult bindingResult, RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			utils.addRedirectMessage("dashboard.validation.error", redirect);
			return "redirect:/dashboards/create";
		}

		try {
			if (dashboardService.hasUserEditPermission(id, utils.getUserId())) {
				dashboardService.cleanDashboardAccess(dashboard, utils.getUserId());
				dashboardService.saveUpdateAccess(dashboard, utils.getUserId());
				dashboardService.updatePublicDashboard(dashboard, utils.getUserId());
			} else {
				throw new DashboardServiceException(
						"Cannot update Dashboard that does not exist or don't have permission");
			}
			return "redirect:/dashboards/list/";

		} catch (DashboardServiceException e) {
			utils.addRedirectException(e, redirect);
			return "redirect:/dashboards/dashboardconf/" + dashboard.getId();
		}
	}

	@GetMapping(value = "/dashboardconf/{id}", produces = "text/html")
	public String updateDashboard(Model model, @PathVariable("id") String id) {
		Dashboard dashboard = this.dashboardService.getDashboardEditById(id, utils.getUserId());

		if (dashboard != null) {

			DashboardCreateDTO dashBDTO = new DashboardCreateDTO();

			dashBDTO.setId(id);
			dashBDTO.setIdentification(dashboard.getIdentification());
			dashBDTO.setDescription(dashboard.getDescription());
			dashBDTO.setPublicAccess(dashboard.isPublic());
			List<DashboardUserAccess> userAccess = dashboardService.getDashboardUserAccesses(id);
			if (userAccess != null && userAccess.size() > 0) {
				ArrayList list = new ArrayList();
				for (Iterator iterator = userAccess.iterator(); iterator.hasNext();) {
					DashboardUserAccess dua = (DashboardUserAccess) iterator.next();
					DashboardAccessDTO daDTO = new DashboardAccessDTO();
					daDTO.setAccesstypes(dua.getDashboardUserAccessType().getName());
					daDTO.setUsers(dua.getUser().getUserId());
					list.add(daDTO);
				}
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					dashBDTO.setAuthorizations(objectMapper.writeValueAsString(list));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			model.addAttribute("dashboard", dashBDTO);
			List<User> users = userService.getAllUsers();
			model.addAttribute("users", users);
			return "dashboards/create";
		} else {
			return "redirect:/dashboards/list";
		}
	}

	@GetMapping(value = "/editor/{id}", produces = "text/html")
	public String editorDashboard(Model model, @PathVariable("id") String id) {
		model.addAttribute("dashboard", dashboardService.getDashboardById(id, utils.getUserId()));
		model.addAttribute("credentials", dashboardService.getCredentialsString(utils.getUserId()));
		return "dashboards/editor";

	}

	@GetMapping(value = "/model/{id}", produces = "application/json")
	public @ResponseBody String getModelById(@PathVariable("id") String id) {
		return this.dashboardService.getDashboardById(id, utils.getUserId()).getModel();
	}

	@GetMapping(value = "/editfull/{id}", produces = "text/html")
	public String editFullDashboard(Model model, @PathVariable("id") String id) {
		model.addAttribute("dashboard", dashboardService.getDashboardById(id, utils.getUserId()));
		model.addAttribute("credentials", dashboardService.getCredentialsString(utils.getUserId()));
		model.addAttribute("edition", true);
		return "dashboards/view";
	}

	@GetMapping(value = "/view/{id}", produces = "text/html")
	public String viewerDashboard(Model model, @PathVariable("id") String id, HttpServletRequest request) {
		if (dashboardService.hasUserViewPermission(id, utils.getUserId())) {
			model.addAttribute("dashboard", dashboardService.getDashboardById(id, utils.getUserId()));
			model.addAttribute("credentials", dashboardService.getCredentialsString(utils.getUserId()));
			model.addAttribute("edition", false);
			return "dashboards/view";
		} else {
			return "redirect:/403";
		}
	}

	@PutMapping(value = "/save/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String updateDashboard(@PathVariable("id") String id,
			@RequestParam("data") Dashboard dashboard) {
		dashboardService.saveDashboard(id, dashboard, utils.getUserId());
		return "ok";
	}

	@PutMapping(value = "/savemodel/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String updateDashboardModel(@PathVariable("id") String id, @RequestBody EditorDTO model) {
		dashboardService.saveDashboardModel(id, model.getModel(), model.isVisible(), utils.getUserId());
		return "{\"ok\":true}";
	}

	@PutMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody String deleteDashboard(@PathVariable("id") String id) {
		dashboardService.deleteDashboard(id, utils.getUserId());
		return "{\"ok\":true}";
	}

	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable("id") String id) {
		this.dashboardService.deleteDashboard(id, utils.getUserId());
		return "redirect:/dashboards/list/";
	}

}
