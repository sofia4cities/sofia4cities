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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.services.deletion.EntityDeletionService;
import com.indracompany.sofia2.config.services.exceptions.UserServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.services.user.UserOperationsService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private AppWebUtils utils;

	@Autowired
	private UserOperationsService operations;
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private EntityDeletionService entityDeleteService;
	


	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/create", produces = "text/html")
	public String createForm(Model model) {
		this.populateFormData(model);
		model.addAttribute("user", new User());
		return "users/create";

	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable("id") String id) {

		this.userService.deleteUser(id);
		return "redirect:/users/list";
	}

	@GetMapping(value = "/update/{id}/{bool}")
	public String updateForm(@PathVariable("id") String id, @PathVariable(name = "bool", required = false) boolean bool, Model model) {
		// If non admin user tries to update any other user-->forbidden
		if (!this.utils.getUserId().equals(id) && !utils.isAdministrator())
			return "error/403";

		this.populateFormData(model);
		model.addAttribute("AccessToUpdate", bool);
		
		User user = this.userService.getUser(id);
		// If user does not exist redirect to create
		if (user == null)
			return "redirect:/users/create";
		else
			model.addAttribute("user", user);

		return "users/create";
	}
	
	@ResponseBody
	@PostMapping("/deleteSimpleData")
	public String deleteSimpleData(HttpServletRequest request, RedirectAttributes redirect, @RequestBody String data){ 
	
		try {
			String[] ontologiesToDelete = request.getParameterValues("ontologiesDelete[]");

			if(ontologiesToDelete == null) {
				return "/users/update/"+ this.utils.getUserId()+"/true" ;
			}
			for(String ontToDelete:ontologiesToDelete) {
				System.out.println("remove: "+ ontToDelete + " \n");
				entityDeleteService.deleteOntology(ontToDelete, this.utils.getUserId());
			}

			return "/users/update/"+this.utils.getUserId()+"/false" ;
			
		}catch (UserServiceException e) {
			log.debug("Cannot update  data user");
			utils.addRedirectMessage("user.remove.data.error", redirect);
			return "/users/show/"+this.utils.getUserId();
		}
	}
	
	@ResponseBody
	@PostMapping("/revokeSimpleData")
	public String revokeSimpleData(HttpServletRequest request, RedirectAttributes redirect, @RequestBody String data){ 
		
		try {
			String[] ontologiesToRevoke = request.getParameterValues("ontologiesRevoke[]");
			Ontology ont;
			
			if(ontologiesToRevoke == null) {
				return "/users/update/"+ this.utils.getUserId()+"/true"   ;
			}
				for(String ontToRevoke : ontologiesToRevoke) {
					System.out.println("revoke: "+ ontToRevoke + " \n");
					
					ont = ontologyService.getOntologyById(ontToRevoke, this.utils.getUserId());
					entityDeleteService.revokeAuthorizations(ont);
				}	
			
			return "/users/update/"+ this.utils.getUserId()+"/false"   ;
			
		}catch (UserServiceException e) {
			log.debug("Cannot update  data user");
			utils.addRedirectMessage("user.remove.data.error", redirect);
			return "/users/show/"+this.utils.getUserId();
			
		}
		
	}
	
	@PutMapping(value = "/update/{id}/{bool}")
	public String update(@PathVariable("id") String id, @Valid User user, @PathVariable(name = "bool", required = false) boolean bool, BindingResult bindingResult,
			RedirectAttributes redirect, HttpServletRequest request, Model model) {

		String newPass = request.getParameter("newpasswordbox");
		String repeatPass = request.getParameter("repeatpasswordbox");
		
	
		
		if (bindingResult.hasErrors()) {
			log.debug("Some user properties missing");
			
			return "redirect:/users/update/"+user.getUserId() +"/"+bool;
		}
		
		model.addAttribute("AccessToUpdate", bool);

		if (!this.utils.getUserId().equals(id) && !utils.isAdministrator())
			return "error/403";
		// If the user is not admin, the RoleType is not in the request by default
		if (!utils.isAdministrator())
			user.setRole(this.userService.getUserRole(this.utils.getRole()));

		try {
			if ((!newPass.isEmpty()) && (!repeatPass.isEmpty())) {
				if (newPass.equals(repeatPass)) {
					user.setPassword(newPass);
					this.userService.updatePassword(user);
				} else {
					utils.addRedirectMessage("user.update.error.password", redirect);
					return "redirect:/users/show/" + user.getUserId();
				}
			}
			this.userService.updateUser(user);
		} catch (UserServiceException e) {
			log.debug("Cannot update user");
			utils.addRedirectMessage("user.update.error", redirect);
			return "redirect:/users/create";
		}
		return "redirect:/users/show/" + user.getUserId();

	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PostMapping(value = "/create")
	public String create(@Valid User user, BindingResult bindingResult, RedirectAttributes redirect, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			log.debug("Some user properties missing");
			utils.addRedirectMessage("user.create.error", redirect);
			return "redirect:/users/create";
		}
		try {
			String newPass = request.getParameter("newpasswordbox");
			String repeatPass = request.getParameter("repeatpasswordbox");
			if ((!newPass.isEmpty()) && (!repeatPass.isEmpty())) {
				if (newPass.equals(repeatPass)) {
					user.setPassword(newPass);
					this.userService.createUser(user);
					operations.createPostOperationsUser(user);
					operations.createPostOntologyUser(user);
					return "redirect:/users/list";
				}
			}
			
			log.debug("Password is not valid");
			utils.addRedirectMessage("user.create.error", redirect);
			return "redirect:/users/create";

		} catch (UserServiceException e) {
			log.debug("Cannot update user that does not exist");
			utils.addRedirectMessage("user.create.error", redirect);
			return "redirect:/users/create";
		}

		
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

		return "users/list";

	}

	@GetMapping(value = "/show/{id}", produces = "text/html")
	public String showUser(@PathVariable("id") String id, Model model) {
		User user = null;
		if (id != null) {
			// If non admin user tries to update any other user-->forbidden
			if (!this.utils.getUserId().equals(id) && !utils.isAdministrator())
				return "error/403";
			user = this.userService.getUser(id);
		}
		// If user does not exist
		if (user == null)
			return "error/404";

		model.addAttribute("user", user);
		UserToken userToken = null;
		try {
			userToken = this.userService.getUserToken(user).get(0);
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

		return "users/show";

	}
	
	private void populateFormData(Model model) {
		model.addAttribute("roleTypes", this.userService.getAllRoles());
		model.addAttribute("ontologies", this.ontologyService.getOntologiesByUserId(this.utils.getUserId()));
		model.addAttribute("ontologies1", this.ontologyService.getOntologiesByUserId(this.utils.getUserId()));
		model.addAttribute("ontologies2", this.ontologyService.getOntologiesByUserId(this.utils.getUserId()));
	
		
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerUserLogin(@ModelAttribute User user, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		String nameRole = request.getParameter("roleName");

		if (user != null) {
			if (this.userService.emailExists(user)) {
				log.debug("There is already an user with this email");
				utils.addRedirectMessage("login.error.email.duplicate", redirectAttributes);
				return "redirect:/login";
			}
			if (utils.paswordValidation(user.getPassword()) && (this.userService.emailExists(user) == false)) {

				try {
					if (nameRole == null) {
						log.debug("A role must be selected");
						utils.addRedirectMessage("login.error.user.register", redirectAttributes);
						return "redirect:/login";
					}
					if (nameRole.toLowerCase().equals("user")) {

						this.userService.registerRoleUser(user);
						operations.createPostOperationsUser(user);
						operations.createPostOntologyUser(user);

					} else {
						this.userService.registerRoleDeveloper(user);
						operations.createPostOperationsUser(user);
						operations.createPostOntologyUser(user);
					}

					log.debug("User created from login");
					utils.addRedirectMessage("login.register.created", redirectAttributes);
					return "redirect:/login";

				} catch (UserServiceException e) {
					log.debug("This user already exist");
					utils.addRedirectMessage("login.error.register", redirectAttributes);
					return "redirect:/login";
				}
			}
		}
		return "redirect:/login?errorRegister";

	}
	@PostMapping(value = "/forgetUser")
	public String forgetUser(@Valid User user, Model model,RedirectAttributes redirect, HttpServletRequest request,BindingResult bindingResult ) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("usersId", this.utils.getUserId());
			System.out.println("eeeeeeeeeeeeeeeeeeeee");
			log.debug("Some user properties missing");
			return "redirect:/forgetUser/";
		}
		System.out.println("adsads user:");

		return "/users/update/"+this.utils.getUserId();
		
	}
	
	@GetMapping(value = "/forgetDataUser/{userId}")
	public String forgetDataUser(Model model,RedirectAttributes redirect, @PathVariable(name = "userId") String userId ) {
		
		try{
			this.userService.deleteUser(userId);
			return "redirect:/logout";
				
		}catch (UserServiceException e) {
			log.debug("Cannot deleted  data user");
			utils.addRedirectMessage("user.remove.data.error", redirect);
			return "redirect:/users/show/"+this.utils.getUserId();
			
		}
		
		
	}
}
