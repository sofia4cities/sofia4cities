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
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

@Configurable
@Entity
@Table(name = "api_comment_reply")
@SuppressWarnings("deprecation")
public class ApiCommentReply extends AuditableEntityWithUUID{

	private static final long serialVersionUID = 1L;
	

	@ManyToOne
    @JoinColumn(name = "COMMENT_ID", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private ApiComment commentId;

	@ManyToOne
    @JoinColumn(name = "REPLY_ID", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private ApiComment replyId;

	

	public ApiComment getCommentId() {
		return commentId;
	}

	public void setCommentId(ApiComment commentId) {
		this.commentId = commentId;
	}

	public ApiComment getReplyId() {
		return replyId;
	}

	public void setReplyId(ApiComment replyId) {
		this.replyId = replyId;
	}

	

	
}
