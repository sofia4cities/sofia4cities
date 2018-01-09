package com.indracompany.sofia2.config.model.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
allowGetters = true)
@EntityListeners(AuditingEntityListener.class)
@ToString
public abstract class AuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@Getter @Setter private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@Getter @Setter  private Date updatedAt;


}
