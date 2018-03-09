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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "GADGET_DATASOURCE")
public class GadgetDatasource extends AuditableEntityWithUUID{
		
	@Column(name = "IDENTIFICATION", length = 100, unique=true ,nullable = false)
    @Getter @Setter private String 	identification;
	
	@Column(name = "MODE", length = 45,nullable = false)
    @Getter @Setter private String mode;
	
	@Column(name = "QUERY",nullable = false)
	@Type(type ="org.hibernate.type.TextType")
    @Lob
    @Getter @Setter  private String query;
	
	@Column(name = "DBTYPE", length = 10, nullable=false)
	@Getter @Setter private String dbtype;
	
	@ManyToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
	@Getter @Setter private User user;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "ONTOLOGY_ID", referencedColumnName = "ID", nullable = true)
	@Getter	@Setter	private Ontology ontology;
	
	@Column(name = "REFRESH")
	@Getter @Setter private Integer refresh;
	
	@Column(name = "MAXVALUES",columnDefinition = "int default 10")
	@Getter @Setter private Integer maxvalues;
	
	@Column(name = "DESCRIPTION", length = 512)
    @Getter @Setter private String description;
	
	 @Column(name = "CONFIG")
	 @Lob
	 @Type(type ="org.hibernate.type.TextType")
    @Getter @Setter private String config;
}
