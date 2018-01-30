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

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "api_comment")
public class ApiComment extends AuditableEntityWithUUID {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "replyId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<ApiCommentReply> commentReplies;

	@ManyToOne
	@JoinColumn(name = "ontologyId", referencedColumnName = "ID")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Ontology ontologyId;

	@ManyToOne
	@JoinColumn(name = "apiId", referencedColumnName = "ID")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Api apiId;

	@Column(name = "TITLE", length = 512, nullable = false)
	@NotNull
	private String title;

	@Column(name = "COMMENT", length = 1024, nullable = false)
	@NotNull
	private String comment;

	@Column(name = "USER_ID", length = 50, nullable = false)
	@NotNull
	private String userId;

	@Column(name = "ASSESSMENT", precision = 10)
	private Double assessment;

	@Column(name = "COMMENT_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	private Calendar date;

	public Set<ApiCommentReply> getCommentReplies() {
		return commentReplies;
	}

	public void setCommentReplies(Set<ApiCommentReply> commentReplies) {
		this.commentReplies = commentReplies;
	}

	public Ontology getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(Ontology ontologyId) {
		this.ontologyId = ontologyId;
	}

	public Api getApiId() {
		return apiId;
	}

	public void setApiId(Api apiId) {
		this.apiId = apiId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

}
