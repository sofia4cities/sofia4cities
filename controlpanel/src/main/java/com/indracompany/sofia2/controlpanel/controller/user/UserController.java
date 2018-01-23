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



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String createForm(Model model)
	{
		this.userService.populateFormData(model);
		return "/users/create";
		
	}
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, @RequestParam(required=false) String userId, @RequestParam(required=false) String fullName, @RequestParam(required=false) String roleTypeId, @RequestParam(required=false)String email, @RequestParam(required=false) Boolean active)
	{
		this.userService.populateFormData(model);
		return "/users/list";
		
	}
	
	
	

}
