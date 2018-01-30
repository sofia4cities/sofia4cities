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

@Entity
@Table(name = "API_HEADER")
@Configurable
@SuppressWarnings("deprecation")
public class ApiHeader extends AuditableEntityWithUUID {
	
   private static final long serialVersionUID = 1L;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "apioperationId", referencedColumnName = "ID", nullable = false)
    private ApiOperation apioperationId;

	@Column(name = "NAME", length = 50,nullable = false)
    @NotNull
    private String name;

	@Column(name = "HEADER_TYPE", length = 50,nullable = false)
    @NotNull
    private String header_type;

	@Column(name = "HEADER_DESCRIPTION", length = 512,nullable = false)
    @NotNull
    private String header_description;

	@Column(name = "HEADER_VALUE", length = 512)
    private String header_value;

	@Column(name = "HEADER_CONDITION", length = 50)
    private String header_condition;

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

	public String getHeader_type() {
		return header_type;
	}

	public void setHeader_type(String header_type) {
		this.header_type = header_type;
	}

	public String getHeader_description() {
		return header_description;
	}

	public void setHeader_description(String header_description) {
		this.header_description = header_description;
	}

	public String getHeader_value() {
		return header_value;
	}

	public void setHeader_value(String header_value) {
		this.header_value = header_value;
	}

	public String getHeader_condition() {
		return header_condition;
	}

	public void setHeader_condition(String header_condition) {
		this.header_condition = header_condition;
	}

	



}
