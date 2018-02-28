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
package com.indracompany.sofia2.router.service.processor;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.services.flownode.FlowNodeService;
import com.indracompany.sofia2.router.client.RouterClientGateway;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;

@Service("routerFlowManagerService")
public class RouterFlowManagerService {
	
	@Autowired
	private RouterCrudService routerCrudService;
	
	@Autowired
	RouterClientGateway<NotificationCompositeModel,OperationResultModel> adviceGateway;
	
	@Autowired
	FlowNodeService flowNodeService;
	
	public NotificationCompositeModel startBrokerFlow(NotificationModel model, Exchange exchange) {
		return new NotificationCompositeModel();
	}
	
	public NotificationCompositeModel executeCrudOperations(NotificationCompositeModel model, Exchange exchange) {
		return new NotificationCompositeModel();
	}
	
	public NotificationCompositeModel getScriptsAndNodereds(NotificationCompositeModel model, Exchange exchange) {
		return new NotificationCompositeModel();
	}
	
	public OperationResultModel adviceScriptsAndNodereds(NotificationCompositeModel model, Exchange exchange) {
		return new OperationResultModel();
	}
	
	

}
