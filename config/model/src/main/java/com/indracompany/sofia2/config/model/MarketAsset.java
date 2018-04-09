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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.annotation.LastModifiedDate;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "MARKET_ASSET")
public class MarketAsset extends AuditableEntityWithUUID {

	private static final long serialVersionUID = 1L;
	
	public static enum MarketAssetState {
		PENDING, APPROVED, REJECTED;
	}
	
	public static enum MarketAssetType {
		API, DOCUMENT, WEBPROJECT, APPLICATION;
	}
	
	public static enum MarketAssetPaymentMode {
		FREE;
	}
	

	@Column(name = "IDENTIFICATION", length = 50, unique = true, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String identification;
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter
	@Setter
	private User user;
	
	
	@Column(name = "IS_PUBLIC", nullable = false)
	@NotNull
	@Getter
	@Setter
	private boolean isPublic;
	
	@Column(name = "STATE", length = 20, nullable = false)
	@NotNull
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private MarketAssetState state;
	
	@Column(name = "MARKETASSET_TYPE", length = 50, nullable = false)
	@NotNull
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private MarketAssetType marketAssetType;
	
	@Column(name = "PAYMENT_MODE", length = 50, nullable = false)
	@NotNull
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private MarketAssetPaymentMode paymentMode;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "IMAGE", length=100000)
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Getter
	@Setter
	private byte[] image;
	
	@Column(name = "IMAGE_TYPE", length = 20)
	@Getter
	@Setter
	private String imageType;
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "CONTENT", length=100000000)
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Getter
	@Setter
	private byte[] content;

	@Column(name = "CONTENT_ID", length = 200)
	@Getter
	@Setter
	private String contentId;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "JSON_DESC", length = 1000000)
	@Lob
	@Getter
	@Setter
	private String jsonDesc;
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "REJECTION_REASON", length = 500)
	@Lob
	@Getter
	@Setter
	private String rejectionReason;
	
	@Column(name = "DELETED_AT",nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter  
	private Date deletedAt;

}
