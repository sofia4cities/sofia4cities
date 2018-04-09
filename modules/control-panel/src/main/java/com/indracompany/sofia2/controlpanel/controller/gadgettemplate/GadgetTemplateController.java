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
package com.indracompany.sofia2.controlpanel.controller.gadgettemplate;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.GadgetTemplate;
import com.indracompany.sofia2.config.services.gadgettemplate.GadgetTemplateService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/gadgettemplates")
@Controller
@Slf4j
public class GadgetTemplateController {

	@Autowired
	private GadgetTemplateService gadgetTemplateService;

	@Autowired
	private UserService userService;

	@Autowired
	private AppWebUtils utils;

	@RequestMapping(value = "/list", produces = "text/html")
	public String list(Model uiModel, HttpServletRequest request) {

		String identification = request.getParameter("identification");
		String description = request.getParameter("description");

		if (identification != null) {
			if (identification.equals(""))
				identification = null;
		}
		if (description != null) {
			if (description.equals(""))
				description = null;
		}

		List<GadgetTemplate> gadgetTemplate = this.gadgetTemplateService
				.findGadgetTemplateWithIdentificationAndDescription(identification, description, utils.getUserId());

		uiModel.addAttribute("gadgetTemplates", gadgetTemplate);
		return "gadgettemplates/list";
	}

	@RequestMapping(method = RequestMethod.POST, value = "getNamesForAutocomplete")
	public @ResponseBody List<String> getNamesForAutocomplete() {
		return this.gadgetTemplateService.getAllIdentifications();
	}

	@GetMapping(value = "/create", produces = "text/html")
	public String createGadget(Model model) {
		model.addAttribute("gadgetTemplate", new GadgetTemplate());

		return "gadgettemplates/create";

	}

	@PostMapping(value = "/create", produces = "text/html")
	public String saveGadget(@Valid GadgetTemplate gadgetTemplate, BindingResult bindingResult, Model uiModel,
			HttpServletRequest httpServletRequest, RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			log.debug("Some gadgetTemplate properties missing");
			utils.addRedirectMessage("gadgets.validation.error", redirect);
			return "redirect:/gadgettemplates/create";
		}

		gadgetTemplate.setUser(this.userService.getUser(this.utils.getUserId()));
		this.gadgetTemplateService.createGadgetTemplate(gadgetTemplate);

		return "redirect:/gadgettemplates/list";

	}

	@GetMapping(value = "/update/{gadgetTemplateId}", produces = "text/html")
	public String createGadget(Model model, @PathVariable("gadgetTemplateId") String gadgetTemplateId) {
		model.addAttribute("gadgetTemplate", this.gadgetTemplateService.getGadgetTemplateById(gadgetTemplateId));
		return "gadgettemplates/create";
	}

	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable("id") String id) {
		this.gadgetTemplateService.deleteGadgetTemplate(id, utils.getUserId());
		return "redirect:/gadgettemplates/list";
	}

	@PutMapping(value = "/update/{id}", produces = "text/html")
	public String updateGadget(Model model, @PathVariable("id") String id, @Valid GadgetTemplate gadgetTemplate,
			BindingResult bindingResult, RedirectAttributes redirect) {

		if (bindingResult.hasErrors()) {
			log.debug("Some GadgetTemplate properties missing");
			utils.addRedirectMessage("gadgets.validation.error", redirect);
			return "redirect:/gadgettemplates/update/" + id;
		}
		if (!gadgetTemplateService.hasUserPermission(id, this.utils.getUserId()))
			return "error/403";

		this.gadgetTemplateService.updateGadgetTemplate(gadgetTemplate);

		return "redirect:/gadgettemplates/list";
	}

	@GetMapping(value = "getUserGadgetTemplate")
	public @ResponseBody List<GadgetTemplate> getUserGadgetTemplate() {
		return this.gadgetTemplateService.getUserGadgetTemplate(utils.getUserId());
	}

}