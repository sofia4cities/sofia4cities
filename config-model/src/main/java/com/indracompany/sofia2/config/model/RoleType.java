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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ROLE_TYPE")
@Configurable
@SuppressWarnings("deprecation")
public class RoleType extends AuditableEntity {

	@Id
	@Column(name = "ID")
	@Getter @Setter private Integer id;    

	@OneToOne(cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "ROLE_PARENT", unique = false, nullable = true, insertable = true, updatable = true)
	@Getter @Setter private RoleType roleparent;

	@Column(name = "NAME", length = 24, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String name;

	@Column(name = "DESCRIPTION", length = 255)
	@Getter @Setter private String description;

}
