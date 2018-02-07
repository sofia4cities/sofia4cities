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
package com.indracompany.sofia2.controlpanel.controller.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.TwitterListening;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.configuration.ConfigurationService;
import com.indracompany.sofia2.config.services.exceptions.ClientPlatformServiceException;
import com.indracompany.sofia2.config.services.exceptions.OntologyServiceException;
import com.indracompany.sofia2.config.services.exceptions.TokenServiceException;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.twitter.TwitterService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.controller.user.UserController;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/twitter")
@Slf4j
public class TwitterListeningController {

	@Autowired
	AppWebUtils utils;
	@Autowired
	TwitterService twitterService;
	@Autowired
	OntologyService ontologyService;
	@Autowired
	ConfigurationService configurationService;
	@Autowired
	ClientPlatformService clientPlatformService;

	@Autowired
	UserService userService;

	@GetMapping("/scheduledsearch/list")
	public String list(Model model) {
		model.addAttribute("twitterListenings", this.twitterService.getAllListeningsByUser(utils.getUserId()));
		return "/twitter/scheduledsearch/list";
	}

	@GetMapping("/scheduledsearch/create")
	public String createForm(Model model) {
		this.loadOntologiesAndConfigurations(model);
		model.addAttribute("twitterListening", new TwitterListening());
		return "/twitter/scheduledsearch/create";
	}

	@GetMapping("/scheduledsearch/update/{id}")
	public String updateForm(Model model, @PathVariable("id") String id) {
		TwitterListening TwitterListening = null;
		TwitterListening = this.twitterService.getListenById(id);
		if (TwitterListening == null)
			TwitterListening = new TwitterListening();
		model.addAttribute("twitterListening", TwitterListening);
		this.loadOntologiesAndConfigurations(model);
		return "/twitter/scheduledsearch/create";
	}

	@PutMapping("/scheduledsearch/update/{id}")
	public String update(Model model, @PathVariable("id") String id, @ModelAttribute TwitterListening twitterListener) {

		if (twitterListener != null)
			this.twitterService.updateListen(twitterListener);
		return "redirect:/twitter/scheduledsearch/update/" + id;
	}

	@PostMapping("/scheduledsearch/create")
	public String create(Model model, @Valid TwitterListening twitterListening,
			BindingResult bindingResult, RedirectAttributes redirect,
			@RequestParam("_new") Boolean newOntology,
			@RequestParam(value = "ontologyId", required = false) String ontologyId,
			@RequestParam(value = "clientPlatformId", required = false) String clientPlatformId) {

		if(bindingResult.hasErrors() && !newOntology)
		{
			log.debug("TwitterListening object has errors");
			this.utils.addRedirectMessage("twitterlistening.validation.error", redirect);
			return "redirect:/twitter/scheduledsearch/create";
		}

		if (!newOntology) {
			if (twitterListening.getUser() == null)
				twitterListening.setUser(this.userService.getUser(this.utils.getUserId()));
			this.twitterService.createListening(twitterListening);
		} else {

			try{
				Ontology ontology = this.twitterService.createTwitterOntology(ontologyId,
						DataModel.MainType.Twitter.toString());
				ontology.setUser(this.userService.getUser(this.utils.getUserId()));
				ontology = this.ontologyService.saveOntology(ontology);

				ArrayList<Ontology> ontologies = new ArrayList<Ontology>();
				ontologies.add(ontology);				

				ClientPlatform client= new ClientPlatform();
				client.setUser(this.userService.getUser(utils.getUserId()));
				client.setIdentification(clientPlatformId);

				Token token = this.clientPlatformService.createClientAndToken(ontologies, client);

				twitterListening.setOntology(ontology);
				twitterListening.setToken(token);
				twitterListening.setUser(this.userService.getUser(this.utils.getUserId()));
				this.twitterService.createListening(twitterListening);
			}catch (RuntimeException e)
			{
				if(e instanceof OntologyServiceException)
					log.debug("Error creating ontology");
				if(e instanceof ClientPlatformServiceException)
					log.debug("Error creating platform client");
				if(e instanceof TokenServiceException)
					log.debug("Error generating token");
				e.printStackTrace();
			}

		}
		return "redirect:/twitter/scheduledsearch/list";

	}

	@PostMapping("/scheduledsearch/getclients")
	public @ResponseBody List<String> getClientsOntology(@RequestBody String ontologyId) {
		return this.twitterService.getClientsFromOntology(ontologyId);
	}

	@PostMapping("/scheduledsearch/gettokens")
	public @ResponseBody List<String> getTokensClient(@RequestBody String clientPlatformId) {
		return this.twitterService.getTokensFromClient(clientPlatformId);
	}

	public void loadOntologiesAndConfigurations(Model model) {
		List<Configuration> configurations = new ArrayList<Configuration>();
		List<Ontology> ontologies = new ArrayList<Ontology>();
		if (utils.isAdministrator()) {
			configurations = this.twitterService.getAllConfigurations();
			ontologies = this.ontologyService.getAllOntologies();
		} else {
			configurations = this.twitterService.getConfigurationsByUserId(this.utils.getUserId());
			for (Ontology ontology : this.ontologyService.getOntologiesByUserId(this.utils.getUserId())) {
				if (ontology.getDataModel().getType().equals(DataModel.MainType.Twitter)) {
					ontologies.add(ontology);
				}
			}
		}
		model.addAttribute("configurations", configurations);
		model.addAttribute("ontologies", ontologies);

	}

	@PostMapping("/scheduledsearch/existontology")
	public @ResponseBody boolean existOntology(@RequestBody String identification) {
		return this.twitterService.existOntology(identification);
	}

	@PostMapping("/scheduledsearch/existclient")
	public @ResponseBody boolean existClient(@RequestBody String identification) {
		return this.twitterService.existClientPlatform(identification);
	}
}
