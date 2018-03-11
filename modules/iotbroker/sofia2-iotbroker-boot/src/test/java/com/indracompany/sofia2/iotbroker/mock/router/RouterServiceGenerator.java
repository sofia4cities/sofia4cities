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

public class RouterServiceGenerator {


	public static NotificationCompositeModel generateNotificationCompositeModel(String subscriptionId, Person subject, IoTSession session) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		final NotificationCompositeModel model = new NotificationCompositeModel();
		model.setNotificationModel(new NotificationModel());
		model.getNotificationModel().setOperationModel(new OperationModel());
		model.getNotificationModel().getOperationModel().setBody(mapper.writeValueAsString(subject));
		model.getNotificationModel().getOperationModel().setClientPlatformId(session.getClientPlatformID());
		model.getNotificationModel().getOperationModel().setObjectId(UUID.randomUUID().toString());
		model.getNotificationModel().getOperationModel().setOntologyName(Person.class.getSimpleName());
		model.getNotificationModel().getOperationModel().setQueryType(OperationModel.QueryType.NATIVE);
		model.getNotificationModel().getOperationModel().setUser(session.getUserID());
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
