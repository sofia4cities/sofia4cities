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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ONTOLOGY_USER_ACCESS", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "ONTOLOGY_ID", "USER_ID" }) })
@Configurable
public class OntologyUserAccess extends AuditableEntityWithUUID {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "ONTOLOGY_USER_ACCESS_TYPE_ID", referencedColumnName = "ID", nullable = false)
	@Getter
	@Setter
	private OntologyUserAccessType ontologyUserAccessType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ONTOLOGY_ID", referencedColumnName = "ID", nullable = false)
	@Getter
	@Setter
	private Ontology ontology;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter
	@Setter
	private User user;
	
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OntologyUserAccess )) return false;
        OntologyUserAccess that = (OntologyUserAccess) o;
        return getOntologyUserAccessType() != null && 
        		getOntologyUserAccessType().equals(that.getOntologyUserAccessType()) &&
        		getOntology() != null  &&
        		getOntology().equals(that.getOntology()) &&
        		getUser() != null &&
        		getUser().equals(that.getUser());
    }
	
    @Override
    public int hashCode() {
    	return java.util.Objects.hash(getOntologyUserAccessType(), getOntology(), getUser());
    }
    
    @Override
    public String toString() {
    	String space = "-";
    	StringBuilder sb = new StringBuilder();
    	sb.append(getOntology());
    	sb.append(space);
    	sb.append(getUser());
    	sb.append(space);
    	sb.append(getOntologyUserAccessType());
    	return sb.toString();
    }

}
