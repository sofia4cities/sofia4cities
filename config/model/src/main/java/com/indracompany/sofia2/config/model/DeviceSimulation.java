package com.indracompany.sofia2.config.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DEVICE_SIMULATION")
@Configurable
public class DeviceSimulation extends AuditableEntityWithUUID {
	
	public static enum Type {
		FIXED_INTEGER, FIXED_NUMBER, FIXED_STRING, FIXED_DATE, RANDOM_INTEGER, RANDOM_NUMBER, RANDOM_STRING, RANDOM_DATE, COSINE_NUMBER, SINE_NUMBER, NULL
	}

	@Column(name = "IDENTIFICATION")
	@Getter
	@NotNull
	@Setter
	private String identification;
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter
	@Setter
	private User user;
	
	@Column(name = "JSON", nullable = false)
	@NotNull
	@Lob
	@JsonRawValue
	@Getter
	@Setter
	private String json;

	@Column(name = "DATE_FROM")
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date dateFrom;

	@Column(name = "DATE_TO")
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date dateTo;

	@Column(name = "CRON")
	@Getter
	@Setter
	private String cron;
	
	@Column(name = "ACTIVE")
	@Getter
	@Setter
	@NotNull
	private boolean active;
	
	@Column(name = "JOB_NAME")
	@Getter
	@Setter
	private String jobName;
	
}
