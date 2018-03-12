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
package com.indracompany.sofia2.controlpanel.controller.graph;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Dashboard;

import com.indracompany.sofia2.config.model.Device;
import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DashboardRepository;

import com.indracompany.sofia2.config.repository.DeviceRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@Component
public class GraphUtil {

	private String urlClientPlatform;
	private String urlDashboard;
	private String urlGadget;
	private String urlOntology;
	private String urlImages;
	private String genericUserName = "USER";

	@Autowired
	private OntologyRepository ontologyRepository;
	@Autowired
	private ClientPlatformRepository clientPlatformRepository;
	@Autowired
	private GadgetRepository gadgetRepository;
	@Autowired
	private DashboardRepository dashboardRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private AppWebUtils utils;
	@Autowired
	private UserService userService;
	@Value("${sofia2.urls.iotbroker}")
	private String url;

	@Value("${sofia2.devices.timeout_devices_inseconds:300}")
	private int MAX_TIME_UPDATE_IN_SECONDS;

	private String ACTIVE = "active";
	private String INACTIVE = "inactive";

	private String IMAGE_DEVICE_ACTIVE = "deviceActive.png";
	private String IMAGE_DEVICE_INACTIVE = "deviceInactive.png";
	private String IMAGE_DEVICE_ERROR = "deviceError.png";
	private String IMAGE_CLIENT_PLATFORMS = "clientPlat.png";
	private String IMAGE_CLIENT = "client.png";
	private String IMAGE_CLIENT_ERROR = "clientError.png";

	@PostConstruct
	public void init() {
		// initialize URLS
		this.urlClientPlatform = this.url + "/controlpanel/devices/";
		this.urlGadget = this.url + "/controlpanel/gadgets/";
		this.urlDashboard = this.url + "/controlpanel/dashboards/";
		this.urlOntology = this.url + "/controlpanel/ontologies/";
		this.urlImages = this.url + "/controlpanel/static/images/";
	}

	public List<GraphDTO> constructGraphWithOntologies() {

		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();
		String name = utils.getMessage("name.ontologies", "ONTOLOGIES");
		String description = utils.getMessage("tooltip_ontologies", null);
		// carga de nodo ontologia con link a crear y con titulo
		arrayLinks.add(new GraphDTO(genericUserName, name, null, urlOntology + "list", genericUserName, name,
				utils.getUserId(), name, "suit", description, urlOntology + "create", null, null, null));
		List<Ontology> ontologies;
		if (utils.getRole().equals(Role.Type.ROLE_ADMINISTRATOR.name()))
			ontologies = ontologyRepository.findAll();
		else
			ontologies = ontologyRepository
					.findByUserAndOntologyUserAccessAndAllPermissions(this.userService.getUser(utils.getUserId()));
		for (Ontology ont : ontologies) {
			arrayLinks.add(new GraphDTO(name, ont.getId(), urlOntology + "list", urlOntology + "show/" + ont.getId(),
					name, "ontology", name, ont.getIdentification(), "licensing", null, null, null, null, null));
		}
		return arrayLinks;
	}

	public List<GraphDTO> constructGraphWithClientPlatforms() {

		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();
		String name = utils.getMessage("name.clients", "PLATFORM CLIENTS");
		String description = utils.getMessage("tooltip_clients", null);

		// carga de nodo clientPlatform
		arrayLinks.add(new GraphDTO(genericUserName, name, null, urlClientPlatform + "list", genericUserName, name,
				utils.getUserId(), name, "suit", description, urlClientPlatform + "create", null, null, null));

		List<ClientPlatform> clientPlatforms = clientPlatformRepository.findByUser(this.userService.getUser(utils.getUserId()));

		for (ClientPlatform clientPlatform : clientPlatforms) {
			// Creación de enlaces
			arrayLinks.add(new GraphDTO(name, clientPlatform.getId(), urlClientPlatform + "list",
					urlClientPlatform + clientPlatform.getId(), name, "clientplatform", name,
					clientPlatform.getIdentification(), "licensing", null, null, null, null, null));

			if (clientPlatform.getClientPlatformOntologies() != null) {
				List<ClientPlatformOntology> clientPlatformOntologies = new LinkedList<ClientPlatformOntology>(
						clientPlatform.getClientPlatformOntologies());
				for (ClientPlatformOntology clientPlatformOntology : clientPlatformOntologies) {
					Ontology ontology = clientPlatformOntology.getOntology();
					// Crea link entre ontologia y clientPlatform
					arrayLinks.add(new GraphDTO(ontology.getId(), clientPlatform.getId(),
							urlOntology + ontology.getId(), urlClientPlatform + clientPlatform.getId(), "ontology",
							"clientplatform", ontology.getIdentification(), clientPlatform.getIdentification(),
							"licensing", null, null, null, null, null));
				}
			}
		}
		return arrayLinks;
	}

