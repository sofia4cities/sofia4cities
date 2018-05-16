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
package com.indracompany.sofia2.controlpanel.controller.main;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.services.main.MainService;
import com.indracompany.sofia2.config.services.menu.MenuService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.simulation.DeviceSimulationService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainPageController {

	@Autowired
	private AppWebUtils utils;
	@Autowired
	private MenuService menuService;
	@Autowired
	private UserService userService;

	// TEMPORAL
	@Autowired
	private DashboardRepository dashboardRepository;
	@Autowired
	private DeviceSimulationService deviceSimulationServicve;
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private ClientPlatformRepository clientPlatformRepository;
	@Autowired
	private ApiRepository apiRepository;
	@Autowired
	private MainService mainService;

	@Value("${sofia2.urls.iotbroker}")
	String url;

	@GetMapping("/main")
	private String main(Model model, HttpServletRequest request) {
		// Load menu by role in session
		String jsonMenu = this.menuService.loadMenuByRole(this.userService.getUser(utils.getUserId()));
		// Remove PrettyPrinted
		String menu = utils.validateAndReturnJson(jsonMenu);
		utils.setSessionAttribute(request, "menu", menu);

		if (utils.getRole().equals(Role.Type.ROLE_ADMINISTRATOR.name())) {
			model.addAttribute("kpis", mainService.createKPIs());

			return "main";
		} else if (utils.getRole().equals(Role.Type.ROLE_DEVELOPER.name())) {
			// FLOW
			model.addAttribute("hasOntology",
					this.ontologyService.getOntologiesByUserId(this.utils.getUserId()).size() > 0 ? true : false);
			model.addAttribute("hasDevice", this.clientPlatformRepository
					.findByUser(this.userService.getUser(this.utils.getUserId())).size() > 0 ? true : false);
			model.addAttribute("hasDashboard",
					this.dashboardRepository.findByUser(this.userService.getUser(this.utils.getUserId())).size() > 0
							? true
							: false);
			model.addAttribute("hasSimulation",
					this.deviceSimulationServicve.getSimulationsForUser(this.utils.getUserId()).size() > 0 ? true
							: false);
			model.addAttribute("hasApi",
					this.apiRepository.findByUser(this.userService.getUser(this.utils.getUserId())).size() > 0 ? true
							: false);

			return "main";
		} else if (utils.getRole().equals(Role.Type.ROLE_USER.name())) {
			return "redirect:/marketasset/list";
		} else if (utils.getRole().equals(Role.Type.ROLE_DATAVIEWER.name())) {
			return "redirect:/dashboards/viewerlist";
		}

		// FLOW
		model.addAttribute("hasOntology",
				this.ontologyService.getOntologiesByUserId(this.utils.getUserId()).size() > 0 ? true : false);
		model.addAttribute("hasDevice",
				this.clientPlatformRepository.findByUser(this.userService.getUser(this.utils.getUserId())).size() > 0
						? true
						: false);
		model.addAttribute("hasDashboard",
				this.dashboardRepository.findByUser(this.userService.getUser(this.utils.getUserId())).size() > 0 ? true
						: false);
		model.addAttribute("hasSimulation",
				this.deviceSimulationServicve.getSimulationsForUser(this.utils.getUserId()).size() > 0 ? true : false);
		model.addAttribute("hasApi",
				this.apiRepository.findByUser(this.userService.getUser(this.utils.getUserId())).size() > 0 ? true
						: false);

		return "main";
	}

}