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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.indracompany.sofia2.persistence.mongodb.MongoQueryAndParams;
import com.indracompany.sofia2.persistence.mongodb.config.MongoDbCredentials;
import com.indracompany.sofia2.persistence.mongodb.index.MongoDbIndex;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBucket;

/**
 * This component provides a lightweight integration with the MongoDB java
 * driver.
 * 
 * @see MongoDbConnectorQueryTests and MongoDbConnectorWriteTests before you get
 *      started.
 * 
 *
 */
public interface MongoDbTemplate extends Serializable {

	/**
	 * Runs a db.<collection>.aggregate(<pipeline>) command in the given database.
	 * 
	 * @param database
	 * @param collection
	 * @param pipeline
	 * @return
	 */
	MongoIterable<BasicDBObject> aggregate(String database, String collection, List<BasicDBObject> pipeline)
			throws PersistenceException;

	/**
	 * Runs a db.<collection>.distinct(<key>, <query>) command in the given
	 * database.
	 * 
	 * @param database
	 * @param collection
	 * @param key
	 * @param query
	 * @return
	 */
	<T> MongoIterable<T> distinct(String database, String collection, String key, String query, Class<T> resultType)
			throws PersistenceException;

	BasicDBObject findById(String database, String collection, String objectId) throws PersistenceException;

	MongoIterable<BasicDBObject> findAll(String database, String collection, int skip, int limit,
			long queryExecutionTimeoutMillis);

	/**
	 * Runs a db.<collection>.find(<query>,
	 * <projection>).sort(<sort>).skip(<skip>).limit(<limit>) command in the given
	 * database
	 * 
	 * @param database
	 * @param collection
	 * @param query
	 * @param projection
	 * @param sort
	 * @param skip
	 * @param limit
	 * @param queryExecutionTimeoutMillis
	 * @return
	 */
	MongoIterable<BasicDBObject> find(String database, String collection, Bson query, Bson projection, Bson sort,
			int skip, int limit, long queryExecutionTimeoutMillis) throws PersistenceException;

	MongoIterable<BasicDBObject> find(String database, String collection, MongoQueryAndParams mq,
			long queryExecutionTimeoutMillis) throws PersistenceException;

	/**
	 * Runs a db.<collection>.count(<query>) query in the given database.
	 * 
	 * @param database
	 * @param collection
	 * @param query
	 * @return
	 */
	long count(String database, String collection, String query) throws PersistenceException;

	/**
	 * Creates a collection in the given database.
	 * 
	 * @param database
	 * @param collection
	 */
	void createCollection(String database, String collection) throws PersistenceException;

	/**
	 * Creates an index
	 * 
	 * @param database
	 * @param collection
	 * @param index
	 * @return
	 */
	String createIndex(String database, String collection, MongoDbIndex index);

	/**
	 * Removes an index
	 * 
	 * @param database
	 * @param collection
	 * @param index
	 */
	void dropIndex(String database, String collection, MongoDbIndex index);

	/**
	 * Returns the indexes of the given collection serialized as a list of strings
	 * 
	 * @param database
	 * @param collection
	 * @return
	 */
	List<String> getIndexes_asStrings(String database, String collection) throws PersistenceException;

	/**
	 * Returns the indexes of the given collection
	 * 
	 * @param database
	 * @param collection
	 * @return
	 */
	List<MongoDbIndex> getIndexes(String database, String collection) throws PersistenceException;

	/**
	 * Returns the names of the collections of the given database.
	 * 
	 * @param database
	 * @return
	 */
	List<String> getCollectionNames(String database) throws PersistenceException;

	/**
	 * Returns the MongoDB connection. This method will be deleted in the new
	 * versions of the connector.
	 * 
	 * @return
	 */
	MongoClient getConnection();

	/**
	 * Returns the names of the existing MongoDB databases.
	 * 
	 * @return
	 */
	Collection<String> getDatabaseNames() throws PersistenceException;

	/**
	 * Returns the statistics of the given database.
	 * 
	 * @param database
	 * @return
	 * @throws PersistenceException
	 */
	Document getDatabaseStats(String database) throws PersistenceException;

	/**
	 * Returns the statistics of the given collection.
	 * 
	 * @param database
	 * @param collection
	 * @return
	 * @throws PersistenceException
	 */
	Document getCollectionStats(String database, String collection) throws PersistenceException;

