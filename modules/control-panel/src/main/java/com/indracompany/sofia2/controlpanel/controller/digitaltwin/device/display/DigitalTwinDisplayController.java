/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.controlpanel.controller.digitaltwin.device.display;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.DigitalTwinType;
import com.indracompany.sofia2.config.service.digitaltwin.device.DigitalTwinDeviceService;
import com.indracompany.sofia2.config.service.digitaltwin.type.DigitalTwinTypeService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/digitaltwindisplay")
public class DigitalTwinDisplayController {

	final static String LOG_COLLECTION = "TwinLogs";
	final static String PROPERTIES_COLLECTION = "TwinProperties";
	final static String EVENTS_COLLECTION = "TwinEvents";

	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Autowired
	MongoBasicOpsDBRepository mongoRepo;

	@Autowired
	private DigitalTwinTypeService typeService;

	@Autowired
	private DigitalTwinDeviceService deviceService;

	@Autowired
	private AppWebUtils utils;

	ObjectMapper mapper = new ObjectMapper();

	@GetMapping("show")
	public String show(Model model) {
		List<DigitalTwinType> types = this.typeService.getDigitalTwinTypesByUserId(utils.getUserId());
		model.addAttribute("types", types);
		return "digitaltwindisplay/show";
	}

	@GetMapping(value = "/getDevices/{typeId}")
	public @ResponseBody List<String> getDevices(Model model, @PathVariable("typeId") String typeId) {
		try {
			List<String> devices = this.deviceService.getDigitalTwinDevicesByTypeId(typeId);
			return devices;
		} catch (Exception e) {
			log.error("Error getting devices of Digital Twin type: " + typeId);
			return null;
		}
	}

	@PostMapping("executeQuery")
	public String executeQuery(Model model, @RequestParam String type, @RequestParam String device,
			@RequestParam String offset, @RequestParam String operation, @RequestParam String eventName) {
		try {
			List<String> results = new ArrayList<String>();
			List<String> devices = new ArrayList<String>();
			List<String> types = new ArrayList<String>();

			String queryResult = null;
			if (type.equalsIgnoreCase("all")) {
				devices = this.deviceService.getDigitalTwinDevicesIdsByUser(utils.getUserId());
				List<DigitalTwinType> lTypes = this.typeService.getDigitalTwinTypesByUserId(utils.getUserId());
				for (DigitalTwinType t : lTypes) {
					types.add(t.getName());
				}
			} else if (device.equalsIgnoreCase("all")) {
				devices = this.deviceService.getDigitalTwinDevicesIdsByUserAndTypeId(utils.getUserId(), type);
				types.add(type);
			} else {
				devices.add(this.deviceService.getDigitalTwinDevicebyName(device).getId());
				types.add(type);
			}

			for (String d : devices) {
				for (String t : types) {
					String collection = null;
					if (operation.equalsIgnoreCase(DigitalTwinModel.EventType.SHADOW.name())) {
						collection = PROPERTIES_COLLECTION + t.substring(0, 1).toUpperCase() + t.substring(1);
					} else if (operation.equalsIgnoreCase(DigitalTwinModel.EventType.LOG.name())) {
						collection = LOG_COLLECTION;
					} else if (operation.equalsIgnoreCase(DigitalTwinModel.EventType.NOTEBOOK.name())
							|| operation.equalsIgnoreCase(DigitalTwinModel.EventType.PIPELINE.name())
							|| operation.equalsIgnoreCase(DigitalTwinModel.EventType.FLOW.name())
							|| operation.equalsIgnoreCase(DigitalTwinModel.EventType.RULE.name())
							|| operation.equalsIgnoreCase(DigitalTwinModel.EventType.CUSTOM.name())) {

						collection = EVENTS_COLLECTION;
					}
					if (operation.equalsIgnoreCase(DigitalTwinModel.EventType.CUSTOM.name()) && eventName != "") {
						queryResult = mongoRepo.queryNativeAsJson(collection,
								"db." + collection + ".find({deviceId:'" + d + "',event:'" + operation.toUpperCase()
										+ "',eventName:'" + eventName + "'}).sort({timestamp: -1}).limit("
										+ Integer.parseInt(offset) + ")");
					} else if (collection.equalsIgnoreCase(EVENTS_COLLECTION)) {
						queryResult = mongoRepo.queryNativeAsJson(collection,
								"db." + collection + ".find({deviceId:'" + d + "',event:'" + operation.toUpperCase()
										+ "'}).sort({timestamp: -1}).limit(" + Integer.parseInt(offset) + ")");
					} else {
						queryResult = mongoRepo.queryNativeAsJson(collection, "db." + collection + ".find({deviceId:'"
								+ d + "'}).sort({timestamp: -1}).limit(" + Integer.parseInt(offset) + ")");
					}

					List<String> lResults = mapper.readValue(queryResult, List.class);
					lResults = processData(lResults);
					for (String r : lResults) {
						results.add(r);
					}
				}
			}
			model.addAttribute("queryResult", results);
			return "digitaltwindisplay/show :: query";
		} catch (Exception e) {
			log.error("Error getting shadow devices");
			model.addAttribute("queryResult",
					utils.getMessage("querytool.query.native.error", "Error malformed query"));
			return null;
		}
	}

	private List<String> processData(List<String> queryResult) {
		List<String> results = new ArrayList<String>();
		try {

			for (String result : queryResult) {
				JSONObject json = new JSONObject(result);
				Long timeInMillisecons = json.getJSONObject("timestamp").getLong("$date");

				Date date = new Date(timeInMillisecons);
				String dateFormatted = formatter.format(date);

				JSONObject timestamp = new JSONObject();
				timestamp.put("$date", dateFormatted);
				json.put("timestamp", timestamp);

				results.add(json.toString());
			}
		} catch (JSONException e) {
			log.error("Error parsing query Result. {}", e);
		}
		return results;
	}
}
