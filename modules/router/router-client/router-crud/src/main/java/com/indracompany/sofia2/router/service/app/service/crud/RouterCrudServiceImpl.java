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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.services.ontologydata.OntologyDataService;
import com.indracompany.sofia2.persistence.services.BasicOpsPersistenceServiceFacade;
import com.indracompany.sofia2.persistence.services.QueryToolService;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;
import com.indracompany.sofia2.router.audit.aop.Auditable;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;
import com.indracompany.sofia2.router.service.app.service.RouterCrudServiceException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RouterCrudServiceImpl implements RouterCrudService {

	@Autowired
	private QueryToolService queryToolService;

	@Autowired
	private BasicOpsPersistenceServiceFacade basicOpsService;

	@Autowired
	private RouterCrudCachedOperationsService routerCrudCachedOperationsService;

	@Autowired
	private OntologyDataService ontologyDataService;

	@Autowired
	private OntologyRepository ontologyRepository;

	@Override
	@Auditable
	public OperationResultModel insert(OperationModel operationModel) throws RouterCrudServiceException {

		log.info("Router Crud Service Operation " + operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();

		final String ontologyName = operationModel.getOntologyName();

		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();

		String OUTPUT = "";
		result.setMessage("OK");
		result.setStatus(true);

		try {

			List<String> processedData = ontologyDataService.preProcessInsertData(operationModel);

			final Ontology ontology = ontologyRepository.findByIdentification(ontologyName);

			if (METHOD.equalsIgnoreCase("POST")
					|| METHOD.equalsIgnoreCase(OperationModel.OperationType.INSERT.name())) {
				final List<BulkWriteResult> results = basicOpsService.insertBulk(ontologyName, ontology.getJsonSchema(),
						processedData, true, true);
				if (results.size() > 1)
					OUTPUT = String.valueOf(results.size());
				else
					OUTPUT = results.get(0).getId();
			}
		} catch (final Exception e) {
			result.setResult("ERROR");
			result.setStatus(false);
			result.setMessage(e.getMessage());
			result.setErrorCode("");
			result.setOperation("INSERT");
			throw new RouterCrudServiceException("Error inserting data", e, result);
		}

		result.setResult(OUTPUT);
		result.setOperation(METHOD);
		return result;

	}

	@Override
	@Auditable
	public OperationResultModel update(OperationModel operationModel) {

		log.info("Router Crud Service Operation " + operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();
		final String QUERY_TYPE = operationModel.getQueryType().name();
		final String ontologyName = operationModel.getOntologyName();
		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();

		String OUTPUT = "";

		result.setMessage("OK");
		result.setStatus(true);

		try {
			if (METHOD.equalsIgnoreCase("PUT") || METHOD.equalsIgnoreCase(OperationModel.OperationType.UPDATE.name())) {

				if (OBJECT_ID != null && OBJECT_ID.length() > 0) {
					basicOpsService.updateNativeByObjectIdAndBodyData(ontologyName, OBJECT_ID, BODY);
					OUTPUT = basicOpsService.findById(ontologyName, OBJECT_ID);
				}

				else {
					OUTPUT = "" + basicOpsService.updateNative(ontologyName, BODY);
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
	@Auditable
	public OperationResultModel delete(OperationModel operationModel) {

		log.info("Router Crud Service Operation " + operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();
		final String QUERY_TYPE = operationModel.getQueryType().name();
		final String ontologyName = operationModel.getOntologyName();
		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();
		String OUTPUT = "";

		result.setMessage("OK");
		result.setStatus(true);

		try {
			if (METHOD.equalsIgnoreCase("DELETE")
					|| METHOD.equalsIgnoreCase(OperationModel.OperationType.DELETE.name())) {

				if (OBJECT_ID != null && OBJECT_ID.length() > 0) {
					OUTPUT = "" + basicOpsService.deleteNativeById(ontologyName, OBJECT_ID);
				}

				else {
					OUTPUT = "" + basicOpsService.deleteNative(ontologyName, BODY);
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
	@Auditable
	public OperationResultModel query(OperationModel operationModel) {

		log.info("Router Crud Service Operation " + operationModel.toString());
		OperationResultModel result = null;
		final boolean cacheable = operationModel.isCacheable();
		if (cacheable) {

			log.info("DO CACHE OPERATION " + operationModel.toString());
			result = routerCrudCachedOperationsService.queryCache(operationModel);

		} else {
			log.info("NOT CACHING, GO TO SOURCE " + operationModel.toString());

			result = queryNoCache(operationModel);
		}

		return result;

	}

	public OperationResultModel queryNoCache(OperationModel operationModel) {

		log.info("Router NO CACHING Crud Service Operation " + operationModel.toString());

		final OperationResultModel result = new OperationResultModel();

		final String METHOD = operationModel.getOperationType().name();
		final String BODY = operationModel.getBody();
		final String QUERY_TYPE = operationModel.getQueryType().name();
		final String ontologyName = operationModel.getOntologyName();
		final String OBJECT_ID = operationModel.getObjectId();
		final String USER = operationModel.getUser();
		final String CLIENTPLATFORM = operationModel.getClientPlatformId();

		String OUTPUT = "";
		result.setMessage("OK");
		result.setStatus(true);

		try {
			if (METHOD.equalsIgnoreCase("GET") || METHOD.equalsIgnoreCase(OperationModel.OperationType.QUERY.name())) {

				if (QUERY_TYPE != null) {
					if (QUERY_TYPE.equalsIgnoreCase(QueryType.SQLLIKE.name())) {
						// OUTPUT = queryToolService.querySQLAsJson(ontologyName, QUERY, 0);
						OUTPUT = (!NullString(CLIENTPLATFORM))
								? queryToolService.querySQLAsJsonForPlatformClient(CLIENTPLATFORM, ontologyName, BODY,
										0)
								: queryToolService.querySQLAsJson(USER, ontologyName, BODY, 0);
					} else if (QUERY_TYPE.equalsIgnoreCase(QueryType.NATIVE.name())) {
						// OUTPUT = queryToolService.queryNativeAsJson(ontologyName, QUERY, 0,0);
						OUTPUT = (!NullString(CLIENTPLATFORM))
								? queryToolService.queryNativeAsJsonForPlatformClient(CLIENTPLATFORM, ontologyName,
										BODY, 0, 0)
								: queryToolService.queryNativeAsJson(USER, ontologyName, BODY, 0, 0);
					} else {
						OUTPUT = basicOpsService.findById(ontologyName, OBJECT_ID);
					}
				} else {
					OUTPUT = basicOpsService.findById(ontologyName, OBJECT_ID);
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
	// @Auditable
	public OperationResultModel execute(OperationModel operationModel) {

		String METHOD = operationModel.getOperationType().name();

		OperationResultModel result = new OperationResultModel();

		try {
			if (METHOD.equalsIgnoreCase("GET") || METHOD.equalsIgnoreCase(OperationModel.OperationType.QUERY.name())) {
				result = query(operationModel);
			}

			if (METHOD.equalsIgnoreCase("POST")
					|| METHOD.equalsIgnoreCase(OperationModel.OperationType.INSERT.name())) {
				result = insert(operationModel);
			}
			if (METHOD.equalsIgnoreCase("PUT") || METHOD.equalsIgnoreCase(OperationModel.OperationType.UPDATE.name())) {
				result = update(operationModel);
			}
			if (METHOD.equalsIgnoreCase("DELETE")
					|| METHOD.equalsIgnoreCase(OperationModel.OperationType.DELETE.name())) {
				result = delete(operationModel);
			}
		} catch (Exception e) {
			log.error("error executin operation model ", e);
		}
		return result;
	}

	public QueryToolService getQueryToolService() {
		return queryToolService;
	}

	public void setQueryToolService(QueryToolService queryToolService) {
		this.queryToolService = queryToolService;
	}

	public static boolean NullString(String l) {
		if (l == null)
			return true;
		else if (l != null && l.equalsIgnoreCase(""))
			return true;
		else
			return false;
	}

	public OperationResultModel insertWithNoAudit(OperationModel model) throws RouterCrudServiceException {
		return insert(model);
	}

}
