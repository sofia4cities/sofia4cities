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
package com.indracompany.sofia2.controlpanel.controller.flow;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomainStatus;
import com.indracompany.sofia2.config.model.Flow;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.services.flow.FlowService;
import com.indracompany.sofia2.config.services.flowdomain.FlowDomainService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.libraries.flow.engine.FlowEngineService;
import com.indracompany.sofia2.libraries.flow.engine.FlowEngineServiceFactory;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/flows")
@Slf4j
public class FlowDomainController {

	@Value("${sofia2.flowengine.services.request.timeout.ms:5000}")
	private int restRequestTimeout;

	@Value("${sofia2.flowengine.services.request.timeout.ms:http://localhost:8082/sofia2/flowengine/admin}")
	private String baseUrl;

	@Autowired
	private FlowDomainService domainService;

	@Autowired
	private FlowService flowService;

	@Autowired
	private UserService userService;

	@Autowired
	private AppWebUtils utils;

	private FlowEngineService flowEngineService;

	@PostConstruct
	public void init() {
		this.flowEngineService = FlowEngineServiceFactory.getFlowEngineService(this.baseUrl, this.restRequestTimeout);
	}

	@GetMapping(value = "/list", produces = "text/html")
	public String list(Model model) {

		FlowDomain domain = this.domainService.getFlowDomainByUser(utils.getUserId());
		if (domain != null) {

			List<String> domainList = new ArrayList<>();
			domainList.add(domain.getIdentification());
			List<FlowEngineDomainStatus> domainStatusList = null;
			// Get status info from FlowEngineAdmin
			try {
				domainStatusList = this.flowEngineService.getFlowEngineDomainStatus(domainList);
				model.addAttribute("flowEngineActive", true);
			} catch (Exception e) {
				// Flow Engine is either unavailable or not synchronized
				log.error("Unable to retrieve Flow Domain info. Cause = {}, Message = {}", e.getCause(),
						e.getMessage());
				model.addAttribute("flowEngineActive", false);
				model.addAttribute("message",
						utils.getMessage("domain.flow.Engine.notstarted", "Flow Engine is temporarily unreachable."));
				model.addAttribute("messageAlertType", "WARNING");
			}

			if (domainStatusList != null && !domainStatusList.isEmpty()) {
				// change memory to MB
				for (FlowEngineDomainStatus domainStatus : domainStatusList) {
					if (!domainStatus.getMemory().isEmpty()) {
						Double mem = new Double(domainStatus.getMemory()) / 1024d;
						domainStatus.setMemory(String.format("%.2f", mem));
					}
					if (!domainStatus.getCpu().isEmpty()) {
						Double cpu = new Double(domainStatus.getCpu());
						domainStatus.setCpu(String.format("%.2f", cpu));
					}
				}
			} else {
				// Create default
				domainStatusList = new ArrayList<>();
				FlowEngineDomainStatus domainStatus = FlowEngineDomainStatus.builder()
						.domain(domain.getIdentification()).home(domain.getHome()).port(domain.getPort())
						.servicePort(domain.getServicePort()).cpu("--").memory("--").state(domain.getState())
						.runtimeState("--").build();
				domainStatusList.add(domainStatus);
			}

			model.addAttribute("domains", domainStatusList);
			List<Flow> flows = flowService.getFlowByDomain(domain.getIdentification());
			model.addAttribute("flows", flows);
		}
		return "/flows/list";
	}

	@PostMapping(value = "/create")
	public String create(@Valid FlowDomain domain, BindingResult bindingResult, RedirectAttributes redirect) {
		if (domain.getIdentification() == null || domain.getIdentification().isEmpty()) {
			log.debug("Domain identifier is missing");
			utils.addRedirectMessage("domain.create.error", redirect);
			return "redirect:/flows/create";
		}
		try {
			this.domainService.createFlowDomain(domain.getIdentification(),
					this.userService.getUser(utils.getUserId()));
		} catch (Exception e) {
			log.debug("Cannot create flow domain.");
			utils.addRedirectMessage("domain.create.error", redirect);
			return "redirect:/flows/create";
		}
		utils.addRedirectMessage("domain.create.success", redirect);
		return "redirect:/flows/list";
	}

	@GetMapping(value = "/create", produces = "text/html")
	public String createForm(Model model) {
		FlowDomain domain = new FlowDomain();
		model.addAttribute("domain", domain);
		return "/flows/create";

	}

	@PostMapping(value = "/start")
	public String startDomain(Model model, @Valid @RequestBody FlowEngineDomainStatus domainStatus,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		try {
			FlowDomain domain = this.domainService.getFlowDomainByIdentification(domainStatus.getDomain());

			// START SERVICE
			FlowEngineDomain engineDom = FlowEngineDomain.builder().domain(domain.getIdentification())
					.port(domain.getPort()).home(domain.getHome()).servicePort(domain.getServicePort()).build();
			flowEngineService.startFlowEngineDomain(engineDom);

			// Update changes into CDB
			domain.setState("START");
			domainService.updateDomain(domain);

			// TODO: ask for new status state
			domainStatus.setState("START");
			model.addAttribute("flowEngineActive", true);
		} catch (Exception e) {
			log.error("Unable to start domain = {}.", domainStatus.getDomain());
			model.addAttribute("message", utils.getMessage("domain.error.notstarted", "Unable to stop domain"));
			model.addAttribute("messageAlertType", "ERROR");
			model.addAttribute("flowEngineActive", false);
		}
		List<FlowEngineDomainStatus> domainStatusList = new ArrayList<>();
		domainStatusList.add(domainStatus);
		model.addAttribute("domains", domainStatusList);
		return "/flows/list :: domain";

	}

	@PostMapping(value = "/stop", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String stopDomain(Model model, @Valid @RequestBody FlowEngineDomainStatus domainStatus,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		try {
			// STOP SERVICE
			flowEngineService.stopFlowEngineDomain(domainStatus.getDomain());
			// Update changes into CDB
			FlowDomain domain = this.domainService.getFlowDomainByIdentification(domainStatus.getDomain());
			domain.setState("STOP");
			domainService.updateDomain(domain);
			// Clean status not executing
			domainStatus.setState("STOP");
			domainStatus.setCpu("--");
			domainStatus.setMemory("--");
		} catch (Exception e) {
			log.error("Unable to start domain = {}.", domainStatus.getDomain());
			model.addAttribute("message", utils.getMessage("domain.error.notstopped", "Unable to stop domain"));
			model.addAttribute("messageAlertType", "ERROR");
		}
		List<FlowEngineDomainStatus> domainStatusList = new ArrayList<>();
		domainStatusList.add(domainStatus);
		model.addAttribute("domains", domainStatusList);
		return "/flows/list :: domain";

	}

	@GetMapping(value = "/show/{domainId}", produces = "text/html")
	public String showNodeRedPanelForm(Model model, @PathVariable(value = "domainId") String domainId) {
		String password = userService.getUser(utils.getUserId()).getPassword();
		model.addAttribute("proxy",
				"http://localhost:5050/" + domainId + "/?usuario=" + utils.getUserId() + "&password=" + password);
		return "/flows/show";
	}

	@GetMapping(value = "/check/{domainId}")
	public @ResponseBody boolean checkAvailableDomainIdentifier(@PathVariable(value = "domainId") String domainId) {
		return !domainService.domainExists(domainId);
	}

}
