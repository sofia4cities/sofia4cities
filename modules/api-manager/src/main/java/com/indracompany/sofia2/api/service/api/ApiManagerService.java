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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.service.exception.BadRequestException;
import com.indracompany.sofia2.api.service.exception.ForbiddenException;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.ApiQueryParameter;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ApiOperationRepository;
import com.indracompany.sofia2.config.repository.ApiRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiManagerService {

	@Autowired
	private ApiRepository apiRepository;

	@Autowired
	private ApiOperationRepository apiOperationRepository;

	public ApiRepository getApiRepository() {
		return apiRepository;
	}

	public void setApiRepository(ApiRepository apiRepository) {
		this.apiRepository = apiRepository;
	}

	public Api getApi(String pathInfo, User user) {
		final String apitipo = null;
		final String apiVersion = this.getApiVersion(pathInfo);
		final String apiIdentifier = this.getApiIdentifier(pathInfo);

		final Api api = getApi(apiIdentifier, Integer.parseInt(apiVersion), apitipo);
		return api;
	}

	public String getApiVersion(String pathInfo) throws BadRequestException {

		String version = "1";
		final Pattern pattern = Pattern.compile("(.*)/api/v(.*)/");
		final Matcher matcher = pattern.matcher(pathInfo);
		if (matcher.find()) {
			final String param = matcher.group(2);
			version = param.substring(0, param.indexOf("/"));
			return version;
		}

		else {
			version = pathInfo;

			if (version.startsWith("/")) {
				version = version.substring(1);
			}

			final int slashIndex = version.indexOf('/');

			if (slashIndex == -1) {
				throw new BadRequestException("com.indra.sofia2.api.service.notvalidformat");
			}

			version = version.substring(0, slashIndex);
			if (version.startsWith("v")) {
				version = version.substring(1);
			}

			if (version == null || version.equals("")) {
				throw new BadRequestException("com.indra.sofia2.api.service.notapiversion");
			}

			return version;
		}
	}

	public String getApiIdentifier(String pathInfo) throws BadRequestException {

		final String apiVersion = this.getApiVersion(pathInfo);

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

		List<Api> api = null;

		if (tipoapi != null) {
			api = apiRepository.findByIdentificationAndNumversionAndApiType(apiIdentifier, apiVersion, tipoapi);
		} else {
			api = apiRepository.findByIdentificationAndNumversion(apiIdentifier, apiVersion);
		}
		return api.get(0);
	}

	public boolean isPathQuery(String pathInfo) {

		final String apiIdentifier = this.getApiIdentifier(pathInfo);
		final String objectId = pathInfo.substring(pathInfo.indexOf(apiIdentifier) + (apiIdentifier).length());

		if (objectId.length() == 0 || !objectId.startsWith("/")) {
			return false;
		} else {
			return true;
		}
	}

	public ApiOperation getCustomSQL(String pathInfo, Api api, String operation) {

		final String apiIdentifier = this.getApiIdentifier(pathInfo);

		String opIdentifier = pathInfo.substring(pathInfo.indexOf(apiIdentifier) + (apiIdentifier).length());
		if (opIdentifier.startsWith("\\") || opIdentifier.startsWith("/")) {
			opIdentifier = opIdentifier.substring(1);
			//opIdentifier= opIdentifier.replace("/", "");
		}
		
		opIdentifier= opIdentifier.replace("/", "");

		final List<ApiOperation> operaciones = apiOperationRepository.findByApiOrderByOperationDesc(api);

		String match = apiIdentifier + "_" + operation;

		if (!opIdentifier.equals("")) {
			match += "_" + opIdentifier;
		}

		for (final ApiOperation operacion : operaciones) {
			if (operacion.getIdentification().equals(opIdentifier)) {
				return operacion;
			}
		}
		return null;
	}

	public HashMap<String, String> getCustomParametersValues(HttpServletRequest request, String body,
			HashSet<ApiQueryParameter> queryParametersCustomQuery) {

		final HashMap<String, String> customqueryparametersvalues = new HashMap<>();
		for (final ApiQueryParameter customqueryparameter : queryParametersCustomQuery) {
			String paramvalue = request.getParameter(customqueryparameter.getName());
			if (paramvalue == null) {
				if (customqueryparameter.getHeaderType().name()
						.equalsIgnoreCase(ApiQueryParameter.HeaderType.body.name())) {
					paramvalue = body;
				}
			} else {
				if (customqueryparameter.getDataType().name()
						.equalsIgnoreCase(ApiQueryParameter.DataType.date.name())) {
					try {
						final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						df.parse(paramvalue);
						paramvalue = "\"" + paramvalue + "\"";
					} catch (final Exception e) {
						final Object parametros[] = { "$" + customqueryparameter.getName(), "Date" };
						throw new BadRequestException(
								"com.indra.sofia2.api.service.wrongparametertype " + parametros[0]);
					}
				} else if (customqueryparameter.getDataType().name()
						.equalsIgnoreCase(ApiQueryParameter.DataType.string.name())) {
					try {
						paramvalue.toString();
						paramvalue = "\"" + paramvalue + "\"";
					} catch (final Exception e) {
						final Object parametros[] = { "$" + customqueryparameter.getName(), "String" };
						throw new BadRequestException(
								"com.indra.sofia2.api.service.wrongparametertype" + parametros[0]);
					}
				} else if (customqueryparameter.getDataType().name()
						.equalsIgnoreCase(ApiQueryParameter.DataType.number.name())) {
					try {
						Double.parseDouble(paramvalue);
					} catch (final Exception e) {
						final Object parametros[] = { "$" + customqueryparameter.getName(), "Integer" };
						throw new BadRequestException(
								"com.indra.sofia2.api.service.wrongparametertype" + parametros[0]);
					}
				} else if (customqueryparameter.getDataType().name().equalsIgnoreCase("boolean")) {
					if (!paramvalue.equalsIgnoreCase("true") && !paramvalue.equalsIgnoreCase("false")) {
						final Object parametros[] = { "$" + customqueryparameter.getName(), "Boolean" };
						throw new BadRequestException(
								"com.indra.sofia2.api.service.wrongparametertype" + parametros[0]);
					}
				}
				customqueryparametersvalues.put(customqueryparameter.getName(), paramvalue);
			}
		}
		return customqueryparametersvalues;
	}

	public String buildQuery(String queryDb, HashMap<String, String> queryParametersValues) {
		for (final String param : queryParametersValues.keySet()) {
			final String value = queryParametersValues.get(param);
			queryDb = queryDb.replace("{$" + param + "}", queryParametersValues.get(param));
		}
		return queryDb;
	}

	public String getObjectidFromPathQuery(String pathInfo) {

		final String apiIdentifier = this.getApiIdentifier(pathInfo);

		String objectId = pathInfo.substring(pathInfo.indexOf(apiIdentifier) + (apiIdentifier).length());

		if (!objectId.startsWith("/")) {
			return null;
		}

		objectId = objectId.substring(1);

		int slashIndex = objectId.indexOf('/');
		final int parentIndex = objectId.indexOf('(');
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
			return queryType.startsWith("SQL");
		}
		return false;
	}

	public String readPayload(HttpServletRequest request) {
		final StringBuilder buffer = new StringBuilder();
		BufferedReader reader;
		try {
			reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	// TODO ALLL
	public String prepareOntologiaQuery(String ontologiaRecurso, String sqlQuery) {
		return "";
	}

}
