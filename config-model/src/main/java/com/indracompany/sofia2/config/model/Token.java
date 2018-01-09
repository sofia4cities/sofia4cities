package com.indracompany.sofia2.config.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TOKEN")
@Configurable
@SuppressWarnings("deprecation")

public class Token extends AuditableEntityWithUUID{

	@ManyToOne
	@JoinColumn(name = "CLIENT_PLATFORM_ID", referencedColumnName = "ID")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter @Setter private ClientPlatform clientPlatformId;

	@Column(name = "token", length = 32, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String token;

	@Column(name = "LAST_CONNECTION")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	@Getter @Setter private Calendar lastConnection;

	@Column(name = "ACTIVE",nullable = false)
	@NotNull
	@Getter @Setter private Integer active;

}
