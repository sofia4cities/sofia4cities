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
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.resources.service.IntegrationResourcesService;
import com.indracompany.sofia2.router.service.app.model.AdviceNotificationModel;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel;
import com.indracompany.sofia2.router.service.app.service.AdviceNotificationService;
import com.indracompany.sofia2.router.service.app.service.suscription.SuscriptionRepository;

@Service
public class IotBrokerAdviceNotificationService implements AdviceNotificationService {

	@Autowired
	SuscriptionRepository<String,SuscriptionModel> suscriptionRepository;
	
	@Autowired
	IntegrationResourcesService resourcesService;
	
	private static String ADVICE_URL="iot-broker.advice.url";
	
	@Override
	public List<AdviceNotificationModel> getAdviceNotificationModel(String ontologyName, String messageType) {
		
		Collection<SuscriptionModel>  listNotifications=null;
		List<AdviceNotificationModel> model =null;
		String baseUrl = resourcesService.getURL(ADVICE_URL);
		
		try {
			listNotifications = suscriptionRepository.findById(ontologyName);
		}catch (Exception e) {}
		
		if (listNotifications !=null)
		{
			model = new ArrayList<AdviceNotificationModel>();
			for (SuscriptionModel notificationEntity : listNotifications) {
				AdviceNotificationModel advice = new AdviceNotificationModel();
				advice.setEntityId(notificationEntity.getSuscriptionId());
				advice.setUrl(baseUrl);
				advice.setSuscriptionModel(notificationEntity);
				model.add(advice);
			}
		}
		return model;
	}
}
