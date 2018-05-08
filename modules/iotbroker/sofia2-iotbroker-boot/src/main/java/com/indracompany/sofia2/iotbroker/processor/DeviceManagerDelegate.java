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
package com.indracompany.sofia2.iotbroker.processor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.Device;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.device.DeviceService;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLogMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@Component
public class DeviceManagerDelegate implements DeviceManager {

	@Autowired
	ClientPlatformService clientPlatformService;
	@Autowired
	DeviceService deviceService;
	// ExecutorService executor = Executors.newFixedThreadPool(10);

	// TODO: Make async event processing
	@Override
	public <T extends SSAPBodyMessage> boolean registerActivity(SSAPMessage<T> request,
			SSAPMessage<SSAPBodyReturnMessage> response, IoTSession session, GatewayInfo info) {

		final List<Device> devices = deviceService.getByClientPlatformIdAndIdentification(
				this.clientPlatformService.getByIdentification(session.getClientPlatform()),
				session.getClientPlatformInstance());
		Device device = null;

		if (devices.size() > 0) {
			device = devices.get(0);
		} else {
			device = new Device();
			device.setClientPlatform(this.clientPlatformService.getByIdentification(session.getClientPlatform()));
			device.setIdentification(session.getClientPlatformInstance());
			device.setProtocol(info.getProtocol());
			if (request.getMessageType().equals(SSAPMessageTypes.JOIN)) {
				SSAPBodyJoinMessage body = (SSAPBodyJoinMessage) request.getBody();
				device.setJsonActions(
						body.getDeviceConfiguration() != null ? body.getDeviceConfiguration().toString() : null);
			}

		}

		switch (request.getMessageType()) {
		case JOIN:
			touchDevice(device, session, true, info, null);
			break;
		case LEAVE:
			touchDevice(device, session, false, info, null);
			break;
		case LOG:
			SSAPBodyLogMessage logMessage = (SSAPBodyLogMessage) request.getBody();
			touchDevice(device, session, true, info, logMessage.getStatus().name());
		default:
			touchDevice(device, session, true, info, null);
			break;
		}

		return true;
	}

	@Scheduled(fixedDelay = 60000)
	public void updatingDevicesPeriodic() {
		updatingDevices();
	}

	@PostConstruct
	public void updatingDevicesAtStartUp() {
		updatingDevices();
	}

	private void updatingDevices() {
		log.info("Start Updating all devices");
		final Calendar c = Calendar.getInstance();
		long millis = c.getTimeInMillis() - 5 * 60 * 1000l;
		c.setTimeInMillis(millis);

		// Setting connected false when 5 minutes without activity
		int n = deviceService.updateDeviceStatusAndDisableWhenUpdatedAtLessThanDate(false, false, c.getTime());
		log.info("End Updating all devices:" + n + " disconected");

		// Setting disabled a true when 1 day witout activity
		millis = c.getTimeInMillis() - 24 * 60 * 60 * 1000l;
		c.setTimeInMillis(millis);
		n = deviceService.updateDeviceStatusAndDisableWhenUpdatedAtLessThanDate(false, true, c.getTime());
		log.info("End Updating all devices:" + n + " disabled");

	}

	private void touchDevice(Device device, IoTSession session, boolean connected, GatewayInfo info, String status) {
		log.info("Start Updating device " + device.getIdentification());
		device.setStatus(status == null ? Device.StatusType.OK.name() : status);
		device.setClientPlatform(this.clientPlatformService.getByIdentification(session.getClientPlatform()));
		device.setIdentification(session.getClientPlatformInstance());
		device.setSessionKey(session.getSessionKey());
		device.setConnected(connected);
		device.setDisabled(false);
		device.setProtocol(info.getProtocol());
		device.setUpdatedAt(new Date());
		deviceService.updateDevice(device);
		log.info("End Updating device " + device.getIdentification());
	}

}
