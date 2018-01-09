/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "ONTOLOGY_CATEGORY")
@SuppressWarnings("deprecation")
public class OntologyCategory extends AuditableEntity {
	

    @Id
    @Column(name = "ID")
    @Getter @Setter private Integer id;
    
	
	@Column(name = "IDENTIFICATOR", length = 512,nullable = false)
    @NotNull
    @Getter @Setter private String identificator;

	@Column(name = "DESCRIPTION", length = 1024,nullable = false)
    @NotNull
    @Getter @Setter private String description;


}
