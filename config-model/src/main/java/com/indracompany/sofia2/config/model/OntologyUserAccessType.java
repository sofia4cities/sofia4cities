/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "ONTOLOGY_USER_ACCES_TYPE")
@SuppressWarnings("deprecation")

public class OntologyUserAccessType extends AuditableEntity{

	@Id
	@Column(name = "ID")
	@Getter @Setter private Integer id;


	@OneToMany(mappedBy = "ontologyUserAccessTypeId",fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter @Setter private Set<OntologyUserAccess> ontologyUserAccess;

	@Column(name = "NAME", length = 24, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String name;

	@Column(name = "DESCRIPCTION", length = 255)
	@Getter @Setter private String description;

}
