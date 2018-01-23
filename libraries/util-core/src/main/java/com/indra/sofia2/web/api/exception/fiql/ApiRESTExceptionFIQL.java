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
package com.indra.sofia2.web.api.exception.fiql;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import com.indra.sofia2.web.api.exception.ApiRESTExceptionCodes;
import com.indra.sofia2.web.api.exception.dto.ApiRESTExceptionDTO;

public class ApiRESTExceptionFIQL {


	/**
	 * Log internacionalizado
	 */
	
	public static ApiRESTExceptionDTO toApiRESTExceptionDTO(Status status, ApiRESTExceptionCodes cod) {
		return toApiRESTExceptionDTO(status.getStatusCode(), cod);
	}
	
	public static ApiRESTExceptionDTO toApiRESTExceptionDTO(int status, ApiRESTExceptionCodes cod) {
		ApiRESTExceptionDTO dto = new ApiRESTExceptionDTO();
		Map<String, String> errorCodes = new HashMap<String, String>(); 
		
		errorCodes.put("errorCode", cod.getStringCode());
		errorCodes.put("statusText", cod.name());
		dto.setStatus(status);
		dto.setResponse(errorCodes);
		
		return dto;
	}	
	
	public static ApiRESTExceptionDTO toApiRESTExceptionDTO(Status status, ApiRESTExceptionCodes cod, String detail) {
		return toApiRESTExceptionDTO(status.getStatusCode(), cod, detail);
	}
	
	public static ApiRESTExceptionDTO toApiRESTExceptionDTO(int status, ApiRESTExceptionCodes cod, String detail) {
		ApiRESTExceptionDTO dto = new ApiRESTExceptionDTO();
		Map<String, String> errorCodes = new HashMap<String, String>(); 
		
		errorCodes.put("errorCode", cod.getStringCode());
		errorCodes.put("statusText", cod.name());
		if (detail != null && !"".equals(detail)) {
			errorCodes.put("detail", detail);
		}
		dto.setStatus(status);
		dto.setResponse(errorCodes);
		
		return dto;
	}
	
	
}
