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
/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.PersistenceException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.mongodb.quasar.connector.QuasarMongoDBbHttpConnector;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplate;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component("MongoBasicOpsDBRepository")
@Scope("prototype")
@Lazy
@Slf4j
public class MongoBasicOpsDBRepository implements BasicOpsDBRepository {
	@Autowired
	private UtilMongoDB util;

	@Autowired
	private MongoDbTemplate mongoDbConnector;

	@Autowired
	private QuasarMongoDBbHttpConnector quasarMongoConnector;

	@Value("${sofia2.database.mongodb.database:#{null}}")
	@Getter
	@Setter
	private String database;

	@Value("${sofia2.database.mongodb.queries.executionTimeout:30000}")
	@Getter
	@Setter
	private long queryExecutionTimeout;

	@Value("${sofia2.database.mongodb.queries.defaultLimit:100}")
	@Getter
	@Setter
	private int numMaxRegisters;

	protected ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}

	@Override
	public String insert(String ontology, String instance) throws DBPersistenceException {
		log.debug("insertInstance", ontology, instance);
		try {
			ObjectId objectId = mongoDbConnector.insert(database, ontology, util.prepareQuotes(instance));
			String retorno = util.getObjectIdString(objectId);
			return retorno;
		} catch (javax.persistence.PersistenceException e) {
			log.error("insertInstance", e);
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public List<BulkWriteResult> insertBulk(String ontology, List<String> instances, boolean order, boolean includeIds)
			throws DBPersistenceException {
		ArrayList<String> dataToInsert = new ArrayList<String>(instances.size());
		for (String document : instances) {
			dataToInsert.add(util.prepareQuotes(document));
		}
		try {
			return mongoDbConnector.bulkInsert(database, ontology, dataToInsert, order, includeIds);
		} catch (javax.persistence.PersistenceException e) {
			log.error("insertBulk", e);
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public List<String> deleteNative(String collection, String query) throws DBPersistenceException {
		log.debug("removeInstance", collection, query);
		try {
			List<String> objectIds = getObjectIdsOfAffectedDocuments(collection, query);
			mongoDbConnector.remove(database, collection, query);
			return objectIds;
		} catch (javax.persistence.PersistenceException e) {
			log.error("remove", e);
			throw new DBPersistenceException(e);
		}
	}

	private List<String> getObjectIdsOfAffectedDocuments(String collection, String query) {
		Map<String, Integer> projection = new HashMap<String, Integer>();
		projection.put("_id", 1);
		List<String> objectIds = new ArrayList<String>();
		for (BasicDBObject document : mongoDbConnector.find(database, collection, query, projection, null, 0, 0,
				this.queryExecutionTimeout)) {
			objectIds.add("{\"_id\":ObjectId(\"" + document.get("_id") + "\")}");
		}
		return objectIds;
	}

	@Override
	public List<String> updateNative(String collection, String query, String data) throws DBPersistenceException {
		try {
			log.debug("update", collection, query, data);
			MongoIterable<BasicDBObject> cursor = mongoDbConnector.find(database, collection, util.prepareQuotes(query),
					queryExecutionTimeout);
			List<String> ids = new ArrayList<String>();
			BasicDBObject updatedInstance = (BasicDBObject) JSON.parse(util.prepareQuotes(data));
			for (BasicDBObject document : cursor) {
				/*
				 * En 'datos' llega sólo la instancia de la ontología, sin ContextData. En el
				 * documento a actualizar debemos poner al mismo nivel el ContextData y la
				 * instancia de la ontología. El bucle hace falta porque no siempre la instancia
				 * de ontología tiene una única clave de primer nivel y el resto de campos
				 * anidados bajo ella.
				 */
				BasicDBObject contextData = (BasicDBObject) document.get("contextData");
				BasicDBObject updatedDocument = new BasicDBObject();
				updatedDocument.append("contextData", contextData);
				for (String key : updatedInstance.keySet()) {
					updatedDocument.append(key, updatedInstance.get(key));
				}
				mongoDbConnector.replace(database, collection, document, updatedDocument);
				ids.add(util.getObjectIdString(document.getObjectId("_id")));
			}
			return ids;
		} catch (javax.persistence.PersistenceException e) {
			log.error("update", e);
			throw new PersistenceException(e);
		}
	}

	@Override
	public List<String> updateNative(String ontology, String statement) throws DBPersistenceException {
		log.debug("update", statement);
		String statementAux = statement;
		String data = "";
		String query = "";
		try {
			if (statementAux == null || statementAux.length() == 0)
				throw new DBPersistenceException("Statement null: " + statement);

			if (statementAux.startsWith("{")) {
				statementAux = statementAux.substring(1);
			}
			if (statementAux.endsWith("}")) {
				statementAux = statementAux.substring(0, statementAux.length() - 1);
			}
			if (!statementAux.endsWith(";")) {
				statementAux = statementAux.concat(";");
			}
			if (!statementAux.toLowerCase().startsWith("db.")) {
				log.warn("updateByNativeQuery", "Expected MongoDB update statement");
				throw new DBPersistenceException("Expected MongoDB update statement");
			}
			if (statementAux.contains("db.")) {
				statementAux = statementAux.replace("db.", "");
			}
			if (statementAux.contains("update(")) {
				statementAux = statementAux.substring(statementAux.indexOf("(") + 1, statementAux.lastIndexOf(")"));
				statementAux = statementAux.trim();

				int anidamiento = 0;
				int indiceInicioObjeto = 0;
				List<String> objetos = new ArrayList<String>();
				for (int i = 0; i < statementAux.length(); i++) {
					if (statementAux.charAt(i) == '{') {
						anidamiento++;
					} else if (statementAux.charAt(i) == '}') {
						anidamiento--;
					}
					if ((statementAux.charAt(i) == ',' || i == statementAux.length() - 1) && anidamiento == 0) {
						if (statementAux.charAt(i) == ',') {
							objetos.add(new String(statementAux.substring(indiceInicioObjeto, i)));
						} else {
							objetos.add(new String(statementAux.substring(indiceInicioObjeto)));
						}
						indiceInicioObjeto = i + 1;
					}
				}

				if (objetos.size() >= 2) {
					query = objetos.get(0);
					data = objetos.get(1);

				} else {
					log.warn("update", "Expected {$set:{[field:value]}} ||  {$inc:{[field:value]}}");
					throw new DBPersistenceException("Expected {$set:{[field:value]}} ||  {$inc:{[field:value]}}");
				}
			}
			/*
			 * Native updates specify the document to be replaced; SQL-LIKE ones specify an
			 * update operator. We should remove this inconsistency.
			 */
			String updateOperation = util.prepareQuotes(data);
			String result = getUpdateStatement(data, ontology);
			return updateNative(ontology, query, updateOperation);
		} catch (Exception e) {
			log.error("update", e, statement);
			throw new PersistenceException("Necesary indicate a valid value " + e.getMessage());
		}
	}

	private String getUpdateStatement(String data, String collection) throws Exception {
		if (data.contains("$set")) {
			Map<String, Object> mapa = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
			});
			String setStatement = (String) mapa.get("$set");
			return setStatement;
		} else {
			// FIX: in these cases, we replace one document with another one.
			// The $set makes the new
			// driver raise an exception.
			// return buildStatementForUpdateNativeFromSSAPResourceData(data);
			return data;
		}
	}

	@Override
	public String queryNativeAsJson(String collection, String query) throws DBPersistenceException {
		return queryNativeAsJson(collection, query, 0, numMaxRegisters);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		log.debug("find", query, ontology);
		try {
			query = util.convertObjectIdV1toVLegacy(query);
			return JSON.serialize(queryNative(ontology, query, offset, limit));
		} catch (javax.persistence.PersistenceException e) {
			log.error("find", e, query, ontology);
			throw new PersistenceException(e);
		}
	}

	@Override
	public List<String> queryNative(String ontology, String query) throws DBPersistenceException {
		return queryNative(ontology, query, 0, numMaxRegisters);
	}

	@Override
	public List<String> queryNative(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		log.debug("find", query, ontology);
		List<String> result = new ArrayList<>();
		try {
			query = util.convertObjectIdV1toVLegacy(query);
			MongoIterable<BasicDBObject> cursor = mongoDbConnector.find(database, ontology,
					util.prepareQuotes4find(ontology, query), numMaxRegisters, queryExecutionTimeout);
			for (BasicDBObject obj : cursor) {
				result.add(obj.toJson());
			}
			return result;
		} catch (javax.persistence.PersistenceException e) {
			log.error("find", e, query, ontology);
			throw new PersistenceException(e);
		}
	}

	@Override
	public String findById(String collection, String objectId) throws DBPersistenceException {
		try {
			BasicDBObject o = mongoDbConnector.findById(database, collection, objectId);
			if (o != null)
				return o.toJson();
			return null;
		} catch (Exception e) {
			log.error("findById", e, objectId);
			throw new PersistenceException("findById Error:" + e.getMessage());
		}
	}

	@Override
	public String findAllAsJson(String ontology) throws DBPersistenceException {
		return JSON.serialize(findAll(ontology));
	}

	@Override
	public String findAllAsJson(String ontology, int limit) throws DBPersistenceException {
		return JSON.serialize(findAll(ontology));
	}

	@Override
	public List<String> findAll(String collection) throws DBPersistenceException {
		return findAll(collection, numMaxRegisters);
	}

	@Override
	public List<String> findAll(String collection, int limit) throws DBPersistenceException {
		try {
			List<String> result = new ArrayList<>();
			log.debug("findAll", collection, limit);
			for (BasicDBObject obj : mongoDbConnector.find(database, collection, "{}", limit)) {
				result.add(obj.toJson());
			}
			return result;
		} catch (javax.persistence.PersistenceException e) {
			log.error("findAll Error:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public long count(String collectionName) throws DBPersistenceException {
		try {
			return mongoDbConnector.count(database, collectionName, "{}");
		} catch (javax.persistence.PersistenceException e) {
			log.error("count Error:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public long countNative(String collectionName, String query) throws DBPersistenceException {
		try {
			return mongoDbConnector.count(database, collectionName, query);
		} catch (javax.persistence.PersistenceException e) {
			log.error("count Error:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String querySQLAsJson(String ontology, String query) throws DBPersistenceException {
		try {
			return quasarMongoConnector.queryAsJson(query, 0, numMaxRegisters);
		} catch (Exception e) {
			log.error("Error executing query in Quasar: " + query, e);
			throw new DBPersistenceException("Error executing query in Quasar: " + query, e);
		}
	}

	@Override
	public String querySQLAsTable(String ontology, String query) throws DBPersistenceException {
		try {
			return quasarMongoConnector.queryAsTable(query, 0, numMaxRegisters);
		} catch (Exception e) {
			log.error("Error executing query in Quasar: " + query, e);
			throw new DBPersistenceException("Error executing query in Quasar: " + query, e);
		}
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		try {
			return quasarMongoConnector.queryAsJson(query, offset, numMaxRegisters);
		} catch (Exception e) {
			log.error("Error executing query in Quasar: " + query, e);
			throw new DBPersistenceException("Error executing query in Quasar: " + query, e);
		}
	}

	@Override
	public String querySQLAsTable(String ontology, String query, int offset) throws DBPersistenceException {
		try {
			return quasarMongoConnector.queryAsTable(query, offset, numMaxRegisters);
		} catch (Exception e) {
			log.error("Error executing query in Quasar: " + query, e);
			throw new DBPersistenceException("Error executing query in Quasar: " + query, e);
		}
	}

}
