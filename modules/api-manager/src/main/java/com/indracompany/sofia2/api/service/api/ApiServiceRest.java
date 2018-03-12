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
package com.indracompany.sofia2.api.service.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.dto.ApiHeaderDTO;
import com.indracompany.sofia2.api.rest.api.dto.ApiQueryParameterDTO;
import com.indracompany.sofia2.api.rest.api.dto.AutenticacionAtribDTO;
import com.indracompany.sofia2.api.rest.api.dto.AutenticacionDTO;
import com.indracompany.sofia2.api.rest.api.dto.OperacionDTO;
import com.indracompany.sofia2.api.rest.api.fiql.ApiFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.AuthenticationFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.HeaderFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.OperationFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.QueryParameterFIQL;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.Api.ApiStates;
import com.indracompany.sofia2.config.model.ApiAuthentication;
import com.indracompany.sofia2.config.model.ApiAuthenticationAttribute;
import com.indracompany.sofia2.config.model.ApiAuthenticationParameter;
import com.indracompany.sofia2.config.model.ApiHeader;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.ApiQueryParameter;
import com.indracompany.sofia2.config.model.ApiSuscription;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.ApiAuthenticationAttributeRepository;
import com.indracompany.sofia2.config.repository.ApiAuthenticationParameterRepository;
import com.indracompany.sofia2.config.repository.ApiAuthenticationRepository;
import com.indracompany.sofia2.config.repository.ApiHeaderRepository;
import com.indracompany.sofia2.config.repository.ApiOperationRepository;
import com.indracompany.sofia2.config.repository.ApiQueryParameterRepository;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.repository.ApiSuscriptionRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.repository.UserTokenRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiServiceRest {

	@Autowired
	private ApiFIQL apiFIQL;

	@Autowired
	private ApiRepository apiRepository;

	@Autowired
	private ApiOperationRepository apiOperationRepository;

	@Autowired
	private ApiHeaderRepository apiHeaderRepository;

	@Autowired
	private ApiQueryParameterRepository apiQueryParameterRepository;

	@Autowired
	private ApiAuthenticationRepository apiAuthenticationRepository;

	@Autowired
	private ApiAuthenticationParameterRepository apiAuthenticationParameterRepository;

	@Autowired
	private ApiAuthenticationAttributeRepository apiAuthenticationAttributeRepository;
	
	@Autowired
	private ApiSuscriptionRepository apiSuscriptionRepository;

	@Autowired
	private ApiSecurityService apiSecurityService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private UserTokenRepository userTokenRepository;

	public List<Api> findApisByUser(String userId, String token) {
		List<Api> apis = null;
		apis = apiRepository.findByUser(apiSecurityService.getUser(userId));
		return apis;
	}
	
	public Api getApi(String identificacionApi) {
		Api api = null;
		List<Api> apis = apiRepository.findByIdentification(identificacionApi);
		for (Api apiAux : apis) {
			if (apiAux.getState().name().equalsIgnoreCase(Api.ApiStates.PUBLISHED.name())) {
				api = apiAux;
			}
		}
		if (api == null) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.ApiNoPublicada");
		}
		return api;
	}

	public Api getApiMaxVersion(String identificacionApi) {
		Api api = null;
		List<Api> apis = apiRepository.findByIdentification(identificacionApi);
		for (Api apiAux : apis) {
			if (api == null || api.getNumversion() < apiAux.getNumversion()) {
				api = apiAux;
			}
		}
		if (api == null) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.ApiNoExiste");
		}
		return api;
	}

	public Api findApi(String identificacion, String token) {
		Api api = getApiMaxVersion(identificacion);
		if (api != null) {
			if (apiSecurityService.authorized(api, token)) {
				return api;
			}
		} else {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.ApiNoExiste");
		}
		return null;
	}

	public List<Api> findApis(String identificacion, String estado, String usuario, String token) {
		List<Api> apis = null;
		String userFiltro = null;
		if (usuario != null && !usuario.equals("")) {
			User users = apiSecurityService.getUser(usuario);
			if (users != null) {
				userFiltro = users.getUserId();
			} else {
				userFiltro = "USUARIOERROR";
			}
		}
		apis = apiRepository.findByIdentification(identificacion);
		return apis;
	}
	

	public Api changeState(String indentifier, ApiStates api, String token) {

		User user = apiSecurityService.getUserByApiToken(token);
		if (apiSecurityService.isAdmin(user)) {
			List<Api> apis = apiRepository.findByIdentification(indentifier);
			if (apis!=null) {
				Api theApi = apis.get(0);
				theApi.setState(api);
				apiRepository.saveAndFlush(theApi);
				return theApi;
			}
			else return null;
		}
		return null;
	}

	public void createApi(ApiDTO apiDTO, String token) {
		User user = apiSecurityService.getUserByApiToken(token);
		Api api = apiFIQL.copyProperties(apiDTO, user);

		Integer numVersion = 0;
		List<Api> apis = apiRepository.findByIdentification(api.getIdentification());
		for (Api apiBD : apis) {
			if (numVersion < apiBD.getNumversion()) {
				numVersion = apiBD.getNumversion();
			}
		}
		if (numVersion >= api.getNumversion()) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.wrongversionMin");
		}


		api.setUser(user);

		api.setState(Api.ApiStates.CREATED);
		apiRepository.saveAndFlush(api);
		createOperations(apiDTO.getOperations(), api);
		createAutenticacion(apiDTO.getAuthentication(), api);
	}

	public void updateApi(ApiDTO apiDTO, String token) {
		try {
			User user = apiSecurityService.getUserByApiToken(token);
			Api api = apiFIQL.copyProperties(apiDTO, user);

			Api apiUpdate = apiRepository
					.findByIdentificationAndNumversion(api.getIdentification(), api.getNumversion()).get(0);
			if (apiSecurityService.authorized(api, token)) {
				apiUpdate = apiFIQL.copyProperties(apiUpdate, api);
				apiRepository.saveAndFlush(apiUpdate);
				updateOperaciones(apiDTO.getOperations(), apiUpdate);
				updateAutenticacion(apiDTO.getAuthentication(), apiUpdate);

			} else {
				throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacion");
			}
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoApi");
		}
	}

	public void removeApi(ApiDTO apiDTO, String token) {
		try {
			User user = apiSecurityService.getUserByApiToken(token);
			Api api = apiFIQL.copyProperties(apiDTO, user);
			Api apiDelete = apiRepository
					.findByIdentificationAndNumversion(api.getIdentification(), api.getNumversion()).get(0);
			if (apiSecurityService.authorized(apiDelete, token)) {
				removeOperaciones(apiDelete);
				removeAutorizacion(apiDelete);
				apiRepository.delete(apiDelete);
			} else {
				throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacion");
			}
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoApi");
		}
	}

	public void removeApiByIdentificacionNumversion(String identificacion, String numversion, String token) {
		Integer version = null;
		try {
			version = Integer.parseInt(numversion);
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.wrongversion");
		}
		try {
			Api apiDelete = apiRepository.findByIdentificationAndNumversion(identificacion, version).get(0);
			if (apiSecurityService.authorized(apiDelete, token)) {
				removeOperaciones(apiDelete);
				removeAutorizacion(apiDelete);
				apiRepository.delete(apiDelete);
			} else {
				throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacion");
			}
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoApi");
		}
	}

	private void createOperations(ArrayList<OperacionDTO> operaciones, Api api) {
		for (OperacionDTO operacionDTO : operaciones) {
			ApiOperation operacion = OperationFIQL.copyProperties(operacionDTO);
			operacion.setIdentification(operacionDTO.getIdentification());
//			if (operacion.getIdentification()==null || "".equals(operacion.getIdentification()) ) {
//				String path = operacion.getPath();
//				if (path!=null && path.contains("?")) {
//					path = path.substring(0, path.indexOf("?"));
//					if  ("".equals(path)==false)
//						operacion.setIdentification(api.getIdentification()+"_"+operacion.getOperation()+"_"+path.replace("/", ""));
//				}
//
//				else {
//					operacion.setIdentification(api.getIdentification()+"_"+operacion.getOperation()+"_"+path.replace("/", "").replace("?", ""));
//				}
//			}
			operacion.setApi(api);
			apiOperationRepository.saveAndFlush(operacion);
			if (operacionDTO.getHeaders()!=null)
				createHeaders(operacion, operacionDTO.getHeaders());
			if (operacionDTO.getQueryParams()!=null)
				createQueryParams(operacion, operacionDTO.getQueryParams());
		}
	}

	private void updateOperaciones(ArrayList<OperacionDTO> operacionesDTO, Api api) {
		removeOperaciones(api);
		createOperations(operacionesDTO, api);
	}

	private void removeOperaciones(Api api) {
		List<ApiOperation> operaciones = apiOperationRepository.findByApiOrderByOperationDesc(api);
		for (ApiOperation operacion : operaciones) {
			apiOperationRepository.delete(operacion);
		}
	}

	private void createHeaders(ApiOperation operacion, ArrayList<ApiHeaderDTO> headersDTO) {
		for (ApiHeaderDTO headerDTO : headersDTO) {
			ApiHeader apiHeader = HeaderFIQL.copyProperties(headerDTO);
			apiHeader.setApiOperation(operacion);
			apiHeaderRepository.saveAndFlush(apiHeader);
		}
	}

	private void createQueryParams(ApiOperation operacion, ArrayList<ApiQueryParameterDTO> queryParamsDTO) {
		for (ApiQueryParameterDTO queryParamDTO : queryParamsDTO) {
			ApiQueryParameter apiQueryParam = QueryParameterFIQL.copyProperties(queryParamDTO);
			apiQueryParam.setApiOperation(operacion);

			apiQueryParameterRepository.saveAndFlush(apiQueryParam);

		}
	}

	private void createAutenticacion(AutenticacionDTO autenticacionDTO, Api api) {
		if (autenticacionDTO != null) {
			ApiAuthentication authentication = AuthenticationFIQL.copyProperties(autenticacionDTO);
			authentication.setApi(api);
			apiAuthenticationRepository.saveAndFlush(authentication);

			for (ArrayList<AutenticacionAtribDTO> parametroDTO : autenticacionDTO.getAuthParameters()) {
				ApiAuthenticationParameter parameter = new ApiAuthenticationParameter();
				parameter.setApiAuthentication(authentication);
				apiAuthenticationParameterRepository.saveAndFlush(parameter);
				for (AutenticacionAtribDTO atribDTO : parametroDTO) {
					ApiAuthenticationAttribute autparametroatrib = new ApiAuthenticationAttribute();
					autparametroatrib.setName(atribDTO.getName());
					autparametroatrib.setValue(atribDTO.getValue());
					autparametroatrib.setApiAuthenticationParameter(parameter);
					apiAuthenticationAttributeRepository.saveAndFlush(autparametroatrib);

				}
			}
		}
	}

	private void updateAutenticacion(AutenticacionDTO autenticacionDTO, Api apiUpdate) {
		removeAutorizacion(apiUpdate);
		createAutenticacion(autenticacionDTO, apiUpdate);

	}

	// TODO 
	private void removeAutorizacion(Api apiDelete) {

	}

	public List<ApiSuscription> findApiSuscriptions(String identificacionApi, String tokenUsuario) {
		if (identificacionApi==null){
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.IdentificacionApiRequerido");
		}
		if (tokenUsuario==null){
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.TokenUsuarioApiRequerido");
		}
		
		Api api =findApi(identificacionApi, tokenUsuario);
		List<ApiSuscription> suscripciones = null;
		
		User user = apiSecurityService.getUserByApiToken(tokenUsuario);
		suscripciones = apiSuscriptionRepository.findAllByApiAndUser(api, user);

		return suscripciones;
	}
	
	public List<ApiSuscription> findApiSuscriptions(Api api, User user) {		
		List<ApiSuscription> suscripciones = null;
		suscripciones = apiSuscriptionRepository.findAllByApiAndUser(api, user);
		return suscripciones;
	}
	
	public List<ApiSuscription> findApiSuscripcionesUser(String identificacionUsuario) {
		List<ApiSuscription> suscripciones = null;
		
		
		if (identificacionUsuario==null){
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.IdentificacionApiRequerido");
		}

		User suscriber = apiSecurityService.getUser(identificacionUsuario);
		suscripciones = apiSuscriptionRepository.findAllByUser(suscriber);	
		return suscripciones;
	}
	
	private boolean authorizedOrSuscriptor(Api api, String tokenUsuario, String suscriptor) {
		User user = apiSecurityService.getUserByApiToken(tokenUsuario);
		
		if (apiSecurityService.isAdmin(user) || user.getUserId().equals(api.getUser().getUserId()) || user.getUserId().equals(suscriptor)){
			return true;
		} else {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacionUsuario") ;
		}
	}
	
	public void createSuscripcion(ApiSuscription suscripcion, String tokenUsuario) {
		if (authorizedOrSuscriptor(suscripcion.getApi(), tokenUsuario, suscripcion.getUser().getUserId())){
			try {
				List<ApiSuscription> apiUpdate = findApiSuscriptions(suscripcion.getApi(), suscripcion.getUser());
				if (apiUpdate==null ) {
					apiSuscriptionRepository.save(suscripcion);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("com.indra.sofia2.web.api.services.SuscripcionNoExiste");
			}
		}else {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.NoAutorizado");
		}
	}
	
	public void updateSuscripcion(ApiSuscription suscripcion, String tokenUsuario) {
		if (authorizedOrSuscriptor(suscripcion.getApi(), tokenUsuario, suscripcion.getUser().getUserId())){
			try {
				List<ApiSuscription> apiUpdate = findApiSuscriptions(suscripcion.getApi(), suscripcion.getUser());
				if (apiUpdate!=null && apiUpdate.size()>0 ) {
					apiUpdate.get(0).setIsActive(suscripcion.getIsActive());
					apiUpdate.get(0).setInitDate(suscripcion.getInitDate());
					apiUpdate.get(0).setEndDate(suscripcion.getEndDate());
				}
				
				apiSuscriptionRepository.save(apiUpdate.get(0));
			} catch (Exception e) {
				throw new IllegalArgumentException("com.indra.sofia2.web.api.services.SuscripcionNoExiste");
			}
		}
	}
	
	public void removeSuscripcionByUserAndAPI(ApiSuscription suscripcion, String tokenUsuario) {
		if (authorizedOrSuscriptor(suscripcion.getApi(), tokenUsuario, suscripcion.getUser().getUserId())){
			try {
				List<ApiSuscription> apiUpdate = findApiSuscriptions(suscripcion.getApi(), suscripcion.getUser());
				if (apiUpdate!=null && apiUpdate.size()>0 ) {
					apiSuscriptionRepository.delete(apiUpdate.get(0));
				}

			} catch (Exception e) {
				throw new IllegalArgumentException("com.indra.sofia2.web.api.services.SuscripcionNoExiste");
			}
		}
	}
	
	public UserToken findTokenUserByIdentification(String identificacion, String tokenUsuario) {

		UserToken token = null;
		if (identificacion==null){
			throw new IllegalArgumentException("IdentificacionScriptRequerido");
		}
		
		User user = apiSecurityService.getUserByApiToken(tokenUsuario);
		
		if (apiSecurityService.isAdmin(user) || user.getUserId().equals(identificacion)){
			User userToTokenize = apiSecurityService.getUser(identificacion);
			token = apiSecurityService.getUserToken(userToTokenize, identificacion);
		} else {
			throw new AuthorizationServiceException("NoPermisosOperacionUsuario") ;
		}
		return token;
	}
	
	public UserToken addTokenUsuario(String identificacion, String tokenUsuario) {
		UserToken token = null;
		if (identificacion==null){
			throw new IllegalArgumentException("IdentificacionScriptRequerido");
		}

		User user = apiSecurityService.getUserByApiToken(tokenUsuario);
		
		if (apiSecurityService.isAdmin(user) || user.getUserId().equals(identificacion)){
					
			User userToTokenize = apiSecurityService.getUser(identificacion);
			
			token = apiSecurityService.getUserToken(userToTokenize, tokenUsuario);
			if (token==null)
				token = init_Token(userToTokenize); 
			else {
				token = init_Token(userToTokenize); 
			}
				
		} else {
			throw new AuthorizationServiceException("NoPermisosOperacionUsuario") ;
		}
		return token;
	}

	public UserToken generateTokenUsuario(String identificacion, String tokenUsuario) {
	
		UserToken token = null;
		if (identificacion==null){
			throw new IllegalArgumentException("IdentificacionScriptRequerido");
		}

		User user = apiSecurityService.getUserByApiToken(tokenUsuario);
		
		if (apiSecurityService.isAdmin(user) || user.getUserId().equals(identificacion)){
					
			User userToTokenize = apiSecurityService.getUser(identificacion);
			
			token = apiSecurityService.getUserToken(userToTokenize, tokenUsuario);

			token = init_Token(userToTokenize); 
				
		} else {
			throw new AuthorizationServiceException("NoPermisosOperacionUsuario") ;
		}
		return token;
	}
	
	public String generateTokenUsuario() {
		String candidateToken="";
		candidateToken=UUID.randomUUID().toString();
		return candidateToken;
	}
	
	public UserToken init_Token(User user) {

		UserToken userToken = new UserToken();
		
		userToken.setToken(generateTokenUsuario());
		userToken.setUser(user);
		userToken.setCreatedAt(Calendar.getInstance().getTime());
			
		userTokenRepository.save(userToken);
		return userToken;

	}

}