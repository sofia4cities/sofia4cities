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
@Table(name = "GENERATOR_TYPES")

public class GeneratorType extends AuditableEntity{


	@Id
	@Column(name = "ID")
	@Getter @Setter private Integer id;

	@Column(name = "IDENTIFICATION", length = 50, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String identification;

	@Column(name = "KEY_TYPE", length = 512,nullable = false)
	@NotNull
	@Getter @Setter private String keyType;

	@Column(name = "KEY_VALUE_DEF", length = 512,nullable = false)
	@NotNull
	@Getter @Setter private String keyValueDef;
}
