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
package com.indracompany.sofia2.controlpanel.controller.devicemanagement;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Device;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DeviceRepository;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@Component
public class GraphDeviceUtil {

	private String urlImages;
	private String genericUserName = "USER";

	@Autowired
	private ClientPlatformRepository clientPlatformRepository;

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

		this.urlImages = this.url + "/controlpanel/static/images/";
	}

	public List<GraphDeviceDTO> constructGraphWithClientPlatformsForUser() {

		List<GraphDeviceDTO> arrayLinks = new LinkedList<GraphDeviceDTO>();
		String name = utils.getMessage("name.clients", "PLATFORM CLIENTS");

		arrayLinks.add(new GraphDeviceDTO(genericUserName, name, null, null, genericUserName, name, utils.getUserId(),
				name, "suit", this.urlImages + IMAGE_CLIENT_PLATFORMS, null, null, null, null));

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

			arrayLinks.add(new GraphDeviceDTO(name, clientPlatform.getId(), null, null, name, "clientplatform", name,
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

					arrayLinks.add(new GraphDeviceDTO(clientPlatform.getId(), device.getId(), device.getDescription(),
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
