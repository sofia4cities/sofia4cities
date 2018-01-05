/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.query;


import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoIterable;

public interface MongoQueryNative {

	public MongoIterable<BasicDBObject> onFind(String query, String objectId, boolean ignoreLimit, Integer limit,
			boolean checkAuthorization) throws DBPersistenceException;

	public void validateQuery(String query) throws DBPersistenceException;
	
}
