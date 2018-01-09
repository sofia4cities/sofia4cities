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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ONTOLOGY_USER", uniqueConstraints = {
@UniqueConstraint(columnNames = { "ONTOLOGY_ID", "USER_ID" }) })
@Configurable
@SuppressWarnings("deprecation")

public class OntologyUserAccess extends AuditableEntityWithUUID{
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ONTOLOGY_USER_ACCESS_TYPE_ID", referencedColumnName = "ID", nullable = false)
	@Getter @Setter private OntologyUserAccessType ontologyUserAccessTypeId;
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ONTOLOGY_ID", referencedColumnName = "ID", nullable = false)
	@Getter @Setter private Ontology ontologyId;
	
	@Column(name = "USER_ID", length = 50)
	@Getter @Setter private String userId;
	
	
}
