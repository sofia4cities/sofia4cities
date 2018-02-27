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
package com.indracompany.sofia2.persistence.mongodb.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.PersistenceException;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.mongodb.MongoQueryAndParams;
import com.indracompany.sofia2.persistence.mongodb.UtilMongoDB;
import com.indracompany.sofia2.persistence.mongodb.config.MongoDbCredentials;
import com.indracompany.sofia2.persistence.mongodb.index.MongoDbIndex;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Lazy
@Slf4j
public class MongoDbTemplateImpl implements MongoDbTemplate {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UtilMongoDB util;

	@Value("${sofia2.database.mongodb.servers:#{null}}")
	private String servers;

	@Value("${sofia2.database.mongodb.socketTimeout:30000}")
	private int socketTimeout;

	@Value("${sofia2.database.mongodb.connectTimeout:30000}")
	private int connectTimeout;

	@Value("${sofia2.database.mongodb.maxWaitTime:30000}")
	private int maxWaitTime;

	@Value("${sofia2.database.mongodb.poolSize:10}")
	private int poolSize;

	@Autowired
	private MongoDbCredentials credentials;

	@Value("${sofia2.database.mongodb.readFromSecondaries:false}")
	private boolean readFromSecondaries;

	@Value("${sofia2.database.mongodb.writeConcern:UNACKNOWLEDGED}")
	private WriteConcern writeConcern;

	private List<ServerAddress> serverAddresses;

	private MongoClient mongoDbClient;

	private ConcurrentHashMap<String, String> normalizedCollectionNames;

	@PostConstruct
	public void init() throws PersistenceException {
		log.info("Initializing MongoDB connector...");
		normalizedCollectionNames = new ConcurrentHashMap<String, String>();
		if (this.servers != null) {
			registerMongoDbServers();
			configureMongoDbClient();
		}
	}

	private void registerMongoDbServers() throws PersistenceException {
		log.info("Parsing MongoDB servers property...");
		serverAddresses = new ArrayList<ServerAddress>();
		for (String serverAddr_asStr : servers.split(",")) {
			String[] splittedServerAddr = serverAddr_asStr.split(":");
			if (splittedServerAddr.length != 2) {
				String errorMessage = String.format(
						"The MongoDB server address %s is malformed. The hostname and the port are required.",
						serverAddr_asStr);
				log.error(errorMessage);
				throw new PersistenceException(errorMessage);
			} else {
				log.info("Registering MongoDB server {}.", serverAddr_asStr);
			}
			String host = splittedServerAddr[0].trim();
			int port = Integer.valueOf(splittedServerAddr[1].trim());
			serverAddresses.add(new ServerAddress(host, port));
		}
	}

	private void configureMongoDbClient() {
		log.info("Configuring MongoDB client...");

		MongoClientOptions.Builder mongoClientOptionsBuilder = new MongoClientOptions.Builder();
		mongoClientOptionsBuilder.socketTimeout(this.socketTimeout).connectTimeout(this.connectTimeout)
				.maxWaitTime(this.maxWaitTime).connectionsPerHost(this.poolSize);

		if (readFromSecondaries) {
			log.info("The MongoDB connector will forward the queries to the secondary nodes.");
			mongoClientOptionsBuilder.readPreference(ReadPreference.secondary());
		}

		if (serverAddresses.size() == 1) {
			log.warn(
					"The MongoDB connector has been configured in single-server mode. The configured WriteConcern level ({}) will be ignored.",
					writeConcern);
			writeConcern = null;
		} else {
			log.info("The MongoDB connector has been configured in replica set mode. Using WriteConcern level {}.",
					writeConcern);
			mongoClientOptionsBuilder.writeConcern(writeConcern);
		}

		// MongoClientOptions options
		if (credentials.isEnableMongoDbAuthentication()) {
			MongoCredential credential = MongoCredential.createMongoCRCredential(credentials.getUsername(),
					credentials.getAuthenticationDatabase(), credentials.getPassword().toCharArray());
			mongoDbClient = new MongoClient(serverAddresses, Arrays.asList(credential),
					mongoClientOptionsBuilder.build());

		} else {
			mongoDbClient = new MongoClient(serverAddresses, mongoClientOptionsBuilder.build());
		}
	}

