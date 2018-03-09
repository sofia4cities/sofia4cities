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
package com.indracompany.sofia2.config.services.apimanager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiAuthentication;
import com.indracompany.sofia2.config.model.ApiAuthenticationAttribute;
import com.indracompany.sofia2.config.model.ApiAuthenticationParameter;
import com.indracompany.sofia2.config.model.ApiHeader;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.ApiQueryParameter;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserApi;
import com.indracompany.sofia2.config.repository.ApiAuthenticationAttributeRepository;
import com.indracompany.sofia2.config.repository.ApiAuthenticationParameterRepository;
import com.indracompany.sofia2.config.repository.ApiAuthenticationRepository;
import com.indracompany.sofia2.config.repository.ApiHeaderRepository;
import com.indracompany.sofia2.config.repository.ApiOperationRepository;
import com.indracompany.sofia2.config.repository.ApiQueryParameterRepository;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.repository.UserApiRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.apimanager.authentication.AuthenticationJson;
import com.indracompany.sofia2.config.services.apimanager.operation.HeaderJson;
import com.indracompany.sofia2.config.services.apimanager.operation.OperationJson;
import com.indracompany.sofia2.config.services.apimanager.operation.QueryStringJson;
import com.indracompany.sofia2.config.services.utils.ServiceUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiManagerServiceImpl implements ApiManagerService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	ApiRepository apiRepository;
	@Autowired
	UserApiRepository userApiRepository;
	@Autowired
	ApiOperationRepository apiOperationRepository;
	@Autowired
	ApiAuthenticationRepository apiAuthenticationRepository;
	@Autowired
	ApiAuthenticationParameterRepository apiAuthenticationParameterRepository;
	@Autowired
	ApiAuthenticationAttributeRepository apiAuthenticationAttributeRepository;
	@Autowired
	ApiQueryParameterRepository apiQueryParameterRepository;
	@Autowired
	ApiHeaderRepository apiHeaderRepository;
	@Autowired
	ServiceUtils serviceUtils;
	
	public List<Api> loadAPISByFilter(String apiId, String state, String userId) {
		List<Api> apis = null;
		// Gets context User
		if ((apiId==null || "".equals(apiId)) && (state==null || "".equals(state)) && (userId==null || "".equals(userId))) {
			apis = apiRepository.findAll();
		} else if (state==null || "".equals(state)){
			apis = apiRepository.findApisByIdentificationOrUser(apiId, userId);
		} else {
			apis = apiRepository.findApisByIdentificationOrStateOrUser(apiId, Api.ApiStates.valueOf(state), userId);
		}
		return apis;
	}

	@Override
	public Integer calculateNumVersion(String numversionData) {
		List<Api> apis = null;
		Integer version = 0;
		String identification;
		String apiType="";
		Map<String, String> obj;
		try {
			obj = new ObjectMapper().readValue(numversionData, new TypeReference<Map<String, String>>(){});
			identification = obj.get("identification");
			apiType = obj.get("apiType");

			if (StringUtils.isEmpty(apiType) || apiType.equals("null")) {
				apiType = null;
			}
			
			apis = apiRepository.findByIdentificationAndApiType(identification,apiType);
			for (Api api : apis) {
				if (api.getNumversion() > version) {
					version = api.getNumversion();
				}
			}
		} catch (JsonParseException e) {
			log.warn(e.getClass().getName() + ":" + e.getMessage());
		} catch (JsonMappingException e) {
			log.warn(e.getClass().getName() + ":" + e.getMessage());
		} catch (IOException e) {
			log.warn(e.getClass().getName() + ":" + e.getMessage());
		}
		return (version + 1);
	}
	
	@Override
	public String createApi(Api api, String operationsObject, String authenticationObject) {
			
		String numversionData = "{\"identificador\":\""+api.getIdentification()+"\",\"odata\":\""+api.getApiType()+"\"}";
		api.setNumversion(calculateNumVersion(numversionData));
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		List<OperationJson> operationsJson = null;
		
		try {
			operationsJson = objectMapper.readValue(operationsObject, new TypeReference<List<OperationJson>>(){});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AuthenticationJson authenticationJson = null;
		if (authenticationObject!=null && !authenticationObject.equals("")){
		}
			
		apiRepository.save(api);

		createAuthentication(api, authenticationJson);
		
		createOperations(api, operationsJson);	
	
		return api.getId();	
	}
	
	private void createAuthentication(Api api, AuthenticationJson authenticationJson) {
		if (authenticationJson!=null) {
			ApiAuthentication authentication = new ApiAuthentication();
			authentication.setType(authenticationJson.getType());
			authentication.setDescription(authenticationJson.getDescription());
			authentication.setApi(api);
			
			apiAuthenticationRepository.save(authentication);
			
			createHeaderParams(authentication, authenticationJson.getParams());
		}
	}

	private void createHeaderParams(ApiAuthentication authentication, List<List<Map<String, String>>> authParameters) {
		for (List<Map<String, String>> authParameterJson : authParameters) {
			ApiAuthenticationParameter authParam = new ApiAuthenticationParameter();
			authParam.setApiAuthentication(authentication);
			
			apiAuthenticationParameterRepository.save(authParam);
			
			createHeaderParamAtribs(authParam, authParameterJson);
		}
	}

	private void createHeaderParamAtribs(ApiAuthenticationParameter authparam, List<Map<String, String>> autHeaderParametersJson) {
		for (Map<String, String> autHeaderParameterJson : autHeaderParametersJson) {
			ApiAuthenticationAttribute autParameterAtrib = new ApiAuthenticationAttribute();
			autParameterAtrib.setName(autHeaderParameterJson.get("key"));
			autParameterAtrib.setValue(autHeaderParameterJson.get("value"));
			autParameterAtrib.setApiAuthenticationParameter(authparam);
			
			apiAuthenticationAttributeRepository.save(autParameterAtrib);
		}		
	}

	private void createOperations(Api api, List<OperationJson> operationsJson) {
		for (OperationJson operationJson : operationsJson) {
			ApiOperation operation = new ApiOperation();
			operation.setApi(api);
			operation.setIdentification(operationJson.getIdentification());
			operation.setDescription(operationJson.getDescription());
			operation.setOperation(ApiOperation.Type.valueOf(operationJson.getOperation()));
			if (operationJson.getBasepath()!=null && !operationJson.getBasepath().equals("")){
				String basepath = operationJson.getBasepath().replace("&amp;", "&");
				operation.setBasePath(basepath);
			}
			if (operationJson.getEndpoint()!=null && !operationJson.getEndpoint().equals("")){
				String endpoint = operationJson.getEndpoint().replace("&amp;", "&");
				operation.setEndpoint(endpoint);
			}
			String path=operationJson.getPath().replace("&amp;", "&");
			operation.setPath(path);
			
			apiOperationRepository.save(operation);
			
			if (operationJson.getQuerystrings()!=null && operationJson.getQuerystrings().size()>0) {
				createQueryStrings(operation, operationJson.getQuerystrings());
			}
			if (operationJson.getHeaders()!=null && operationJson.getHeaders().size()>0){
				createHeaders(operation, operationJson.getHeaders());
			}
		}
	}
	
	private void createQueryStrings(ApiOperation operation, List<QueryStringJson> querystrings) {
		for (QueryStringJson queryStringJson : querystrings) {
			ApiQueryParameter apiQueryParameter = new ApiQueryParameter();
			apiQueryParameter.setApiOperation(operation);
			apiQueryParameter.setName(queryStringJson.getName());
			apiQueryParameter.setDescription(queryStringJson.getDescription());
			apiQueryParameter.setDataType(ApiQueryParameter.DataType.valueOf(queryStringJson.getDataType()));
			apiQueryParameter.setHeaderType(ApiQueryParameter.HeaderType.valueOf(queryStringJson.getHeaderType()));
			apiQueryParameter.setValue(queryStringJson.getValue());
			apiQueryParameter.setCondition(queryStringJson.getCondition());

			apiQueryParameterRepository.save(apiQueryParameter);
		}
	}

	private void createHeaders(ApiOperation operation, List<HeaderJson> headers) {
		for (HeaderJson headerJson : headers) {
			ApiHeader header = new ApiHeader();
			header.setApiOperation(operation);
			header.setName(headerJson.getName());
			header.setHeader_description(headerJson.getDescription());
			header.setHeader_type(headerJson.getType());
			header.setHeader_value(headerJson.getValue());
			header.setHeader_condition(headerJson.getCondition());
			
			apiHeaderRepository.save(header);
		}
	}
	
	@Override
	public void updateApi(Api api, String deprecateApis, String operationsObject, String authenticationObject) {

		if (deprecateApis!=null && !deprecateApis.equals("")){
			deprecateApis(api.getIdentification());
		}
		
		Api apimemory = apiRepository.findById(api.getId());
		
		byte[] imagenOriginal = apimemory.getImage();
		
		apimemory.setPublic(api.isPublic());
		apimemory.setSsl_certificate(api.isSsl_certificate());
		apimemory.setDescription(api.getDescription());
		apimemory.setCategory(api.getCategory());
		

		apimemory.setEndpoint(api.getEndpoint());
		apimemory.setEndpointExt(api.getEndpointExt());
		apimemory.setMetaInf(api.getMetaInf());
		
		if (api.getCachetimeout()!=null && !api.getCachetimeout().equals("")){
			apimemory.setCachetimeout(api.getCachetimeout());
		} else {
			apimemory.setCachetimeout(null);
		}
		if (api.getApilimit()!=null && !api.getApilimit().equals("")){
			Integer apiLimit=api.getApilimit();
			if (apiLimit < 5) {
				apiLimit = 5;
			}
			if (apiLimit > 100) {
				apiLimit = 100;
			}
			apimemory.setApilimit(apiLimit);
		}else{
			apimemory.setApilimit(null);
		}
		
		apimemory.setState(api.getState());
		
		if (api.getImage()!=null && api.getImage().length>0){
			apimemory.setImage(api.getImage());
		} else {
			apimemory.setImage(imagenOriginal);
		}

		// Si el tipo es vacio o no es igual a un tipo conocido, se asigna null para ser retrocompatible
//		if ((apiMultipart.getTipoapi()!=null && apiMultipart.getTipoapi().trim().length()==0) ||
//			 (apiMultipart.getTipoapi()!=null && !apiMultipart.getTipoapi().equalsIgnoreCase("webservice") && !apiMultipart.getTipoapi().equalsIgnoreCase("odata4"))){
//				apimemory.setTipoapi(null);
//		} else {
//				apimemory.setTipoapi(apiMultipart.getTipoapi());
//		}

		apiRepository.save(apimemory);
		
		ObjectMapper objectMapper = new ObjectMapper();
				
		List<OperationJson> operationsJson = null;
		
		try {
			operationsJson = objectMapper.readValue(reformat(operationsObject), new TypeReference<List<OperationJson>>(){});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AuthenticationJson authenticationJson = null;
		
		if (authenticationObject!=null && !authenticationObject.equals("")){
			
//			if (autenticacion!=null && !autenticacion.equals("")){
//				JSONDeserializer<AutenticationJson> JSONDeserializer = new JSONDeserializer<AutenticationJson>().use(null, AutenticationJson.class).use("parametros.values", ArrayList.class);
//				 AutenticationJson autenticacionJson = JSONDeserializer.deserialize(autenticacion);	
//				 
//				 updateAutenticacion(apimemory, autenticacionJson);
//			}
		}
		
		updateOperations(apimemory, operationsJson);
	}

	private String reformat(String operationsObject) {
		if (operationsObject.indexOf(",")==0) {
			operationsObject = operationsObject.substring(1);
		}
		return operationsObject;
	}

	private void updateAuthentication(Api apimemory, AuthenticationJson authenticationJson) {
		List<ApiAuthentication> apiAutenticationlist = apiAuthenticationRepository.findAllByApi(apimemory);
		for (ApiAuthentication apiAuthentication : apiAutenticationlist) {
			apiAuthenticationRepository.delete(apiAuthentication);
		}
		createAuthentication(apimemory, authenticationJson);
	}

	private void updateOperations(Api api, List<OperationJson> operationsJson) {
		List<ApiOperation> apiOperations = apiOperationRepository.findAllByApi(api);
		for (ApiOperation apiOperation : apiOperations) {
			for (ApiHeader apiHeader : apiOperation.getApiheaders()) {
				apiHeaderRepository.delete(apiHeader);
			}
			for (ApiQueryParameter apiQueryParameter : apiOperation.getApiqueryparameters()) {
				apiQueryParameterRepository.delete(apiQueryParameter);
			}
			apiOperationRepository.delete(apiOperation);
		}
		createOperations(api, operationsJson);
	}
	
	private void deprecateApis(String apiId) {
		List<Api> apis = null;
		try {
			apis = apiRepository.findByIdentification(apiId);
			for (Api api : apis) {
				if (api.getState().equals(Api.ApiStates.PUBLISHED)){
					api.setState(Api.ApiStates.DEPRECATED);
					apiRepository.save(api);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public UserApi updateAuthorization(String apiId, String userId) {
		UserApi userApi = userApiRepository.findByApiIdAndUser(apiId, userId);

		if (userApi==null) {
			Api api = apiRepository.findById(apiId);
			User user = userRepository.findByUserId(userId);
			
			userApi = new UserApi();
			userApi.setApi(api);
			userApi.setUser(user);
			
			userApiRepository.save(userApi);
		}
		return userApi;
	}

	@Override
	public void removeAuthorizationById(String id) {
		UserApi userApi = userApiRepository.findById(id);
		userApiRepository.delete(userApi);
	}

	@Override
	public byte[] getImgBytes(String id) {
		Api api= apiRepository.findById(id);
		
		byte[] buffer=api.getImage();
		
		return buffer;
	}

	@Override
	public void updateState(String id, String state) {
		Api api = apiRepository.findById(id);
		api.setState(Api.ApiStates.valueOf(state));
		apiRepository.save(api);	
	}
}
