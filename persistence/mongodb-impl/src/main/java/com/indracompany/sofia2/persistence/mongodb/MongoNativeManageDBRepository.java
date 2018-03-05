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
package com.indracompany.sofia2.persistence.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;
import com.indracompany.sofia2.persistence.mongodb.index.MongoDbIndex;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplate;
import com.mongodb.client.model.IndexOptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component("MongoManageDBRepository")
@Scope("prototype")
@Lazy
@Slf4j
public class MongoNativeManageDBRepository implements ManageDBRepository {

	@Autowired
	private UtilMongoDB util;

	@Autowired
	private MongoDbTemplate mongoDbConnector;

	@Value("${sofia2.database.mongodb.database:#{null}}")
	@Getter
	@Setter
	private String database;

	protected ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}

	@Override
	public String createTable4Ontology(String collection, String schema) throws DBPersistenceException {
		log.debug("createTable4Ontology", collection, schema);
		try {
			if (collection == null || schema == null)
				throw new DBPersistenceException(
						"DAOMongoDBImpl needs a collection and a schema to create a collection into the database");

			/**
			 * Sino existe la collection la crea
			 */
			if (!mongoDbConnector.collectionExists(database, collection)) {
				mongoDbConnector.createCollection(database, collection);
			} else {

				/**
				 * Permitir creación solamente si no tiene elementos: usa la que existe sin
				 * registros
				 */
				long countCollection = mongoDbConnector.count(database, collection, "{}");
				if (countCollection > 0) {
					log.error("createTable4Ontology", "The collection already exists and has records", collection);
					throw new DBPersistenceException("The collection already exists and has records");
				}
			}

			// validar que tiene geometry y crear indice en ese caso.
			validateIndexes(collection, schema);
			return collection;
		} catch (DBPersistenceException e) {
			log.error("createTable4Ontology", e, collection);
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public List<String> getListOfTables() throws DBPersistenceException {
		return mongoDbConnector.getCollectionNames(database);
	}

	@Override
	public List<String> getListOfTables4Ontology(String ontology) throws DBPersistenceException {
		log.debug("getCollectionNames", ontology);
		List<String> collections = getListOfTables();
		ArrayList<String> result = new ArrayList<String>();
		result.add(ontology);
		String prefix = ontology + "@";
		for (String collection : collections) {
			if (collection.startsWith(prefix))
				result.add(collection);
		}
		log.debug("/getCollectionNames");
		return result;
	}

	@Override
	public void removeTable4Ontology(String ontology) throws DBPersistenceException {
		log.debug("removeTable4Ontology", ontology);
		try {
			mongoDbConnector.dropCollection(database, ontology);
		} catch (javax.persistence.PersistenceException e) {
			log.error("removeTable4Ontology" + e.getMessage());
			throw new DBPersistenceException(e);
		}
		log.debug("/removeTable4Ontology");

	}

	@Override
	public void createIndex(String sentence) throws DBPersistenceException {
		log.debug("createIndex", sentence);
		String pquery = null;
		String collection = null;
		Map<String, Integer> indexKeys = null;
		IndexOptions indexOptions = null;
		try {
			pquery = sentence.trim();
			if (pquery.indexOf(".createIndex(") == -1)
				throw new DBPersistenceException("No db.<collection>.createIndex() found in sentence");
			if (pquery.indexOf(".createIndex({\"") == -1 && pquery.indexOf(".createIndex({'") == -1)
				throw new DBPersistenceException(
						"Please use ' in sentences, example: db.<collection>.createIndex({'<attribute>':1}) ");

			collection = util.getCollectionName(pquery);

			try {
				pquery = pquery.substring(pquery.indexOf("createIndex(") + 12, pquery.indexOf("})") + 1);
			} catch (Exception e) {
				log.error("Query bad formed:" + pquery
						+ ".Expected db.<collection>.createIndex({<attribute>:1},{name:'name_index',....})");
				throw new DBPersistenceException("Query bad formed:" + pquery
						+ ".Expected db.<collection>.createIndex({<attribute>:1},{name:'name_index',....})");
			}
			if (pquery.contains("},{")) {// complex index
				String keysOptions[] = pquery.split(",");
				try {
					indexKeys = objectMapper.readValue(util.prepareQuotes(keysOptions[0]),
							new TypeReference<Map<String, Integer>>() {
							});
					indexOptions = objectMapper.readValue(util.prepareQuotes(keysOptions[1]), IndexOptions.class);
				} catch (IOException e) {
					log.error("Invalid index key or index options. Sentence = {}, cause = {}, errorMessage = {}.",
							sentence, e.getCause(), e.getMessage());
					throw new DBPersistenceException("Invalid index key or index options", e);
				}
			} else {
				try {
					indexKeys = objectMapper.readValue(util.prepareQuotes(pquery),
							new TypeReference<Map<String, Integer>>() {
							});
				} catch (IOException e) {
					log.error("Invalid index key. Sentence = {}, cause = {}, errorMessage = {}.", sentence,
							e.getCause(), e.getMessage());
					throw new DBPersistenceException("Invalid index key", e);
				}
			}
			mongoDbConnector.createIndex(database, collection, new MongoDbIndex(indexKeys, indexOptions));
		} catch (DBPersistenceException e) {
			log.error("createIndex" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public Map<String, Boolean> getStatusDatabase() {
		Map<String, Boolean> map = new HashMap<>();
		map.put(database, mongoDbConnector.testConnection());
		return map;
	}

	@Override
	public void dropIndex(String ontology, String indexName) throws DBPersistenceException {
		log.debug("dropIndex", indexName, ontology);
		if (indexName != null && ontology != null) {
			try {
				mongoDbConnector.dropIndex(database, ontology, new MongoDbIndex(indexName));
			} catch (DBPersistenceException e) {
				log.error("dropIndex", e, indexName);
				throw new DBPersistenceException(e);
			}
		}
	}

	@Override
	public List<String> getListIndexes(String ontology) throws DBPersistenceException {
		log.debug("getIndexes", ontology);
		try {
			List<String> index = new ArrayList<String>();
			if (ontology != null) {
				return mongoDbConnector.getIndexes_asStrings(database, ontology);
			}
			return index;
		} catch (Exception e) {
			log.error("getIndexes", e);
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String getIndexes(String ontology) throws DBPersistenceException {
		log.debug("getIndexes", ontology);
		try {
			List<MongoDbIndex> list = mongoDbConnector.getIndexes(database, ontology);
			return objectMapper.writeValueAsString(list);
		} catch (Exception e) {
			log.error("getIndexes", e);
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public void validateIndexes(String collection, String schema) throws DBPersistenceException {
		log.debug("validateIndexes", collection, schema);
		if (collection != null) {
			if (schema.trim().length() > 0) {
				String esquema = util.prepareEsquema(schema);
				if (esquema.contains("'")) {
					esquema = esquema.replace("'", "\"");
				}
				try {
					Map<String, Object> obj2 = objectMapper.readValue(esquema,
							new TypeReference<Map<String, Object>>() {
							});
					String name = "";
					if (obj2.containsKey("properties")) {

						Map<String, Object> proper = (Map<String, Object>) obj2.get("properties");
						name = util.getParentProperties(proper, obj2);
						if (!name.isEmpty()) {
							createIndex(collection, name + ".geometry: \"2dsphere\"");
						}
					}
				} catch (JsonParseException e) {
					log.error("validateIndexes", e);
					throw new DBPersistenceException(e.getMessage());
				} catch (JsonMappingException e) {
					log.error("validateIndexes", e);
					throw new DBPersistenceException(e.getMessage());
				} catch (IOException e) {
					log.error("validateIndexes", e);
					throw new DBPersistenceException(new Exception(e.getMessage()));
				}
			} else {
				log.warn("validateIndexes", "Not found ontology");
				throw new DBPersistenceException(new Exception("Not found ontology " + collection));
			}
		}
	}

	@Override
	public void createIndex(String ontology, String attribute) throws DBPersistenceException {
		log.debug("createIndex", attribute, ontology);
		Map<String, Integer> indexKey = new HashMap<String, Integer>();
		indexKey.put(attribute, 1);
		try {
			mongoDbConnector.createIndex(database, ontology, new MongoDbIndex(indexKey));
		} catch (DBPersistenceException e) {
			log.error("createIndex", e, attribute);
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public void createIndex(String ontology, String name, String attribute) throws DBPersistenceException {
		log.debug("createIndex", attribute, name, ontology);
		try {
			createIndex("db." + ontology + ".createIndex({'" + attribute + "':1},{'name':'" + name + "'})");
		} catch (DBPersistenceException e) {
			log.error("createIndex", e, attribute);
			throw new DBPersistenceException(e);
		}
	}

}
