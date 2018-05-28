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
package com.indracompany.sofia2.config.services.digitaltwin.device;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.model.DigitalTwinType;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DigitalTwinDeviceRepository;
import com.indracompany.sofia2.config.repository.DigitalTwinTypeRepository;
import com.indracompany.sofia2.config.services.exceptions.DigitalTwinServiceException;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DigitalTwinDeviceServiceImpl implements DigitalTwinDeviceService {

	@Autowired
	private DigitalTwinDeviceRepository digitalTwinDeviceRepo;

	@Autowired
	private DigitalTwinTypeRepository digitalTwinTypeRepo;

	@Autowired
	private UserService userService;

	@Override
	public List<String> getAllIdentifications() {
		List<DigitalTwinDevice> digitalDevices = this.digitalTwinDeviceRepo.findAllByOrderByIdentificationAsc();
		List<String> identifications = new ArrayList<String>();
		for (DigitalTwinDevice device : digitalDevices) {
			identifications.add(device.getIdentification());
		}
		return identifications;
	}

	@Override
	public List<DigitalTwinDevice> getAll() {
		return this.digitalTwinDeviceRepo.findAll();
	}

	@Override
	public List<String> getAllDigitalTwinTypeNames() {
		return this.digitalTwinTypeRepo.findAllNames();
	}

	@Override
	public String generateToken() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	@Override
	public String getLogicFromType(String type) {
		DigitalTwinType digitalTwinType = digitalTwinTypeRepo.findByName(type);
		if (digitalTwinType != null) {
			String logic = digitalTwinType.getLogic();
			if (logic != null) {
				return logic;
			} else {
				log.error("Error, logic not found for Digital Twin Type: " + type);
				return null;
			}
		} else {
			log.error("Error, Digital Twin Type not found: " + type);
			return null;
		}

	}

	@Override
	public void createDigitalTwinDevice(DigitalTwinDevice digitalTwinDevice, HttpServletRequest httpServletRequest) {
		try {
			String type = httpServletRequest.getParameter("typeSelected").trim();
			if (type != null && type != "") {
				DigitalTwinType digitalTwinType = this.digitalTwinTypeRepo.findByName(type);
				if (digitalTwinType == null) {
					log.error("Digital Twin Type : " + type + "doesn't exist.");
					return;
				}
				digitalTwinDevice.setIp("");
				User user = userService.getUser(digitalTwinDevice.getUser().getUserId());
				if (user != null) {
					digitalTwinDevice.setUser(user);
					digitalTwinDevice.setTypeId(digitalTwinType);
					this.digitalTwinDeviceRepo.save(digitalTwinDevice);
				} else {
					log.error("Invalid user");
					return;
				}
			} else {
				log.error("Invalid Digital Twin Type.");
				return;
			}
		} catch (Exception e) {
			throw new DigitalTwinServiceException("Problems creating the digital twin device", e);
		}
	}

	@Override
	public void getDigitalTwinToUpdate(Model model, String id) {
		DigitalTwinDevice digitalTwinDevice = digitalTwinDeviceRepo.findById(id);
		if (digitalTwinDevice != null) {
			model.addAttribute("digitaltwindevice", digitalTwinDevice);
			model.addAttribute("typeDigital", digitalTwinDevice.getTypeId().getName());
		} else {
			log.error("DigitalTwinDevice with id:" + id + ", not found.");
		}
	}

	@Override
	public DigitalTwinDevice getDigitalTwinDeviceById(String id) {
		return digitalTwinDeviceRepo.findById(id);
	}

	@Override
	public void updateDigitalTwinDevice(DigitalTwinDevice digitalTwinDevice, HttpServletRequest httpServletRequest) {
		// Update DigitalTwinDevice
		DigitalTwinDevice digitalTwinDeviceDb = this.digitalTwinDeviceRepo.findById(digitalTwinDevice.getId());

		this.digitalTwinDeviceRepo.delete(digitalTwinDeviceDb);
		this.createDigitalTwinDevice(digitalTwinDevice, httpServletRequest);
	}

	@Override
	public void deleteDigitalTwinDevice(DigitalTwinDevice digitalTwinDevice) {
		this.digitalTwinDeviceRepo.delete(digitalTwinDevice);
	}

	@Override
	public List<String> getDigitalTwinDevicesByTypeId(String typeId) {
		return this.digitalTwinDeviceRepo.findNamesByTypeId(this.digitalTwinTypeRepo.findByName(typeId));

	}

	@Override
	public List<String> getDigitalTwinDevicesIdsByUser(String userId) {
		User user = this.userService.getUser(userId);
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return this.digitalTwinDeviceRepo.findAllIds();
		} else {
			return this.digitalTwinDeviceRepo.findIdsByUser(this.userService.getUser(userId));
		}

	}

	@Override
	public List<String> getDigitalTwinDevicesIdsByUserAndTypeId(String userId, String typeId) {
		User user = this.userService.getUser(userId);
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return this.digitalTwinDeviceRepo.findIdsByTypeId(this.digitalTwinTypeRepo.findByName(typeId));
		} else {
			return this.digitalTwinDeviceRepo.findIdsByUserAndTypeId(user, this.digitalTwinTypeRepo.findByName(typeId));
		}
	}

	@Override
	public DigitalTwinDevice getDigitalTwinDevicebyName(String name) {
		return this.digitalTwinDeviceRepo.findByIdentification(name);
	}

	@Override
	public Integer getNumOfDevicesByTypeId(String type) {
		DigitalTwinType digitalTwinType = this.digitalTwinTypeRepo.findByName(type);
		if (digitalTwinType != null) {
			return this.digitalTwinDeviceRepo.findByTypeId(digitalTwinType).size();
		} else {
			return 0;
		}
	}

	@Override
	public List<DigitalTwinDevice> getAllByUserId(String userId) {
		User user = this.userService.getUser(userId);
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return this.digitalTwinDeviceRepo.findAll();
		} else {
			return this.digitalTwinDeviceRepo.findByUser(user);
		}

	}

}
