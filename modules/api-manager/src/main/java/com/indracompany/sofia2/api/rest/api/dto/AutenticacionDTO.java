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


public class AutenticacionDTO implements Cloneable, Serializable{
	
	@ApiModelProperty(value = "Tipo de Autenticación utilizada en la API")
    private String type;
	
	@ApiModelProperty(value = "Descripción de la autenticación")
    private String description;

    private ArrayList<ArrayList<AutenticacionAtribDTO>> autParametros;
	
	
	
	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<ArrayList<AutenticacionAtribDTO>> getAutParametros() {
		return autParametros;
	}


	public void setType(String tipo) {
		this.type = tipo;
	}


	public void setDescription(String descripcion) {
		this.description = descripcion;
	}
	
	public void setAutParametros(ArrayList<ArrayList<AutenticacionAtribDTO>> autParametros) {
		this.autParametros = autParametros;
	}


}
