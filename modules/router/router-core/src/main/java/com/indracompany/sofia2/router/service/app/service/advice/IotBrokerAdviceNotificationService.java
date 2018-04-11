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
package com.indracompany.sofia2.router.service.app.service.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.SuscriptionNotificationsModel;
import com.indracompany.sofia2.config.repository.SuscriptionModelRepository;
import com.indracompany.sofia2.resources.service.IntegrationResourcesService;
import com.indracompany.sofia2.resources.service.IntegrationResourcesServiceImpl.Module;
import com.indracompany.sofia2.resources.service.IntegrationResourcesServiceImpl.ServiceUrl;
import com.indracompany.sofia2.router.service.app.model.AdviceNotificationModel;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel.QueryType;
import com.indracompany.sofia2.router.service.app.service.AdviceNotificationService;

@Service
public class IotBrokerAdviceNotificationService implements AdviceNotificationService {

	@Autowired
	SuscriptionModelRepository repository;

	@Autowired
	IntegrationResourcesService resourcesService;
	
	
	@Override
	public List<AdviceNotificationModel> getAdviceNotificationModel(String ontologyName, String messageType) {
		
		List<SuscriptionNotificationsModel>  listNotifications=null;
		List<AdviceNotificationModel> model =null;
		String baseUrl = resourcesService.getUrl(Module.iotbroker, ServiceUrl.advice);
		
		
		try {
			listNotifications = repository.findAllByOntologyName(ontologyName);
		}catch (Exception e) {}
		
		if (listNotifications !=null)
		{
			model = new ArrayList<AdviceNotificationModel>();
			for (SuscriptionNotificationsModel notificationEntity : listNotifications) {
				AdviceNotificationModel advice = new AdviceNotificationModel();
				advice.setEntityId(notificationEntity.getSuscriptionId());
				advice.setUrl(baseUrl);
				
				SuscriptionModel sus = new SuscriptionModel();
				sus.setOntologyName(notificationEntity.getOntologyName());
				sus.setOperationType(OperationType.valueOf(notificationEntity.getOperationType().name()));
				sus.setQuery(notificationEntity.getQuery());
				sus.setQueryType(QueryType.valueOf(notificationEntity.getQueryType().name()));
				sus.setSessionKey(notificationEntity.getSessionKey());
				sus.setSuscriptionId(notificationEntity.getSuscriptionId());
				sus.setUser(notificationEntity.getUser());
				
				advice.setSuscriptionModel(sus);
				model.add(advice);
			}
		}
		return model;
	}
}
