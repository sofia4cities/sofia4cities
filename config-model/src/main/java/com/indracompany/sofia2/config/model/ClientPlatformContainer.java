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
@Table(name = "CLIENT_PLATFORM_CONTAINER")
@Configurable
@SuppressWarnings("deprecation")

public class ClientPlatformContainer extends AuditableEntityWithUUID{
	
	@ManyToOne
    @JoinColumn(name = "CLIENT_PLATFORM_ID", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter @Setter private ClientPlatform clientPlatformId;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "AUTHENTICATION_TOKEN_ID", referencedColumnName = "id", nullable = false)
	@Getter @Setter private Token authenticationTokenId;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "CLIENT_PLATFORM_CONTAINER_TYPE_ID", referencedColumnName = "ID", nullable = false)
	@Getter @Setter private ClientPlatformContainerType clientPlatformContainerTypeId;
/*
	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @ForeignKey(name = "FK_KPCONTENEDOR_LENGUAJERUNTIME")
    @JoinColumn(name = "IDLENGUAJE", referencedColumnName = "ID", nullable = false)
    private Lenguajeruntime idlenguaje;

	@ManyToOne	
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @ForeignKey(name = "FK_KPCONTENEDOR_EJECUTABLEPROGRAMAKPCONTENEDOR")
    @JoinColumn(name = "IDEJECUTABLEPROGRAMA", referencedColumnName = "ID", nullable = false)
    private Ejecutableprogramakpcontenedor idejecutableprograma;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @ForeignKey(name = "FK_kpcontenedor_EXPRESIONEJECUCION")
    @JoinColumn(name = "IDEXPRESIONEJECUCION", referencedColumnName = "ID", nullable = false)
    private Expresionejecucionkpcontenedor idexpresionejecucion;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_KPCONTMONIT")
    @JoinColumn(name = "IDMONITORIZACION", referencedColumnName = "ID", nullable = false)
    private Monitorizacionkpcontenedor idmonitorizacion;
    
    
	public Lenguajeruntime getIdlenguaje() {
        return idlenguaje;
    }

	public void setIdlenguaje(Lenguajeruntime idlenguaje) {
        this.idlenguaje = idlenguaje;
    }

	public Ejecutableprogramakpcontenedor getIdejecutableprograma() {
        return idejecutableprograma;
    }

	public void setIdejecutableprograma(Ejecutableprogramakpcontenedor idejecutableprograma) {
        this.idejecutableprograma = idejecutableprograma;
    }

	public Expresionejecucionkpcontenedor getIdexpresionejecucion() {
        return idexpresionejecucion;
    }

	public void setIdexpresionejecucion(Expresionejecucionkpcontenedor idexpresionejecucion) {
        this.idexpresionejecucion = idexpresionejecucion;
    }

	public Monitorizacionkpcontenedor getIdmonitorizacion() {
        return idmonitorizacion;
    }

	public void setIdmonitorizacion(Monitorizacionkpcontenedor idmonitorizacion) {
        this.idmonitorizacion = idmonitorizacion;
    }
*/
	@Column(name = "CLIENT_CONNECTION", length = 50,nullable = false)
    @NotNull
    @Getter @Setter private String clientConnection;

	@Column(name = "ENCODING", length = 50)
	@Getter @Setter private String encoding;

	@Column(name = "PROGRAM_NAME", length = 50,nullable = false)
    @NotNull
    @Getter @Setter private String programName;

	@Column(name = "MAX_EXECUTION_TIME",nullable = false)
    @NotNull
    @Getter @Setter private Integer maxExecutionTime;

	@Column(name = "MESSAGE_FILES_PREFIX", length = 50,nullable = false)
    @NotNull
    @Getter @Setter private String messageFilesPrefix;

	@Column(name = "LOG_FILES_PREFIX", length = 50,nullable = false)
    @NotNull
    @Getter @Setter private String logFilesPrefix;

	@Column(name = "DESCRIPTION", length = 512)
	@Getter @Setter private String description;

	@Column(name = "STATE", length = 30,nullable = false)
    @NotNull
    @Getter @Setter private String state;

	








	


}
