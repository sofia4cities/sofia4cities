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
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;

@Service("routerServiceImpl")
public class RouterServiceImpl implements RouterService, RouterClient<NotificationModel,OperationResultModel >{

	@Value("${sofia2.flowengine.home.base:http://localhost:19100/router/router/}")
	private String routerStandaloneURL;

	@Override
	public OperationResultModel execute(NotificationModel input) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("admin", "admin"));
		
		OperationModel model = input.getOperationModel();
		String operation = model.getOperationType();
		
		OperationResultModel quote=new OperationResultModel();
		
		if (operation.equalsIgnoreCase("POST") || operation.equalsIgnoreCase(OperationModel.Operations.INSERT.name())) {
			quote = restTemplate.postForObject(routerStandaloneURL+"/insert",input, OperationResultModel.class);
		}
		else if  (operation.equalsIgnoreCase("PUT") || operation.equalsIgnoreCase(OperationModel.Operations.UPDATE.name())) {
			restTemplate.put(routerStandaloneURL+"/update", input);
		}
		
		else if  (operation.equalsIgnoreCase("DELETE") || operation.equalsIgnoreCase(OperationModel.Operations.DELETE.name())) {
			restTemplate.delete(routerStandaloneURL+"/delete",input);
		}
		
		else if  (operation.equalsIgnoreCase("GET") || operation.equalsIgnoreCase(OperationModel.Operations.QUERY.name())) {
			quote = restTemplate.postForObject(routerStandaloneURL+"/query",input, OperationResultModel.class);
		}
		
		//OperationResultModel quote = restTemplate.postForObject(routerStandaloneURL,input, OperationResultModel.class);
		System.out.println(quote.toString());
		return quote;
	}

	@Override
	public OperationResultModel insert(NotificationModel model) throws Exception {
		return execute(model);
	}

	@Override
	public OperationResultModel update(NotificationModel model) throws Exception {
		return execute(model);
	}

	@Override
	public OperationResultModel delete(NotificationModel model) throws Exception {
		return execute(model);
	}

	@Override
	public OperationResultModel query(NotificationModel model) throws Exception {
		return execute(model);
	}

	@Override
	public OperationResultModel subscribe(NotificationModel model) throws Exception {
		return execute(model);
	}

	public String getRouterStandaloneURL() {
		return routerStandaloneURL;
	}

	public void setRouterStandaloneURL(String routerStandaloneURL) {
		this.routerStandaloneURL = routerStandaloneURL;
	}

	

}
