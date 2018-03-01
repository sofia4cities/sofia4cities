package com.indracompany.sofia2.controlpanel.controller.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indracompany.sofia2.config.model.DeviceSimulation;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.simulation.DeviceSimulationService;
import com.indracompany.sofia2.controlpanel.services.simulation.SimulationService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@Controller
@RequestMapping("devicesimulation")
public class DeviceSimulatorController {

	@Autowired
	private DeviceSimulationService deviceSimulationService;
	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private AppWebUtils utils;
	@Autowired
	private SimulationService simulationService;

	@GetMapping("create")
	public String simulate(Model model) {
		List<String> clients = this.deviceSimulationService.getClientsForUser(this.utils.getUserId());
		List<String> simulators = this.deviceSimulationService.getSimulatorTypes();
		model.addAttribute("platformClients", clients);
		model.addAttribute("simulators", simulators);
		model.addAttribute("simulation", new DeviceSimulation());
		return "/simulator/create";
	}

	@PostMapping("create")
	public @ResponseBody String debug(Model model, @RequestParam String identification, @RequestParam String jsonMap,
			@RequestParam String ontology, @RequestParam String clientPlatform, @RequestParam String token,
			@RequestParam int interval) throws JsonProcessingException, IOException {

		this.simulationService.scheduleSimulation(identification, interval, utils.getUserId(),
				this.simulationService.getDeviceSimulationJson(clientPlatform, token, ontology, jsonMap));
		return "{\"message\":\"ok\"}";
	}

	@PostMapping("ontologiesandtokens")
	public String getOntologiesAndTokens(Model model, @RequestParam String clientPlatformId) {

		model.addAttribute("ontologies",
				this.deviceSimulationService.getClientOntologiesIdentification(clientPlatformId));
		model.addAttribute("tokens", this.deviceSimulationService.getClientTokensIdentification(clientPlatformId));

		return "/simulator/create :: ontologiesAndTokens";
	}

	@PostMapping("ontologyfields")
	public String getOntologyfields(Model model, @RequestParam String ontologyIdentification)
			throws JsonProcessingException, IOException {

		model.addAttribute("fields", this.ontologyService.getOntologyFields(ontologyIdentification));
		model.addAttribute("simulators", this.deviceSimulationService.getSimulatorTypes());
		return "/simulator/create :: ontologyFields";
	}

}
