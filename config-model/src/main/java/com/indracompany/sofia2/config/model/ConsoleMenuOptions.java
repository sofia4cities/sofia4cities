/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;


import org.hibernate.annotations.OnDeleteAction;

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

public class ConsoleMenuOptions extends AuditableEntity{

	@Id
	@Column(name = "ID", length = 50)
	@Getter @Setter private String id;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "CONSOLE_MENU_ID", referencedColumnName = "id", nullable = false)
	@Getter @Setter private ConsoleMenu consoleMenuId;

	@Column(name = "OPTION", length = 75,nullable = false)
	@NotNull
	@Getter @Setter private String option;

	@Column(name = "PERMITTED")
	@NotNull
	@Getter @Setter private boolean permitted;



}
