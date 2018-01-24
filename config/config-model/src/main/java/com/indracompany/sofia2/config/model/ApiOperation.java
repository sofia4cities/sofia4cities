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
package com.indracompany.sofia2.config.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

@Entity
@Table(name = "API_OPERATION")
@Configurable
@SuppressWarnings("deprecation")
public class ApiOperation extends AuditableEntityWithUUID {
	
   private static final long serialVersionUID = 1L;


	@OneToMany(mappedBy = "apioperationId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ApiHeader> apiheaders;

	@OneToMany(mappedBy = "apioperationId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ApiQueryParameter> apiqueryparameters;

	@ManyToOne
    @JoinColumn(name = "apiId", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Api apiId;

	@Column(name = "IDENTIFICATION", length = 50,nullable = false)
    @NotNull
    private String identification;

	@Column(name = "DESCRIPTION", length = 512,nullable = false)
    @NotNull
    private String description;

	@Column(name = "OPERATION", length = 50,nullable = false)
    @NotNull
    private String operation;

	@Column(name = "ENDPOINT", length = 512)
    private String endpoint;

	@Column(name = "BASE_PATH", length = 512)
    private String basePath;

	@Column(name = "PATH", length = 512)
    private String path;

	public Set<ApiHeader> getApiheaders() {
		return apiheaders;
	}

	public void setApiheaders(Set<ApiHeader> apiheaders) {
		this.apiheaders = apiheaders;
	}

	public Set<ApiQueryParameter> getApiqueryparameters() {
		return apiqueryparameters;
	}

	public void setApiqueryparameters(Set<ApiQueryParameter> apiqueryparameters) {
		this.apiqueryparameters = apiqueryparameters;
	}

	public Api getApiId() {
		return apiId;
	}

	public void setApiId(Api apiId) {
		this.apiId = apiId;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
}
