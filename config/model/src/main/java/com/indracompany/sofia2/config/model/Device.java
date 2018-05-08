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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DEVICE")
@Configurable
public class Device extends AuditableEntityWithUUID {

	public static enum StatusType {
		OK, ERROR, WARNING, COMPLETED, EXECUTED, UP, DOWN
	}

	@ManyToOne
	@JoinColumn(name = "CLIENT_PLATFORM_ID", referencedColumnName = "ID", nullable = false)
	@NotNull
	@Getter
	@Setter
	private ClientPlatform clientPlatform;

	@Column(name = "IDENTIFICATION", length = 255, unique = true, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String identification;

	@Column(name = "CONNECTED", nullable = false, columnDefinition = "boolean default false")
	@NotNull
	@Getter
	@Setter
	private boolean connected;

	@Column(name = "STATUS", length = 255, unique = false, nullable = true)
	@Getter
	@Setter
	private String status;

	public void setAccesEnum(Device.StatusType status) {
		this.status = status.toString();
	}

	@Column(name = "JSON_ACTIONS", nullable = true)
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter
	@Setter
	private String jsonActions;

	@Column(name = "PROTOCOL", nullable = true)
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter
	@Setter
	private String protocol;

	@Column(name = "SESSION_KEY", length = 512, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String sessionKey;

	@Column(name = "disabled", nullable = false, columnDefinition = "boolean default false")
	@NotNull
	@Getter
	@Setter
	private boolean disabled;

	@Column(name = "tags")
	@Getter
	@Setter
	private String tags;

}
