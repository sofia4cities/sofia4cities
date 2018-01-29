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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "header")
public class ApiHeaderDTO {
	
	
	@ApiModelProperty(value = "Nombre del Header")
    private String nombre;

	@ApiModelProperty(value = "Tipo de Header")
    private String tipo;
	
	@ApiModelProperty(value = "Descripción del Header")
    private String descripcion;
    
	@ApiModelProperty(value = "Valor del Header")
    private String valor;

	@ApiModelProperty(value = "Condición del Header")
    private String condicion;

	public String getNombre() {
		return nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getValor() {
		return valor;
	}

	public String getCondicion() {
		return condicion;
	}

	@XmlElement(required=true)
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@XmlElement(required=true)
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@XmlElement(required=true)
	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}    
}
