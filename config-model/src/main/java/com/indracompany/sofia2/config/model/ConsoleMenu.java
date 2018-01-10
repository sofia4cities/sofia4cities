/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;
@Configurable
@Entity
@Table(name = "CONSOLE_MENU")
@SuppressWarnings("deprecation")
public class ConsoleMenu extends AuditableEntity{

	@Id
	@Column(name = "ID", length = 50)
	@Getter @Setter private String id;

	@Column(name = "NAME", length = 75,nullable = false)
	@NotNull
	@Getter @Setter private String name;




}
