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
package com.indracompany.sofia2.router.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.indracompany.sofia2.router.client.RouterClient;
import com.indracompany.sofia2.router.client.RouterClientGateway;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.advice.AdviceServiceImpl;

@Configuration
public class ClientsConfig {
	
	@Autowired
	private AdviceServiceImpl adviceServiceImpl;
	
	
	@Bean
	RouterClientGateway<NotificationCompositeModel,OperationResultModel> adviceGateway(AdviceServiceImpl adviceServiceImpl) {

		
		RouterClient<NotificationCompositeModel,OperationResultModel> routerClient= (RouterClient<NotificationCompositeModel,OperationResultModel>)adviceServiceImpl;
		RouterClientGateway<NotificationCompositeModel,OperationResultModel> gateway = 	new RouterClientGateway<NotificationCompositeModel,OperationResultModel>
				(RouterClientGateway.setupDefault("AdviceService","AdviceServiceGroup"),routerClient);
		
		//gateway.setFallback(fallback);
		
		return gateway;

	}


}
