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
package com.indracompany.sofia2.controlpanel.controller.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.controlpanel.controller.user.UserController;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.service.user.UserService;
import com.indracompany.sofia2.services.configuration.ConfigurationService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/configurations")
@Slf4j
public class ConfigurationController {
	
	@Autowired
	ConfigurationService configurationService;
	@Autowired
	private AppWebUtils utils;
	@Autowired
	UserService userService;
	public static final String ROLE_ADMINISTRATOR="ROLE_ADMINISTRATOR";
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping
	public String listTypes(Model model)
	{
		
		
		return "redirect:/main";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping("/list")
	public String list(Model model)
	{
		
		List<Configuration> configurations=this.configurationService.getAllConfigurations();
		model.addAttribute("configurations",configurations);
		return "/configurations/list";
		
	}
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping("/create")
	public String createForm(Model model)
	{
		this.populateFormData(model);
		Configuration configuration=new Configuration();
		//Logged user is going to be the creator of the new config
		configuration.setUserId(this.userService.getUser(this.utils.getUserId()));;
		model.addAttribute("configuration", configuration);
		return "/configurations/create";
		
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PostMapping("/create")
	public String create(Model model, @ModelAttribute Configuration configuration)
	{
		if(configuration!=null)
		{
			//Fields needed
			if(configuration.getConfigurationTypeId()!=null && configuration.getJsonSchema()!=null && configuration.getUserId()!=null)
			{
				this.configurationService.createConfiguration(configuration);
			}else
			{
				log.debug("Missing fields");
				return "redirect:/configurations/create";
			}
		}
		
		
		return "redirect:/configurations/list";
		
	}

	@GetMapping("/update/{id}")
	public String updateForm(@PathVariable String id,Model model)
	{
		
		Configuration configuration= this.configurationService.getConfiguration(id);
		
		if(configuration==null){
			configuration=new Configuration();
			configuration.setUserId(this.userService.getUser(this.utils.getUserId()));
		}else{
			//User trying to modify another's config
			if(!this.utils.getUserId().equals(configuration.getUserId().getUserId()) && !this.utils.getRole().equals(ROLE_ADMINISTRATOR)) return "/error/403";
		}
		model.addAttribute("configuration", configuration);
		return "/configurations/create";
		
	}

	@PutMapping("/update/{id}")
	public String update(@PathVariable String id,Model model, @ModelAttribute Configuration configuration)
	{
		
		if(configuration!=null)
		{
			if(!this.utils.getUserId().equals(configuration.getUserId().getUserId()) && !this.utils.getRole().equals(ROLE_ADMINISTRATOR)) return "/error/403";
			try{
				this.configurationService.updateConfiguration(configuration);
			}catch(Exception e)
			{
				log.debug(e.getMessage());
				return "/configurations/create";
			}
		}else
		{
			return "redirect:/update/"+id;
		}

		model.addAttribute("configuration", configuration);
		return "redirect:/configurations/show/"+id;
		
	}
	
	public void populateFormData(Model model)
	{
		model.addAttribute("configurationTypes", this.configurationService.getAllConfigurationTypes());
	}
	
	@GetMapping(value = "/show/{id}", produces = "text/html")
	public String show(@PathVariable("id") String id, Model model)
	{
		Configuration configuration=null;
		if(id!=null)
		{
			configuration=this.configurationService.getConfiguration(id);
		}
		if(configuration==null) return "/error/404";
		if(!this.utils.getUserId().equals(configuration.getUserId().getUserId()) && !this.utils.getRole().equals(ROLE_ADMINISTRATOR)) return "/error/403";
		model.addAttribute("configuration", configuration);
		return "/configurations/show";
		
		
	}
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable ("id") String id)
	{
		this.configurationService.deleteConfiguration(id);
		return "redirect:/configurations/list";
	}
	
	

}
