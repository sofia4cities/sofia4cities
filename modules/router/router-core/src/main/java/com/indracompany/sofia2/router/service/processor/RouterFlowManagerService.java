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

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.NotificationEntity;
import com.indracompany.sofia2.config.services.flownode.FlowNodeService;
import com.indracompany.sofia2.router.client.RouterClientGateway;
import com.indracompany.sofia2.router.service.ClientsConfigFactory;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;

import lombok.extern.slf4j.Slf4j;

@Service("routerFlowManagerService")
@Slf4j
public class RouterFlowManagerService {
	
	@Autowired
	private RouterCrudService routerCrudService;
	
	@Autowired
	ClientsConfigFactory clientsFactory;
	
	@Autowired
	FlowNodeService flowNodeService;
	
	@Autowired
	@Qualifier("camel-context-reference")
	CamelContext camelContext;
	
	private String executeCrudOperationsRoute = "direct:execute-crud-operations";
	
	
	
	public OperationResultModel startBrokerFlow(NotificationModel model, Exchange exchange) {
		log.debug("startBrokerFlow: Notification Model arrived");
		NotificationCompositeModel compositeModel = new NotificationCompositeModel();
		compositeModel.setNotificationModel(model);
		
		if (checkModelIntegrity(compositeModel)) {
			ProducerTemplate t = camelContext.createProducerTemplate();
			NotificationCompositeModel result = (NotificationCompositeModel)t.requestBody(executeCrudOperationsRoute, compositeModel);
			return result.getOperationResultModel();
		}
		else {
			OperationResultModel output = new OperationResultModel();
			output.setResult("ERROR");
			output.setMessage("Input Model Integrity Check Failed");
			return output;
		}
		
		
	}
	
	public void executeCrudOperations(Exchange exchange) {
		log.debug("executeCrudOperations: Begin");
		
		NotificationCompositeModel compositeModel = (NotificationCompositeModel) exchange.getIn().getBody();
		OperationModel model = compositeModel.getNotificationModel().getOperationModel();
		String METHOD = model.getOperationType();
		
		OperationResultModel fallback = new OperationResultModel();
		fallback.setResult("NO_RESULT");
		fallback.setMessage("Operation Not Executed due to lack of OperationType");
		compositeModel.setOperationResultModel(fallback);
		
		try {
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.GET.name()) || METHOD.equalsIgnoreCase(OperationModel.Operations.QUERY.name())) {
				
				OperationResultModel result =routerCrudService.query(model);
				compositeModel.setOperationResultModel(result);
			}
			
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.POST.name()) || METHOD.equalsIgnoreCase(OperationModel.Operations.INSERT.name())) {
				OperationResultModel result =routerCrudService.insert(model);
				compositeModel.setOperationResultModel(result);
			}
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.PUT.name()) || METHOD.equalsIgnoreCase(OperationModel.Operations.UPDATE.name())) {
				OperationResultModel result =routerCrudService.update(model);
				compositeModel.setOperationResultModel(result);
			}
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.DELETE.name()) || METHOD.equalsIgnoreCase(OperationModel.Operations.DELETE.name())) {
				OperationResultModel result =routerCrudService.delete(model);
				compositeModel.setOperationResultModel(result);
			}
		} catch (Exception e) {
			
		}
		
		exchange.getIn().setBody(compositeModel);
		
		log.debug("executeCrudOperations: End");
	}
	
	public void getScriptsAndNodereds(Exchange exchange) {
		log.debug("getScriptsAndNodereds: Begin");
		NotificationCompositeModel compositeModel = (NotificationCompositeModel) exchange.getIn().getBody();
		OperationModel model = compositeModel.getNotificationModel().getOperationModel();
		
		String ontologyName = model.getOntologyName();
		String messageType = model.getOperationType();
		
		List<NotificationEntity>  listNotifications=null;
		try {
			listNotifications = flowNodeService.getNotificationsByOntologyAndMessageType(ontologyName, messageType);
		}catch (Exception e) {}
		
		/*List<NotificationEntity>  listNotifications2 = new ArrayList<NotificationEntity>();
		FlowNode arg0 = new FlowNode();
		arg0.setNodeRedNodeId("pepe");
		arg0.setPartialUrl("pepeurl");
		arg0.setId("fasafsdfasdfas");
		
		listNotifications2.add(arg0);
		
		arg0 = new FlowNode();
		arg0.setNodeRedNodeId("juan");
		arg0.setPartialUrl("juanurl");
		arg0.setId("fasafsdfasaasdassdfas");
		
		
		listNotifications2.add(arg0);*/
		
		if (listNotifications!=null && listNotifications.size()>0)
			exchange.getIn().setHeader("endpoints", listNotifications);
		
		log.debug("getScriptsAndNodereds: End");
		
	}
	
	public OperationResultModel adviceScriptsAndNodereds(@Header(value = "theBody") Object header, Exchange exchange) {
		NotificationCompositeModel compositeModel = (NotificationCompositeModel) header;
		NotificationEntity entity = (NotificationEntity)exchange.getIn().getBody();
		
		OperationResultModel fallback = new OperationResultModel();
		fallback.setResult("ERROR");
		fallback.setMessage("Operation Failed. Returned Default FallBack with :"+entity.getNotificationEntityId());
		
		RouterClientGateway<NotificationCompositeModel, OperationResultModel> adviceGateway =  clientsFactory.createAdviceGateway("advice", "adviceGroup");
		adviceGateway.setFallback(fallback);
		
		OperationResultModel ret = adviceGateway.execute(compositeModel);
		
		return ret;
		
		
	}
	
	
	
	public boolean checkModelIntegrity(NotificationCompositeModel model) {
		log.debug("checkModelIntegrity: Begin");
		return true;
	}
	
	
	
	

}
