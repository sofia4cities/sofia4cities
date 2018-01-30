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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

@Configurable
@Entity
@Table(name = "api_suscription")
public class ApiSuscription extends AuditableEntityWithUUID {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "apiId", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Api apiId;

	@Column(name = "USER_ID", length = 50, nullable = false)
	@NotNull
	private String userId;

	@Column(name = "INIT_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style = "M-")
	private Date initDate;

	@Column(name = "END_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style = "M-")
	private Date endDate;

	@Column(name = "ACTIVE", columnDefinition = "boolean default false")
	private Boolean isActive;

	public Api getApiId() {
		return apiId;
	}

	public void setApiId(Api apiId) {
		this.apiId = apiId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
