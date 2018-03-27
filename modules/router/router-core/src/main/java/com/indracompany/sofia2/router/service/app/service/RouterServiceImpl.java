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
package com.indracompany.sofia2.router.service.app.service;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.SuscriptionNotificationsModel;
import com.indracompany.sofia2.config.model.SuscriptionNotificationsModel.OperationType;
import com.indracompany.sofia2.config.model.SuscriptionNotificationsModel.QueryType;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.SuscriptionModelRepository;
import com.indracompany.sofia2.config.services.ontologydata.OntologyDataService;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel;

@Service("routerServiceImpl")
public class RouterServiceImpl implements RouterService, RouterSuscriptionService {

	@Autowired
	CamelContext camelContext;
	
	@Autowired
	SuscriptionModelRepository repository;
	
	//TODO the router will use that repository to obtain ontology information for validations.
	//TODO this access is not based on any user authorization so it will use the repository instead the service.
	//TODO if this assumption is not correct, it should be fixed here. If correct, it is neccesary to remove the TODOs
	@Autowired
	OntologyRepository ontologyRepository;
	
	@Autowired
	OntologyDataService ontologyDataService;
	
	private String defaultStartupRoute = "direct:start-broker-flow";

	@Override
	public OperationResultModel insert(NotificationModel model) throws Exception {
		String data = model.getOperationModel().getBody();
		String ontologyName = model.getOperationModel().getOntologyName();
		Ontology ontology = ontologyRepository.findByIdentification(ontologyName);
		boolean dataValidated = ontologyDataService.hasOntologySchemaCompliance(data, ontology);
		if (dataValidated) {
			ProducerTemplate t = camelContext.createProducerTemplate();
			OperationResultModel result = (OperationResultModel)t.requestBody(defaultStartupRoute, model);
			return result;
		} else {
			OperationResultModel errorResult = new OperationResultModel();
			errorResult.setResult("ERROR");
			errorResult.setStatus(false);
			errorResult.setMessage("Body is not copliant with the ontology " + ontologyName + " schema, data: "+data);
			return errorResult;
		}
	}

	@Override
	public OperationResultModel update(NotificationModel model) throws Exception {
		ProducerTemplate t = camelContext.createProducerTemplate();
		OperationResultModel result = (OperationResultModel)t.requestBody(defaultStartupRoute, model);
		return result;
	}

	@Override
	public OperationResultModel delete(NotificationModel model) throws Exception {
		ProducerTemplate t = camelContext.createProducerTemplate();
		OperationResultModel result = (OperationResultModel)t.requestBody(defaultStartupRoute, model);
		return result;
	}

	@Override
	public OperationResultModel query(NotificationModel model) throws Exception {
		ProducerTemplate t = camelContext.createProducerTemplate();
		OperationResultModel result = (OperationResultModel)t.requestBody(defaultStartupRoute, model);
		return result;
	}

	@Override
	public OperationResultModel suscribe(SuscriptionModel model) throws Exception {
		

		SuscriptionNotificationsModel m = new SuscriptionNotificationsModel();
		m.setOntologyName(model.getOntologyName());
		m.setOperationType(OperationType.valueOf(model.getOperationType().name()));
		m.setQuery(model.getQuery());
		m.setQueryType(QueryType.valueOf(model.getQueryType().name()));
		m.setSessionKey(model.getSessionKey());
		m.setSuscriptionId(model.getSuscriptionId());
		m.setUser(model.getUser());
		
		SuscriptionNotificationsModel saved = repository.save(m);
		
		OperationResultModel result = new OperationResultModel();
		result.setErrorCode("");
		result.setOperation("SUSCRIBE");
		result.setResult(saved.getId());
		result.setMessage("Suscription to "+saved.getOntologyName()+" has "+repository.findAllByOntologyName(model.getOntologyName()).size());
		return result;
	}

	@Override
	public OperationResultModel unSuscribe(SuscriptionModel model) throws Exception {
		
		repository.deleteBySuscriptionId(model.getSuscriptionId());
		
		OperationResultModel result = new OperationResultModel();
		result.setErrorCode("");
		result.setOperation("UNSUSCRIBE");
		result.setResult("OK");
		result.setMessage("Suscription to "+model.getOntologyName()+" has "+repository.findAllByOntologyName(model.getOntologyName()).size());
		return result;
	}

}
