/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "GADGET_DATA_MODEL")
@Configurable
@SuppressWarnings("deprecation")
public class GadgetDataModel extends AuditableEntityWithUUID{



	@Column(name = "IDENTIFICATION", length = 50, unique = true)
	@NotNull
	@Getter @Setter private String identification;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "IMAGE", columnDefinition = "BLOB")
	@Getter @Setter private byte[] image;

	@Column(name = "USER_ID", length = 50)
	@NotNull
	@Getter @Setter private String userId;

	@Column(name = "GCODE")
	@Lob
	@Getter @Setter private String gCode;

	@Column(name = "IMAGE_TYPE", length = 20)
	@Getter @Setter private String imageType;

	@Column(name = "PUBLIC")
	@NotNull
	@Getter @Setter private boolean isPublic;

	@Column(name = "DESCRIPTION", length = 512)
	@Getter @Setter private String description;

}
