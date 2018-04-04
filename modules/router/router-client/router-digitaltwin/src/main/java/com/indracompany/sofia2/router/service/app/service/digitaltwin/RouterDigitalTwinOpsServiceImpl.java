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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.persistence.mongodb.MongoDBDigitalTwinRepository;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinCompositeModel;
import com.indracompany.sofia2.router.service.app.model.DigitalTwinModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterDigitalTwinService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RouterDigitalTwinOpsServiceImpl implements RouterDigitalTwinService {
	
	final static String LOG_COLLECTION = "TwinLog";
	final static String PROPERTIES_COLLECTION = "TwinProperties";
	
	@Autowired
	private MongoDBDigitalTwinRepository mongoDBDigitalTwinRepository;

	@Override
	public OperationResultModel insertLog(DigitalTwinCompositeModel compositeModel) {
		log.info("Router Digital Twin Service Operation "+compositeModel.getDigitalTwinModel().toString());
		OperationResultModel result = new OperationResultModel();
		DigitalTwinModel model = compositeModel.getDigitalTwinModel();
		
		final String DEVICE_TYPE = model.getType().substring(0, 1).toUpperCase() +  model.getType().substring(1);
		final String LOG = model.getLog();
		final String EVENT = model.getEvent().name();
		
		String OUTPUT="";
		result.setMessage("OK");
		result.setStatus(true);

		try {
			
			OUTPUT = mongoDBDigitalTwinRepository.insert(LOG_COLLECTION + DEVICE_TYPE, LOG);
			
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
		// TODO Auto-generated method stub
		return null;
	}


}
