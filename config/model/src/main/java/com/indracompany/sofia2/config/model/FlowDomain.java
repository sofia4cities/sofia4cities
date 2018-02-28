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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "FLOW_DOMAIN")
public class FlowDomain extends AuditableEntityWithUUID {

	@NotNull
	@Getter
	@Setter
	@Column(name = "IDENTIFICATION", length = 50, unique = true, nullable = false)
	private String identification;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter
	@Setter
	private User user;

	@NotNull
	@Getter
	@Setter
	@Column(name = "STATE", length = 20, nullable = false)
	private String state;

	@NotNull
	@Getter
	@Setter
	@Column(name = "PORT", nullable = false)
	private Integer port;

	@NotNull
	@Getter
	@Setter
	@Column(name = "SERVICE_PORT", nullable = false)
	private Integer servicePort;

	@NotNull
	@Getter
	@Setter
	@Column(name = "HOME", nullable = false)
	private String home;

	@NotNull
	@Getter
	@Setter
	@Column(name = "ACTIVE", nullable = false)
	private Boolean active;

}
