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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/create", produces = "text/html")
	public String createForm(Model model) {
		this.populateFormData(model);
		return "/users/create";

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
	@RequestMapping(value = "/show/{id}", produces = "text/html")
	public String showUser(@PathVariable("id") String id, Model uiModel) {
		User user = this.userService.getUser(utils.getUserId());
		uiModel.addAttribute("user", user);
		UserToken userToken = null;
		try {
			userToken = this.userService.getUserToken(user);
		} catch (Exception e) {
			log.debug("No token found for user: "+user);
		}
		
		uiModel.addAttribute("userToken", userToken);
		uiModel.addAttribute("itemId", user.getId());
		
		
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
		
		uiModel.addAttribute("userId", user.getId());
		return "/users/show";
	}

	public void populateFormData(Model model) {
		model.addAttribute("roleTypes", this.userService.getAllRoles());
	}

}
