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

import com.indracompany.sofia2.config.model.base.AuditableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "KPCONTAINER_TYPE")
public class KPContainerType extends AuditableEntity {

    @Id
    @Column(name = "ID")
    @Getter @Setter private Integer id;
    
	private static final long serialVersionUID = 1L;
	@Column(name = "TYPE", length = 50,nullable = false)
    @NotNull
    @Getter @Setter private String type;
	
}
