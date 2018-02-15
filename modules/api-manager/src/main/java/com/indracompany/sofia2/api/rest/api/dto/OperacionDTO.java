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

import com.indracompany.sofia2.config.model.ApiOperation.Type;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class OperacionDTO implements Cloneable, Serializable {

	@ApiModelProperty(value = "Identificación de la Operacion")
	@Getter
	@Setter
	private String identification;

	@ApiModelProperty(value = "Descripción de la Operacion")
	@Getter
	@Setter
	private String description;

	@ApiModelProperty(value = "Tipo de Operacion")
	@Getter
	@Setter
	private Type operation;
	

	@ApiModelProperty(value = "Enpoint Particular de la Operacion")
	@Getter
	@Setter
	private String endpoint;

	@ApiModelProperty(value = "Path de la Operacion")
	@Getter
	@Setter
	private String path;

	@ApiModelProperty(value = "Headers de la Operacion")
	@Getter
	@Setter
	private ArrayList<ApiHeaderDTO> headers;

	@ApiModelProperty(value = "QueryParams de la Operacion")
	@Getter
	@Setter
	private ArrayList<ApiQueryParameterDTO> queryParams;

}
