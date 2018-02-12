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
package com.indracompany.sofia2.api.rest.api.fiql;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.i18n.LocaleContextHolder;

import com.indracompany.sofia2.api.rest.api.dto.ApiQueryParameterDTO;
import com.indracompany.sofia2.config.model.ApiQueryParameter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class QueryParameterFIQL {

	

	static Locale locale = LocaleContextHolder.getLocale();

	private QueryParameterFIQL() {
		throw new AssertionError("Instantiating utility class...");
	}

	public static ArrayList<ApiQueryParameterDTO> toQueryParamDTO(Set<ApiQueryParameter> apiqueryparams) {
		ArrayList<ApiQueryParameterDTO> apiquertparamsDTO = new ArrayList<ApiQueryParameterDTO>();
		for (ApiQueryParameter apiqueryparam : apiqueryparams) {
			ApiQueryParameterDTO apiqueryparamDTO = toQueryParamDTO(apiqueryparam);
			apiquertparamsDTO.add(apiqueryparamDTO);
		}
		return apiquertparamsDTO;
	}

	public static ApiQueryParameterDTO toQueryParamDTO(ApiQueryParameter apiqueryparam) {
		ApiQueryParameterDTO apiqueryparamDTO = new ApiQueryParameterDTO();
		apiqueryparamDTO.setName(apiqueryparam.getName());
		apiqueryparamDTO.setDataType(apiqueryparam.getDataType());
		apiqueryparamDTO.setHeaderType(apiqueryparam.getHeaderType());
		apiqueryparamDTO.setDescription(apiqueryparam.getDescription());
		apiqueryparamDTO.setValue(apiqueryparam.getValue());
		return apiqueryparamDTO;
	}

	public static ApiQueryParameter copyProperties(ApiQueryParameterDTO apiqueryparamDTO) {
		ApiQueryParameter apiqueryparam = new ApiQueryParameter();

		if (apiqueryparamDTO.getName() == null || apiqueryparamDTO.getName().equals("")) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.QueryParamNameRequired");
		}
		if (apiqueryparamDTO.getDataType() == null || apiqueryparamDTO.getDataType().equals("")) {
			Object parametros[] = { apiqueryparamDTO.getName() };
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.QueryParamDataTypeRequired");
		}
		if (!isValidType(apiqueryparamDTO.getDataType().name())) {
			Object parametros[] = { apiqueryparamDTO.getName() };
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.QueryParamWrongDataType");
		}
		if (apiqueryparamDTO.getHeaderType() == null || apiqueryparamDTO.getHeaderType().equals("")) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.QueryParamConditionRequired");
		}
		if (!isValidHeaderType(apiqueryparamDTO.getHeaderType().name())) {
			Object parametros[] = { apiqueryparamDTO.getName() };
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.QueryParamWrongCondition");
		}
		if (!isValidTypeValue(apiqueryparamDTO.getDataType().name(), apiqueryparamDTO.getValue())) {
			Object parametros[] = { apiqueryparamDTO.getValue(), apiqueryparamDTO.getName(),
					apiqueryparamDTO.getDataType() };
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.QueryParamWrongDataTypeValue");
		}
		

		apiqueryparam.setName(apiqueryparamDTO.getName());
		apiqueryparam.setDataType(apiqueryparamDTO.getDataType());
		apiqueryparam.setHeaderType(apiqueryparamDTO.getHeaderType());
		apiqueryparam.setDescription(apiqueryparamDTO.getDescription());
		apiqueryparam.setValue(apiqueryparamDTO.getValue());

		return apiqueryparam;
	}

	
	private static boolean isValidTypeValue(String DataType, String Value) {
		if (DataType.equalsIgnoreCase(ApiQueryParameter.DataType.number.name())) {
			try {
				Integer.parseInt(Value);
			} catch (Exception e) {
				return false;
			}
		} else if (DataType.equalsIgnoreCase("boolean")) {
			try {
				Boolean.parseBoolean(Value);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}


	private static boolean isValidHeaderType(String DataType) {
		return (DataType.equalsIgnoreCase(ApiQueryParameter.HeaderType.body.name())
				|| DataType.equalsIgnoreCase(ApiQueryParameter.HeaderType.path.name()) 
				|| DataType.equalsIgnoreCase(ApiQueryParameter.HeaderType.query.name()));
	}

	private static boolean isValidType(String DataType) {
		return (DataType.equalsIgnoreCase(ApiQueryParameter.DataType.string.name()) 
				|| DataType.equalsIgnoreCase(ApiQueryParameter.DataType.array.name())
				|| DataType.equalsIgnoreCase(ApiQueryParameter.DataType.date.name())
				|| DataType.equalsIgnoreCase("boolean")
				|| DataType.equalsIgnoreCase(ApiQueryParameter.DataType.uri.name())
				|| DataType.equalsIgnoreCase(ApiQueryParameter.DataType.password.name())
				|| DataType.equalsIgnoreCase(ApiQueryParameter.DataType.binary.name())
				|| DataType.equalsIgnoreCase(ApiQueryParameter.DataType.email.name())
				|| DataType.equalsIgnoreCase(ApiQueryParameter.DataType.uuid.name())
				
				);
	}
}

