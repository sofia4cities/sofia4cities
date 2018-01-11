/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.multipart.MultipartFile;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "ONTOLOGY")
@SuppressWarnings("deprecation")
public class Ontology extends AuditableEntityWithUUID{



	@Column(name = "JSON_SCHEMA",nullable = false)
	@NotNull
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String jsonSchema;

	@Column(name = "XML_Diagram")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Getter @Setter private String xmlDiagram;


	@Column(name = "ONTOLOGY_CLASS", length = 50)
	@Getter @Setter private String ontologyClass;



	public String getJsonSchema() {
		String schema = "";
		schema = this.jsonSchema;

		if (schema != null && schema.length() > 0) {
			schema= schema.replaceAll("\\<.*?>", "");
			schema = schema.replaceAll("&nbsp;", "");
			schema = schema.replaceAll("&amp;", "");
			schema = schema.replaceAll("&quot;", "\"");
			schema = schema.replaceAll("\"", "'");
			schema = schema.replaceAll("\n", "");
		}
		return schema;
	}



	private String prepareJsonSchema(String jsonSchema) {
		String myJsonSchema = jsonSchema;
		if (myJsonSchema != null) {
			myJsonSchema = myJsonSchema.replace("\t", "");
			myJsonSchema = myJsonSchema.replace("\r", "");
			myJsonSchema = myJsonSchema.replace("\n", "");
		}
		return myJsonSchema;
	}



	@OneToMany(mappedBy = "ontologyId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter @Setter private Set<ClientPlatformOntology> clientPlatformOntologyId;

	/*
	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_USUARIO_USUARIOONTOLOGIA")
    private Set<Usuarioontologia> usuarioontologias;*/

	@OneToMany(mappedBy = "ontologyId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter @Setter private Set<OntologyEmulator> ontologyEmulator;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "DATA_MODEL_ID", referencedColumnName = "ID")
	@Getter @Setter private DataModel dataModelId;

	@Column(name = "USER_ID", length = 50)
	@Getter @Setter private String userId;

	@Column(name = "IDENTIFICATION", length = 50, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String identification;

	@Column(name = "ACTIVE",nullable = false)
	@NotNull
	@Getter @Setter private boolean active;

	@Column(name = "RTDBCLEAN",nullable = false)
	@NotNull
	@Getter @Setter private boolean rtdbClean;

	@Column(name = "RTDBHDB", nullable = false)
	@NotNull
	@Getter @Setter private boolean rtdbToHdb;

	@Column(name = "PUBLIC",nullable = false)
	@NotNull
	@Getter @Setter private boolean isPublic;

	@Column(name = "DESCRIPTION", length = 512)
	@Getter @Setter private String description;

	@Column(name = "METAINF", length = 1024)
	@Getter @Setter private String metainf;

	@Column(name = "DATA_MODEL_VERSION", length = 50)
	@Getter @Setter private String dataModelVersion;







}
