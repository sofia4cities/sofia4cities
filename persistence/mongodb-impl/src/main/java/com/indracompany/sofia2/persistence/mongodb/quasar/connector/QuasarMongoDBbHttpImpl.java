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
package com.indracompany.sofia2.persistence.mongodb.quasar.connector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.http.BaseHttpClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Lazy
@Slf4j
public class QuasarMongoDBbHttpImpl extends BaseHttpClient implements QuasarMongoDBbHttpConnector {

	@Value("${sofia2.database.mongodb.quasar.maxHttpConnections:10}")
	private int maxHttpConnections;
	@Value("${sofia2.database.mongodb.quasar.maxHttpConnectionsPerRoute:10}")
	private int maxHttpConnectionsPerRoute;
	@Value("${sofia2.database.mongodb.quasar.connectionTimeout.millis:10000}")
	private int connectionTimeout;
	@Value("${sofia2.database.mongodb.quasar.connector.http.endpoint:http://localhost:18200/query/fs/}")
	private String quasarEndpoint;
	@Value("${sofia2.database.mongodb.database:sofia2_s4c}")
	private String database;

	
	@PostConstruct
	public void init() {
		build(maxHttpConnections,maxHttpConnectionsPerRoute,connectionTimeout);
	}

	@Override
	public String queryAsJson(String query, int offset, int limit) throws DBPersistenceException {
		String url;
		try {
			url = buildUrl(query, offset, limit);
		} catch (UnsupportedEncodingException e) {
			log.error("Error building URL", e);
			throw new DBPersistenceException("Error building URL", e);
		}
		String result = invokeSQLPlugin(url, ACCEPT_APPLICATION_JSON,null);
		return result;
	}

	@Override
	public String queryAsTable(String query, int offset, int limit) throws DBPersistenceException {
		String url;
		try {
			url = buildUrl(query, offset, limit);
		} catch (UnsupportedEncodingException e) {
			log.error("Error building URL", e);
			throw new DBPersistenceException("Error building URL", e);
		}
		String result = invokeSQLPlugin(url, ACCEPT_TEXT_CSV,null);
		return result;

	}

	/**
	 * FORMAT QUERY:
	 * /query/fs/[path]?q=[query]&offset=[offset]&limit=[limit]&var.[foo]=[value]
	 * 
	 * @param query
	 * @param offset
	 * @param limit
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String buildUrl(String query, int offset, int limit) throws UnsupportedEncodingException {
		String params = "q=" + URLEncoder.encode(query, "UTF-8");
		if (offset > 0) {
			params += "&offset=" + offset;
		}
		if (limit > 0) {
			params += "&limit=" + limit;
		}
		String url = this.quasarEndpoint + database + "/?" + params;
		return url;
	}

}
