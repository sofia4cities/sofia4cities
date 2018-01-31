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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USER")
@Configurable
public class User extends AuditableEntity {
	
	@Id
	@Column(name = "USER_ID", length = 50, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String userId;
	
	@Column(name = "EMAIL", length = 255,nullable = false)
	@NotNull
	@Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@[a-z0-9-]+(.[a-z0-9-]+)*(.[a-z]{2,3})$")
	@Getter @Setter private String email;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "ROLE_TYPE_ID", referencedColumnName = "ID", nullable = false)
	@Getter @Setter private RoleType roleTypeId;


	@Column(name = "PASSWORD", length = 128,nullable = false)
	@NotNull
	@Getter @Setter private String password;

	@Column(name = "DATE_CREATED",nullable = false)
	@NotNull
	@Temporal(TemporalType.DATE)
	@Getter @Setter private Date dateCreated;

	@Column(name = "ACTIVE",nullable = false)
	@NotNull
	@Getter @Setter private boolean active;

	@Column(name = "FULL_NAME", length = 255)
	@Getter @Setter private String fullName;

	@Column(name = "DATE_DELETED")
	@Temporal(TemporalType.DATE)
	@Getter @Setter private Date dateDeleted;


}
