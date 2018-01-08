/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "GADGET_MEASURE")
@Configurable
@SuppressWarnings("deprecation")

public class GadgetMeasure extends AuditableEntityWithUUID {

	

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "GADGET_ID", referencedColumnName = "ID", nullable = false)
	@Getter @Setter private Gadget gadgetId;
	/*
	@ManyToOne
    @JoinColumn(name = "ONTOLOGIA_ID", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_GADGET_MEASURE_ONTOLOGIA")
    private Ontologia ontologiaId;
    
	public Ontologia getOntologiaId() {
        return ontologiaId;
    }

	public void setOntologiaId(Ontologia ontologiaId) {
        this.ontologiaId = ontologiaId;
    }
    */

	@Column(name = "ATTRIBUTE", length = 200,nullable = false)
    @NotNull
    @Getter @Setter private String attribute;

	@Column(name = "ATTRIBUTE2", length = 200)
	@Getter @Setter private String attribute2;

	@Column(name = "ATTRIBUTE3", length = 200)
	@Getter @Setter private String attribute3;

	@Column(name = "TRANS_ATTRIBUTE", length = 250)
	@Getter @Setter private String transAttribute;

	@Column(name = "TRANS_ATTRIBUTE2", length = 250)
	@Getter @Setter  private String transAttribute2;

	@Column(name = "TRANS_ATTRIBUTE3", length = 250)
	@Getter @Setter private String transAttribute3;

	@Column(name = "SERIAL_NAME", length = 250)
	@Getter @Setter private String serialName;

	@Column(name = "ATTRIBUTE4", length = 200)
	@Getter @Setter  private String attribute4;

	@Column(name = "TRANS_ATTRIBUTE4", length = 250)
	@Getter @Setter private String transAttribute4;

	@Column(name = "POSITION_ID")
	@Getter @Setter private Integer positionId;

	@Column(name = "ICON_TYPE", length = 50)
	@Getter @Setter private String iconType;

	@Column(name = "ICON_COLOR", length = 50)
	@Getter @Setter private String iconColor;

	@Column(name = "QUERY_ID", length = 50)
	@Getter @Setter private String queryId;

	


}
