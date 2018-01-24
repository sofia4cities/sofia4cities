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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

@Configurable
@Entity
@Table(name = "API")
@SuppressWarnings("deprecation")
public class Api extends AuditableEntityWithUUID {

    private static final long serialVersionUID = 1L;

   

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "IMAGE")
    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] image;
    
    @Column(name = "SSL_CERTIFICATE")
    @NotNull
    private boolean ssl_certificate;

	

	@OneToMany(mappedBy = "apiId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ApiComment> comments;

	@OneToMany(mappedBy = "apiId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ApiUserAssessment> userAssessments;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ontologyId", referencedColumnName = "ID")
    private Ontology ontologyId;

	@Column(name = "IDENTIFICATION", length = 50,nullable = false)
    @NotNull
    private String identification;

	@Column(name = "NUM_VERSION")
    private Integer numversion;

	@Column(name = "DESCRIPTION", length = 512,nullable = false)
    @NotNull
    private String description;

	@Column(name = "CATEGORY", length = 50)
    private String category;

	@Column(name = "ENDPOINT", length = 512)
    private String endpoint;

	@Column(name = "ENDPOINT_EXT", length = 512)
    private String endpointExt;

	@Column(name = "STATE", length = 10,nullable = false)
    @NotNull
    private String state;

	@Column(name = "META_INF", length = 512)
    private String metaInf;

	@Column(name = "IMAGE_TYPE", length = 20)
    private String imageType;

	

	@Column(name = "USER_ID", length = 50,nullable = false)
	@NotNull
    private String userId;

	@Column(name = "IS_PUBLIC",nullable = false)
    @NotNull
    private boolean isPublic;

	@Column(name = "CACHE_TIMEOUT")
    private Integer cachetimeout;

	@Column(name = "API_LIMIT")
    private Integer apilimit;

	@Column(name = "API_TYPE", length = 50)
    private String apiType;

	@Column(name = "ASSESSMENT", precision = 10)
    private Double assessment;

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public boolean isSsl_certificate() {
		return ssl_certificate;
	}

	public void setSsl_certificate(boolean ssl_certificate) {
		this.ssl_certificate = ssl_certificate;
	}

	public Set<ApiComment> getComments() {
		return comments;
	}

	public void setComments(Set<ApiComment> comments) {
		this.comments = comments;
	}

	

	public Ontology getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(Ontology ontologyId) {
		this.ontologyId = ontologyId;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public Integer getNumversion() {
		return numversion;
	}

	public void setNumversion(Integer numversion) {
		this.numversion = numversion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEndpointExt() {
		return endpointExt;
	}

	public void setEndpointExt(String endpointExt) {
		this.endpointExt = endpointExt;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMetaInf() {
		return metaInf;
	}

	public void setMetaInf(String metaInf) {
		this.metaInf = metaInf;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Integer getCachetimeout() {
		return cachetimeout;
	}

	public void setCachetimeout(Integer cachetimeout) {
		this.cachetimeout = cachetimeout;
	}

	public Integer getApilimit() {
		return apilimit;
	}

	public void setApilimit(Integer apilimit) {
		this.apilimit = apilimit;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public Double getAssessment() {
		return assessment;
	}

	public void setAssessment(Double assessment) {
		this.assessment = assessment;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Set<ApiUserAssessment> getUserAssessments() {
		return userAssessments;
	}

	public void setUserAssessments(Set<ApiUserAssessment> userAssessments) {
		this.userAssessments = userAssessments;
	}
	
	

}
