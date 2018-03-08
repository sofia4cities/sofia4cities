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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.services.flownode.FlowNodeService;
import com.indracompany.sofia2.router.client.RouterClientGateway;
import com.indracompany.sofia2.router.service.ClientsConfigFactory;
import com.indracompany.sofia2.router.service.app.model.AdviceNotificationModel;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel;
import com.indracompany.sofia2.router.service.app.service.AdviceNotificationService;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;

import lombok.extern.slf4j.Slf4j;

@Service("routerFlowManagerService")
@Slf4j
public class RouterFlowManagerService {
	
	@Autowired
	private RouterCrudService routerCrudService;
	
	@Autowired
	private ClientsConfigFactory clientsFactory;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private CamelContext camelContext;
	
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
		String METHOD = model.getOperationType().name();
		
		OperationResultModel fallback = new OperationResultModel();
		fallback.setResult("NO_RESULT");
		fallback.setMessage("Operation Not Executed due to lack of OperationType");
		compositeModel.setOperationResultModel(fallback);
		
		try {
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.GET.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.QUERY.name())) {
				
				OperationResultModel result =routerCrudService.query(model);
				compositeModel.setOperationResultModel(result);
			}
			
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.POST.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.INSERT.name())) {
				OperationResultModel result =routerCrudService.insert(model);
				compositeModel.setOperationResultModel(result);
			}
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.PUT.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.UPDATE.name())) {
				OperationResultModel result =routerCrudService.update(model);
				compositeModel.setOperationResultModel(result);
			}
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.DELETE.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.DELETE.name())) {
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
		String messageType = model.getOperationType().name();
		
		List<AdviceNotificationModel> listNotifications =new ArrayList<AdviceNotificationModel>();
		
		Map<String, AdviceNotificationService>  map = applicationContext.getBeansOfType(AdviceNotificationService.class);
		
		Iterator<Entry<String, AdviceNotificationService>> iterator = map.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String, AdviceNotificationService> item = iterator.next();
			AdviceNotificationService service = item.getValue();
			List<AdviceNotificationModel> list = service.getAdviceNotificationModel(ontologyName, messageType);
			if (list!=null)
				listNotifications.addAll(list);
		}
		
		if (listNotifications!=null && listNotifications.size()>0)
			exchange.getIn().setHeader("endpoints", listNotifications);
		
		log.debug("getScriptsAndNodereds: End");
		
	}
	
	public OperationResultModel adviceScriptsAndNodereds(@Header(value = "theBody") Object header, Exchange exchange) {
		NotificationCompositeModel compositeModel = (NotificationCompositeModel) header;
		AdviceNotificationModel entity = (AdviceNotificationModel)exchange.getIn().getBody();
		
		compositeModel.setUrl(entity.getUrl());
		compositeModel.setNotificationEntityId(entity.getEntityId());
		
		SuscriptionModel model =entity.getSuscriptionModel();
		if (model!=null)
		{
			OperationModel operationModel = new OperationModel();
			operationModel.setBody(model.getQuery());
			operationModel.setOntologyName(model.getOntologyName());
			operationModel.setQueryType(QueryType.valueOf(model.getQueryType().name()));
			operationModel.setUser(model.getUser());
			operationModel.setOperationType(OperationType.QUERY);
			OperationResultModel result =null;
			try {
				result =routerCrudService.execute(operationModel);
			} catch (Exception e) {
			}
			
			if (result!=null) {
				compositeModel.setOperationResultModel(result);
			}
			
		}
				
		OperationResultModel fallback = new OperationResultModel();
		fallback.setResult("ERROR");
		fallback.setMessage("Operation Failed. Returned Default FallBack with :"+entity.getEntityId());
		
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
