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
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.collection.impl.common.DataAwareItemEvent;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.config.services.user.UserServiceImpl;
import com.indracompany.sofia2.config.services.utils.ServiceUtils;
import com.indracompany.sofia2.router.service.ClientsConfigFactory;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;
import com.indracompany.sofia2.router.service.app.service.RouterCrudServiceException;
import com.indracompany.sofia2.router.service.processor.bean.AuditParameters;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Service("auditFlowManagerService")
@Slf4j
public class AuditFlowManagerService {
	
	@Autowired
	private RouterCrudService routerCrudService;
	
	private static final String USER_KEY = "user";	
	private static final String EVENT_TYPE_KEY = "type";
	private static final String OPERATION_TYPE_KEY = "operationType";
	private static final String ANONYMOUS_USER = "anonymousUser";
	
	public void audit(Exchange exchange) throws JSONException, RouterCrudServiceException {
		
		log.debug("executeAuditOperations: Begin");
		
		DataAwareItemEvent event = (DataAwareItemEvent) exchange.getIn().getBody();
		
		String item = (String)event.getItem();
		JSONObject jsonObj = new JSONObject(item);
		
		AuditParameters commonParams =  getAuditParameters (jsonObj);
		
		OperationResultModel result = null;
		
		if (EventType.valueOf(commonParams.getEventType()).equals(EventType.SECURITY)) {
			
			result = processSecurityEvent(commonParams, jsonObj, item);
			
		} else if (EventType.valueOf(commonParams.getEventType()).equals(EventType.IOTBROKER)) {
			
			result = processIotBrokerEvent(commonParams, jsonObj, item);
		}
	
		exchange.getIn().setBody(result);
		log.debug("executeAuditOperations: End");
	}
	
	private AuditParameters getAuditParameters (JSONObject jsonObj) throws JSONException {
		
		String user = jsonObj.getString(USER_KEY);
		String eventType = jsonObj.getString(EVENT_TYPE_KEY);
		String operationType = jsonObj.getString(OPERATION_TYPE_KEY);
		return new AuditParameters(user, eventType, operationType);
	}
	
	private OperationResultModel processSecurityEvent (AuditParameters params, JSONObject jsonObj, String item) throws RouterCrudServiceException {
		
		OperationResultModel result = null;
		
		if (!ANONYMOUS_USER.equals(params.getUser())) {
			
			String ontology = ServiceUtils.getAuditCollectionName(params.getUser());
			
			OperationModel model = OperationModel.builder(
					ontology, 
					OperationType.INSERT,
					params.getUser(), 
					OperationModel.Source.AUDIT)
					.body(item)
					.queryType(QueryType.NONE)
					.clientPlatformId("")
					.cacheable(false)
					.build();
			
			result = routerCrudService.insert(model);
			
		} 
		
		return result;
	}
	
	private OperationResultModel processIotBrokerEvent (AuditParameters params, JSONObject jsonObj, String item) 
																		throws RouterCrudServiceException, JSONException {
		
		OperationResultModel result = null;
		
		if ("JOIN".equals(params.getOperationType())) {
			
			String clientPlatformId = jsonObj.getString("clientPlatform");
			String clientPlatformInstance = jsonObj.getString("clientPlatformInstance");
			
			log.info(clientPlatformId +" -> " + clientPlatformInstance);
			
		} else {
		
			String ontology = ServiceUtils.getAuditCollectionName(params.getUser());
			
			OperationModel model = OperationModel.builder(
					ontology, 
					OperationType.INSERT,
					params.getUser(), 
					OperationModel.Source.IOTBROKER)
					.body(item)
					.queryType(QueryType.NONE)
					.cacheable(false)
					.build();
			
			result = routerCrudService.insert(model);
		} 
		
		return result;
	}

}
