/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "GADGET")
@Configurable
@SuppressWarnings("deprecation")
public class Gadget extends AuditableEntityWithUUID{

	@Column(name = "GCODE")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String gCode;

	@Column(name = "GCODE2")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String gCode2;


	@Column(name = "PUBLIC",nullable = false)
	@NotNull
	@Getter @Setter private boolean isPublic;


	/*
	 * 
		

	    @OneToMany(mappedBy = "gadgetId", cascade = CascadeType.REMOVE)
		@OnDelete(action = OnDeleteAction.CASCADE)
	    @ForeignKey(name = "FK_GRUPOSGADGET_GADGETID")
	    private Set<Gruposgadget> gruposgadgets;


		@ManyToOne
	    @JoinColumn(name = "TOKEN_ID", referencedColumnName = "id")
		@OnDelete(action = OnDeleteAction.NO_ACTION)
	    @ForeignKey(name = "FK_GADGET_TOKEN")
	    private Token tokenId;ç

	   	 public Set<GadgetQuery> getGadgetQueries() {
		 return gadgetQueries;
	 }

	 public void setGadgetQueries(Set<GadgetQuery> gadgetQueries) {
		 this.gadgetQueries = gadgetQueries;
	 }

	 public Set<GadgetMeasure> getGadgetMeasures() {
		 return gadgetMeasures;
	 }

	 public void setGadgetMeasures(Set<GadgetMeasure> gadgetMeasures) {
		 this.gadgetMeasures = gadgetMeasures;
	 }

	 public Set<Gruposgadget> getGruposgadgets() {
		 return gruposgadgets;
	 }

	 public void setGruposgadgets(Set<Gruposgadget> gruposgadgets) {
		 this.gruposgadgets = gruposgadgets;
	 }

	 public Token getTokenId() {
		 return tokenId;
	 }

	 public void setTokenId(Token tokenId) {
		 this.tokenId = tokenId;
	 }


	 */
	@OneToMany(mappedBy = "gadgetId", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter @Setter private Set<GadgetQuery> gadgetQueries;

	@OneToMany(mappedBy = "gadgetId", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter @Setter private Set<GadgetMeasure> gadgetMeasures;

	@Column(name = "USER_ID", length = 50,nullable = false)
	@NotNull
	@Getter @Setter private String userId;

	@Column(name = "URL", length = 255)
	@Getter @Setter private String url;

	@Column(name = "NAME", length = 100,nullable = false)
	@NotNull
	@Getter @Setter private String name;

	@Column(name = "Type", length = 50,nullable = false)
	@NotNull
	@Getter @Setter private String type;

	@Column(name = "MODE", length = 50)
	@Getter @Setter private String mode;

	@Column(name = "REFRESH")
	@Getter @Setter private Integer refresh;

	@Column(name = "MAXVALUES",columnDefinition = "int default 10")
	@Getter @Setter private Integer maxvalues;

	@Column(name = "MINRANGE")
	@Getter @Setter private Double minrange;

	@Column(name = "MAXRANGE")
	@Getter @Setter private Double maxrange;

	@Column(name = "DESCRIPTION", length = 255)
	@Getter @Setter private String description;

	@Column(name = "SUBTITLE", length = 100)
	@Getter @Setter private String subtitle;

	@Column(name = "STYLE", length = 100)
	@Getter @Setter private String style;

	@Column(name = "TOOLTIP", length = 100)
	@Getter @Setter private String tooltip;

	@Column(name = "DBTYPE", length = 5)
	@Getter @Setter private String dbType;













}
