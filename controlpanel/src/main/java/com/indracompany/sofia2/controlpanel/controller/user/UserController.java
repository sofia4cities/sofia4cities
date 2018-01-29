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
package com.indracompany.sofia2.controlpanel.controller.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.service.user.UserService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/users")
@Controller
@Slf4j
public class UserController {

	@Autowired
	UserService userService;
	@Autowired
	private AppWebUtils utils;
	public static final String ROLE_ADMINISTRATOR="ROLE_ADMINISTRATOR";

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/create", produces = "text/html")
	public String createForm(Model model) {
		this.populateFormData(model);
		model.addAttribute("user",new User());
		return "/users/create";

	}
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value="/update/{id}")
	public String updateForm(@PathVariable("id") String id,Model model)
	{
		//If non admin user tries to update any other user-->forbidden
		if(!this.utils.getUserId().equals(id) && !this.utils.getRole().equals(ROLE_ADMINISTRATOR)) return "/error/403";
		
		this.populateFormData(model);
		User user=this.userService.getUser(id);
		//If user does not exist redirect to create
		if(user==null) return "redirect:/users/create";
		else model.addAttribute("user",user);
		
		return "/users/create";
	}
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PutMapping(value="/update/{id}")
	public String update(@PathVariable("id") String id,@ModelAttribute User user)
	{
		if(user!=null)
		{
			if(user.getPassword()!=null && user.getEmail()!=null
					&& user.getRoleTypeId()!=null && user.getUserId()!=null)
			{
				try{
					this.userService.updateUser(user);
				}catch(Exception e)
				{
					log.debug(e.getMessage());
					return "/users/create";
				}
			}else {
				log.debug("Some user properties missing");
				return "/users/create";
			}
			return "redirect:/users/show/"+user.getUserId();
		}
		
		return "redirect:/users/update/";
		
	}
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PostMapping(value="/create")
	public String create(@ModelAttribute User user)
	{
		if(user!=null)
		{
			if(user.getPassword()!=null && user.getDateCreated()!=null && user.getEmail()!=null
					&& user.getRoleTypeId()!=null && user.getUserId()!=null)
			{
				try{
					this.userService.createUser(user);
				}catch(Exception e)
				{
					log.debug(e.getMessage());
					return "/users/create";
				}
			}else {
				log.debug("Some user properties missing");
				return "/users/create";
			}
		}
		
		 return "redirect:/users/list";
		
	}
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String fullName, @RequestParam(required = false) String roleType,
			@RequestParam(required = false) String email, @RequestParam(required = false) Boolean active) {
		this.populateFormData(model);
		if(userId!=null){if(userId.equals("")) userId=null;}
		if(fullName!=null){if(fullName.equals("")) fullName=null;}
		if(email!=null){if(email.equals("")) email=null;}
		if(roleType!=null){if(roleType.equals("")) roleType=null;}

		if((userId==null) && (email==null) && (fullName==null) && (active==null)
				&& (roleType==null)){
			log.debug("No params for filtering, loading all users");
			model.addAttribute("users",this.userService.getAllUsers());
			
		}else
		{
			log.debug("Params detected, filtering users...");
			model.addAttribute("users",this.userService.getAllUsersByCriteria(userId, fullName, email, roleType, active));
		}
		
		
		return "/users/list";

	}
	@GetMapping(value = "/show/{id}", produces = "text/html")
	public String showUser(@PathVariable("id") String id, Model uiModel) {
		User user=null;
		if(id!=null){
			//If non admin user tries to update any other user-->forbidden
			if(!this.utils.getUserId().equals(id) && !this.utils.getRole().equals(ROLE_ADMINISTRATOR)) return "/error/403";
			user = this.userService.getUser(id);
		}
		//If user does not exist
		if(user==null) return "/error/404";

		uiModel.addAttribute("user", user);
		UserToken userToken = null;
		try {
			userToken = this.userService.getUserToken(user);
		} catch (Exception e) {
			log.debug("No token found for user: "+user);
		}
		
		uiModel.addAttribute("userToken", userToken);
		uiModel.addAttribute("itemId", user.getUserId());
		
		
		Date today = new Date();
		if (user.getDateDeleted()!=null){
			if (user.getDateDeleted().before(today)){
				uiModel.addAttribute("obsolete", true);
			}
			else{
				uiModel.addAttribute("obsolete", false);
			}
		}else{
			uiModel.addAttribute("obsolete", false);
		}
		


		return "/users/show";
	
		
	}

	public void populateFormData(Model model) {
		model.addAttribute("roleTypes", this.userService.getAllRoles());
	}
	
	
	
}
