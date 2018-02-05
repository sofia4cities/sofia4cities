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
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ROLE_TYPE")
@Configurable
public class Role extends AuditableEntity {

	public static enum Type {
		USER, COLLABORATOR, ADMINISTRATOR, ANALYTICS, PARTNER, OPERATIONS, SYS_ADMIN, DEVOPS;
	}

	@Id
	@Column(name = "ID")
	@Getter
	@Setter
	private String id;

	public void setIdEnum(Role.Type role) {
		this.id = role.toString();
	}

	@OneToOne(cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "ROLE_PARENT", unique = false, nullable = true, insertable = true, updatable = true)
	@Getter
	@Setter
	private Role roleParent;

	@Column(name = "NAME", length = 24, unique = true, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String name;

	@Column(name = "DESCRIPTION", length = 255)
	@Getter
	@Setter
	private String description;

}
