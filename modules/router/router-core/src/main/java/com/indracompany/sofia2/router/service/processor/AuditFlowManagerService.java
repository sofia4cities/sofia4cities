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

import com.hazelcast.collection.impl.common.DataAwareItemEvent;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.config.services.utils.ServiceUtils;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;
import com.indracompany.sofia2.router.service.app.service.RouterCrudServiceException;
import com.indracompany.sofia2.router.service.processor.bean.AuditParameters;

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

		String item = (String) event.getItem();
		JSONObject jsonObj = new JSONObject(item);

		AuditParameters commonParams = getAuditParameters(jsonObj);

		OperationResultModel result = null;

		if (!ANONYMOUS_USER.equals(commonParams.getUser()) && commonParams.getUser() != null) {

			String ontology = ServiceUtils.getAuditCollectionName(commonParams.getUser());
			OperationModel.Source operation = null;

			if (EventType.valueOf(commonParams.getEventType()).equals(EventType.SECURITY)) {
				operation = OperationModel.Source.AUDIT;
			} else if (EventType.valueOf(commonParams.getEventType()).equals(EventType.IOTBROKER)) {
				operation = OperationModel.Source.IOTBROKER;
			} else {
				operation = OperationModel.Source.AUDIT;
			}

			OperationModel model = OperationModel
					.builder(ontology, OperationType.INSERT, commonParams.getUser(), operation).body(item)
					.queryType(QueryType.NONE).cacheable(false).build();

			result = routerCrudService.insert(model);

		}

		exchange.getIn().setBody(result);
		log.debug("executeAuditOperations: End");
	}

	private AuditParameters getAuditParameters(JSONObject jsonObj) throws JSONException {

		String user = !(jsonObj.isNull(USER_KEY)) ? jsonObj.getString(USER_KEY) : null;
		String eventType = jsonObj.getString(EVENT_TYPE_KEY);
		String operationType = jsonObj.getString(OPERATION_TYPE_KEY);
		return new AuditParameters(user, eventType, operationType);
	}

}
