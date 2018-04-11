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
package com.indracompany.sofia2.iotbroker.mock.router;

import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.Source;

public class RouterServiceGenerator {


	public static NotificationCompositeModel generateNotificationCompositeModel(String subscriptionId, Person subject, IoTSession session) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		final NotificationCompositeModel model = new NotificationCompositeModel();
		model.setNotificationModel(new NotificationModel());
		model.getNotificationModel().setOperationModel(OperationModel.builder(
				Person.class.getSimpleName(), 
				OperationModel.OperationType.QUERY, 
				session.getUserID(), 
				Source.IOTBROKER)
				.body(mapper.writeValueAsString(subject))
				.clientPlatformId(mapper.writeValueAsString(subject))
				.queryType(QueryType.NATIVE)
				.objectId(UUID.randomUUID().toString())
				.build()
				);

		model.setNotificationEntityId(subscriptionId);
		model.setOperationResultModel(new OperationResultModel());
		model.getOperationResultModel().setErrorCode("");
		model.getOperationResultModel().setMessage("OK");
		model.getOperationResultModel().setOperation("QUERY");
		final ArrayList<Person> persons = new ArrayList<>();
		persons.add(subject);
		model.getOperationResultModel().setResult(mapper.writeValueAsString(persons));
		model.getOperationResultModel().setStatus(false);
		model.setNotificationEntityId(subscriptionId);

		return model;

	}


}
