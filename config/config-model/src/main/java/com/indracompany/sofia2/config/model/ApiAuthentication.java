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
@Table(name = "API_AUTHENTICATION")
@Configurable
public class ApiAuthentication extends AuditableEntityWithUUID {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "autId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<ApiAuthenticationParameter> apiAuthenticationParameters;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "apiId", referencedColumnName = "ID", nullable = false)
	private Api apiId;

	@Column(name = "TYPE", length = 50, nullable = false)
	@NotNull
	private String type;

	@Column(name = "DESCRIPTION", length = 512, nullable = false)
	@NotNull
	private String description;

	public Set<ApiAuthenticationParameter> getApiAuthenticationParameters() {
		return apiAuthenticationParameters;
	}

	public void setApiAuthenticationParameters(Set<ApiAuthenticationParameter> apiAuthenticationParameters) {
		this.apiAuthenticationParameters = apiAuthenticationParameters;
	}

	public Api getApiId() {
		return apiId;
	}

	public void setApiId(Api apiId) {
		this.apiId = apiId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}