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
@Table(name = "api_user_assessment")
@SuppressWarnings("deprecation")
public class ApiUserAssessment extends AuditableEntityWithUUID {
    
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
   
    @JoinColumn(name = "ontologyId", referencedColumnName = "ID")
    private Ontology ontologyId;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
   
    @JoinColumn(name = "commentId", referencedColumnName = "ID")
    private ApiComment commentId;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
   
    @JoinColumn(name = "apiId", referencedColumnName = "ID")
    private Api apiId;

	@Column(name = "USER_ID", length = 50,nullable = false)
    @NotNull
    private String userId;

	@Column(name = "ASSESSMENT", precision = 10,nullable = false)
    @NotNull
    private Double assessment;

	@Column(name = "ASSESSMENT_DATE")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "M-")
    private Date fecha;

	public Ontology getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(Ontology ontologyId) {
		this.ontologyId = ontologyId;
	}

	public ApiComment getCommentId() {
		return commentId;
	}

	public void setCommentId(ApiComment commentId) {
		this.commentId = commentId;
	}

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

	public Double getAssessment() {
		return assessment;
	}

	public void setAssessment(Double assessment) {
		this.assessment = assessment;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	


	
}
