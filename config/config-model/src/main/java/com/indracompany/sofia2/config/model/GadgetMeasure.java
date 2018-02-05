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
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

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

@Entity
@Table(name = "GADGET_MEASURE")
@Configurable
public class GadgetMeasure extends AuditableEntityWithUUID {

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "GADGET_ID", referencedColumnName = "ID", nullable = false)
	@Getter
	@Setter
	private Gadget gadget;

	@Column(name = "ATTRIBUTE", length = 200, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String attribute;

	@Column(name = "ATTRIBUTE2", length = 200)
	@Getter
	@Setter
	private String attribute2;

	@Column(name = "ATTRIBUTE3", length = 200)
	@Getter
	@Setter
	private String attribute3;

	@Column(name = "TRANS_ATTRIBUTE", length = 250)
	@Getter
	@Setter
	private String transAttribute;

	@Column(name = "TRANS_ATTRIBUTE2", length = 250)
	@Getter
	@Setter
	private String transAttribute2;

	@Column(name = "TRANS_ATTRIBUTE3", length = 250)
	@Getter
	@Setter
	private String transAttribute3;

	@Column(name = "SERIAL_NAME", length = 250)
	@Getter
	@Setter
	private String serialName;

	@Column(name = "ATTRIBUTE4", length = 200)
	@Getter
	@Setter
	private String attribute4;

	@Column(name = "TRANS_ATTRIBUTE4", length = 250)
	@Getter
	@Setter
	private String transAttribute4;

	@Column(name = "POSITION_ID")
	@Getter
	@Setter
	private Integer positionId;

	@Column(name = "ICON_TYPE", length = 50)
	@Getter
	@Setter
	private String iconType;

	@Column(name = "ICON_COLOR", length = 50)
	@Getter
	@Setter
	private String iconColor;

	@Column(name = "QUERY_ID", length = 50)
	@Getter
	@Setter
	private String queryId;

}
