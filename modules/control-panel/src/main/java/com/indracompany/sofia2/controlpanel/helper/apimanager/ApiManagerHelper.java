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
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.controlpanel.helper.apimanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiAuthentication;
import com.indracompany.sofia2.config.model.ApiAuthenticationAttribute;
import com.indracompany.sofia2.config.model.ApiAuthenticationParameter;
import com.indracompany.sofia2.config.model.ApiHeader;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.ApiQueryParameter;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserApi;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.ApiAuthenticationRepository;
import com.indracompany.sofia2.config.repository.ApiOperationRepository;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.UserApiRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.repository.UserTokenRepository;
import com.indracompany.sofia2.config.services.apimanager.authentication.AuthenticationJson;
import com.indracompany.sofia2.config.services.apimanager.operation.HeaderJson;
import com.indracompany.sofia2.config.services.apimanager.operation.OperationJson;
import com.indracompany.sofia2.config.services.apimanager.operation.QueryStringJson;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.multipart.ApiMultipart;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@Component
public class ApiManagerHelper {

	@Autowired
	ApiRepository apiRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserApiRepository userApiRepository;
	@Autowired
	ApiOperationRepository apiOperationRepository;
	@Autowired
	ApiAuthenticationRepository apiAuthenticationRepository;
	@Autowired
	UserTokenRepository userTokenRepository;
	
	
	@Autowired
	UserService userService;
	@Autowired
	OntologyRepository ontologyRepository;
	@Autowired
	AppWebUtils utils;

	@Value("${apimanager.services.baseUrl:http://localhost:8080/sib-api}${apimanager.services.apiEndpoint.path:/api}")
	private String apiManagerBaseUrl;
	
	// To populate the List Api Form
	public void populateApiManagerListForm(Model uiModel) {
		List<User> users = userRepository.findAll();
		
		User user = this.userService.getUser(utils.getUserId());
		
		uiModel.addAttribute("users", users);
		uiModel.addAttribute("states", Api.ApiStates.values());
		uiModel.addAttribute("auths", userApiRepository.findByUser(user));
	}
	
	// To populate the Create Api Form
	public void populateApiManagerCreateForm(Model uiModel) {
		List<Ontology> ontologies;
		User user = this.userService.getUser(utils.getUserId());
		
		if (utils.getRole().equals(Role.Type.ROLE_DEVELOPER.toString())){
			ontologies = ontologyRepository.findByUserAndActiveTrue(user);
		} else {
			ontologies = ontologyRepository.findByActiveTrue();
		}
		
		uiModel.addAttribute("endpointBase", apiManagerBaseUrl);
		
		uiModel.addAttribute("categories", Api.ApiCategories.values());
		uiModel.addAttribute("operations", new ArrayList());
		uiModel.addAttribute("ontologies", ontologies);
		uiModel.addAttribute("api", new Api());
	}
	
	// To populate de Api Create Form
	public void populateApiManagerUpdateForm(Model uiModel, String apiId) {
		
		//POPULATE API TAB
		populateApiManagerCreateForm(uiModel);
		
		Api api= apiRepository.findById(apiId);
		
		List<ApiAuthentication> apiAuthenticacion = apiAuthenticationRepository.findAllByApi(api);
		AuthenticationJson authenticacion = populateAuthenticationObject(apiAuthenticacion);
		List<ApiOperation> apiOperations = apiOperationRepository.findAllByApi(api);
		List<OperationJson> operations = populateOperationsObject(apiOperations);
		
		uiModel.addAttribute("authenticacion", authenticacion);
		uiModel.addAttribute("operations", operations);
		uiModel.addAttribute("api", api);
		
		//POPULATE AUTH TAB
		uiModel.addAttribute("clients", userApiRepository.findByApiId(apiId));
		uiModel.addAttribute("users", userRepository.findUserByIdentificationAndNoRol(utils.getUserId(), Role.Type.ROLE_ADMINISTRATOR.toString()));
	}
	
