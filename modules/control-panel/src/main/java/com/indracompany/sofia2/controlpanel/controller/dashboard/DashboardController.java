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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@RequestMapping(value = "/list", produces = "text/html")
	public String list (Model uiModel, HttpServletRequest request) {
		
		//Para poder hacer búsquedas por Nombre y descripcion
		String identification = request.getParameter("identification");
		String description = request.getParameter("description");
		
		List<Dashboard> dashboard=this.dashboardService.findDashboardWithIdentificationAndDescription(identification, description, utils.getUserId());
				
		uiModel.addAttribute("dashboards", dashboard);
		return "/dashboards/list";
				
	}
	
}

