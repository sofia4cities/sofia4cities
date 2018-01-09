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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "INSTANCE_GENERATOR")
@SuppressWarnings("deprecation")

public class InstanceGenerator extends AuditableEntity{
	

    @Id
    @Column(name = "ID")
    @Getter @Setter private Integer id;

    @Column(name = "VALUES_G",nullable = false)
    @NotNull
    @Lob
	@Type(type = "org.hibernate.type.TextType")
    @Getter @Setter private String valuesG;
    
    @ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "GENERATOR_TYPE_ID", referencedColumnName = "ID", nullable = false)
    @Getter @Setter private GeneratorType generatorTypeId;

	@Column(name = "IDENTIFICATION", length = 50, unique = true,nullable = false)
    @NotNull
    @Getter @Setter private String identification;

	@Column(name = "USER", length = 50)
	@Getter @Setter private String user;

}
