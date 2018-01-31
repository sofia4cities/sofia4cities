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

public class ApiDTO implements Cloneable, Serializable{
	
	@ApiModelProperty(value = "Identificación de la Api")
    private String identificacion;
	
	@ApiModelProperty(value = "Número de versión de la Api")
    private Integer numversion;

	@ApiModelProperty(value = "PUBLICA/PRIVADA")
    private String tipo;
    
	@ApiModelProperty(value = "Categoría a la que pertenece la Api")
    private String categoria;
    
	@ApiModelProperty(value = "Si es o no Externa")
    private Boolean apiexterna;
    
	@ApiModelProperty(value = "Identificación de la ontología si el Api se disponibiliza desde una ontología existente")
    private String ontologiaId;
    
	@ApiModelProperty(value = "Endpoint de invocación del Api")
    private String endpoint;
    
	@ApiModelProperty(value = "Endpoint de invocación del Api Externo a la plataforma")
    private String endpointExt;
    
	@ApiModelProperty(value = "Descripción de la Api")
    private String descripcion;
    
	@ApiModelProperty(value = "Tags de Meta-inf asociados a la Api")
    private String metainf;
    
	@ApiModelProperty(value = "Tipo de Imagen")
    private String tipoimagen;
    
	@ApiModelProperty(value = "Estado del Api")
    private String estado;
    
	@ApiModelProperty(value = "Fecha de creación")
    private String fechaalta;
    
	@ApiModelProperty(value = "Usuario propietario de la Api")
    private String usuarioId;

	@ApiModelProperty(value = "Coleccion de operaciones de la Api")
    private ArrayList<OperacionDTO> operaciones;
	
	@ApiModelProperty(value = "Autenticación aplicable a la Api")
    private AutenticacionDTO autenticacion;	

	protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
	public String getIdentificacion() {
		return identificacion;
	}

	public AutenticacionDTO getAutenticacion() {
		return autenticacion;
	}

	public Integer getNumversion() {
		return numversion;
	}

	public String getTipo() {
		return tipo;
	}

	public String getCategoria() {
		return categoria;
	}

	public Boolean getApiexterna() {
		return apiexterna;
	}

	public String getOntologiaId() {
		return ontologiaId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getEndpointExt() {
		return endpointExt;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getMetainf() {
		return metainf;
	}

	public String getTipoimagen() {
		return tipoimagen;
	}

	public String getEstado() {
		return estado;
	}

	public String getFechaalta() {
		return fechaalta;
	}

	public String getUsuarioId() {
		return usuarioId;
	}
	
	public ArrayList<OperacionDTO> getOperaciones() {
		return operaciones;
	}


	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}


	public void setNumversion(Integer numversion) {
		this.numversion = numversion;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}


	public void setApiexterna(Boolean apiexterna) {
		this.apiexterna = apiexterna;
	}

	public void setOntologiaId(String ontologiaId) {
		this.ontologiaId = ontologiaId;
	}


	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setEndpointExt(String endpointExt) {
		this.endpointExt = endpointExt;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public void setMetainf(String metainf) {
		this.metainf = metainf;
	}

	public void setTipoimagen(String tipoimagen) {
		this.tipoimagen = tipoimagen;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	public void setFechaalta(String fechaalta) {
		this.fechaalta = fechaalta;
	}


	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}
    
	public void setOperaciones(ArrayList<OperacionDTO> operaciones) {
		this.operaciones = operaciones;
	}
	

	public void setAutenticacion(AutenticacionDTO autenticacion) {
		this.autenticacion = autenticacion;
	}
}
