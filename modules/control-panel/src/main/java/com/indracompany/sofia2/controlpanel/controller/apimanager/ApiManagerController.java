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
package com.indracompany.sofia2.controlpanel.controller.apimanager;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.services.apimanager.ApiManagerService;
import com.indracompany.sofia2.config.services.exceptions.ApiManagerServiceException;
import com.indracompany.sofia2.controlpanel.helper.apimanager.ApiManagerHelper;
import com.indracompany.sofia2.controlpanel.multipart.ApiMultipart;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/apimanager")
@Slf4j
public class ApiManagerController {

	@Autowired
	ApiManagerService apiManagerService;
	@Autowired
	ApiManagerHelper apiManagerHelper;
	@Autowired
	private AppWebUtils utils;

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@GetMapping(value = "/create", produces = "text/html")
	public String createForm(Model model) {

		apiManagerHelper.populateApiManagerCreateForm(model);

		return "apimanager/create";
	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@GetMapping(value = "/update/{id}")
	public String updateForm(@PathVariable("id") String id, Model model) {

		apiManagerHelper.populateApiManagerUpdateForm(model, id);

		return "apimanager/create";
	}

	@GetMapping(value = "/show/{id}", produces = "text/html")
	public String show(@PathVariable("id") String id, Model model) {

		apiManagerHelper.populateApiManagerShowForm(model, id);

		return "apimanager/show";
	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@RequestMapping(value = "/list", produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, Model uiModel, HttpServletRequest request) {

		apiManagerHelper.populateApiManagerListForm(uiModel);

		String apiId = request.getParameter("apiId");
		String state = request.getParameter("state");
		String user = request.getParameter("user");

		uiModel.addAttribute("apis", apiManagerService.loadAPISByFilter(apiId, state, user));

		return "apimanager/list";
	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@PostMapping(value = "/create")
	public String create(ApiMultipart api, BindingResult bindingResult, HttpServletRequest request,
			RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			log.debug("Some user properties missing");
			utils.addRedirectMessage("api.create.error", redirect);
			return "redirect:/apimanager/create";
		}

		// if (api.getMultipartImage()!=null && api.getMultipartImage().getSize()>0 &&
		// !"image/png".equalsIgnoreCase(api.getMultipartImage().getContentType()) &&
		// !"image/jpeg".equalsIgnoreCase(api.getMultipartImage().getContentType())
		// && !"image/jpg".equalsIgnoreCase(api.getMultipartImage().getContentType()) &&
		// !"application/octet-stream".equalsIgnoreCase(api.getMultipartImage().getContentType())){
		// log.debug("Some user properties missing");
		// utils.addRedirectMessage("user.create.error", redirect);
		// return "redirect:/apimanager/create";
		// }

		try {
			String operationsObject = request.getParameter("operationsObject");
			String authenticationObject = request.getParameter("authenticationObject");

			String apiId = apiManagerService.createApi(apiManagerHelper.apiMultipartMap(api), operationsObject,
					authenticationObject);

			utils.addRedirectMessage("user.create.success", redirect);
			return "redirect:/apimanager/" + utils.encodeUrlPathSegment(apiId, request);
		} catch (ApiManagerServiceException e) {
			log.debug("Cannot update user that does not exist");
			utils.addRedirectMessage("user.create.error", redirect);
			return "redirect:/apimanager/create";
		}
	}

	@PutMapping(value = "/update/{id}")
	public String update(@PathVariable("id") String id, ApiMultipart api, BindingResult bindingResult,
			HttpServletRequest request, RedirectAttributes redirect) {

		if (bindingResult.hasErrors()) {
			utils.addRedirectMessage("api.update.error", redirect);
			return "redirect:/apimanager/update";
		}
		// if (api.getImagen()!=null && api.getImagen().getSize()>0 &&
		// !"image/png".equalsIgnoreCase(api.getImagen().getContentType()) &&
		// !"image/jpeg".equalsIgnoreCase(api.getImagen().getContentType())
		// && !"image/jpg".equalsIgnoreCase(api.getImagen().getContentType()) &&
		// !"application/octet-stream".equalsIgnoreCase(api.getImagen().getContentType())){
		// LOG.error("Error. La imagen introducida no esta permitida");
		// utils.addRedirectMessage("api.update.error", redirect);
		// return "redirect:/apimanager/update";
		// }

		try {
			String operationsObject = request.getParameter("operationsObject");
			String authenticationObject = request.getParameter("authenticationObject");
			String deprecateApis = request.getParameter("deprecateApis");

			apiManagerService.updateApi(apiManagerHelper.apiMultipartMap(api), deprecateApis, operationsObject,
					authenticationObject);

			utils.addRedirectMessage("api.update.success", redirect);
			return "redirect:/apimanager/" + utils.encodeUrlPathSegment(api.getId(), request);
		} catch (Exception e) {
			log.debug("Cannot update user that does not exist");
			utils.addRedirectMessage("api.update.error", redirect);
			return "redirect:/apimanager/update";
		}
	}

	// AUTHORIZATIONS//

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@RequestMapping(value = "/authorize/list", produces = "text/html")
	public String index(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, Model model) {
		apiManagerHelper.populateAutorizationForm(model);
		return "apimanager/authorize";
	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@PostMapping(value = "/authorize")
	public String update(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, Model modelMap, HttpServletRequest request,
			RedirectAttributes redirect) {
		try {
			apiManagerService.updateAuthorization(request.getParameter("apiId"), request.getParameter("userId"));
			return "redirect:/apimanager/authorize/list";
		} catch (Exception e) {
			log.debug("Cannot update authorization that does not exist");
			utils.addRedirectMessage("api.autn.update.error", redirect);
			return "redirect:/apimanager/update";
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@DeleteMapping(value = "authorize/list/{id}")
	public String remove(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		try {
			apiManagerService.removeAuthorizationById(id);
		} catch (Exception e) {
		}
		uiModel.asMap().clear();
		return "redirect:/apimanager/authorize/list";
	}

	@RequestMapping(value = "/token/list", produces = "text/html")
	public String token(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, Model model, HttpServletRequest request) {
		apiManagerHelper.populateUserTokenForm(model);
		return "apimanager/token";
	}

	@RequestMapping(value = "numVersion")
	public @ResponseBody Integer numVersion(@RequestBody String numversionData) {
		return (apiManagerService.calculateNumVersion(numversionData));
	}

	@RequestMapping(value = "/{id}/getImage")
	public void showImg(@PathVariable("id") String id, HttpServletResponse response) {
		byte[] buffer = apiManagerService.getImgBytes(id);
		if (buffer.length > 0) {
			OutputStream output = null;
			try {
				output = response.getOutputStream();
				response.setContentLength(buffer.length);
				output.write(buffer);
			} catch (Exception e) {
			} finally {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@GetMapping(value = "/updateState/{id}/{state}")
	public String publish(@PathVariable("id") String id, @PathVariable("state") String state, Model uiModel) {
		apiManagerService.updateState(id, state);
		return "redirect:/apimanager/list";
	}

}
