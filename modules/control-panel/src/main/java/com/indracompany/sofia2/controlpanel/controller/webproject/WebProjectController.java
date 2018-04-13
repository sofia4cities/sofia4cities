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
package com.indracompany.sofia2.controlpanel.controller.webproject;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.WebProject;
import com.indracompany.sofia2.config.services.exceptions.WebProjectServiceException;
import com.indracompany.sofia2.config.services.webproject.WebProjectService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/webprojects")
@Slf4j
public class WebProjectController {

	@Autowired
	private WebProjectService webProjectService;

	@Autowired
	private AppWebUtils utils;

	@Value("${sofia2.webproject.baseurl:https://localhost:18080/web/}")
	private String rootWWW = "";

	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model, HttpServletRequest request,
			@RequestParam(required = false, name = "identification") String identification,
			@RequestParam(required = false, name = "description") String description) {

		List<WebProject> webprojects = this.webProjectService
				.getWebProjectsWithDescriptionAndIdentification(utils.getUserId(), identification, description);
		model.addAttribute("webprojects", webprojects);
		model.addAttribute("rootWWW", rootWWW);

		return "webprojects/list";
	}

	@PostMapping("/getNamesForAutocomplete")
	public @ResponseBody List<String> getNamesForAutocomplete() {
		return this.webProjectService.getWebProjectsIdentifications(utils.getUserId());
	}

	@GetMapping(value = "/create", produces = "text/html")
	public String create(Model model, @Valid WebProject webProject, BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			model.addAttribute("webproject", new WebProject());
		return "webprojects/create";
	}

	@PostMapping(value = "/create")
	public String createWebProject(Model model, @Valid WebProject webProject, BindingResult bindingResult,
			RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			log.debug("Some web project properties missing");
			utils.addRedirectMessage("webproject.validation.error", redirect);
			return "redirect:/webprojects/create";
		}
		try {
			webProjectService.createWebProject(webProject, utils.getUserId());
		} catch (WebProjectServiceException e) {
			log.error("Cannot create webproject because of:" + e.getMessage());
			utils.addRedirectException(e, redirect);
			return "redirect:/webprojects/create";
		}
		return "redirect:/webprojects/list";
	}

	@GetMapping(value = "/update/{id}", produces = "text/html")
	public String update(Model model, @PathVariable("id") String id) {
		try {
			WebProject webProject = this.webProjectService.getWebProjectById(id, utils.getUserId());

			if (webProject != null) {
				model.addAttribute("webproject", webProject);
				return "webprojects/create";
			} else
				return "webprojects/create";
		} catch (RuntimeException e) {
			return "webprojects/create";
		}
	}

	@PutMapping(value = "/update/{id}", produces = "text/html")
	public String updateWebProject(Model model, @PathVariable("id") String id, @Valid WebProject webProject,
			BindingResult bindingResult, RedirectAttributes redirect) {

		if (bindingResult.hasErrors()) {
			log.debug("Some web project properties missing");
			utils.addRedirectMessage("webproject.validation.error", redirect);
			return "redirect:/webprojects/update/" + id;
		}
		try {
			this.webProjectService.updateWebProject(webProject, utils.getUserId());
		} catch (WebProjectServiceException e) {
			log.debug("Cannot update web project");
			utils.addRedirectMessage("webproject.update.error", redirect);
			return "redirect:/webprojects/create";
		}
		return "redirect:/webprojects/list";

	}

	@GetMapping(value = "/delete/{id}")
	public String deleteWebProject(Model model, @PathVariable("id") String id, RedirectAttributes redirect) {

		WebProject webProject = webProjectService.getWebProjectById(id, utils.getUserId());
		if (webProject != null) {
			try {
				this.webProjectService.deleteWebProject(webProject, utils.getUserId());
			} catch (Exception e) {
				utils.addRedirectMessage("webproject.delete.error", redirect);
				return "redirect:/webprojects/list";
			}
			return "redirect:/webprojects/list";
		} else {
			return "redirect:/webprojects/list";
		}
	}

	@PostMapping(value = "/uploadZip")
	public @ResponseBody ResponseEntity<String> uploadZip(MultipartHttpServletRequest request) {

		Iterator<String> itr = request.getFileNames();
		String uploadedFile = itr.next();
		MultipartFile file = request.getFile(uploadedFile);
		try {
			this.webProjectService.uploadZip(file, utils.getUserId());
			return new ResponseEntity<String>("{\"status\" : \"ok\"}", HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

}
