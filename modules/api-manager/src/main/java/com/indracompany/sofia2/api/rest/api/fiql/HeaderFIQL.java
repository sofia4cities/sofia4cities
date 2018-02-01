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

import com.indracompany.sofia2.api.rest.api.dto.ApiHeaderDTO;
import com.indracompany.sofia2.config.model.ApiHeader;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public final class HeaderFIQL {
	
	private static final String API_REQUERIDO = "REQUIRED";
	private static final String API_OPCIONAL = "OPTIONAL";
	private static final String API_CONSTANTE = "CONSTANT";
	private static final String API_NUMBER = "NUMBER";
	private static final String API_BOOLEAN = "BOOLEAN";
	private static final String API_STRING = "STRING";
	
	
	static Locale locale = LocaleContextHolder.getLocale();
	
	private HeaderFIQL() {
		throw new AssertionError("Instantiating utility class...");
	}

	public static ArrayList<ApiHeaderDTO> toHeaderDTO(Set<ApiHeader> apiheaders) {
		ArrayList<ApiHeaderDTO> headersDTO = new ArrayList<ApiHeaderDTO>();
		for (ApiHeader apiheader : apiheaders) {
			ApiHeaderDTO apiheaderDTO = toHeaderDTO(apiheader);	
			headersDTO.add(apiheaderDTO);
		}
		return headersDTO;
	}
	
	public static ApiHeaderDTO toHeaderDTO(ApiHeader apiheader) {
		ApiHeaderDTO apiheaderDTO = new ApiHeaderDTO();
		apiheaderDTO.setNombre(apiheader.getName());
		apiheaderDTO.setTipo(apiheader.getHeader_type());
		apiheaderDTO.setCondicion(apiheader.getHeader_condition());
		apiheaderDTO.setDescripcion(apiheader.getHeader_description());
		apiheaderDTO.setValor(apiheader.getHeader_value());
		return apiheaderDTO;
	}
	
	public static ApiHeader copyProperties(ApiHeaderDTO apiheaderDTO) {
		ApiHeader apiheader = new ApiHeader();
		
		if (apiheaderDTO.getNombre()==null || apiheaderDTO.getNombre().equals("")){
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.HeaderNombreRequired");
		}
		if (apiheaderDTO.getTipo()==null || apiheaderDTO.getTipo().equals("")){
			Object parametros[]={apiheaderDTO.getNombre()};
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.HeaderTipoRequired");
		}
		if (!isValidType(apiheaderDTO.getTipo())){
			Object parametros[]={apiheaderDTO.getNombre()};
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.HeaderWrongTipo");
		}
		if (apiheaderDTO.getCondicion()==null || apiheaderDTO.getCondicion().equals("")){
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.HeaderCondicionRequired");
		}
		if (!isValidCondition(apiheaderDTO.getCondicion())){
			Object parametros[]={apiheaderDTO.getNombre()};
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.HeaderWrongCondicion");
		}
		if (!isValidTypeValue(apiheaderDTO.getTipo(), apiheaderDTO.getValor())){
			Object parametros[]={apiheaderDTO.getValor(), apiheaderDTO.getNombre(), apiheaderDTO.getTipo()};
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.HeaderWrongTipoValue");
		}
		if (!isValidCondicionValue(apiheaderDTO.getCondicion(), apiheaderDTO.getValor())){
			Object parametros[]={apiheaderDTO.getValor(), apiheaderDTO.getNombre(), apiheaderDTO.getCondicion()};
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.HeaderWrongCondicionValue");
		}
		apiheader.setName(apiheaderDTO.getNombre());
		apiheader.setHeader_type(apiheaderDTO.getTipo());
		apiheader.setHeader_condition(apiheaderDTO.getCondicion());
		apiheader.setHeader_description(apiheaderDTO.getDescripcion());
		apiheader.setHeader_value(apiheaderDTO.getValor());
		
		return apiheader;
	}

	private static boolean isValidCondicionValue(String condicion, String valor) {
		if (condicion.equals(API_CONSTANTE)){
			return (valor!=null && !valor.equals(""));
		}
		return true;
	}

	private static boolean isValidTypeValue(String tipo, String valor) {
		if (tipo.equals(API_NUMBER)){
			try {
				Integer.parseInt(valor);
			} catch (Exception e) {
				return false;
			}
		} else if (tipo.equals(API_BOOLEAN)){
			try {
				Boolean.parseBoolean(valor);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private static boolean isValidCondition(String tipo) {
		return (tipo.equalsIgnoreCase(API_CONSTANTE)||tipo.equalsIgnoreCase(API_OPCIONAL)||tipo.equalsIgnoreCase(API_REQUERIDO));
	}

	private static boolean isValidType(String tipo) {
		return (tipo.equalsIgnoreCase(API_STRING)||tipo.equalsIgnoreCase(API_NUMBER)||tipo.equalsIgnoreCase(API_BOOLEAN));
	}

}
