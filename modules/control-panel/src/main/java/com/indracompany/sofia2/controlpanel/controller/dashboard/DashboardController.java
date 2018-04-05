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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.services.dashboard.DashboardService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import groovy.util.logging.Slf4j;

@RequestMapping("/dashboards")
@Controller
@Slf4j

public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private AppWebUtils utils;

	@RequestMapping(value = "/list", produces = "text/html")
	public String list(Model uiModel, HttpServletRequest request) {

		// Para poder hacer búsquedas por Nombre y descripcion
		String identification = request.getParameter("identification");
		String description = request.getParameter("description");

		List<Dashboard> dashboard = this.dashboardService.findDashboardWithIdentificationAndDescription(identification,
				description, utils.getUserId());

		uiModel.addAttribute("dashboards", dashboard);
		return "dashboards/list";

	}

	@PostMapping(value = "/create", produces = "text/html")
	public @ResponseBody String createDashboard(@RequestParam("identification") String identification) {
		String dashboardId = dashboardService.createNewDashboard(identification, utils.getUserId());
		return dashboardId;
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
	public String viewerDashboard(Model model, @PathVariable("id") String id) {
		model.addAttribute("dashboard", dashboardService.getDashboardById(id, utils.getUserId()));
		model.addAttribute("credentials", dashboardService.getCredentialsString(utils.getUserId()));
		model.addAttribute("edition", false);
		return "dashboards/view";
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

	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable("id") String id) {

		dashboardService.deleteDashboard(id, utils.getUserId());
		return "redirect:/dashboards/list";
	}
}
