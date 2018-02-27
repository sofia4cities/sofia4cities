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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "GADGET")
@Configurable
public class Gadget extends AuditableEntityWithUUID {

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter
	@Setter
	private User user;

	@Column(name = "GCODE")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter
	@Setter
	private String gCode;

	@Column(name = "GCODE2")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter
	@Setter
	private String gCode2;

	@Column(name = "PUBLIC", nullable = false)
	@NotNull
	@Getter
	@Setter
	private boolean isPublic;

	@ManyToOne
	@JoinColumn(name = "TOKEN_ID", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@Getter
	@Setter
	private Token token;

	@OneToMany(mappedBy = "gadget", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<GadgetQuery> gadgetQueries;

	@OneToMany(mappedBy = "gadget", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<GadgetMeasure> gadgetMeasures;

	@Column(name = "URL", length = 255)
	@Getter
	@Setter
	private String url;

	@Column(name = "NAME", length = 100, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String name;

	@Column(name = "Type", length = 50, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String type;

	@Column(name = "MODE", length = 50)
	@Getter
	@Setter
	private String mode;

	@Column(name = "REFRESH")
	@Getter
	@Setter
	private Integer refresh;

	@Column(name = "MAXVALUES", columnDefinition = "int default 10")
	@Getter
	@Setter
	private Integer maxvalues;

	@Column(name = "MINRANGE")
	@Getter
	@Setter
	private Double minrange;

	@Column(name = "MAXRANGE")
	@Getter
	@Setter
	private Double maxrange;

	@Column(name = "DESCRIPTION", length = 255)
	@Getter
	@Setter
	private String description;

	@Column(name = "SUBTITLE", length = 100)
	@Getter
	@Setter
	private String subtitle;

	@Column(name = "STYLE", length = 100)
	@Getter
	@Setter
	private String style;

	@Column(name = "TOOLTIP", length = 100)
	@Getter
	@Setter
	private String tooltip;

	@Column(name = "DBTYPE", length = 5)
	@Getter
	@Setter
	private String dbType;

}
