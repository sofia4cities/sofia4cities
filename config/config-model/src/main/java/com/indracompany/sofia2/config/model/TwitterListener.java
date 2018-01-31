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
@Table(name = "TWITTER_LISTENER")
@Configurable
public class TwitterListener extends AuditableEntityWithUUID{
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ONTOLOGY_ID", referencedColumnName = "ID", nullable = false)
    @Getter @Setter private Ontology ontologyId;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CONFIGURATION_ID", referencedColumnName = "ID")
	@Getter @Setter private Configuration confogurationId;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "TOKEN_ID", referencedColumnName = "ID")
	@Getter @Setter private Token tokenId;

	@Column(name = "IDENTIFICATOR", length = 50,nullable = false)
    @NotNull
    @Getter @Setter private String identificator;

	@Column(name = "dateFrom", length = 100,nullable = false)
    @NotNull
    @Getter @Setter private String dateFrom;

	@Column(name = "dateTo", length = 100,nullable = false)
    @NotNull
    @Getter @Setter private String dateTo;

	@Column(name = "topics", length = 512,nullable = false)
    @NotNull
    @Getter @Setter private String topics;

	@Column(name = "cron", length = 100)
	@Getter @Setter private String cron;


	
}