	public void populateApiManagerShowForm(Model uiModel, String apiId) {
		
		//POPULATE API TAB
		Api api= apiRepository.findById(apiId);
		
		List<ApiAuthentication> apiAuthenticacion = apiAuthenticationRepository.findAllByApi(api);
		AuthenticationJson authenticacion = populateAuthenticationObject(apiAuthenticacion);
		List<ApiOperation> apiOperations = apiOperationRepository.findAllByApi(api);
		List<OperationJson> operations = populateOperationsObject(apiOperations);

		uiModel.addAttribute("authenticacion", authenticacion);
		uiModel.addAttribute("operations", operations);
		uiModel.addAttribute("api", api);
		
		//POPULATE AUTH TAB
		uiModel.addAttribute("clients", userApiRepository.findByApiId(apiId));
	}
	
	private AuthenticationJson populateAuthenticationObject(List<ApiAuthentication> apiAuthentications) {
		if (apiAuthentications!=null && apiAuthentications.size()>0){
			ApiAuthentication apiAuthentication = apiAuthentications.get(0);
			AuthenticationJson authenticacion = new AuthenticationJson();
			authenticacion.setType(apiAuthentication.getType());
			authenticacion.setDescription(apiAuthentication.getDescription());
			
			List<List<Map<String, String>>> paramList = new ArrayList<List<Map<String,String>>>();
			
			for (ApiAuthenticationParameter apiparam : apiAuthentication.getApiAuthenticationParameters()) {
				List<Map<String, String>> params = new ArrayList<Map<String,String>>();
				
				for (ApiAuthenticationAttribute apiAttrib : apiparam.getApiautenticacionattribs()) {
					Map<String, String> attrib = new HashMap<String, String>();
					attrib.put("key", apiAttrib.getName());
					attrib.put("value", apiAttrib.getValue());
					
					params.add(attrib);
				}
				paramList.add(params);
			}
			authenticacion.setParams(paramList);
			
			return authenticacion;
		}
		return null;
	}

	private static List<OperationJson> populateOperationsObject(List<ApiOperation> apiOperations) {
		List<OperationJson> operations = new ArrayList<OperationJson>();

		for (ApiOperation operation : apiOperations) {
			OperationJson operationJson = new OperationJson();
			operationJson.setIdentification(operation.getIdentification());
			operationJson.setDescription(operation.getDescription());
			operationJson.setBasepath(operation.getBasePath());
			operationJson.setOperation(operation.getOperation().toString());
			operationJson.setPath(operation.getPath());
			operationJson.setEndpoint(operation.getEndpoint());
						
			List<HeaderJson> headers = new ArrayList<HeaderJson>();
			
			for (ApiHeader header : operation.getApiheaders()) {
				HeaderJson headerJson = new HeaderJson();
				headerJson.setName(header.getName());
				headerJson.setDescription(header.getHeader_description());
				headerJson.setType(header.getHeader_type());
				headerJson.setValue(header.getHeader_value());
				headerJson.setCondition(header.getHeader_condition());
				
				headers.add(headerJson);
			}
			
			operationJson.setHeaders(headers);
			
			List<QueryStringJson> queryStrings = new ArrayList<QueryStringJson>();
			
			for (ApiQueryParameter apiQueryParameter : operation.getApiqueryparameters()) {
				QueryStringJson queryStringJson = new QueryStringJson();
				queryStringJson.setName(apiQueryParameter.getName());
				queryStringJson.setDescription(apiQueryParameter.getDescription());
				queryStringJson.setDataType(apiQueryParameter.getDataType().toString());
				queryStringJson.setHeaderType(apiQueryParameter.getHeaderType().toString());
				queryStringJson.setValue(apiQueryParameter.getValue());
				queryStringJson.setCondition(apiQueryParameter.getCondition());
				
				queryStrings.add(queryStringJson);
			}

			operationJson.setQuerystrings(queryStrings);
			
			operations.add(operationJson);
		}
		return operations;

	}
	
