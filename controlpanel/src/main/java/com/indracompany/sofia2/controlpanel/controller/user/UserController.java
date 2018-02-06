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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.services.exceptions.UserServiceException;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/users")
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
		model.addAttribute("user", new User());
		return "/users/create";

	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable("id") String id) {

		this.userService.deleteUser(id);
		return "redirect:/users/list";
	}

	@GetMapping(value = "/update/{id}")
	public String updateForm(@PathVariable("id") String id, Model model) {
		// If non admin user tries to update any other user-->forbidden
		if (!this.utils.getUserId().equals(id) && !utils.isAdministrator())
			return "/error/403";

		this.populateFormData(model);
		User user = this.userService.getUser(id);
		// If user does not exist redirect to create
		if (user == null)
			return "redirect:/users/create";
		else
			model.addAttribute("user", user);

		return "/users/create";
	}

	@PutMapping(value = "/update/{id}")
	public String update(@PathVariable("id") String id, @Valid User user,
			BindingResult bindingResult, RedirectAttributes redirect) {
		if (bindingResult.hasErrors())
		{
			log.debug("Some user properties missing");
			redirect.addFlashAttribute("message", "Errors in user form");
			return "redirect:/users/update/";		
		}

		if (!this.utils.getUserId().equals(id) && !utils.isAdministrator())
			return "/error/403";
		// If the user is not admin, the RoleType is not in the request by default
		if (!utils.isAdministrator())
			user.setRole(this.userService.getUserRole(this.utils.getRole()));

		try{
			this.userService.updateUser(user);
		} catch (UserServiceException e) {
			log.debug("Cannot update user");
			redirect.addFlashAttribute("message", "Account not updated");
			return "redirect:/users/create";
		}
		redirect.addFlashAttribute("message", "Account updated successfully");
		return "redirect:/users/show/" + user.getUserId();

	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PostMapping(value = "/create")
	public String create(@Valid User user, BindingResult bindingResult, RedirectAttributes redirect) {

		if (bindingResult.hasErrors())
		{
			log.debug("Some user properties missing");
			redirect.addFlashAttribute("message", "Account not created");
			return "redirect:/users/create";			
		}


		try
		{
			this.userService.createUser(user);
		}catch(UserServiceException e)
		{
			log.debug("Cannot update user that does not exist");
			return "redirect:/users/create";
		}

		redirect.addFlashAttribute("message", "Account created successfully");
		return "redirect:/users/list";
	}




	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String fullName, @RequestParam(required = false) String roleType,
			@RequestParam(required = false) String email, @RequestParam(required = false) Boolean active) {
		this.populateFormData(model);
		if (userId != null) {
			if (userId.equals(""))
				userId = null;
		}
		if (fullName != null) {
			if (fullName.equals(""))
				fullName = null;
		}
		if (email != null) {
			if (email.equals(""))
				email = null;
		}
		if (roleType != null) {
			if (roleType.equals(""))
				roleType = null;
		}

		if ((userId == null) && (email == null) && (fullName == null) && (active == null) && (roleType == null)) {
			log.debug("No params for filtering, loading all users");
			model.addAttribute("users", this.userService.getAllUsers());

		} else {
			log.debug("Params detected, filtering users...");
			model.addAttribute("users",
					this.userService.getAllUsersByCriteria(userId, fullName, email, roleType, active));
		}

		return "/users/list";

	}

	@GetMapping(value = "/show/{id}", produces = "text/html")
	public String showUser(@PathVariable("id") String id, Model model) {
		User user = null;
		if (id != null) {
			// If non admin user tries to update any other user-->forbidden
			if (!this.utils.getUserId().equals(id) && !utils.isAdministrator())
				return "/error/403";
			user = this.userService.getUser(id);
		}
		// If user does not exist
		if (user == null)
			return "/error/404";

		model.addAttribute("user", user);
		UserToken userToken = null;
		try {
			userToken = this.userService.getUserToken(user);
		} catch (Exception e) {
			log.debug("No token found for user: " + user);
		}

		model.addAttribute("userToken", userToken);
		model.addAttribute("itemId", user.getUserId());

		Date today = new Date();
		if (user.getDateDeleted() != null) {
			if (user.getDateDeleted().before(today)) {
				model.addAttribute("obsolete", true);
			} else {
				model.addAttribute("obsolete", false);
			}
		} else {
			model.addAttribute("obsolete", false);
		}

		return "/users/show";

	}

	public void populateFormData(Model model) {
		model.addAttribute("roleTypes", this.userService.getAllRoles());
	}

}
