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
package com.indracompany.sofia2.service.dashboard;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.UserRepository;


@Service
public class DashboardServiceImpl implements DashboardService{


	@Autowired
	private DashboardRepository dashboardRepository;
	@Autowired
	private UserRepository userRepository;
	
	public static final String ADMINISTRATOR="ROLE_ADMINISTRATOR";
	
	@Override
	public List<Dashboard> findDashboardWithIdentificationAndDescription(String identification,String description, String userId)
	{
		List<Dashboard> dashboards;
		User user= this.userRepository.findByUserId(userId);
		
		if(user.getRoleTypeId().getName().equals(DashboardServiceImpl.ADMINISTRATOR))
		{
			if(description!=null && identification!=null){

				dashboards=this.dashboardRepository.findByIdentificationContainingAndDescriptionContaining(identification, description);

			}else if(description==null && identification!=null){

				dashboards=this.dashboardRepository.findByIdentificationContaining(identification);

			}else if(description!=null && identification==null){	

				dashboards=this.dashboardRepository.findByDescriptionContaining(description);

			}else{

				dashboards=this.dashboardRepository.findAll();
			}
		}else
		{
			if(description!=null && identification!=null){

				dashboards=this.dashboardRepository.findByUserAndIdentificationContainingAndDescriptionContaining(userId ,identification, description);

			}else if(description==null && identification!=null){

				dashboards=this.dashboardRepository.findByUserAndIdentificationContaining(userId,identification);

			}else if(description!=null && identification==null){	

				dashboards=this.dashboardRepository.findByUserAndDescriptionContaining(userId,description);

			}else{

				dashboards=this.dashboardRepository.findByUser(userId);
			}
		}
		return dashboards;
	}
	
	@Override
	public List<String> getAllIdentifications()
	{
		List<Dashboard> dashboards=this.dashboardRepository.findAllByOrderByIdentificationAsc();
		List<String> identifications=new ArrayList<String>();
		for(Dashboard dashboard:dashboards)
		{
			identifications.add(dashboard.getIdentification());
			
		}
		return identifications;
	}
	

	@Override
	public List<Dashboard> findAllDashboard() {
	 List <Dashboard> dashboard = this.dashboardRepository.findAll();
		return null;
	}

	@Override
	public Dashboard getDashboardById(String id) {
		return dashboardRepository.findById(id);
	}
}