	/**
	 * Runs a db.eval(<code>, <args>) command on the given database.
	 * 
	 * @param database
	 * @param command
	 * @return
	 */
	@Deprecated
	Object eval(String database, String code, Object[] args) throws PersistenceException;

	/**
	 * Runs a db.<collection>.insert(<data>) command on the given database.
	 * 
	 * @param database
	 * @param collection
	 * @param data
	 * @return
	 * @throws PersistenceException
	 */
	ObjectId insert(String database, String collection, String data) throws PersistenceException;

	/**
	 * Runs a db.<collection>.insert(<data>) command on the given database.
	 * 
	 * @param database
	 * @param collection
	 * @param data
	 * @return
	 * @throws PersistenceException
	 */
	ObjectId insert(String database, String collection, BasicDBObject data) throws PersistenceException;

	/**
	 * Performs an unordered bulk insert.
	 * 
	 * @param database
	 * @param collection
	 * @param data
	 * @param orderedOp
	 * @param includeObjectIds
	 * @throws PersistenceException
	 */
	List<BulkWriteResult> bulkInsert(String database, String collection, List<String> data, boolean orderedOp,
			boolean includeObjectIds) throws PersistenceException;

	/**
	 * Runs a db.<collection>.remove(<query>) command on the given database.
	 * 
	 * @param database
	 * @param collection
	 * @param query
	 * @return number of deletes
	 */
	long remove(String database, String collection, String query) throws PersistenceException;

	/**
	 * Runs a db.<collection>.remove(<query>) command on the given database.
	 * 
	 * @param database
	 * @param collection
	 * @param document
	 * @return number of deletes
	 */
	long remove(String database, String collection, BasicDBObject query) throws PersistenceException;

	/**
	 * Runs a db.<collection>.update(<query>, <update>, {multi: true}) command on
	 * the given database.
	 * 
	 * @param database
	 * @param collection
	 * @param query
	 * @param update
	 * @param multi
	 */
	long update(String database, String collection, String query, String update, boolean multi)
			throws PersistenceException;

	/**
	 * Replaces a document of a collection
	 * 
	 * @param database
	 * @param collection
	 * @param oldDocument
	 * @param newDocument
	 * @throws PersistenceException
	 */
	void replace(String database, String collection, BasicDBObject oldDocument, BasicDBObject newDocument)
			throws PersistenceException;

	/**
	 * Runs a db.<collection>.drop() command on the given database.
	 * 
	 * @param database
	 * @param collection
	 * @throws PersistenceException
	 */
	void dropCollection(String database, String collection) throws PersistenceException;

	/**
	 * Tests the connection with the MongoDB server.
	 * 
	 * @return
	 */
	boolean testConnection();

	/**
	 * Converts the results of a MongoDB query to the given target type.
	 * 
	 * @param cursor
	 * @param keepObjectIds
	 * @param raiseExceptionsOnErrors
	 * @param targetQueryResultType
	 * @return
	 * @throws PersistenceException
	 */
	<T> List<T> convertQueryResults(MongoIterable<BasicDBObject> cursor, boolean keepObjectIds,
			boolean raiseExceptionsOnErrors, Class<T> targetQueryResultType) throws PersistenceException;

	/**
	 * Converts the results of a MongoDB query to the given target type. The
	 * ObjectIds will be removed from the target documents, and a
	 * PersistenceException will be raised when a deserialization error is detected.
	 * 
	 * @param cursor
	 * @param targetQueryResultType
	 * @return
	 * @throws PersistenceException
	 */
	<T> List<T> convertQueryResults(MongoIterable<BasicDBObject> cursor, Class<T> targetQueryResultType)
			throws PersistenceException;

	/**
	 * Returns the identifier of the host that is the current master of the replica
	 * set.
	 * 
	 * @return
	 */
	ServerAddress getReplicaSetMaster();

	/**
	 * Returns the credentials that the connector uses.
	 * 
	 * @return
	 */
	MongoDbCredentials getCredentials();

	/**
	 * Normalizes a given collection name
	 * 
	 * @param database
	 * @param collectionName
	 * @return
	 */
	String getNormalizedCollectionName(String database, String collectionName);

	/**
	 * Deletes the given database.
	 * 
	 * @param database
	 */
	void dropDatabase(String database);

	/**
	 * Configures a GridFS bucket on the given database
	 * 
	 * @param database
	 * @return
	 */
	GridFSBucket configureGridFSBucket(String database);

	/**
	 * Exists collection
	 */
	Boolean collectionExists(String database, String collection);
}
