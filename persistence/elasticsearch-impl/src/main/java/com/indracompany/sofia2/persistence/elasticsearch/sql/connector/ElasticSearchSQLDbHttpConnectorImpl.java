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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;

import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchUtil;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
public class ElasticSearchSQLDbHttpConnectorImpl implements ElasticSearchSQLDbHttpConnector {

	public final static String ACCEPT_TEXT_CSV = "text/csv; columnDelimiter=|&rowDelimiter=;&quoteChar='&escapeChar=\\\\";
	public final static String ACCEPT_APPLICATION_JSON = "application/json";
	final static String CONTENT_TYPE_HEADER = "Content-Type";

	@Value("${sofia2.database.elasticsearch.sql.maxHttpConnections:10}")
	private int maxHttpConnections;
	@Value("${sofia2.database.elasticsearch.sql.maxHttpConnectionsPerRoute:10}")
	private int maxHttpConnectionsPerRoute;
	@Value("${sofia2.database.elasticsearch.sql.connectionTimeout.millis:10000}")
	private int connectionTimeout;
	@Value("${sofia2.database.elasticsearch.sql.connector.http.endpoint:http://localhost:9200}")
	private String endpoint;
	
	private int maxLimit =200;
	
	// private PoolingHttpClientConnectionManager cm1;
	// private RequestConfig config;
	private PoolingClientConnectionManager cm;
	private BasicHttpParams httpParams;

	@PostConstruct
	public void init() {
		/**
		 * Using the new way we obtain a java.io.EOFException: Unexpected end of ZLIB
		 * input stream It must be a bug in HttpClient
		 */
		// this.cm1 = new PoolingHttpClientConnectionManager();
		// this.cm1.setMaxTotal(maxHttpConnections);
		// this.cm1.setDefaultMaxPerRoute(maxHttpConnectionsPerRoute);
		// this.cm1.closeIdleConnections(0, TimeUnit.SECONDS);
		// config = RequestConfig.custom().setConnectTimeout(connectionTimeout).build();
		// config =
		// RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionTimeout).setSocketTimeout(connectionTimeout).build();

		this.cm = new PoolingClientConnectionManager();
		this.httpParams = new BasicHttpParams();
		this.cm.setMaxTotal(maxHttpConnections);
		this.cm.setDefaultMaxPerRoute(maxHttpConnectionsPerRoute);
		HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
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
		String result = invokeSQLPlugin(url, ACCEPT_APPLICATION_JSON);
		
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
		String result = invokeSQLPlugin(url, ACCEPT_TEXT_CSV);
		try {
			 res =  ElasticSearchUtil.parseElastiSearchResult(result);
		} catch (JSONException e) {
			log.error("Error Parsing ES Result", e.getMessage());
			return result;
		}
		
		return res;
	}
	
	


	private String invokeSQLPlugin(String endpoint, String accept) throws DBPersistenceException {
		HttpGet httpGet = null;
		CloseableHttpClient httpClient = null;
		HttpResponse httpResponse = null;
		String output = null;
		try {
			// httpClient = HttpClientBuilder.create().setConnectionManager(cm1).build();
			// httpClient =
			// HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			httpClient = new DefaultHttpClient(this.cm, this.httpParams);
			httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.SECONDS);
			httpGet = createHttpGetRequest(endpoint, accept, null);
		} catch (Exception e) {
			log.error("Unable to send message: error detected while building POST request.", e);
		}
		
		if (httpGet != null) {
			try {
				log.info("Send message: to {}.", endpoint);
				httpResponse = httpClient.execute(httpGet);
				if (httpResponse != null && httpResponse.getStatusLine() != null) {
					int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
					if (httpStatusCode != 200) {
						log.warn("Error notifying message to endpoint: {}. HTTP status code {}.", endpoint,
								httpStatusCode);
					}
				} else {
					log.error("Error notifying message to endpoint: {}. Malformed HTTP response.", endpoint);
				}
				HttpEntity en = httpResponse.getEntity();
				Header[] headers = httpResponse.getHeaders(CONTENT_TYPE_HEADER);
				output = EntityUtils.toString(en);
				return output;

			} catch (HttpHostConnectException e) {
				log.error("Error notifing message to endpoint: {}", endpoint, e);
				throw new DBPersistenceException(e);
			} catch (Exception e) {
				log.error("Error notifing message to endpoint: {}", endpoint, e);
				throw new DBPersistenceException(e);
			} finally {
				httpGet.releaseConnection();
			}

		} else {
			log.warn("Cannot notify message: the HTTPPost request cannot be build.");
			throw new DBPersistenceException("Cannot notify message: the HTTPPost request cannot be build.");
		}
	}

	private HttpGet createHttpGetRequest(String endpoint, String accept, String contentType) {
		HttpGet httpGet;
		try {
			httpGet = new HttpGet(new URI(endpoint));
			if (null != accept && accept.trim().length() > 0) {
				httpGet.setHeader("Accept", accept);
			}
			if (null != contentType && contentType.trim().length() > 0) {
				httpGet.setHeader("Content-Type", contentType);
			}
			httpGet.setHeader("Connection", "close");
		} catch (URISyntaxException e1) {
			throw new IllegalArgumentException("The URI of the endpoint is invalid.");
		}
		return httpGet;
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
