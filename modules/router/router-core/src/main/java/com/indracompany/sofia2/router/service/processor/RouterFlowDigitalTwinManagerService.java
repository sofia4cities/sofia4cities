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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.router.service.app.model.DigitalTwinCompositeModel;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.digitaltwin.RouterDigitalTwinOpsServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Service("routerFlowDigitalTwinManagerService")
@Slf4j
public class RouterFlowDigitalTwinManagerService {

	@Autowired
	private RouterDigitalTwinOpsServiceImpl routerDigitalTwinOpsServiceImpl;

	@Autowired
	private CamelContext camelContext;

	private String executeDigitalTwinOpsRoute = "direct:execute-digitaltwin-operations";

	public OperationResultModel startDigitalTwinBrokerFlow(DigitalTwinCompositeModel compositeModel,
			Exchange exchange) {
		log.debug("startBrokerFlow: Notification Model arrived");

		ProducerTemplate t = camelContext.createProducerTemplate();
		DigitalTwinCompositeModel result = (DigitalTwinCompositeModel) t.requestBody(executeDigitalTwinOpsRoute,
				compositeModel);
		return result.getOperationResultModel();

	}

	public void executeDigitalTwinOperations(Exchange exchange) {
		log.debug("executeDigitalTwinOperations: Begin");

		DigitalTwinCompositeModel compositeModel = (DigitalTwinCompositeModel) exchange.getIn().getBody();
		DigitalTwinModel model = compositeModel.getDigitalTwinModel();

		String event = model.getEvent() != null ? model.getEvent().name() : null;
		String action = model.getActionName();

		OperationResultModel fallback = new OperationResultModel();
		fallback.setResult("NO_RESULT");
		fallback.setStatus(false);
		fallback.setMessage("Operation Not Executed due to lack of Event");
		compositeModel.setOperationResultModel(fallback);

		if (null != event && event.trim().length() > 0) {
			this.dispathEvent(exchange, compositeModel, event);
		} else if (null != action && action.trim().length() > 0) {
			this.dispathAction(exchange, compositeModel);
		}

		exchange.getIn().setBody(compositeModel);

		log.debug("executeDigitalTwinOperations: End");

	}

	private void dispathEvent(Exchange exchange, DigitalTwinCompositeModel compositeModel, String event) {
		try {
			if (event.equalsIgnoreCase(DigitalTwinModel.EventType.PING.name())
					|| event.equalsIgnoreCase(DigitalTwinModel.EventType.REGISTER.name())
					|| event.equalsIgnoreCase(DigitalTwinModel.EventType.RULE.name())
					|| event.equalsIgnoreCase(DigitalTwinModel.EventType.FLOW.name())
					|| event.equalsIgnoreCase(DigitalTwinModel.EventType.NOTEBOOK.name())
					|| event.equalsIgnoreCase(DigitalTwinModel.EventType.CUSTOM.name())) {
				OperationResultModel result = routerDigitalTwinOpsServiceImpl.insertEvent(compositeModel);
				compositeModel.setOperationResultModel(result);

			} else if (event.equalsIgnoreCase(DigitalTwinModel.EventType.LOG.name())) {
				OperationResultModel result = routerDigitalTwinOpsServiceImpl.insertLog(compositeModel);
				compositeModel.setOperationResultModel(result);

			} else if (event.equalsIgnoreCase(DigitalTwinModel.EventType.SHADOW.name())) {
				OperationResultModel result = routerDigitalTwinOpsServiceImpl.updateShadow(compositeModel);
				compositeModel.setOperationResultModel(result);

			}

		} catch (Exception e) {
			log.error("executeDigitalTwinOperations: Exception " + e.getMessage(), e);
		}

	}

	private void dispathAction(Exchange exchange, DigitalTwinCompositeModel compositeModel) {
		try {
			OperationResultModel result = routerDigitalTwinOpsServiceImpl.insertAction(compositeModel);
			compositeModel.setOperationResultModel(result);

		} catch (Exception e) {
			log.error("executeDigitalTwinOperations: Exception " + e.getMessage(), e);
		}

	}
}
