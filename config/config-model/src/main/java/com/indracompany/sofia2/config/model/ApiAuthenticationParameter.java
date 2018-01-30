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
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

@Entity
@Table(name = "Api_Authentication_Parameter")
@Configurable
public class ApiAuthenticationParameter extends AuditableEntityWithUUID {

    private static final long serialVersionUID = 1L;
    
	@OneToMany(mappedBy = "autparamId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ApiAuthenticationAttribute> apiautenticacionattribs;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
   
    @JoinColumn(name = "autId", referencedColumnName = "ID", nullable = false)
    private ApiAuthentication autId;

	public Set<ApiAuthenticationAttribute> getApiautenticacionattribs() {
		return apiautenticacionattribs;
	}

	public void setApiautenticacionattribs(Set<ApiAuthenticationAttribute> apiautenticacionattribs) {
		this.apiautenticacionattribs = apiautenticacionattribs;
	}

	public ApiAuthentication getAutId() {
		return autId;
	}

	public void setAutId(ApiAuthentication autId) {
		this.autId = autId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}