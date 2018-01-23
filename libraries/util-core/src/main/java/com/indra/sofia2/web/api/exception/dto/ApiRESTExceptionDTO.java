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
package com.indra.sofia2.web.api.exception.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@ApiObject(name = "exception")
@XmlRootElement(name = "exception")

public class ApiRESTExceptionDTO {
	
	@ApiObjectField(description = "Resultado de la ejecución del servicio")
	private int status;
	
	@ApiObjectField(description = "Códigos de error asociados a la respuesta")
    private Map<String, String> response;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Map<String, String> getResponse() {
		return response;
	}

	public void setResponse(Map<String, String> response) {
		this.response = response;
	}
		
	public String toJson() {
		return new JSONSerializer().include("response").exclude("*.class").serialize(this);
	}

	public static ApiRESTExceptionDTO fromJsonToApiRESTExceptionDTO(String json) {
        return new JSONDeserializer<ApiRESTExceptionDTO>().use(null, ApiRESTExceptionDTO.class).deserialize(json);
    }

	public static String toJsonArray(Collection<ApiRESTExceptionDTO> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<ApiRESTExceptionDTO> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<ApiRESTExceptionDTO> fromJsonArrayToApiRESTExceptioes(String json) {
        return new JSONDeserializer<List<ApiRESTExceptionDTO>>().use(null, ArrayList.class).use("values", ApiRESTExceptionDTO.class).deserialize(json);
    }
}
