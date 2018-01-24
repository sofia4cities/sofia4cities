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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DASHBOARD_TYPE")
@Configurable
public class DashboardType extends AuditableEntity{

	@Id
	@Column(name = "ID")
	@Getter @Setter private Integer id;   

	@Column(name = "MODEL",nullable = false)
	@NotNull
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String model;



	@Column(name = "JSONI18N")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String jsonI18n;

	@Column(name = "CUSTOMCSS")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String customCss;

	@Column(name = "CUSTOMJS")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String customJs;

	/*
    @OneToMany(mappedBy = "dashboardtipoId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Gruposdashboardtipo> gruposdashboardtipoes;

    public Set<Gruposdashboardtipo> getGruposdashboardtipoes() {
        return gruposdashboardtipoes;
    }

	public void setGruposdashboardtipoes(Set<Gruposdashboardtipo> gruposdashboardtipoes) {
        this.gruposdashboardtipoes = gruposdashboardtipoes;
    }

	 */

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter @Setter private User userId;

	@Column(name = "TYPE", length = 50, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String type;

	@Column(name = "PUBLIC",nullable = false)
	@NotNull
	@Getter @Setter private boolean isPublic;



}
