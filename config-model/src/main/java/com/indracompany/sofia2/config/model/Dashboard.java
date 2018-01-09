/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "DASHBOARD")
@SuppressWarnings("deprecation")
public class Dashboard extends AuditableEntityWithUUID{


	@Column(name = "MODEL",nullable = false)
	@NotNull
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String model;
	/*
	@OneToMany(mappedBy = "dashboardId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_GRUPOSDASHBOARD_DASHBOARDID")
    private Set<Gruposdashboard> gruposdashboards;

	public Set<Gruposdashboard> getGruposdashboards() {
        return gruposdashboards;
    }

	public void setGruposdashboards(Set<Gruposdashboard> gruposdashboards) {
        this.gruposdashboards = gruposdashboards;
    }
	 */

	@Column(name = "USER_ID", length = 50,nullable = false)
	@NotNull
	@Getter @Setter private String userId;

	@Column(name = "NAME", length = 100,nullable = false)
	@NotNull
	@Getter @Setter private String name;

	@Column(name = "DASHBOARD_TYPE_ID", length = 50,nullable = false)
	@NotNull
	@Getter @Setter private String dashboardTypeId;



}
