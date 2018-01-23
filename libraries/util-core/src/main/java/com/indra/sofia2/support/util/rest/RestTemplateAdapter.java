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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2015  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/

package com.indra.sofia2.support.util.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.indra.sofia2.support.util.encryption.StringEncryptor;
import com.indra.sofia2.support.util.encryption.StringEncryptorFactory;

@Deprecated
public class RestTemplateAdapter<T> {
	
	private static final Log logger = LogFactory.getLog(RestTemplateAdapter.class);
	
	private RestTemplate template;
	
	public RestTemplateAdapter(){
		this.template = new RestTemplate();
	}
	
	public RestTemplateAdapter(int readTimeout, int connectTimeout){
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(readTimeout);
		factory.setConnectTimeout(connectTimeout);
		this.template = new RestTemplate(factory);
	}
	
	/**
	 * Sends an HTTP GET request to the specified URL. No authentication headers will be generated
	 * @param url
	 * @return
	 */
	public T get(String url, Class<T> dtoClass) throws RestClientException {
		logger.debug("Sending HTTP GET request to URL " + url);
		return template.getForObject(url, dtoClass);
	}
	
	
	/**
	 * Sends an HTTP GET request to the specified URL. The given credentials will be used to perform
	 * basic Base64 authentication
	 * @param url
	 * @param username
	 * @param password
	 * @param dtoClass
	 * @return
	 * @throws HttpClientErrorException
	 */
	public T get(String url, String username, String password, Class<T> dtoClass)
		throws RestClientException {
		HttpEntity<String> request = buildAuthenticationRequest(url, username, password);
		ResponseEntity<T> response = template.exchange(url, HttpMethod.GET, request, dtoClass);
		return response.getBody();		
	}
	
	private HttpEntity<String> buildAuthenticationRequest(String url, String username, String password){
		StringEncryptor base64Encryptor = StringEncryptorFactory.buildEncryptor("Base64", "");
		String encodedCredentials;
		try {
			logger.debug("Encoding credentials");
			encodedCredentials = base64Encryptor.encrypt2String(username + ":" + password);
		} catch (Exception e){
			logger.fatal("Unable to encode credentials. This is probably a bug.", e);
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + encodedCredentials);
		logger.debug("Sending HTTP request with authentication token " + encodedCredentials + " to URL " + url);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		return request;
	}
	
}
