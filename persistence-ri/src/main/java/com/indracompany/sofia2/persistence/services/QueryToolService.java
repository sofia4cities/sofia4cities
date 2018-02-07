package com.indracompany.sofia2.persistence.services;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;

public interface QueryToolService {

	String queryNativeAsJson(String ontology, String query, int offset, int limit) throws DBPersistenceException;

	String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException;

}
