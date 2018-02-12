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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "API_OPERATION")
@Configurable
public class ApiOperation extends AuditableEntityWithUUID {

	private static final long serialVersionUID = 1L;
	
	public static enum Type {
		PUT, POST, GET, DELETE;
	}

	@ManyToOne
	@JoinColumn(name = "apiId", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Api api;

	@OneToMany(mappedBy = "apiOperation", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<ApiHeader> apiheaders;

	@OneToMany(mappedBy = "apiOperation", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<ApiQueryParameter> apiqueryparameters;

	@Column(name = "IDENTIFICATION", length = 50, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String identification;

	@Column(name = "DESCRIPTION", length = 512, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String description;

	@Column(name = "OPERATION", length = 50, nullable = false)
	@NotNull
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Type operation;

	@Column(name = "ENDPOINT", length = 512)
	@Getter
	@Setter
	private String endpoint;

	@Column(name = "BASE_PATH", length = 512)
	@Getter
	@Setter
	private String basePath;

	@Column(name = "PATH", length = 512)
	@Getter
	@Setter
	private String path;

}
