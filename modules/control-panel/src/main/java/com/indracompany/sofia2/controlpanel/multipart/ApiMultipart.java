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
package com.indracompany.sofia2.controlpanel.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.multipart.MultipartFile;

import com.indracompany.sofia2.config.model.ApiComment;
import com.indracompany.sofia2.config.model.ApiUserAssessment;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;

import lombok.Getter;
import lombok.Setter;

public class ApiMultipart {
	
	private String id;
	
	private MultipartFile image;
	
	private Ontology ontology;
	private User user;
	private boolean ssl_certificate;
	private Set<ApiComment> comments;
	private Set<ApiUserAssessment> userAssessments;
	private String identification;
	private Integer numversion;
	private String description;
	private String category;
	private String endpoint;
	private String endpointExt;
	private String state;
	private String metaInf;
	private String imageType;
	private boolean isPublic;
	private Integer cachetimeout;
	private Integer apilimit;
	private String apiType;
	private Double assessment;
	
	private Date createdAt;
	private Date updatedAt;
	
	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Set<ApiUserAssessment> getUserAssessments() {
		return userAssessments;
	}

	public void setUserAssessments(Set<ApiUserAssessment> userAssessments) {
		this.userAssessments = userAssessments;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}
	
	public void setImage() {
		this.image = new MultipartFile() {		
			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {
			}
			@Override
			public boolean isEmpty() {
				return false;
			}
			@Override
			public long getSize() {
				return 0;
			}
			@Override
			public String getOriginalFilename() {
				return null;
			}
			@Override
			public String getName() {
				return null;
			}
			@Override
			public InputStream getInputStream() throws IOException {
				return null;
			}
			@Override
			public String getContentType() {
				return null;
			}
			@Override
			public byte[] getBytes() throws IOException {
				return new byte[0];
			}
		};
	}

}
