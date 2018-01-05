/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.interfaces;

import java.util.List;
import java.util.Map;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;

public interface ManageDBRepository{

	public final static String BDTR_MONGO_SCHEMA_KEY = "BDTR_MONGO_SCHEMA_KEY";
	public final static String BDTR_RELATIONAL_TABLE_FIELDS="BDTR_RELATIONAL_TABLE_FIELDS";
	
	public Map<String,Boolean> getStatusDatabase() throws DBPersistenceException; 

	public String createTable4Ontology(String ontology, String schema) throws DBPersistenceException;
	
	public List<String> getListOfTables() throws DBPersistenceException;

	public List<String> getListOfTables4Ontology(String ontology) throws DBPersistenceException;
	
	public void removeTable4Ontology(String ontology) throws DBPersistenceException;
	
	public void createIndex(String ontology, String attribute) throws DBPersistenceException;

	public List<String> createIndex(String sentence) throws DBPersistenceException;

	public void dropIndex(String ontology, String indexName) throws DBPersistenceException;

	public List<String> getIndexes(String ontology) throws DBPersistenceException;
	
	public void validateIndexes(String ontology, String schema) throws DBPersistenceException;
	
	
}