	private List<GraphDTO> constructGraphWithGadgets(String visualizationId, String visualizationName) {

		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();
		/*String name = utils.getMessage("name.gadgets", "GADGETS");

		// carga de nodo gadget dependiente de visualizacion
		arrayLinks.add(new GraphDTO(visualizationId, name, null, urlGadget + "list", visualizationId, name,
				visualizationName, name, "suit", null, urlGadget + "selectWizard", null, null, null));

		List<Gadget> gadgets = gadgetRepository.findByUser(this.userService.getUser(utils.getUserId()));

		if (gadgets != null) {
			for (Gadget gadget : gadgets) {
				// Creación de enlaces
				arrayLinks.add(new GraphDTO(name, gadget.getId(), urlGadget + "list", urlDashboard + gadget.getId(),
						name, "gadget", name, gadget.getName(), "licensing", null, null, null, null, null));
				if (gadget.getToken() != null) {
					// si tiene token , tiene kp
					arrayLinks.add(new GraphDTO(gadget.getToken().getClientPlatform().getId(), gadget.getId(),
							urlClientPlatform + gadget.getToken().getClientPlatform().getId(),
							urlDashboard + gadget.getId(), "clientplatform", "gadget",
							gadget.getToken().getClientPlatform().getIdentification(), gadget.getName(), "suit", null,
							null, null, null, null));
				}
			}
			gadgets.clear();
		}*/
		return arrayLinks;
	}

	private List<GraphDTO> constructGraphWithDashboard(String visualizationId, String visualizationName) {

		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();
		/*String name = utils.getMessage("name.dashboards", "DASHBOARDS");

		arrayLinks.add(new GraphDTO(visualizationId, name, null, urlDashboard + "list", visualizationId, name,
				visualizationName, name, "suit", null, urlDashboard + "creategroup?", null, null, null));

		// dashboardTipo---> son los dashboard
		List<DashboardType> dashboardTypes = dashboardTypeRepository
				.findByUser(this.userService.getUser(utils.getUserId()));
		for (DashboardType dashboardType : dashboardTypes) {
			// Ahora hay que buscar la relacion entre dashboard y gadget. Eso nos lo da el
			// dashboard
			List<Dashboard> dashboards = dashboardRepository.findByDashboardType(dashboardType);
			arrayLinks.add(new GraphDTO(name, Integer.toString(dashboardType.getId()), urlDashboard + "list",
					urlDashboard + Integer.toString(dashboardType.getId()), name, "dashboard", null,
					dashboardType.getType(), "licensing", null, null, null, null, null));

			for (Dashboard dashboard : dashboards) {
				try {
					List<String> gadgetIds = this.getGadgetIdsFromModel(dashboard.getModel());
					for (String gadget : gadgetIds) {
						arrayLinks.add(new GraphDTO(gadget, Integer.toString(dashboardType.getId()),
								urlDashboard + gadget, urlDashboard + dashboardType.getId(), "gadget", "dashboard",
								null, dashboardType.getType(), "licensing", null, null, null, null, null));
					}
				} catch (Exception e) {

				}
			}
			dashboards.clear();
		}*/

		return arrayLinks;
	}

	public List<GraphDTO> constructGraphWithVisualization() {

		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();
		String name = utils.getMessage("name_visualization", "VISUALIZATION");
		String description = utils.getMessage("tooltip_visualization", null);
		// carga de nodo gadget
		arrayLinks.add(new GraphDTO(genericUserName, name, null, null, genericUserName, name, utils.getUserId(), name,
				"suit", description, null, null, null, null));

		arrayLinks.addAll(constructGraphWithGadgets(name, name));

		arrayLinks.addAll(constructGraphWithDashboard(name, name));

		return arrayLinks;
	}

	public List<String> getGadgetIdsFromModel(String modelJson) throws JsonProcessingException, IOException {
		List<String> gadgetIds = new LinkedList<String>();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(modelJson);
		int rows = jsonNode.path("rows").size();
		for (int i = 0; i < rows; i++) {
			int columns = jsonNode.path("rows").path(i).path("columns").size();
			for (int j = 0; j < columns; j++) {
				int widgets = jsonNode.path("rows").path(i).path("columns").path(j).path("widgets").size();
				for (int k = 0; k < widgets; k++) {
					String gadgetId = jsonNode.path("rows").path(i).path("columns").path(j).path("widgets").path(k)
							.path("config").get("gadgetId").asText();
					gadgetId = gadgetId.split("_")[0];
					gadgetIds.add(gadgetId);
				}
			}

		}
		return gadgetIds;

	}

