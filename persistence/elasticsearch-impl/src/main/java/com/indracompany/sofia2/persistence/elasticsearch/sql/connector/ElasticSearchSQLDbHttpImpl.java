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
package com.indracompany.sofia2.persistence.elasticsearch.sql.connector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchUtil;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.http.BaseHttpClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Lazy
@Slf4j
public class ElasticSearchSQLDbHttpImpl extends BaseHttpClient implements ElasticSearchSQLDbHttpConnector {

	@Value("${sofia2.database.elasticsearch.sql.maxHttpConnections:10}")
	private int maxHttpConnections;
	@Value("${sofia2.database.elasticsearch.sql.maxHttpConnectionsPerRoute:10}")
	private int maxHttpConnectionsPerRoute;
	@Value("${sofia2.database.elasticsearch.sql.connectionTimeout.millis:10000}")
	private int connectionTimeout;
	@Value("${sofia2.database.elasticsearch.sql.connector.http.endpoint:http://localhost:9200}")
	private String endpoint;
	
	private int maxLimit =200;
	
	
	@PostConstruct
	public void init() {
		build(maxHttpConnections,maxHttpConnectionsPerRoute,connectionTimeout);
	}


	@Override
	public String queryAsJson(String query, int scroll_init, int scroll_end) throws DBPersistenceException {
		String url=null;
		String res=null;
		try {
			if (scroll_end<=0) scroll_end = maxLimit;
			if (scroll_init<=0) scroll_init = 0;
			
			if (scroll_init>0 && scroll_end>0) {
				String result = query.replaceFirst("select ", "");
				result = result.replaceFirst("SELECT ", "");
				 
				String scroll = "SELECT /*! USE_SCROLL("+scroll_init+","+scroll_end+")*/ ";
				query = scroll+result;
				
				//query = "SELECT /*! USE_SCROLL(5,10)*/ * FROM shakespeare";
				url = buildUrl(query);
			}
			else if (scroll_init==0 && scroll_end>0)
			{
				url = buildUrl(query, 0, scroll_end,true);
			}

		} catch (UnsupportedEncodingException e) {
			log.error("Error building URL", e);
			throw new DBPersistenceException("Error building URL", e);
		}
		String result = invokeSQLPlugin(url, ACCEPT_APPLICATION_JSON,null);
		
		try {
			 res =  ElasticSearchUtil.parseElastiSearchResult(result);
		} catch (JSONException e) {
			log.error("Error Parsing ES Result", e.getMessage());
			return result;
		}
		
		return res;
	}
	
	@Override
	public String queryAsJson(String query, int limit) throws DBPersistenceException {
		String url;
		String res=null;
		try {
			url = buildUrl(query, 0, limit,true);
		} catch (UnsupportedEncodingException e) {
			log.error("Error building URL", e);
			throw new DBPersistenceException("Error building URL", e);
		}
		String result = invokeSQLPlugin(url, ACCEPT_TEXT_CSV,null);
		try {
			 res =  ElasticSearchUtil.parseElastiSearchResult(result);
		} catch (JSONException e) {
			log.error("Error Parsing ES Result", e.getMessage());
			return result;
		}
		
		return res;
	}
	

	
	private String buildUrl(String query, int offset, int limit, boolean encode) throws UnsupportedEncodingException {
		String params = "" + query;
		if (offset > 0) {
			params += " OFFSET " + offset;
		}
		if (limit > 0) {
			params += " limit " + limit;
		}
		
		if (encode)
			params = URLEncoder.encode(params, "UTF-8");
		String url = this.endpoint  + "/_sql?sql=" + params;
		return url;
	}
	
	private String buildUrl(String query) throws UnsupportedEncodingException {
		String url = this.endpoint  + "/_sql?sql=" + URLEncoder.encode(query, "UTF-8");
		return url;
	}

	
}
