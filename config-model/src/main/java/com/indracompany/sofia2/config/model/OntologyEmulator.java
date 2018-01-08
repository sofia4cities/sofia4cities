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
@Table(name = "ONTOLOGY_EMULATOR")
@Configurable
@SuppressWarnings("deprecation")

public class OntologyEmulator extends AuditableEntityWithUUID{
	


    @Column(name = "VALUES",nullable = false)
    @NotNull
    @Lob
	@Type(type = "org.hibernate.type.TextType")
    @Getter @Setter private String values;



	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ONTOLOGY_ID", referencedColumnName = "ID", nullable = false)
	@Getter @Setter private Ontology ontologyId;

	@Column(name = "IDENTIFICATION", length = 50, unique = true,nullable = false)
    @NotNull
    @Getter @Setter private String identification;

	@Column(name = "USER_ID", length = 50,nullable = false)
    @NotNull
    @Getter @Setter private String userId;

	@Column(name = "INSERT_EVERY",nullable = false)
    @NotNull
    @Getter @Setter private Integer insertEvery;

	

}