	public Api apiMultipartMap(ApiMultipart apiMultipart) {
		Api api = new Api();
		
		api.setId(apiMultipart.getId());
		
		api.setIdentification(apiMultipart.getIdentification());
		api.setApiType(apiMultipart.getApiType());
		
		api.setPublic(apiMultipart.isPublic());
		api.setDescription(apiMultipart.getDescription());
		api.setCategory(Api.ApiCategories.valueOf(apiMultipart.getCategory()));
		api.setOntology(apiMultipart.getOntology());
		api.setEndpoint(apiMultipart.getEndpoint());
		api.setEndpointExt(apiMultipart.getEndpointExt());
		api.setMetaInf(apiMultipart.getMetaInf());
		api.setImageType(apiMultipart.getImageType());
		if (apiMultipart.getState()==null) {
			api.setState(Api.ApiStates.CREATED);
		} else {
			api.setState(Api.ApiStates.valueOf(apiMultipart.getState()));
		}
		
		api.setSsl_certificate(apiMultipart.isSsl_certificate());
		
		api.setUser(this.userService.getUser(utils.getUserId()));
		
		if (apiMultipart.getCachetimeout()!=null && !apiMultipart.getCachetimeout().equals("")){
			
			if (apiMultipart.getCachetimeout() > 1000 || apiMultipart.getCachetimeout() < 10) {
				//throw new Exception("Cache Limits exceded");
			} else {
				api.setCachetimeout(apiMultipart.getCachetimeout());
			}
		}
		
		if (apiMultipart.getApilimit()!=null && !apiMultipart.getApilimit().equals("")){
			if (apiMultipart.getApilimit() < 5) {
				api.setApilimit(5);
			} else if (apiMultipart.getApilimit() > 100) {
				api.setApilimit(100);
			} else {
				api.setApilimit(apiMultipart.getApilimit());
			}
		}
		
		api.setCreatedAt(apiMultipart.getCreatedAt());
		
		
//		if (apiMultipart.getImage()!=null && apiMultipart.getImage().getSize()>0 && !"image/png".equalsIgnoreCase(apiMultipart.getImage().getContentType()) && !"image/jpeg".equalsIgnoreCase(apiMultipart.getImage().getContentType())
//				&& !"image/jpg".equalsIgnoreCase(apiMultipart.getImage().getContentType()) && !"application/octet-stream".equalsIgnoreCase(apiMultipart.getImage().getContentType())){
//			return null;
//		}
//		
		
		try {
			api.setImage(apiMultipart.getImage().getBytes());
		} catch (Exception e) {
			//throw new Exception("ERROR IMAGEN");
		}
		
		api.setApiType(apiMultipart.getApiType());

		return api;
	}

	public void populateAutorizationForm(Model model) {
		model.addAttribute("userapi", new UserApi());
		model.addAttribute("users", userRepository.findUserByIdentificationAndNoRol(utils.getUserId(), Role.Type.ROLE_ADMINISTRATOR.toString()));
	
		if (utils.getRole().equals(Role.Type.ROLE_ADMINISTRATOR.toString())){
			model.addAttribute("apis", apiRepository.findApisNotPublicAndPublishedOrDevelopment());
			
			List<UserApi> clients = userApiRepository.findAll();
			model.addAttribute("clients", clients);
		} else if (utils.getRole().equals(Role.Type.ROLE_DEVELOPER.toString())){
			
			model.addAttribute("apis", apiRepository.findApisByUserNotPublicAndPublishedOrDevelopment(utils.getUserId()));

			List<UserApi> clients = userApiRepository.findByOwner(utils.getUserId());
			model.addAttribute("clients", clients);
		}
	}

	public void populateUserTokenForm(Model model) {
		User user = this.userService.getUser(utils.getUserId());
		model.addAttribute("tokens", userTokenRepository.findByUser(user));	
	}

}
