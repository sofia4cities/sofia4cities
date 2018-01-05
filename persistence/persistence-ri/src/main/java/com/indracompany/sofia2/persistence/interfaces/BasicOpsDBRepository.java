/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.interfaces;

import java.util.List;

import org.bson.types.ObjectId;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

public interface BasicOpsDBRepository{
		
	public String insert(String ontology, String instance) throws DBPersistenceException;
	public List<BulkWriteResult> insertBulk(String ontology, List<String> instances, boolean order, boolean includeIds)	throws DBPersistenceException;

	public List<String> remove(String ontology, String idInstance) throws DBPersistenceException;
	
	public List<String> update(String ontology, String updateStmt) throws DBPersistenceException;
	public List<String> update(String collection, String query, String data) throws DBPersistenceException;

	public List<String> find(String ontology, String query) throws DBPersistenceException;		
	public List<String> find(String ontology, String query, int limit) throws DBPersistenceException;
	
	public String findAsOneJSON(String ontology, String query) throws DBPersistenceException;		
	public String findAsOneJSON(String ontology, String query, int limit) throws DBPersistenceException;
	
	public String findById(String ontology, String objectId) throws DBPersistenceException;
	
	public String findAllAsOneJSON(String ontology) throws DBPersistenceException;
	public String findAllAsOneJSON(String ontology,int limit) throws DBPersistenceException;
	public List<String> findAll(String ontology) throws DBPersistenceException;
	public List<String> findAll(String ontology,int limit) throws DBPersistenceException;
	
	public long count(String ontology)  throws DBPersistenceException;


}
