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

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.indra.sofia2.ssap.ssap.SSAPQueryType;
import com.indracompany.sofia2.api.service.Constants;
import com.indracompany.sofia2.api.service.exception.BadRequestException;
import com.indracompany.sofia2.api.service.exception.ForbiddenException;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.ApiQueryParameter;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.ApiOperationRepository;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiManagerService {

	@Autowired
	private ApiRepository apiRepository;

	@Autowired
	private ApiOperationRepository apiOperationRepository;

	@Autowired
	private UserService userService;

	/*
	 * @Autowired ApiSecurityService apiSecurityService;
	 */

	public User getUser(String userId) {
		return userService.getUser(userId);
	}

	public UserToken getUserToken(User userId) {
		return this.userService.getUserToken(userId);
	}

	public ApiRepository getApiRepository() {
		return apiRepository;
	}

	public void setApiRepository(ApiRepository apiRepository) {
		this.apiRepository = apiRepository;
	}

	public Api getApi(String pathInfo, User user) {
		Locale locale = LocaleContextHolder.getLocale();

		String apitipo = null;

		/*
		 * String apitipo=null; if (pathInfo.startsWith(openDataPath)) { pathInfo =
		 * pathInfo.substring(openDataPath.length()); apitipo="OData"; } else if
		 * (pathInfo.startsWith(webServicePath)) { pathInfo =
		 * pathInfo.substring(webServicePath.length()); apitipo = WEB_SERVICE_API; }
		 */
		String apiVersion = this.getApiVersion(pathInfo);
		String apiIdentifier = this.getApiIdentifier(pathInfo);

		// Recuperamos el API de BDC
		Api api = getApi(apiIdentifier, Integer.parseInt(apiVersion), apitipo);

		// Comprobamos si está disponible --> publicada, deprecada, en desarrollo o
		// creada y el usuario es propietario
		boolean disponible = apiSecurityService.checkApiAvailable(api, user);
		if (!disponible) {
			throw new ForbiddenException("com.indra.sofia2.api.service.wrongapistatus");
		}
		return api;
	}

	public String getApiVersion(String pathInfo) throws BadRequestException {
		Locale locale = LocaleContextHolder.getLocale();

		String apiVersion = pathInfo;

		if (apiVersion.startsWith("/")) {
			apiVersion = apiVersion.substring(1);
		}

		int slashIndex = apiVersion.indexOf('/');

		// Comprueba que existe el delimitador a partir del que vendrá el identificador
		// de la ontologia
		if (slashIndex == -1) {
			throw new BadRequestException("com.indra.sofia2.api.service.notvalidformat");
		}
		// Ya tenemos el campo de versión
		// Eliminamos la v inicial
		apiVersion = apiVersion.substring(0, slashIndex);
		if (apiVersion.startsWith("v")) {
			apiVersion = apiVersion.substring(1);
		}

		if (apiVersion == null || apiVersion.equals("")) {
			throw new BadRequestException("com.indra.sofia2.api.service.notapiversion");
		}

		return apiVersion;
	}

	public String getApiIdentifier(String pathInfo) throws BadRequestException {
		Locale locale = LocaleContextHolder.getLocale();

		String apiVersion = this.getApiVersion(pathInfo);

		// Acotamos el identificador del API quitando la versión
		String apiIdentifier = pathInfo.substring(pathInfo.indexOf(apiVersion + "/") + (apiVersion + "/").length());

		int slashIndex = apiIdentifier.indexOf('/');
		if (slashIndex == -1) {
			slashIndex = apiIdentifier.length();
		}

		apiIdentifier = apiIdentifier.substring(0, slashIndex);
		if (apiIdentifier == null || apiIdentifier.equals("")) {
			throw new BadRequestException("com.indra.sofia2.api.service.notapiid");
		}

		return apiIdentifier;
	}

	public Api getApi(String apiIdentifier, int apiVersion, String tipoapi)
			throws BadRequestException, ForbiddenException {
		Locale locale = LocaleContextHolder.getLocale();

		List<Api> api = null;

		// Recupera la entidad del API de BDC
		if (tipoapi != null) {
			api = apiRepository.findByIdentificationAndNumversionAndApiType(apiIdentifier, apiVersion, tipoapi);
		} else {
			api = apiRepository.findByIdentificationAndNumversion(apiIdentifier, apiVersion);
		}
		return api.get(0);
	}

	public User getUsuarioByApiToken(String headerToken) throws ForbiddenException {
		User user = userService.getUserByToken(headerToken);
		return user;
	}

	public boolean isPathQuery(String pathInfo) {

		String apiIdentifier = this.getApiIdentifier(pathInfo);

		// Acotamos el identificador del API quitando la versión
		String objectId = pathInfo.substring(pathInfo.indexOf(apiIdentifier) + (apiIdentifier).length());

		if (objectId.length() == 0 || !objectId.startsWith("/")) {
			return false;
		} else {
			return true;
		}
	}

	public ApiOperation getCustomSQL(String pathInfo, Api api) {

		String apiIdentifier = this.getApiIdentifier(pathInfo);

		// Acotamos el identificador del API quitando la versión
		String opIdentifier = pathInfo.substring(pathInfo.indexOf(apiIdentifier) + (apiIdentifier).length());
		if (opIdentifier.startsWith("\\") || opIdentifier.startsWith("/")) {
			opIdentifier = opIdentifier.substring(1);
		}

		List<ApiOperation> operaciones = apiOperationRepository.findByApiOrderByOperationDesc(api);

		// Se recorren las operaciones de la API, buscando las que coincidan por metodo
		// HTTP y por Path
		for (ApiOperation operacion : operaciones) {
			if (operacion.getId().equals(opIdentifier)) {
				return operacion;
			}
		}
		return null;
	}

	public HashMap<String, String> getCustomParametersValues(HttpServletRequest request,
			HashSet<ApiQueryParameter> queryParametersCustomQuery) {
		Locale locale = LocaleContextHolder.getLocale();

		HashMap<String, String> customqueryparametersvalues = new HashMap<String, String>();
		for (ApiQueryParameter customqueryparameter : queryParametersCustomQuery) {
			String paramvalue = request.getParameter("$" + customqueryparameter.getName());
			if (paramvalue == null) {
				// No se encuentra el valor del parametro configurado en la operacion en la
				// peticion
				throw new BadRequestException("com.indra.sofia2.api.service.wrongparametertype");
			} else {
				// Se comprueba que el valor es del tipo definido en la operacion
				if (customqueryparameter.getDataType().equals(Constants.API_TIPO_DATE)) {
					try {
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						df.parse(paramvalue);
						paramvalue = "'" + paramvalue + "'";
					} catch (Exception e) {
						// Esta definido como un string pero no se recibe un String
						Object parametros[] = { "$" + customqueryparameter.getName(), "Date" };
						throw new BadRequestException("com.indra.sofia2.api.service.wrongparametertype");
					}
				} else if (customqueryparameter.getDataType().equals(Constants.API_TIPO_STRING)) {
					try {
						paramvalue.toString();
						paramvalue = "'" + paramvalue + "'";
					} catch (Exception e) {
						// Esta definido como un string pero no se recibe un String
						Object parametros[] = { "$" + customqueryparameter.getName(), "String" };
						throw new BadRequestException("com.indra.sofia2.api.service.wrongparametertype");
					}
				} else if (customqueryparameter.getDataType().equals(Constants.API_TIPO_NUMBER)) {
					try {
						Double.parseDouble(paramvalue);
					} catch (Exception e) {
						// Esta definido como un Integer pero no se recibe un Integer
						Object parametros[] = { "$" + customqueryparameter.getName(), "Integer" };
						throw new BadRequestException("com.indra.sofia2.api.service.wrongparametertype");
					}
				} else if (customqueryparameter.getDataType().equals(Constants.API_TIPO_BOOLEAN)) {
					if (!paramvalue.equalsIgnoreCase("true") && !paramvalue.equalsIgnoreCase("false")) {
						Object parametros[] = { "$" + customqueryparameter.getName(), "Boolean" };
						throw new BadRequestException("com.indra.sofia2.api.service.wrongparametertype");
					}
				}
				// el parametro es de tipo correcto, se añade a la lista
				customqueryparametersvalues.put(customqueryparameter.getName(), paramvalue);
			}
		}
		return customqueryparametersvalues;
	}

	public String buildQuery(String queryDb, HashMap<String, String> queryParametersValues) {
		for (String param : queryParametersValues.keySet()) {
			queryDb = queryDb.replace("{$" + param + "}", queryParametersValues.get(param));
		}
		return queryDb;
	}

	public String getObjectidFromPathQuery(String pathInfo) {

		String apiIdentifier = this.getApiIdentifier(pathInfo);

		// Acotamos el identificador del API quitando la versión
		String objectId = pathInfo.substring(pathInfo.indexOf(apiIdentifier) + (apiIdentifier).length());

		if (!objectId.startsWith("/")) {
			return null;
		}

		objectId = objectId.substring(1);

		int slashIndex = objectId.indexOf('/');
		int parentIndex = objectId.indexOf('(');
		if (slashIndex == -1) {
			slashIndex = objectId.length();
		}
		// En el caso de OData se informa /ontologia(oid)/
		if (parentIndex != -1 && parentIndex < slashIndex) {
			slashIndex = parentIndex;
		}

		return objectId.substring(0, slashIndex);

	}

	public boolean isSQLLIKE(String query, String queryType) {
		if (query != null && query.length() > 0 && queryType != null && queryType.length() > 0) {
			return queryType.equals(SSAPQueryType.SQLLIKE.toString());
		}
		return false;
	}

	public String readPayload(HttpServletRequest request) {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader;
		try {
			reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/*
	 * public void checkApiLimit(Api api){ Locale locale =
	 * LocaleContextHolder.getLocale(); if(cacheApiLimitService.isLimit(api)){
	 * Integer timeLeft=cacheApiLimitService.getApiLimitTimeoutLeft(api.getId());
	 * LogService.getLogI18n(this.getClass()).debug("ApiService.debug.peticiones",
	 * api.getEndpoint(), cacheApiLimitService.getApiRequestsNumber(api.getId()));
	 * throw new ApiLimitException(log.getMensaje(locale,
	 * "com.indra.sofia2.api.service.apilimit",timeLeft)); } }
	 */

}
