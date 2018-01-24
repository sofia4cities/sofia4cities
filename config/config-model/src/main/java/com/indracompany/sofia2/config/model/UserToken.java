package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;;

@Configurable
@Entity
@Table(name = "USER_TOKEN")
public class UserToken extends AuditableEntityWithUUID {

	@JoinColumn(name = "TOKEN", referencedColumnName = "TOKEN", nullable = false)
	@NotNull
	private Token token;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@NotNull
	private User userId;

}
