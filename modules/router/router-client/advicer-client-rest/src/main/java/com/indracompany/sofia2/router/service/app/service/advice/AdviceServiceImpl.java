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
package com.indracompany.sofia2.router.service.app.service.advice;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.commons.ssl.SSLUtil;
import com.indracompany.sofia2.router.client.RouterClient;
import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.AdviceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("adviceServiceImpl")
public class AdviceServiceImpl
		implements AdviceService, RouterClient<NotificationCompositeModel, OperationResultModel> {

	@Override
	public OperationResultModel advicePostProcessing(NotificationCompositeModel input) {
		return execute(input);
	}
	
	@Value("${sofia2.router.avoidsslverification:false")
	private boolean avoidSSLVerification;
	
	public AdviceServiceImpl() throws KeyManagementException, NoSuchAlgorithmException {
		if (avoidSSLVerification) {
			SSLUtil.turnOffSslChecking();
		}
	}

	@Override
	public OperationResultModel execute(NotificationCompositeModel input) {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("admin", "admin"));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		ObjectMapper mapper = new ObjectMapper();
		HttpEntity<String> domainToStart;
		try {
			domainToStart = new HttpEntity<String>(mapper.writeValueAsString(input), headers);
			OperationResultModel quote = restTemplate.postForObject(input.getUrl(), domainToStart,
					OperationResultModel.class);
			log.debug(quote.toString());
			return quote;
		} catch (Exception e) {
			log.error("Error while sending notification. Unable to parse notification to JSON. Cause = {}, message={}.",
					e.getCause(), e.getMessage());
			OperationResultModel resultModel = new OperationResultModel();
			resultModel.setErrorCode("500");
			resultModel.setMessage("Error while sending notification. Unable to parse notification to JSON.");
			return resultModel;
		}

	}

}
