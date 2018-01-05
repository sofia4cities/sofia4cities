/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.query;

import javax.persistence.PersistenceException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.mongodb.template.MongoDbTemplate;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoIterable;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@Scope("prototype")
@Lazy
public class MongoQueryNativeImpl implements MongoQueryNative {

	private MongoDbTemplate mongoDbConnector;

	private MongoQueryNativeUtil mongoQueryNativeUtil;

	

	@Value("${sofia2.database.mongodb.database:#{null}}")
	@Getter @Setter private String database;

	@Value("${sofia2.database.mongodb.queries.executionTimeout:30000}")
	@Getter @Setter private long queryExecutionTimeout;

	@Value("${sofia2.database.mongodb.queries.defaultLimit:100}")
	@Getter @Setter private int numMaxRegisters;

	@Value("${sofia2.database.mongodb.queries.maxConcurrentQueries:3}")
	@Getter @Setter private int maxConcurrentQueries;
	
	
	@Override
	public MongoIterable<BasicDBObject> onFind(String query, String objectId, boolean ignoreLimit, Integer limit,
			boolean checkAuthorization) throws DBPersistenceException {
		log.info("Request operation: "+query+ " on database:"+database);
		String collName = MongoQueryNativeUtil.getCollNameFromAction(query, "find");		
		if (checkAuthorization) {
			//FIXME
			//this.securityPluginManager.checkAuthorization(SSAPMessageTypes.QUERY, collName, null);
		}
		MFindQuery findQuery = mongoQueryNativeUtil.parseFindQuery(database, query);
		if (limit != null) {
			findQuery.setLimitArg(limit);
		}
		if (objectId != null) {
			BasicDBList findArgList = (BasicDBList) findQuery.get("findArg");
			if (findArgList.size() == 0) {
				ObjectId id = new ObjectId(objectId);
				BasicDBObject obj = new BasicDBObject();
				obj.append("_id", id);
				findArgList.add(obj);
			}
			for (int i = 0; i < findArgList.size(); i++) {
				((BasicDBObject) findArgList.get(i)).put("_id", new ObjectId(objectId));
			}
		}
		return runQuery(collName, findQuery, ignoreLimit);
	}
	
	private MongoIterable<BasicDBObject> runQuery(String collName, MFindQuery findQuery, boolean ignoreLimit) {
		BasicDBObject nativeQuery = null;
		BasicDBObject projection = null;
		BasicDBObject sortObject = null;
		BasicDBList findArgList = (BasicDBList) findQuery.get("findArg");

		if (findArgList.size() == 0) {
			nativeQuery = new BasicDBObject();
		} else if (findArgList.size() == 1) {
			nativeQuery = (BasicDBObject) findArgList.get(0);
		} else if (findArgList.size() >= 2) {
			nativeQuery = (BasicDBObject) findArgList.get(0);
			projection = (BasicDBObject) findArgList.get(1);
		}

		if (findQuery.getInvokedFunctionNameList().contains("sort")) {
			sortObject = findQuery.getSortArg();
		}

		int skip = (int) findQuery.getSkipArg();
		if (skip < 0)
			skip = 0;

		int limit = (int) findQuery.getLimitArg();

		if (ignoreLimit) {
			limit = 0;
		} else if (limit == -1) {
			limit = numMaxRegisters;
		} else {
			limit = Math.min(limit, numMaxRegisters);
		}

		try {
			return mongoDbConnector.find(database, collName, nativeQuery, projection, sortObject, skip, limit,
					queryExecutionTimeout);
		} catch (javax.persistence.PersistenceException e) {
			throw new PersistenceException(e);
		}
	}
	
	@Override
	public void validateQuery(String query) throws DBPersistenceException {
		mongoQueryNativeUtil.parseFindQuery(database, query);
	}
}
