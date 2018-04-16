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
package com.indracompany.sofia2.controlpanel.controller.market;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.services.market.MarketAssetService;
import com.indracompany.sofia2.controlpanel.helper.market.MarketAssetHelper;
import com.indracompany.sofia2.controlpanel.multipart.MarketAssetMultipart;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/marketasset")
@Slf4j
public class MarketAssetController {

	@Autowired
	MarketAssetService marketAssetService;
	@Autowired
	MarketAssetHelper marketAssetHelper;
	@Autowired
	private AppWebUtils utils;
		
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@GetMapping(value = "/create", produces = "text/html")
	public String createForm(Model model) {
		
		marketAssetHelper.populateMarketAssetCreateForm(model);

		return "marketasset/create";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@GetMapping(value = "/update/{id}")
	public String updateForm(@PathVariable("id") String id, Model model) {

		try {
			marketAssetHelper.populateMarketAssetUpdateForm(model, id);
		} catch (Exception e) {
			marketAssetHelper.populateMarketAssetShowForm(model, id);
			return "marketasset/show";
		}

		return "marketasset/create";
	}
	
	@GetMapping(value = "/show/{id}", produces = "text/html")
	public String show(@PathVariable("id") String id, Model model) {
		
		marketAssetHelper.populateMarketAssetShowForm(model, id);

		return "marketasset/show";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER') or hasRole('ROLE_USER')")
	@GetMapping(value = "/list" , produces = "text/html")
	public String list(Model model,	@RequestParam(required = false) String marketassetId) {		
		
		marketAssetHelper.populateMarketAssetListForm(model);
		
		model.addAttribute("marketAssets", marketAssetHelper.toMarketAssetBean(marketAssetService.loadMarketAssetByFilter(marketassetId, utils.getUserId())));
		
		return "marketasset/list";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@PostMapping(value = "/create")
	public String create(MarketAssetMultipart marketAssetMultipart, BindingResult bindingResult, MultipartHttpServletRequest request, RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			log.debug("Some user properties missing");
			utils.addRedirectMessage("resource.create.error", redirect);
			return "redirect:/marketasset/create";
		}
		
		try {
			
			String apiId = marketAssetService.createMarketAsset(marketAssetHelper.marketAssetMultipartMap(marketAssetMultipart));

			return "redirect:/marketasset/show/" + apiId;
		} catch (Exception e) {
			log.debug("Cannot update user that does not exist");
			utils.addRedirectMessage("user.create.error", redirect);
			return "redirect:/marketasset/create";
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@PostMapping(value="/update/{id}")
	public String update(@PathVariable("id") String id, MarketAssetMultipart marketAssetMultipart, MultipartHttpServletRequest request, BindingResult bindingResult, RedirectAttributes redirect) {

		if (bindingResult.hasErrors()) {
			utils.addRedirectMessage("api.update.error", redirect);
			return "redirect:/marketasset/update";
		}
		
		try {
			marketAssetService.updateMarketAsset(id, marketAssetHelper.marketAssetMultipartMap(marketAssetMultipart));

			return "redirect:/marketasset/show/" + id;
		} catch (Exception e) {
			log.debug("Cannot update user that does not exist");
			utils.addRedirectMessage("api.update.error", redirect);
			return "redirect:/marketasset/update";
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_DEVELOPER')")
	@GetMapping(value = "/delete/{id}" , produces = "text/html")
	public String delete(Model model, @PathVariable("id") String id) {		
		
		marketAssetService.delete(id, utils.getUserId());

		return "redirect:/marketasset/list";
	}
	
	@GetMapping(value = "/rateit/{id}/{rate}" , produces = "text/html")
	public String rateit(Model model, @PathVariable("id") String id, @PathVariable("rate") String rate) {		
		
		marketAssetService.rate(id, rate, utils.getUserId());

		return "redirect:/marketasset/show/" + id;
	}
	
	@PostMapping(value="/comment")
	public String comment(HttpServletRequest request, RedirectAttributes redirect) {
		String id = request.getParameter("marketAssetId");
		String title = request.getParameter("commentTitle");
		String comment = request.getParameter("comment");
		
		try {
			marketAssetService.createComment(id, utils.getUserId(), title, comment);

			return "redirect:/marketasset/show/" + id;
		} catch (Exception e) {
			log.debug("Cannot update user that does not exist");
			utils.addRedirectMessage("api.update.error", redirect);
			return "redirect:/marketasset/show/" + id;
		}
	}
	
	@GetMapping(value = "/deletecomment/{marketassetid}/{id}" , produces = "text/html")
	public String deletecomment(Model model, @PathVariable("marketassetid") String marketassetid, @PathVariable("id") String id) {		
		
		marketAssetService.deleteComment(id);

		return "redirect:/marketasset/show/" + marketassetid;
	}
	
	
	@RequestMapping(value = "/fragment/{type}")
	public String fragment(Model model, @PathVariable("type") String type) {
		
		marketAssetHelper.populateMarketAssetFragment(model, type);
		
		return "marketasset/marketassetfragments :: " + type + "MarketAssetFragment";
	}
	
	@RequestMapping(value = "/apiversions/{identification}")
	public String apiversions(Model model, @PathVariable("identification") String identification) {
		
		marketAssetHelper.populateApiVersions(model, identification);
		
		return "marketasset/marketassetfragments :: #versions";
	}
	
	
	@RequestMapping(value = "/apidescription")
	public @ResponseBody String apidescription(@RequestBody String apiData){
		return (marketAssetHelper.getApiDescription(apiData));
	}
	
	@RequestMapping(value = "/urlwebproject")
	public @ResponseBody String urlwebproject(@RequestBody String webProjectData) {
		return (marketAssetHelper.getUrlWebProjectData(webProjectData));
	}

	@RequestMapping(value = "/validateId")
	public @ResponseBody String validateId(@RequestBody String marketAssetId){
		return (marketAssetHelper.validateId(marketAssetId));
	}
	
	@RequestMapping(value="/{id}/getImage")
	public void showImg(@PathVariable("id") String id, HttpServletResponse response) {
		byte[] buffer = marketAssetService.getImgBytes(id);
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
	
	@RequestMapping(value="/{id}/downloadContent")
	public void download(@PathVariable("id") String id, HttpServletResponse response) {
		try {
			marketAssetService.downloadDocument(id, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@PostMapping(value = "/updateState/{id}/{state}")
	public String updateState(@PathVariable("id") String id, @PathVariable("state") String state, @RequestBody String reasonData){
		marketAssetService.updateState(id, state, reasonData);
		return "redirect:/marketasset/list";
	}
	
}
