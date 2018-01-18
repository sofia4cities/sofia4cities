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
a * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.model;

import java.util.Calendar;

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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CLIENT_CONNECTION")
@Configurable
public class ClientConnection extends AuditableEntityWithUUID{

	/* Comentado para posterior implementacion
	
	@OneToMany(mappedBy = "idInstanciaKp", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_ASSET_INSTANCIAKP")
    private Set<Asset> assets;

	@OneToMany(mappedBy = "instanciaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_GRUPOSINSTANCIAKP_INSTANCIA")
    private Set<Gruposinstanciakp> gruposinstanciakps;

    
    public Set<Asset> getAssets() {
        return assets;
    }

	public void setAssets(Set<Asset> assets) {
        this.assets = assets;
    }

	public Set<Gruposinstanciakp> getGruposinstanciakps() {
        return gruposinstanciakps;
    }

	public void setGruposinstanciakps(Set<Gruposinstanciakp> gruposinstanciakps) {
        this.gruposinstanciakps = gruposinstanciakps;
    }

	*/
	

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CLIENT_PLATFORM_ID", referencedColumnName = "ID", nullable = false)
    @Getter @Setter private ClientPlatform clientPlatformId;
	
	@Column(name = "IDENTIFICATION", length = 255, unique = true,nullable = false)
    @NotNull
    @Getter @Setter private String identification;

	@Column(name = "LAST_IP", length = 39)
	@Getter @Setter private String lastIp;

	@Column(name = "IP_STRICT",nullable = false,columnDefinition = "boolean default false")
    @NotNull
    @Getter @Setter private boolean ipStrict;

	@Column(name = "LAST_CONNECTION")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
	@Getter @Setter private Calendar lastConnection;

	@Column(name = "STATIC_IP",nullable = false,columnDefinition = "boolean default false")
    @NotNull
    @Getter @Setter private boolean staticIp;

	

	

}
