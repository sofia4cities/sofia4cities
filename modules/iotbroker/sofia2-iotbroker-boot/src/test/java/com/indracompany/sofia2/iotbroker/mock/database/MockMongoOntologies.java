package com.indracompany.sofia2.iotbroker.mock.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.mongodb.DBCollection;

public class MockMongoOntologies {

	@Autowired
	MongoTemplate springDataMongoTemplate;

	public <T> boolean createOntology(Class<T> ontology) {
		return createOntology(ontology.getSimpleName());
	}

	public <T> boolean createOntology(String ontology) {
		if (springDataMongoTemplate.collectionExists(ontology)) {
			springDataMongoTemplate.dropCollection(ontology);
		}
		final DBCollection created = springDataMongoTemplate.createCollection(Person.class);

		return (created != null);
	}
}
