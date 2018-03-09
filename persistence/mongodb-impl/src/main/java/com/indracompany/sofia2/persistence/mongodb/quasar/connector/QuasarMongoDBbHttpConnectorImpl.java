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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Lazy
@Slf4j
public class QuasarMongoDBbHttpConnectorImpl implements QuasarMongoDBbHttpConnector {

	public final static String ACCEPT_TEXT_CSV = "text/csv; columnDelimiter=|&rowDelimiter=;&quoteChar='&escapeChar=\\\\";
	public final static String ACCEPT_APPLICATION_JSON = "application/json";
	final static String CONTENT_TYPE_HEADER = "Content-Type";

	@Value("${sofia2.database.mongodb.quasar.maxHttpConnections:10}")
	private int maxHttpConnections;
	@Value("${sofia2.database.mongodb.quasar.maxHttpConnectionsPerRoute:10}")
	private int maxHttpConnectionsPerRoute;
	@Value("${sofia2.database.mongodb.quasar.connectionTimeout.millis:10000}")
	private int connectionTimeout;
	@Value("${sofia2.database.mongodb.quasar.connector.http.endpoint:http://localhost:18200/query/fs}")
	private String quasarEndpoint;
	@Value("${sofia2.database.mongodb.database:sofia2_s4c}")
	private String database;

	private PoolingClientConnectionManager cm;

	private BasicHttpParams httpParams;

	@PostConstruct
	public void init() {
		this.cm = new PoolingClientConnectionManager();
		this.httpParams = new BasicHttpParams();

		this.cm.setMaxTotal(maxHttpConnections);
		this.cm.setDefaultMaxPerRoute(maxHttpConnectionsPerRoute);
		HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
	}

	@Override
	public String queryAsJson(String query, int offset, int limit) throws Exception {
		String url = buildUrl(query, offset, limit);
		String result = invokeQuasar(url, ACCEPT_APPLICATION_JSON);
		return result;
	}

	@Override
	public String queryAsTable(String query, int offset, int limit) throws Exception {
		String url = buildUrl(query, offset, limit);
		String result = invokeQuasar(url, ACCEPT_TEXT_CSV);
		return result;

	}

	/*
	 * @Override public String query(String query, int offset, int limit, String
	 * accept) throws Exception { String url = buildUrl(query, offset, limit);
	 * 
	 * QuasarResponseDTO response = this.get(url, accept, "", false);
	 * 
	 * if (accept.equals(TEXT_CSV)) { IQuasarTableFormatter tableFormatter = null;
	 * if (null != formatter && formatter.trim().length() > 0) { tableFormatter =
	 * this.formatters.get(formatter); }
	 * 
	 * if (null == tableFormatter) { log.warn("Table formatter: " + formatter +
	 * " not found. Using defult format: " + accept); } else { if
	 * (response.getData().startsWith("<empty>")) { response.setData(""); }
	 * FormatResult formattedData = tableFormatter.format(response.getData(),
	 * response.getColumnDelimiter(), response.getRowDelimiter(),
	 * response.getQuoteChar(), response.getEscapeChar(), response.getCharset());
	 * 
	 * response.setContentType(formattedData.getContentType());
	 * response.setData(formattedData.getData()); response.setFormatted(true); } }
	 * 
	 * return response; }
	 */

	private String invokeQuasar(String endpoint, String accept) throws Exception {
		try (CloseableHttpClient httpClient = new DefaultHttpClient(this.cm, this.httpParams)) {
			
			httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.SECONDS);

			HttpGet httpGet = null;
			try {
				httpGet = createHttpGetRequest(endpoint, accept, null);
			} catch (Exception e) {
				log.error("Unable to send message: error detected while building POST request.", e);
			}
			if (httpGet != null) {
				HttpResponse httpResponse = null;
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
					String data = EntityUtils.toString(en);
					Header[] headers = httpResponse.getHeaders(CONTENT_TYPE_HEADER);
					return data;
				} catch (HttpHostConnectException e) {
					log.error("Error notifing message to endpoint: {}", endpoint, e);
					throw e;
				} catch (Exception e) {
					log.error("Error notifing message to endpoint: {}", endpoint, e);
					throw e;
				} finally {
					httpGet.releaseConnection();
				}

			} else {
				log.warn("Cannot notify message: the HTTPPost request cannot be build.");
				throw new Exception("Cannot notify message: the HTTPPost request cannot be build.");
			}
		} catch (Exception e) {
			log.warn("Error notifing message to endpoint: {}", endpoint, e);
			throw e;
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