	public List<GraphDTO> constructGraphWithClientPlatformsForDevice(String id) {

		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();
		String name = utils.getMessage("name.clients", "PLATFORM CLIENTS");
		String description = utils.getMessage("tooltip_clients", null);

		arrayLinks.add(new GraphDTO(genericUserName, name, null, urlClientPlatform + "list", genericUserName, name,
				utils.getUserId(), name, "suit", description, urlClientPlatform + "create", null, null, null));

		ClientPlatform clientPlatform = clientPlatformRepository.findById(id);

		arrayLinks.add(new GraphDTO(name, clientPlatform.getId(), urlClientPlatform + "list",
				urlClientPlatform + clientPlatform.getId(), name, "clientplatform", name,
				clientPlatform.getIdentification(), "licensing", null, null, null, null, null));

		List<Device> listDevice = deviceRepository.findByClientPlatform(id);
		if (listDevice != null && listDevice.size() > 0) {
			for (Iterator iterator = listDevice.iterator(); iterator.hasNext();) {
				Device device = (Device) iterator.next();
				String state = "inactive";
				if (device.isConnected()) {
					state = "active";
					if (device.getStatus() != null && device.getStatus().trim().length() > 0) {
						if (!device.getStatus().equals("ok")) {
							state = "error";
						}
					}
				} else {
					state = "inactive";
					if (device.getStatus() != null && device.getStatus().trim().length() > 0) {
						if (!device.getStatus().equals("ok")) {
							state = "error";
						}
					}
				}

				arrayLinks.add(new GraphDTO(clientPlatform.getId(), device.getId(), urlClientPlatform + device.getId(),
						urlClientPlatform + clientPlatform.getId(), "clientplatform", "clientplatform",
						clientPlatform.getIdentification(), device.getIdentification(), state, null, null, null, null,
						null));
			}

		}

		return arrayLinks;
	}

	public List<GraphDTO> constructGraphWithClientPlatformsForUser() {

		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();
		String name = utils.getMessage("name.clients", "PLATFORM CLIENTS");
		String description = utils.getMessage("tooltip_clients", null);

		arrayLinks.add(new GraphDTO(genericUserName, name, null, null, genericUserName, name, utils.getUserId(), name,
				"suit", this.urlImages + IMAGE_CLIENT_PLATFORMS, null, null, null, null));

		List<ClientPlatform> clientPlatforms = null;
		if (utils.isAdministrator()) {
			clientPlatforms = clientPlatformRepository.findAll();

		} else {
			clientPlatforms = clientPlatformRepository.findByUser(this.userService.getUser(utils.getUserId()));

		}

		for (ClientPlatform clientPlatform : clientPlatforms) {

			List<Device> listDevice = deviceRepository.findByClientPlatform(clientPlatform.getId());

			String clientImage = IMAGE_CLIENT;
			if (listDevice != null && listDevice.size() > 0) {
				for (Iterator iterator = listDevice.iterator(); iterator.hasNext();) {
					Device device = (Device) iterator.next();
					if (!device.getStatus().equals(Device.StatusType.OK.toString())) {
						clientImage = IMAGE_CLIENT_ERROR;
					}
				}
			}

			arrayLinks.add(new GraphDTO(name, clientPlatform.getId(), null, null, name, "clientplatform", name,
					clientPlatform.getIdentification(), "licensing", this.urlImages + clientImage, null, null, null,
					null));

			if (listDevice != null && listDevice.size() > 0) {
				for (Iterator iterator = listDevice.iterator(); iterator.hasNext();) {
					Device device = (Device) iterator.next();
					String state = INACTIVE;
					String image = IMAGE_DEVICE_INACTIVE;
					if (device.isConnected() && !maximunTimeUpdatingExceeded(device.getUpdatedAt())) {
						state = ACTIVE;
						image = IMAGE_DEVICE_ACTIVE;
						if (device.getStatus() != null && device.getStatus().trim().length() > 0) {
							if (!device.getStatus().equals(Device.StatusType.OK.toString())) {

								image = IMAGE_DEVICE_ERROR;
							}
						}
					} else {
						state = INACTIVE;
						image = IMAGE_DEVICE_INACTIVE;
						if (device.getStatus() != null && device.getStatus().trim().length() > 0) {
							if (!device.getStatus().equals(Device.StatusType.OK.toString())) {

								image = IMAGE_DEVICE_ERROR;
							}
						}
					}

					arrayLinks.add(new GraphDTO(clientPlatform.getId(), device.getId(), device.getDescription(),
							device.getJsonActions(), "clientplatform", "clientplatform",
							clientPlatform.getIdentification(), device.getIdentification(), state,
							this.urlImages + image, device.getStatus(), state, device.getSessionKey(),
							device.getUpdatedAt()));
				}

			}
		}
		return arrayLinks;
	}

	private boolean maximunTimeUpdatingExceeded(Date lastUpdate) {
		Date currentDate = new Date();
		long diff = currentDate.getTime() - lastUpdate.getTime();

		long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
		if (seconds >= MAX_TIME_UPDATE_IN_SECONDS) {
			return true;

		} else {
			return false;
		}

	}

}
