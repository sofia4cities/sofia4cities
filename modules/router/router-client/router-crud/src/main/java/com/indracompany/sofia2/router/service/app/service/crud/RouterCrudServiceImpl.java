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
package com.indracompany.sofia2.router.service.app.service.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;
import com.indracompany.sofia2.persistence.services.QueryToolService;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RouterCrudServiceImpl implements RouterCrudService {

	@Autowired
	private QueryToolService  queryToolService;

	@Autowired
	private MongoBasicOpsDBRepository mongoBasicOpsDBRepository;

	@Override
	public OperationResultModel insert(OperationModel operationModel) {

		log.info("Router Crud Service Operation "+operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();

		final String ontologyName = operationModel.getOntologyName();

		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();

		String OUTPUT="";
		result.setMessage("OK");
		result.setStatus(true);

		try {
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.POST.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.INSERT.name())) {
				OUTPUT = mongoBasicOpsDBRepository.insert(ontologyName, BODY);
			}
		} 
		catch (DBPersistenceException dbe) {
			result.setResult(OUTPUT);
			result.setStatus(false);
			result.setMessage(dbe.getMessage());
		}
		catch (final Exception e) {
			result.setResult(OUTPUT);
			result.setStatus(false);
			result.setMessage(e.getMessage());
		}

		result.setResult(OUTPUT);
		result.setOperation(METHOD);
		return result;


	}

	@Override
	public OperationResultModel update(OperationModel operationModel) {

		log.info("Router Crud Service Operation "+operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();
		final String QUERY_TYPE = operationModel.getQueryType().name();
		final String ontologyName = operationModel.getOntologyName();
		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();

		String OUTPUT="";

		result.setMessage("OK");
		result.setStatus(true);

		try {
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.PUT.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.UPDATE.name())) {

				if (OBJECT_ID!=null && OBJECT_ID.length()>0) {
					mongoBasicOpsDBRepository.updateNativeByObjectIdAndBodyData(ontologyName, OBJECT_ID, BODY);
					OUTPUT = mongoBasicOpsDBRepository.findById(ontologyName, OBJECT_ID);
				}

				else {
					OUTPUT = ""+mongoBasicOpsDBRepository.updateNative(ontologyName, BODY);
				}

			}
		} catch (final Exception e) {
			result.setResult(OUTPUT);
			result.setStatus(false);
			result.setMessage(e.getMessage());
		}

		result.setResult(OUTPUT);
		result.setOperation(METHOD);
		return result;
	}

	@Override
	public OperationResultModel delete(OperationModel operationModel) {

		log.info("Router Crud Service Operation "+operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();
		final String QUERY_TYPE = operationModel.getQueryType().name();
		final String ontologyName = operationModel.getOntologyName();
		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();
		String OUTPUT="";

		result.setMessage("OK");
		result.setStatus(true);

		try {
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.DELETE.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.DELETE.name())) {

				if (OBJECT_ID!=null && OBJECT_ID.length()>0) {
					OUTPUT = ""+ mongoBasicOpsDBRepository.deleteNativeById(ontologyName, OBJECT_ID);
				}

				else {
					OUTPUT = ""+ mongoBasicOpsDBRepository.deleteNative(ontologyName, BODY);
				}

			}
		} catch (final Exception e) {
			result.setResult(OUTPUT);
			result.setStatus(false);
			result.setMessage(e.getMessage());
		}

		result.setResult(OUTPUT);
		result.setOperation(METHOD);
		return result;
	}

	@Override
	@Cacheable("queries")
	public OperationResultModel query(OperationModel operationModel) {

		log.info("Router Crud Service Operation "+operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();
		final String QUERY_TYPE = operationModel.getQueryType().name();
		final String ontologyName = operationModel.getOntologyName();
		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();

		String OUTPUT="";
		result.setMessage("OK");
		result.setStatus(true);

		try {
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.GET.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.QUERY.name())) {

				if (QUERY_TYPE !=null)
				{
					if (QUERY_TYPE.equalsIgnoreCase(QueryType.SQLLIKE.name())) {
						//						OUTPUT = queryToolService.querySQLAsJson(ontologyName, QUERY, 0);
						OUTPUT = queryToolService.querySQLAsJson(USER, ontologyName, BODY, 0);
					}
					else if (QUERY_TYPE.equalsIgnoreCase(QueryType.NATIVE.name())) {
						//						OUTPUT = queryToolService.queryNativeAsJson(ontologyName, QUERY, 0,0);
						OUTPUT = queryToolService.queryNativeAsJson(USER, ontologyName, BODY, 0,0);
					}
					else {
						OUTPUT = mongoBasicOpsDBRepository.findById(ontologyName, OBJECT_ID);
					}
				}
				else {
					OUTPUT = mongoBasicOpsDBRepository.findById(ontologyName, OBJECT_ID);
				}
			}
		} catch (final Exception e) {
			result.setResult(OUTPUT);
			result.setStatus(false);
			result.setMessage(e.getMessage());
		}

		result.setResult(OUTPUT);
		result.setOperation(METHOD);
		return result;
	}

	@Override
	public OperationResultModel execute(OperationModel operationModel) {

		String METHOD = operationModel.getOperationType().name();
		
		OperationResultModel result = new OperationResultModel();
	
		try {
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.GET.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.QUERY.name())) {
				 result =query(operationModel);
			}
			
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.POST.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.INSERT.name())) {
				result =insert(operationModel);
			}
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.PUT.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.UPDATE.name())) {
				result =update(operationModel);
			}
			if (METHOD.equalsIgnoreCase(ApiOperation.Type.DELETE.name()) || METHOD.equalsIgnoreCase(OperationModel.OperationType.DELETE.name())) {
				result =delete(operationModel);
			}
		} catch (Exception e) {
		}
		return result;
	}


	public QueryToolService getQueryToolService() {
		return queryToolService;
	}

	public void setQueryToolService(QueryToolService queryToolService) {
		this.queryToolService = queryToolService;
	}

	public MongoBasicOpsDBRepository getMongoBasicOpsDBRepository() {
		return mongoBasicOpsDBRepository;
	}

	public void setMongoBasicOpsDBRepository(MongoBasicOpsDBRepository mongoBasicOpsDBRepository) {
		this.mongoBasicOpsDBRepository = mongoBasicOpsDBRepository;
	}



}
