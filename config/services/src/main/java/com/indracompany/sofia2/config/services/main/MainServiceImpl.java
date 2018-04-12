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
package com.indracompany.sofia2.config.services.main;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.DeviceRepository;
import com.indracompany.sofia2.config.repository.GadgetRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.main.dto.KpisDTO;

@Service
public class MainServiceImpl implements MainService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OntologyRepository ontologyRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private DashboardRepository dashboardRepository;
	
	@Autowired
	private ApiRepository apiRepository;
	
	@Autowired
	private GadgetRepository gadgetRepository;
	
	
//	@Autowired
//	private UserService userService;

	@Override
	public ArrayList<KpisDTO> createKPIs() {
		
		KpisDTO kpisDTO = null;
		ArrayList<KpisDTO> kpisDTOList = new ArrayList<KpisDTO>();
		
		//KPI Users Number
		kpisDTO = new KpisDTO();
		long usersNumber = userRepository.count();
		kpisDTO.setValue(usersNumber);
		kpisDTO.setIdentification("Users");
		
		kpisDTOList.add(kpisDTO);

		//KPI Ontologies Number
		kpisDTO = new KpisDTO();
		long ontologyNumber = ontologyRepository.count();
		kpisDTO.setValue(ontologyNumber);
		kpisDTO.setIdentification("Ontologies");
		
		kpisDTOList.add(kpisDTO);
		
		//KPI Devices Number
		kpisDTO = new KpisDTO();
		long deviceNumber = deviceRepository.count();
		kpisDTO.setValue(deviceNumber);
		kpisDTO.setIdentification("Devices");
		
		kpisDTOList.add(kpisDTO);
		
		//KPI Dashboards Number
		kpisDTO = new KpisDTO();
		long dashboardNumber = dashboardRepository.count();
		kpisDTO.setValue(dashboardNumber);
		kpisDTO.setIdentification("Dashboards");
		
		kpisDTOList.add(kpisDTO);
		
		//KPI Apis Number
		kpisDTO = new KpisDTO();
		long apiNumber = apiRepository.count();
		kpisDTO.setValue(apiNumber);
		kpisDTO.setIdentification("Apis");
		
		kpisDTOList.add(kpisDTO);
		
		//KPI Gadgets Number
		kpisDTO = new KpisDTO();
		long gadgetNumber = gadgetRepository.count();
		kpisDTO.setValue(gadgetNumber);
		kpisDTO.setIdentification("Gadgets");
		
		kpisDTOList.add(kpisDTO);

		return kpisDTOList;
	}

}
