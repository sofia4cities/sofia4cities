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
package com.indracompany.sofia2.api.rest.api.dto;

import java.io.Serializable;
import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;



public class OperacionDTO implements Cloneable, Serializable{
	
	@ApiModelProperty(value = "Identificación de la Operacion")  
    private String identificacion;
    
	@ApiModelProperty(value = "Descripción de la Operacion")  
    private String descripcion;
    
	@ApiModelProperty(value = "Tipo de Operacion")
    private String operacion;
    
	@ApiModelProperty(value = "Enpoint Particular de la Operacion")
    private String endpoint;
    
	@ApiModelProperty(value = "Path de la Operacion")
    private String path;

	@ApiModelProperty(value = "Headers de la Operacion")
	private ArrayList<ApiHeaderDTO> headers;
	
	@ApiModelProperty(value = "QueryParams de la Operacion")
	private ArrayList<ApiQueryParameterDTO> queryParams;
	
	public String getIdentificacion() {
		return identificacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getOperacion() {
		return operacion;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getPath() {
		return path;
	}

	public ArrayList<ApiHeaderDTO> getHeaders() {
		return headers;
	}

	public ArrayList<ApiQueryParameterDTO> getQueryParams() {
		return queryParams;
	}


	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setPath(String path) {
		this.path = path;
	}
    
	public void setHeaders(ArrayList<ApiHeaderDTO> headersDTO) {
		this.headers = headersDTO;
	}

	public void setQueryParams(ArrayList<ApiQueryParameterDTO> queryParamsDTO) {
		this.queryParams = queryParamsDTO;
	}
}
