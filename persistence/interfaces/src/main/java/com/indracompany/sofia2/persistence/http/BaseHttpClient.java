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
package com.indracompany.sofia2.persistence.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseHttpClient {

	public final static String ACCEPT_TEXT_CSV = "text/csv; columnDelimiter=|&rowDelimiter=;&quoteChar='&escapeChar=\\\\";
	public final static String ACCEPT_APPLICATION_JSON = "application/json";
	final static String CONTENT_TYPE_HEADER = "Content-Type";

	private int maxHttpConnections;
	private int maxHttpConnectionsPerRoute;
	private int connectionTimeout;

	private PoolingHttpClientConnectionManager cm;
	private CloseableHttpClient client;
	private RequestConfig config;

	public void build(int maxHttpConnections, int maxHttpConnectionsPerRoute, int connectionTimeout) {

		this.maxHttpConnections = maxHttpConnections;
		this.maxHttpConnectionsPerRoute = maxHttpConnectionsPerRoute;
		this.connectionTimeout = connectionTimeout;

		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(this.maxHttpConnections);
		cm.setDefaultMaxPerRoute(this.maxHttpConnectionsPerRoute);

		config = RequestConfig.custom().setConnectTimeout(this.connectionTimeout)
				.setConnectionRequestTimeout(this.connectionTimeout).setSocketTimeout(this.connectionTimeout).build();
	}

	public String invokeSQLPlugin(String endpoint, String accept, String contentType) throws DBPersistenceException {

		HttpGet httpGet = null;
		CloseableHttpResponse httpResponse = null;
		String output = null;
		try {

			client = HttpClientBuilder.create().disableContentCompression().setConnectionManager(cm).build();

			cm.closeIdleConnections(0, TimeUnit.SECONDS);
			httpGet = createHttpGetRequest(endpoint, accept, contentType);
			httpGet.setConfig(config);

			log.info("Send message: to {}.", endpoint);
			httpResponse = client.execute(httpGet);
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
			ResponseUtil.closeResponse(httpResponse);

		}

	}

	public String invokeSQLPlugin(String endpoint) throws DBPersistenceException {
		return invokeSQLPlugin(endpoint, ACCEPT_TEXT_CSV, null);
	}

	private HttpGet createHttpGetRequest(String endpoint, String accept, String contentType) {
		HttpGet httpGet;
		try {
			httpGet = new HttpGet(new URI(endpoint));
			if (null != accept && accept.trim().length() > 0) {
				httpGet.setHeader(HttpHeaders.ACCEPT, accept);
			}
			if (null != contentType && contentType.trim().length() > 0) {
				httpGet.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
			}
			httpGet.setHeader(HttpHeaders.CONNECTION, "close");
			// httpGet.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate");

		} catch (URISyntaxException e1) {
			throw new IllegalArgumentException("The URI of the endpoint is invalid.");
		}
		return httpGet;
	}

}
