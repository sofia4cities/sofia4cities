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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

@Configurable
@Entity
@Table(name = "api_query_parameter")
@SuppressWarnings("deprecation")
public class ApiQueryParameter extends AuditableEntityWithUUID {
	
   private static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "apioperationId", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private ApiOperation apioperationId;

	@Column(name = "NAME", length = 50,nullable = false)
    @NotNull
    private String name;

	@Column(name = "QUERY_TYPE", length = 50,nullable = false)
    @NotNull
    private String tipo;

	@Column(name = "QUERY_DESCRIPTION", length = 512,nullable = false)
    @NotNull
    private String description;

	@Column(name = "QUERY_VALUE", length = 512)
    private String value;

	@Column(name = "QUERY_CONDITION", length = 50)
    private String condition;




	public ApiOperation getApioperationId() {
		return apioperationId;
	}

	public void setApioperationId(ApiOperation apioperationId) {
		this.apioperationId = apioperationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	
}
