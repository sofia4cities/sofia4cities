/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
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

@Entity
@Table(name = "CLIENT_PLATFORM_ONTOLOGY")
@Configurable
@SuppressWarnings("deprecation")
public class ClientPlatformOntology extends AuditableEntityWithUUID{

	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CLIENT_PLATFORM_ID", referencedColumnName = "ID", nullable = false)
    private ClientPlatform clientPlatformId;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ONTOLOGY_ID", referencedColumnName = "ID", nullable = false)
    private Ontology ontologyId;
}
