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
package com.indracompany.sofia2.streaming.twitter.persistence;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.indracompany.sofia2.persistence.ContextData;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersistenceServiceImpl implements PeristenceService {

	@Autowired
	private RouterService routerService;

	@Override
	public void insertOntologyInstance(String instance, String ontology, String user, String clientPlatform,
			String clientPlatformInstance) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(instance);

		final ContextData contextData = new ContextData();

		contextData.setClientConnection("");
		contextData.setClientPatform(clientPlatform);
		contextData.setClientPatformInstance(clientPlatformInstance);
		contextData.setTimezoneId(ZoneId.systemDefault().toString());
		contextData.setUser(user);
		((ObjectNode) json).set("contextData", mapper.valueToTree(contextData));

		final OperationModel model = new OperationModel();
		model.setBody(json.toString());
		model.setOntologyName(ontology);
		model.setUser(user);
		model.setOperationType(OperationType.POST);
		model.setQueryType(QueryType.NATIVE);

		final NotificationModel modelNotification = new NotificationModel();
		modelNotification.setOperationModel(model);

		final OperationResultModel response = routerService.insert(modelNotification);
		log.debug("Response from router: " + response);
	}

}
