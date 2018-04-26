/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.persistence.elasticsearch;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.elasticsearch.api.ESBaseApi;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESDeleteService;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;
import com.indracompany.sofia2.persistence.util.JSONPersistenceUtilsElasticSearch;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component("ElasticSearchManageDBRepository")
@Scope("prototype")
@Lazy
@Slf4j
public class ElasticSearchManageDBRepository implements ManageDBRepository {

	private static final String NOT_IMPLEMENTED_ALREADY = "Not Implemented Already";

	@Autowired
	ESBaseApi connector;

	@Autowired
	private ESDeleteService eSDeleteService;

	/*
	 * @Value("${sofia2.database.elasticsearch.database:es_sofia2_s4c}")
	 * 
	 * @Getter
	 * 
	 * @Setter private String database;
	 */
	@Value("${sofia2.database.elasticsearch.dump.path:null}")
	@Getter
	@Setter
	private String dumpPath;

	@Value("${sofia2.database.elasticsearch.elasticdump.path:null}")
	@Getter
	@Setter
	private String elasticDumpPath;

	@Value("${sofia2.database.elasticsearch.sql.connector.http.endpoint:null}")
	@Getter
	@Setter
	private String elasticSearchEndpoint;

	private String createTestIndex(String index) {
		String res = connector.createIndex(index);
		log.info("ElasticSearchManageDBRepository createTestIndex :" + index + " res: " + res);
		return res;
	}

	@Override
	public Map<String, Boolean> getStatusDatabase() throws DBPersistenceException {
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);
	}

	@Override
	public String createTable4Ontology(String ontology, String schema) throws DBPersistenceException {

		ontology = ontology.toLowerCase();
		try {
			String res = connector.createIndex(ontology);
			log.info("Index result :  " + res);

		} catch (Exception e) {
			log.info("Resource already exists ");
		}

		if (JSONPersistenceUtilsElasticSearch.isJSONSchema(schema)) {
			try {
				schema = JSONPersistenceUtilsElasticSearch.getElasticSearchSchemaFromJSONSchema(ontology, schema);
			} catch (JSONException e) {
				log.error("Cannot generate ElasticSearch effective Schema, turn to default " + e.getMessage(), e);
				schema = "{}";
			}
		}

		else if (schema.equals(""))
			schema = "{}";
		else if (schema.equals("{}")) {

			log.info("No schema is declared");
		}

		connector.createType(ontology, ontology, schema);

		return ontology;

	}

	@Override
	public List<String> getListOfTables() throws DBPersistenceException {

		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);

	}

	@Override
	public List<String> getListOfTables4Ontology(String ontology) throws DBPersistenceException {
		ontology = ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);

	}

	@Override
	public void removeTable4Ontology(String ontology) throws DBPersistenceException {

		ontology = ontology.toLowerCase();
		eSDeleteService.deleteAll(ontology, ontology);

	}

	@Override
	public void createIndex(String ontology, String attribute) throws DBPersistenceException {

		ontology = ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);

	}

	@Override
	public void createIndex(String ontology, String nameIndex, String attribute) throws DBPersistenceException {
		ontology = ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);

	}

	@Override
	public void createIndex(String sentence) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);
	}

	@Override
	public void dropIndex(String ontology, String indexName) throws DBPersistenceException {

		ontology = ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);

	}

	@Override
	public List<String> getListIndexes(String ontology) throws DBPersistenceException {

		ontology = ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);

	}

	@Override
	public String getIndexes(String ontology) throws DBPersistenceException {

		ontology = ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);
	}

	@Override
	public void validateIndexes(String ontology, String schema) throws DBPersistenceException {

		ontology = ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);

	}

	@Override
	public void exportToJson(String ontology, long startDateMillis) throws DBPersistenceException {
		String command = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM-hh-mm");
		Runtime r = Runtime.getRuntime();
		String query = " --searchBody {\\\"query\\\":{\\\"range\\\":{\\\"contextData.timestampMillis\\\":{\\\"lte\\\":"
				+ startDateMillis + "}}}}";

		command = this.elasticDumpPath + " --input=" + this.elasticSearchEndpoint + "/" + ontology.toLowerCase()
				+ " --output=" + this.dumpPath + ontology.toLowerCase() + format.format(new Date()) + ".json" + query
				+ " --delete=true";
		try {
			log.info("Executed command: " + command);
			r.exec(command).waitFor();
		} catch (IOException | InterruptedException e) {
			throw new DBPersistenceException("Could not execute command: " + command + e);
		}

	}

}
