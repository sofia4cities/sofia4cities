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
package com.indra.sofia2.support.util.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestTemplateFactoryImpl implements RestTemplateFactory {

	@Autowired
	@Qualifier("restApiObjectMapper")
	private ObjectMapper objMapper;

	public RestTemplate getRestTemplate(int connectTimeout, int readTimeout) {
		HttpComponentsClientHttpRequestFactory requestsFactory = new HttpComponentsClientHttpRequestFactory();
		requestsFactory.setConnectTimeout(connectTimeout);
		requestsFactory.setReadTimeout(readTimeout);
		RestTemplate template = new RestTemplate(requestsFactory);
		return template;
	}

	public RestTemplate getRestTemplateWithJodaTimeSupport(int connectTimeout, int readTimeout) {
		RestTemplate template = getRestTemplate(connectTimeout, readTimeout);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
		jsonMessageConverter.setObjectMapper(objMapper);
		messageConverters.add(jsonMessageConverter);
		template.setMessageConverters(messageConverters);
		return template;
	}
}
