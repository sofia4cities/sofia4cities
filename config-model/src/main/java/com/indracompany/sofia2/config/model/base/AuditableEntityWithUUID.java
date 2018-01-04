package com.indracompany.sofia2.config.model.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntityWithUUID extends AuditableEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "ID", length = 50)
	@Getter @Setter private String id;
 
	   
}
