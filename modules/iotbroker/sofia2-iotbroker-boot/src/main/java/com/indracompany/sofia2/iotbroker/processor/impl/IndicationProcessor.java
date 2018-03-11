package com.indracompany.sofia2.iotbroker.processor.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.SuscriptionNotificationsModel;
import com.indracompany.sofia2.config.repository.SuscriptionModelRepository;
import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping(path="/")
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
public class IndicationProcessor {

	@Autowired
	GatewayNotifier notifier;
	@Autowired
	SuscriptionModelRepository repositoy;
	@Autowired
	ObjectMapper mapper;

	@RequestMapping(value="/advice", method=RequestMethod.POST)
	public OperationResultModel create(@RequestBody NotificationCompositeModel notification) {
		final OperationResultModel model = new OperationResultModel();

		model.setErrorCode("");
		model.setMessage("");
		model.setOperation(notification.getOperationResultModel().getOperation());
		model.setResult(notification.getOperationResultModel().getResult());
		model.setStatus(false);

		final List<SuscriptionNotificationsModel> notifications = repositoy.findAllBySuscriptionId(notification.getNotificationEntityId());

		for(final SuscriptionNotificationsModel n : notifications) {
			try {

				final SSAPMessage<SSAPBodyIndicationMessage> indication = new SSAPMessage<>();
				indication.setDirection(SSAPMessageDirection.REQUEST);
				indication.setMessageType(SSAPMessageTypes.INDICATION);
				indication.setSessionKey(n.getSessionKey());
				indication.setBody(new SSAPBodyIndicationMessage());

				JsonNode data;
				//				final String body = notification.getNotificationModel().getOperationModel().getBody();
				final String body = notification.getOperationResultModel().getResult();
				if(StringUtils.isEmpty(body)) {
					createErrorResponse(notification, "Blank notification NOT PROCESSING");
				}

				data = mapper.readTree(body);
				indication.getBody().setData(data);

				indication.getBody().setOntology(notification.getNotificationModel().getOperationModel().getOntologyName());
				indication.getBody().setQuery("");
				indication.getBody().setSubsciptionId(notification.getNotificationEntityId());

				notifier.notify(indication);

			} catch (final IOException e) {
				// TODO LOG
				log.error("Indication result can't be process", e.getMessage());
				createErrorResponse(notification, e.getMessage());
				return model;
			}
		}

		return model;
	}

	private OperationResultModel createErrorResponse(NotificationCompositeModel notification, String message) {
		final OperationResultModel model = new OperationResultModel();
		model.setErrorCode("ERROR");
		model.setMessage(message);
		model.setOperation(notification.getOperationResultModel().getOperation());
		model.setResult(notification.getOperationResultModel().getResult());
		model.setStatus(false);
		return model;
	}

}
