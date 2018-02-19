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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "ONTOLOGY")
public class Ontology extends AuditableEntityWithUUID {

	@Column(name = "JSON_SCHEMA", nullable = false)
	@NotNull
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter
	@Setter
	private String jsonSchema;

	@Column(name = "XML_Diagram")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter
	@Setter
	private String xmlDiagram;

	@Column(name = "ONTOLOGY_CLASS", length = 50)
	@Getter
	@Setter
	private String ontologyClass;


	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "DATA_MODEL_ID", referencedColumnName = "ID")
	@Getter
	@Setter
	private DataModel dataModel;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter
	@Setter
	private User user;

	@Column(name = "IDENTIFICATION", length = 50, unique = true, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String identification;

	@Column(name = "ACTIVE", nullable = false)
	@NotNull
	@Getter
	@Setter
	private boolean active;

	@Column(name = "RTDBCLEAN", nullable = false)
	@NotNull
	@Getter
	@Setter
	private boolean rtdbClean;

	@Column(name = "RTDBHDB", nullable = false)
	@NotNull
	@Getter
	@Setter
	private boolean rtdbToHdb;

	@Column(name = "PUBLIC", nullable = false)
	@NotNull
	@Getter
	@Setter
	private boolean isPublic;

	@Column(name = "DESCRIPTION", length = 512)
	@Getter
	@Setter
	private String description;

	@Column(name = "METAINF", length = 1024)
	@Getter
	@Setter
	private String metainf;

	@Column(name = "DATA_MODEL_VERSION", length = 50)
	@Getter
	@Setter
	private String dataModelVersion;
	
	@PostLoad
	protected void trim(){
	    if(this.identification!=null){
	        this.identification=this.identification.replaceAll(" ", "");
	    }
	}

}
