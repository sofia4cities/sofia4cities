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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModelProperty;


@XmlRootElement(name = "autenticacion")
public class AutenticacionDTO {
	
	@ApiModelProperty(value = "Tipo de Autenticación utilizada en la API")
    private String tipo;
	
	@ApiModelProperty(value = "Descripción de la autenticación")
    private String descripcion;

    private ArrayList<ArrayList<AutenticacionAtribDTO>> autParametros;
	
	public String getTipo() {
		return tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public ArrayList<ArrayList<AutenticacionAtribDTO>> getAutParametros() {
		return autParametros;
	}

	@XmlElement(required=true)
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@XmlElement(required=true)
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public void setAutParametros(ArrayList<ArrayList<AutenticacionAtribDTO>> autParametros) {
		this.autParametros = autParametros;
	}


}
