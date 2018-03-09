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
package com.indracompany.sofia2.controlpanel.controller.gadget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetMeasure;
import com.indracompany.sofia2.config.services.gadget.GadgetDatasourceService;
import com.indracompany.sofia2.config.services.gadget.GadgetService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import groovy.util.logging.Slf4j;

@RequestMapping("/gadgets")
@Controller
@Slf4j

public class GadgetController {

	@Autowired
	private GadgetService gadgetService;
	
	@Autowired
	private GadgetDatasourceService gadgetDatasourceService;
	
	@Autowired
	private AppWebUtils utils;
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@RequestMapping(value = "/list", produces = "text/html")
	public String list (Model uiModel, HttpServletRequest request) {
				
		String identification = request.getParameter("identification");
		String description = request.getParameter("description");
		
		if(identification!=null){if(identification.equals("")) identification=null;}
		if(description!=null){if(description.equals("")) description=null;}

		List<Gadget> gadget=this.gadgetService.findGadgetWithIdentificationAndDescription( identification, description, utils.getUserId());
				//gadgets: tiene que coincidir con el del list
		uiModel.addAttribute("gadgets", gadget);
		return "/gadgets/list";
				
	}
		
	@RequestMapping(method = RequestMethod.POST, value="getNamesForAutocomplete")
	public @ResponseBody List<String> getNamesForAutocomplete(){
		return this.gadgetService.getAllIdentifications();
	}
	
	@GetMapping(value="getUserGadgetsByType/{type}")
	public @ResponseBody List<Gadget> getUserGadgetsByType(@PathVariable("type") String type){
		return this.gadgetService.getUserGadgetsByType(utils.getUserId(), type);
	}
	
	@GetMapping(value="getGadgetConfigById/{gadgetId}")
	public @ResponseBody Gadget getGadgetConfigById(@PathVariable("gadgetId") String gadgetId){
		return this.gadgetService.getGadgetById(utils.getUserId(), gadgetId);
	}
	
	@GetMapping(value="getGadgetMeasuresByGadgetId/{gadgetId}")
	public @ResponseBody List<GadgetMeasure> getGadgetMeasuresByGadgetId(@PathVariable("gadgetId") String gadgetId){
		return this.gadgetService.getGadgetMeasuresByGadgetId(utils.getUserId(), gadgetId);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	@GetMapping(value = "/create", produces = "text/html")
	public String createGadget(Model model) {
		model.addAttribute("gadgets",new Gadget());
		model.addAttribute("datasources",this.gadgetDatasourceService.getAllIdentifications());
		return "/gadgets/create";

	}
	
	
	
}