package com.indracompany.sofia2.persistence.mongodb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;
import com.indracompany.sofia2.persistence.services.QueryToolService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QueryToolServiceMongoDbImpl implements QueryToolService {

	@Autowired
	MongoBasicOpsDBRepository mongoRepo = null;
//	@Autowired
//	OntologyService ontologyService;

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {	
		try {
			return mongoRepo.queryNativeAsJson(ontology, query, offset, limit);
		} catch (Exception e) {
			throw new DBPersistenceException("Error executing query:" + e.getMessage(), e);
		}
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		try {
			return mongoRepo.querySQLAsJson(ontology, query, offset);
		} catch (Exception e) {
			throw new DBPersistenceException("Error executing query:" + e.getMessage(), e);
		}
	}

}
