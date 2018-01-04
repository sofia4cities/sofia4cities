/*******************************************************************************

 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.model;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USER")
@Configurable
@SuppressWarnings("deprecation")
public class UserCDB extends AuditableEntityWithUUID{
	
    @Column(name = "EMAIL", length = 255,nullable = false)
    @NotNull
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@[a-z0-9-]+(.[a-z0-9-]+)*(.[a-z]{2,3})$")
    @Getter @Setter private String email;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", nullable = false)
	@Getter @Setter private RoleType role;
    
    @Column(name = "USER_ID", length = 50, unique = true,nullable = false)
    @NotNull
    @Getter @Setter private String userId;
    
    @Column(name = "PASSWORD", length = 128,nullable = false)
    @NotNull
    @Getter @Setter private String password;
    
    @Column(name = "DATE_CREATED",nullable = false)
    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "M-")
    @Getter @Setter private Date dateCreated;
    
    @Column(name = "ACTIVE",nullable = false)
    @NotNull
    @Getter @Setter private boolean active;
    
    @Column(name = "FULL_NAME", length = 255)
    @Getter @Setter private String fullName;
    
    @Column(name = "DATE_DELETED")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "M-")
    private Date dateDeleted;
	    

}
