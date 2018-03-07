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
package com.indracompany.sofia2.router.service.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.indracompany.sofia2.router.client.RouterClient;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel;

@Service("routerSuscriptionServiceImpl")
public class RouterSuscriptionServiceImpl implements RouterSuscriptionService, RouterClient<SuscriptionModel,OperationResultModel >{

	@Value("${sofia2.flowengine.home.base:http://localhost:19100/router/router/}")
	private String routerStandaloneURL;

	
	public OperationResultModel execute(SuscriptionModel model) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("admin", "admin"));
		
		
		String operation = model.getOperationType().name();
		
		OperationResultModel quote=new OperationResultModel();
		
		if (operation.equalsIgnoreCase("SUSCRIBE") || operation.equalsIgnoreCase(SuscriptionModel.OperationType.SUSCRIBE.name())) {
			quote = restTemplate.postForObject(routerStandaloneURL+"/suscribe",model, OperationResultModel.class);
		}
		else if  (operation.equalsIgnoreCase("UNSUSCRIBE") || operation.equalsIgnoreCase(SuscriptionModel.OperationType.UNSUSCRIBE.name())) {
			quote = restTemplate.postForObject(routerStandaloneURL+"/unsuscribe",model, OperationResultModel.class);
		}
		
		System.out.println(quote.toString());
		return quote;
	}

	@Override
	public OperationResultModel suscribe(SuscriptionModel model) throws Exception {
		return execute(model);
	}

	@Override
	public OperationResultModel unSuscribe(SuscriptionModel model) throws Exception {
		return execute(model);
	}

	

	

}
