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
package com.indracompany.sofia2.router.service.app.service.digitaltwin;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinCompositeModel;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterDigitalTwinService;
import com.indracompany.sofia2.router.service.app.service.digitaltwin.dto.LogDTO;
import com.indracompany.sofia2.router.service.app.service.digitaltwin.dto.ShadowDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RouterDigitalTwinOpsServiceImpl implements RouterDigitalTwinService {
	
	final static String LOG_COLLECTION = "TwinLogs";
	final static String PROPERTIES_COLLECTION = "TwinProperties";
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private MongoBasicOpsDBRepository mongoRepo;

	@Override
	public OperationResultModel insertLog(DigitalTwinCompositeModel compositeModel) {
		log.info("Router Digital Twin Service Operation "+compositeModel.getDigitalTwinModel().toString());
		OperationResultModel result = new OperationResultModel();
		LogDTO logInstance = new LogDTO();
		DigitalTwinModel model = compositeModel.getDigitalTwinModel();
		
		final String EVENT = model.getEvent().name();
		
		logInstance.setTrace(model.getLog());
		logInstance.setDeviceId(model.getId());
		logInstance.setType(model.getType());
		logInstance.setTimestamp(compositeModel.getTimestamp());
		
		String OUTPUT="";
		result.setMessage("OK");
		result.setStatus(true);

		try {
			
			OUTPUT = mongoRepo.insert(LOG_COLLECTION, mapper.writeValueAsString(logInstance));
			
		} catch (final Exception e) {
			result.setResult(OUTPUT);
			result.setStatus(false);
			result.setMessage(e.getMessage());
		}

		result.setResult(OUTPUT);
		result.setOperation(EVENT);
		return result;
	}

	@Override
	public OperationResultModel updateShadow(DigitalTwinCompositeModel compositeModel) {
		log.info("Router Digital Twin Service Operation "+compositeModel.getDigitalTwinModel().toString());
		OperationResultModel result = new OperationResultModel();
		ShadowDTO shadowInstance = new ShadowDTO();
		DigitalTwinModel model = compositeModel.getDigitalTwinModel();
		
		final String EVENT = model.getEvent().name();
		
		JSONObject status = new JSONObject(model.getStatus());
		shadowInstance.setStatus(status.toString());
		shadowInstance.setDaviceId(model.getId());
		shadowInstance.setType(model.getType());
		shadowInstance.setTimestamp(compositeModel.getTimestamp());
		
		String OUTPUT="";
		result.setMessage("OK");
		result.setStatus(true);

		try {
			
			OUTPUT = mongoRepo.insert(PROPERTIES_COLLECTION + model.getType().substring(0,1).toUpperCase() + model.getType().substring(1), mapper.writeValueAsString(shadowInstance));
			
		} catch (final Exception e) {
			result.setResult(OUTPUT);
			result.setStatus(false);
			result.setMessage(e.getMessage());
		}

		result.setResult(OUTPUT);
		result.setOperation(EVENT);
		return result;
	}


}