	@Override
	public MongoClient getConnection() {
		return mongoDbClient;
	}

	@Override
	public List<String> getCollectionNames(String database) throws PersistenceException {
		log.debug("Retrieving collection names. Database = {}.", database);
		try {
			return util.toJavaList(mongoDbClient.getDatabase(database).listCollectionNames());
		} catch (Throwable e) {
			log.error("Unable to retrieve collection names. Database = {}, cause = {}, errorMessage = {}.", database,
					e.getCause(), e.getMessage());
			throw new PersistenceException(e);
		}
	}

	@Override
	public Boolean collectionExists(String database, String collection) {
		MongoIterable<String> resultListCollectionNames = mongoDbClient.getDatabase(database).listCollectionNames();
		if (null != resultListCollectionNames) {
			for (String resultName : resultListCollectionNames) {
				if (resultName.equalsIgnoreCase(collection)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Collection<String> getDatabaseNames() throws PersistenceException {
		try {
			log.debug("Retrieving database names...");
			return util.toJavaCollection(mongoDbClient.listDatabaseNames());
		} catch (Throwable e) {
			log.error("Unable to retrieve database names. Cause = {}, errorMessage = {}.", e.getCause(),
					e.getMessage());
			throw new PersistenceException("Unable to retrieve database names", e);
		}
	}

	@Override
	public Document getDatabaseStats(String database) throws PersistenceException {
		try {
			log.debug("Retrieving database stats. Database = {}.", database);
			return mongoDbClient.getDatabase(database).runCommand(new Document("dbstats", 1));
		} catch (Throwable e) {
			log.error("Unable to retrieve database stats. Database = {}, cause = {}, errorMessage = {}.", database,
					e.getCause(), e.getMessage());
			throw new PersistenceException("Unable to retrieve database stats.", e);
		}
	}

	@Override
	public Document getCollectionStats(String database, String collection) throws PersistenceException {
		try {
			log.debug("Retrieving collection stats. Database = {}, collection = {}.", database, collection);
			return mongoDbClient.getDatabase(database).runCommand(new Document("collStats", collection));
		} catch (Throwable e) {
			log.error(
					"Unable to retrieve collection stats. Database = {}, collection = {}, cause = {}, errorMessage = {}.",
					database, collection, e.getCause(), e.getMessage());
			throw new PersistenceException("Unable to retrieve collection stats", e);
		}
	}

	@Override
	public List<String> getIndexes_asStrings(String database, String collection) throws PersistenceException {
		try {
			log.debug("Retrieving indexes. Database = {}, collection = {}.", database, collection);
			ListIndexesIterable<Document> indexes = getCollection(database, collection, BasicDBObject.class)
					.listIndexes();
			List<String> result = new ArrayList<String>();
			for (Document index_asDocument : indexes) {
				result.add(index_asDocument.toJson());
			}
			return result;
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to retrieve indexes. Database = %s, collection = %s, cause = %s, errorMessage = %s.",
					database, collection, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public List<MongoDbIndex> getIndexes(String database, String collection) throws PersistenceException {
		try {
			log.debug("Retrieving indexes. Database = {}, collection = {}.", database, collection);
			ListIndexesIterable<Document> indexes = getCollection(database, collection, BasicDBObject.class)
					.listIndexes();
			List<MongoDbIndex> result = new ArrayList<MongoDbIndex>();
			for (Document index_asDocument : indexes) {
				result.add(MongoDbIndex.fromIndexDocument(index_asDocument));
			}
			return result;
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to retrieve indexes. Database = %s, collection = %s, cause = %s, errorMessage = %s.",
					database, collection, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public long count(String database, String collection, String query) throws PersistenceException {
		log.debug("Running count command. Database = {}, collection = {}, query = {}.", database, collection, query);
		try {
			if (!query.trim().equals("{}")) {
				query = query.substring(query.indexOf("count(") + 6, query.indexOf(")"));
			}
			if (query != null && query != "" && !query.trim().equals("{}") && !query.isEmpty()) {
				BasicDBObject queryObject = (BasicDBObject) JSON.parse(query);
				return getCollection(database, collection, BasicDBObject.class).count(queryObject);
			} else {
				return getCollection(database, collection, BasicDBObject.class).count();
			}
			// if (!query.trim().equals("{}")) {
			// BasicDBObject queryObject = (BasicDBObject) JSON.parse(query);
			// return getCollection(database, collection,
			// BasicDBObject.class).count(queryObject);
			// } else {
			// return getCollection(database, collection, BasicDBObject.class).count();
			// }
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to run count command. Database = %s, collection = %s, query = %s, cause = %s, errorMessage = %s.",
					database, collection, query, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public void createCollection(String database, String collection) {
		log.debug("Creating the collection...Database = {}, Collection = {} .", database, collection);
		try {
			String normalizedCollectionName = getNormalizedCollectionName(database, collection);
			CreateCollectionOptions options = new CreateCollectionOptions();
			options.capped(false);
			options.autoIndex(true);
			mongoDbClient.getDatabase(database).createCollection(normalizedCollectionName, options);
		} catch (Throwable e) {
			String errorMessage = String.format("Unable to create the collection. Database = %s, collection = %s , "
					+ "cause = %s, errorMessage = %s.", database, collection, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public String createIndex(String database, String collection, MongoDbIndex index) {
		log.debug("Creating indexes. Database = {}, Collection = {}, Index = {}.", database, collection, index);
		try {
			MongoCollection<?> dbCollection = getCollection(database, collection, BasicDBObject.class);
			BasicDBObject indexKey = new BasicDBObject(index.getKey());
			IndexOptions nativeIndexOptions = null;
			if (index.getIndexOptions() != null) {
				nativeIndexOptions = index.getIndexOptions();
			}
			if (nativeIndexOptions == null)
				return dbCollection.createIndex(indexKey);
			else
				return dbCollection.createIndex(indexKey, nativeIndexOptions);
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to create indexes with the given keys. Database = %s , collection = %s,  index = %s, cause = %s , errorMessage = %s.",
					database, collection, index, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public MongoIterable<BasicDBObject> aggregate(String database, String collection, List<BasicDBObject> pipeline)
			throws PersistenceException {
		log.debug("Running aggregate command. Database = {} , Collection = {} , Pipeline = {} ", database, collection,
				pipeline);
		try {
			if (pipeline == null || pipeline.isEmpty())
				throw new IllegalArgumentException(
						"The aggregation pipeline is required, and must contain at least one operation.");
			MongoCollection<BasicDBObject> dbCollection = getCollection(database, collection, BasicDBObject.class);
			return dbCollection.aggregate(pipeline);
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to run aggregate command on the given collection. Database = %s , collection = %s , pipeline = %s, cause = %s , errorMessage = %s.",
					database, collection, pipeline, e.getCause(), e.getMessage());
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public <T> MongoIterable<T> distinct(String database, String collection, String key, String query,
			Class<T> resultType) {
		log.debug(
				"Retrieving the distinct values from the given key/query. Database = {} , collection = {} , key = {} , query = {}.",
				database, collection, key, query);
		try {
			if (key == null || key.isEmpty())
				throw new IllegalArgumentException("The distinct field is required");
			MongoCollection<T> dbCollection = getCollection(database, collection, resultType);
			DistinctIterable<T> result = null;
			if (query == null) {
				dbCollection.distinct(key, resultType);
			} else {
				result = dbCollection.distinct(key, (BasicDBObject) JSON.parse(query), resultType);
			}
			return result;
		} catch (JSONParseException e) {
			String errorMessage = String.format(
					"Unable to parse JSON query. Query = %s, cause = %s, errorMessage = %s.", query, e.getCause(),
					e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to retrieve the values. Database = %s , collection = %s , key = %s , query =%s , cause = %S , errorMessage = %s.",
					database, collection, key, query, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public void dropIndex(String database, String collection, MongoDbIndex index) {
		log.debug("Deleting the given index. Database = {} , collection = {} , index = {}.", database, collection,
				index);
		try {
			MongoCollection<?> col = getCollection(database, collection, BasicDBObject.class);
			col.dropIndex(index.getName());
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to delete the entry. Database = %s , collection = %s , index = %s , cause = {} , errorMessage = %s.",
					database, collection, index, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public BasicDBObject findById(String database, String collection, String objectId) throws PersistenceException {
		// String
		// stmt="{db."+collection+".find({\"_id\":{\"$oid\":\""+objectId+"\"}})};";
		// { "_id" : { "$oid" : "5a326463a2b488aa28e8ed1c"}

		// MongoIterable<BasicDBObject> res= find(database, collection,
		// stmt,maxWaitTime);

		MongoCollection<BasicDBObject> dbCollection = getCollection(database, collection, BasicDBObject.class);
		FindIterable<BasicDBObject> res = dbCollection.find(new BasicDBObject("_id", new ObjectId(objectId)));
		if (res != null)
			return res.first();
		return null;
	}

	@Override
	public MongoIterable<BasicDBObject> findAll(String database, String collection, int skip, int limit,
			long queryExecutionTimeoutMillis) {
		log.debug(
				"Running query. Database = {} , collection = {} , query = {}, projection = {} , sort = {} , skip = {} , limit = {}, executionTimeOut = {}.",
				database, collection, skip, limit, queryExecutionTimeoutMillis);
		try {
			if (queryExecutionTimeoutMillis < 0)
				throw new IllegalArgumentException("The query execution timeout must be greater than or equal to zero");
			if (skip < 0)
				throw new IllegalArgumentException("The skip value must be greater than or equal to zero");
			if (limit < 0)
				throw new IllegalArgumentException("The limit value must be greater than or equal to zero");
			MongoCollection<BasicDBObject> dbCollection = getCollection(database, collection, BasicDBObject.class);
			FindIterable<BasicDBObject> result = null;
			result = dbCollection.find((Bson) JSON.parse("{}"));
			result = result.skip(skip);
			result = result.limit(limit);
			if (queryExecutionTimeoutMillis > 0)
				result = result.maxTime(queryExecutionTimeoutMillis, TimeUnit.MILLISECONDS);
			return result;
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to retrieve the required information. Database = %s , collection = %s , query = %s , projection = %s, "
							+ "sort = %s , skip = %s , limit = %s , queryExecutionTimeOut = %s, cause = %s, errorMessage = %s.",
					database, collection, skip, limit, queryExecutionTimeoutMillis, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public MongoIterable<BasicDBObject> find(String database, String collection, MongoQueryAndParams mq,
			long queryExecutionTimeoutMillis) {
		return find(database, collection, mq.getFinalQuery(), mq.getProjection(), mq.getSort(), mq.getSkip(),
				mq.getLimit(), queryExecutionTimeoutMillis);
	}

	@Override
	public MongoIterable<BasicDBObject> find(String database, String collection, Bson query, Bson projection, Bson sort,
			int skip, int limit, long queryExecutionTimeoutMillis) {
		log.debug(
				"Running query. Database = {} , collection = {} , query = {}, projection = {} , sort = {} , skip = {} , limit = {}, executionTimeOut = {}.",
				database, collection, query, projection, sort, skip, limit, queryExecutionTimeoutMillis);
		try {
			if (queryExecutionTimeoutMillis < 0)
				throw new IllegalArgumentException("The query execution timeout must be greater than or equal to zero");
			if (skip < 0)
				throw new IllegalArgumentException("The skip value must be greater than or equal to zero");
			if (limit < 0)
				throw new IllegalArgumentException("The limit value must be greater than or equal to zero");
			MongoCollection<BasicDBObject> dbCollection = getCollection(database, collection, BasicDBObject.class);
			FindIterable<BasicDBObject> result = null;
			if (projection == null)
				result = dbCollection.find(query);
			else
				result = dbCollection.find(query).projection(projection);
			if (sort != null)
				result = result.sort(sort);
			if (skip > 0)
				result = result.skip(skip);
			if (limit > 0)
				result = result.limit(limit);
			if (queryExecutionTimeoutMillis > 0)
				result = result.maxTime(queryExecutionTimeoutMillis, TimeUnit.MILLISECONDS);
			return result;
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to retrieve the required information. Database = %s , collection = %s , query = %s , projection = %s, "
							+ "sort = %s , skip = %s , limit = %s , queryExecutionTimeOut = %s, cause = %s, errorMessage = %s.",
					database, collection, query, projection, sort, skip, limit, queryExecutionTimeoutMillis,
					e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	@Deprecated
	public Object eval(String database, String code, Object[] args) {
		// FIXME: the eval() method is deprecated. We must remove this ASAP.
		log.debug("Evaluating JavaScript code. Database = {} , code = {} , args = {}", database, code, args);
		try {
			return mongoDbClient.getDB(database).eval(code, (args != null ? args : new Object[1]));
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to evaluate the given function. Database = %s , code = %s , args = %s , cause = %s , errorMessage =%s.",
					database, code, args, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public ObjectId insert(String database, String collection, String data) throws PersistenceException {
		try {
			return insert(database, collection, (BasicDBObject) JSON.parse(data));
		} catch (JSONParseException e) {
			String errorMessage = String.format("Unable to parse JSON data. Data = %s, cause = %s, errorMessage = %s.",
					data, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public ObjectId insert(String database, String collection, BasicDBObject doc) throws PersistenceException {
		log.debug(
				"Inserting the object into MongoDB. Database = {} , collection = {} , document = {}, writeConcern = {}.",
				database, collection, doc, writeConcern);
		MongoCollection<BasicDBObject> dbCollection = getCollection(database, collection, BasicDBObject.class);
		try {
			dbCollection.insertOne(doc);
			return doc.getObjectId("_id");
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to insert the object in MongoDB. Database = %s, collection = %s , document = %s , writeConcern = %s , cause = %s,"
							+ "errorMessage = %s.",
					database, collection, doc, writeConcern, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public List<BulkWriteResult> bulkInsert(String database, String collection, List<String> data, boolean orderedOp,
			boolean includeObjectIds) throws PersistenceException {

		BulkWriteResult[] bwResults = new BulkWriteResult[data.size()];
		log.debug(
				"Performing bulk insert operation. Database= {}, collection = {} , data = {} , writeConcern = {} , orderedOperation = {}, includeObjectIds = {}.",
				database, collection, data, writeConcern, orderedOp, includeObjectIds);
		// mapa con indice de lDatos y Object
		Map<Integer, BasicDBObject> mapDocs = new HashMap<Integer, BasicDBObject>();

		List<WriteModel<BasicDBObject>> bulkWrites = new ArrayList<WriteModel<BasicDBObject>>();
		try {
			String errorMsg;
			for (int i = 0; i < data.size(); i++) {
				errorMsg = null;
				BasicDBObject doc = null;
				boolean jsonParseError = false;
				try {
					doc = (BasicDBObject) JSON.parse(data.get(i));
				} catch (JSONParseException e) {
					String errorMessage = String.format(
							"Unable to parse JSON data. Data = %s, cause = %s, errorMessage = %s.", data, e.getCause(),
							e.getMessage());
					log.error(errorMessage);
					jsonParseError = true;
					errorMsg = "Not a valid JSON object: " + e.getMessage();
				} finally {
					if (!jsonParseError) {
						bulkWrites.add(new InsertOneModel<BasicDBObject>(doc));
						mapDocs.put(i, doc);
					} else {
						BulkWriteResult bwResult = new BulkWriteResult();
						bwResult.setErrorMessage(errorMsg);
						bwResult.setOk(false);
						bwResults[i] = bwResult;
					}
				}
			}

			Map<Integer, String> errorsMap = new HashMap<Integer, String>();
			MongoCollection<BasicDBObject> dbCollection = getCollection(database, collection, BasicDBObject.class);
			BulkWriteOptions options = new BulkWriteOptions();
			options.ordered(orderedOp);
			try {
				dbCollection.bulkWrite(bulkWrites, options);
			} catch (MongoBulkWriteException e) {
				List<BulkWriteError> errors = e.getWriteErrors();
				for (BulkWriteError error : errors) {
					errorsMap.put(error.getIndex(), error.getMessage());
				}
			} finally {
				for (int i : mapDocs.keySet()) {
					BulkWriteResult bwResult = new BulkWriteResult();
					if (errorsMap.containsKey(i)) {
						bwResult.setOk(false);
						bwResult.setErrorMessage(errorsMap.get(i));
						bwResults[i] = bwResult;
					} else {
						bwResult.setOk(true);
						if (includeObjectIds)
							bwResult.setId(util.getObjectIdString(mapDocs.get(i).getObjectId("_id")));
						bwResults[i] = bwResult;
					}
				}
			}
			return new ArrayList<BulkWriteResult>(Arrays.asList(bwResults));
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to perform the bulkInsert operation. Database = %s, collection = %s, data = %s , writeConcern = %s , orderedOperation = %s"
							+ "cause = %s , erroMessage = %s.",
					database, collection, data, writeConcern, orderedOp, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public void remove(String database, String collection, String query) throws PersistenceException {
		try {
			remove(database, collection, (BasicDBObject) JSON.parse(query));
		} catch (JSONParseException e) {
			String errorMessage = String.format(
					"Unable to parse JSON query. Query = %s, cause = %s, errorMessage = %s.", query, e.getCause(),
					e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(e);
		}
	}

	@Override
	public void remove(String database, String collection, BasicDBObject query) throws PersistenceException {
		log.debug("Removing from MongoDB...Database= {} , collection = {} , query = {}.", database, collection, query);
		MongoCollection<?> dbCollection = getCollection(database, collection, BasicDBObject.class);
		try {
			dbCollection.deleteMany(query);
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to delete from MongoDB. Database = %s, collection = %s , query = %s , writeConcern = %s , cause = %s , errorMessage = %s.",
					database, collection, query, writeConcern, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public void dropCollection(String database, String collection) throws PersistenceException {
		log.debug("Dropping collection. Database = {}, collection = {}.", database, collection);
		try {
			MongoCollection<?> dbCollection = getCollection(database, collection, BasicDBObject.class);
			dbCollection.drop();
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to drop the collection from MongoDB. Database = %s, collection = %s , cause = %s , errorMessage = %s.",
					database, collection, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public void update(String database, String collection, String query, String update, boolean multi)
			throws PersistenceException {
		log.info("Updating document. Database= {} , collection = {}, document = {} , update = {}.", database,
				collection, query, update);
		try {
			BasicDBObject parsedQuery = (BasicDBObject) JSON.parse(query);
			BasicDBObject parsedUpdate = (BasicDBObject) JSON.parse(update);
			MongoCollection<?> dbCollection = getCollection(database, collection, BasicDBObject.class);
			if (multi)
				dbCollection.updateMany(parsedQuery, parsedUpdate);
			else
				dbCollection.updateOne(parsedQuery, parsedUpdate);
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to update the document. Database = %s, collection = %s, document = %s, update = %s, cause = %s , errorMessage = %s.",
					database, collection, query, update, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public void replace(String database, String collection, BasicDBObject oldDocument, BasicDBObject newDocument)
			throws PersistenceException {
		log.info("Updating document. Database= {} , collection = {}, oldDocument = {}.", database, collection,
				oldDocument);
		try {
			MongoCollection<BasicDBObject> dbCollection = getCollection(database, collection, BasicDBObject.class);
			dbCollection.findOneAndReplace(oldDocument, newDocument);
		} catch (Throwable e) {
			String errorMessage = String.format(
					"Unable to replace document. Database = %s, collection = %s, oldDocument = %s, newDocument = %s, cause = %s , errorMessage = %s.",
					database, collection, oldDocument, newDocument, e.getCause(), e.getMessage());
			log.error(errorMessage);
			throw new PersistenceException(errorMessage, e);
		}
	}

	@Override
	public <T> List<T> convertQueryResults(MongoIterable<BasicDBObject> cursor, boolean keepObjectIds,
			boolean raiseExceptionsOnErrors, Class<T> targetQueryResultType) throws PersistenceException {
		try {
			List<T> result = new ArrayList<T>();
			ObjectMapper objMapper = new ObjectMapper();
			for (BasicDBObject obj : cursor) {
				if (!keepObjectIds && obj.get("_id") instanceof ObjectId)
					obj.removeField("_id");
				try {
					result.add(objMapper.readValue(JSON.serialize(obj), targetQueryResultType));
				} catch (IOException e) {
					String errorMessage = String.format(
							"Unable to deserialize query result. Object = %s, queryResultType = %s, cause = %s, errorMessage = %s.",
							obj, targetQueryResultType.getName(), e.getCause(), e.getMessage());
					log.error(errorMessage);
					if (raiseExceptionsOnErrors)
						throw new PersistenceException(errorMessage, e);
				}
			}
			return result;

		} catch (Throwable e) {
			log.error("Unable to execute query. Cause = {}, errorMessage = {}.", e.getCause(), e.getMessage());
			throw new PersistenceException("Unable to execute query", e);
		}
	}

	@Override
	public <T> List<T> convertQueryResults(MongoIterable<BasicDBObject> cursor, Class<T> targetQueryResultType)
			throws PersistenceException {
		return convertQueryResults(cursor, false, true, targetQueryResultType);
	}

	@Override
	public boolean testConnection() {
		try {
			getDatabaseNames();
			return true;
		} catch (PersistenceException e) {
			return false;
		}
	}

	@PreDestroy
	public void destroy() {
		if (this.mongoDbClient != null) {
			log.info("Closing MongoDB connections...");
			this.mongoDbClient.close();
			this.mongoDbClient = null;
		}
	}

	@Override
	public ServerAddress getReplicaSetMaster() {
		if (mongoDbClient.getReplicaSetStatus() != null)
			return mongoDbClient.getReplicaSetStatus().getMaster();
		else
			return mongoDbClient.getServerAddressList().get(0);
	}

	@Override
	public MongoDbCredentials getCredentials() {
		return credentials;
	}

	@Override
	public String getNormalizedCollectionName(String database, String collectionName) {
		log.debug("Normalizing collection name. Database = {}, collection = {}.", database, collectionName);
		String key = database + "::" + collectionName;
		String result = normalizedCollectionNames.get(key);
		if (result == null) {
			for (String normalizedCollectionName : getCollectionNames(database)) {
				if (normalizedCollectionName.equalsIgnoreCase(collectionName)) {
					String existingMapping = normalizedCollectionNames.putIfAbsent(key, normalizedCollectionName);
					if (existingMapping != null)
						result = existingMapping;
					else
						result = normalizedCollectionName;
					break;
				}
			}
			if (result == null) {
				result = normalizedCollectionNames.putIfAbsent(key, collectionName);
				if (result == null)
					result = collectionName;
			}
		}
		log.debug("The collection name has been normalized. Database = {}, collection = {}, result = {}.", database,
				collectionName, result);
		return result;
	}

	private <T> MongoCollection<T> getCollection(String database, String collectionName, Class<T> resultType) {
		return mongoDbClient.getDatabase(database).getCollection(getNormalizedCollectionName(database, collectionName),
				resultType);
	}

	@Override
	public void dropDatabase(String database) {
		log.info("Dropping database. DatabaseName = {}.", database);
		mongoDbClient.getDatabase(database).drop();
	}

	@Override
	public GridFSBucket configureGridFSBucket(String database) {
		return GridFSBuckets.create(mongoDbClient.getDatabase(database));
	}
}
