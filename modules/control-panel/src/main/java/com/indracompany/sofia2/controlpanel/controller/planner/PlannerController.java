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
package com.indracompany.sofia2.controlpanel.controller.planner;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.scheduler.scheduler.bean.ListTaskInfo;
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskOperation;
import com.indracompany.sofia2.scheduler.scheduler.bean.response.ResponseInfo;
import com.indracompany.sofia2.scheduler.scheduler.service.TaskService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/planner")
@Slf4j
public class PlannerController {
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private AppWebUtils utils;
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, HttpServletRequest request) {

		List<ListTaskInfo> tasks = taskService.list(utils.getUserId());
		
		model.addAttribute("tasks", tasks);
		return "planner/list";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@RequestMapping (method = RequestMethod.GET, value="/unschedule/{jobName}")
	public String unschedule(@PathVariable String jobName) {
		
		boolean unscheduled = taskService.unscheduled(new TaskOperation(jobName));		
		return "redirect:/planner/list";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@RequestMapping(method = RequestMethod.GET, value="/pause/{jobName}")
	public String pause(@PathVariable String jobName){
		
		boolean resumed = taskService.pause(new TaskOperation(jobName));		
		return "redirect:/planner/list";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@RequestMapping(method = RequestMethod.GET, value="/resume/{jobName}")
	public String resume(@PathVariable String jobName){
		
		boolean resumed = taskService.resume(new TaskOperation(jobName));		
		return "redirect:/planner/list";
	}

}
