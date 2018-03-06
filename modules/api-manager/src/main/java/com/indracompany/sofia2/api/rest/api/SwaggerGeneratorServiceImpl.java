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
package com.indracompany.sofia2.api.rest.api;

import java.net.InetAddress;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.fiql.ApiFIQL;
import com.indracompany.sofia2.api.rest.swagger.RestSwaggerReader;
import com.indracompany.sofia2.api.service.api.ApiServiceRest;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Swagger;

@Component("swaggerGeneratorServiceImpl")
public class SwaggerGeneratorServiceImpl implements SwaggerGeneratorService {

	@Autowired
	private ApiServiceRest apiService;
	
	@Autowired
	private ApiFIQL apiFIQL;
	
	@Value("${server.port:19090}")
	private String port;
	
	public Response getApi(String identificacion, String token) throws Exception {
		
		ApiDTO apiDto = apiFIQL.toApiDTO(apiService.findApi(identificacion, token));
		
		int version = apiDto.getVersion();
		String vVersion="v"+version;
		
		BeanConfig config = new BeanConfig();
		config.setHost("localhost:8080");
		config.setSchemes(new String[]{"http"});
		config.setBasePath("/api"+"/"+vVersion+"/"+identificacion);
		
		RestSwaggerReader reader = new RestSwaggerReader();

		Swagger swagger = reader.read(apiDto, config);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String json = mapper.writeValueAsString(swagger);
		
		return Response.ok(json).build();
	}

	public ApiServiceRest getApiService() {
		return apiService;
	}

	public void setApiService(ApiServiceRest apiService) {
		this.apiService = apiService;
	}

	public ApiFIQL getApiFIQL() {
		return apiFIQL;
	}

	public void setApiFIQL(ApiFIQL apiFIQL) {
		this.apiFIQL = apiFIQL;
	}

	@Override
	public Response getApiWithoutToken(String identificacion) throws Exception {
		ApiDTO apiDto = apiFIQL.toApiDTO(apiService.getApiMaxVersion(identificacion));
		
		int version = apiDto.getVersion();
		String vVersion="v"+version;
		String hostname = InetAddress.getLocalHost().getHostName();
		
		BeanConfig config = new BeanConfig();
		config.setHost(hostname+":"+port);
		config.setSchemes(new String[]{"http"});
		config.setBasePath("/server/api"+"/"+vVersion+"/"+identificacion);
		
		RestSwaggerReader reader = new RestSwaggerReader();

		Swagger swagger = reader.read(apiDto, config);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String json = mapper.writeValueAsString(swagger);
		
		return Response.ok(json).build();
	}

}
