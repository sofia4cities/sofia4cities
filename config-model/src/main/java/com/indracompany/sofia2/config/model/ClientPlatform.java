package com.indracompany.sofia2.config.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CLIENT_PLATFORM")
@Configurable
@SuppressWarnings("deprecation")
public class ClientPlatform extends AuditableEntityWithUUID  {
	
	/* Se deja comentado hasta migración
	@OneToMany(mappedBy = "kpId", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_KP_INSTANCIAKP")
    private Set<Instanciakp> instanciakps;

    @OneToMany(mappedBy = "kpId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_KPO_KP")
    private Set<Kpontologia> kpontologias;

    @OneToMany(mappedBy = "kpId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_KP_ONTOLOGIAGRUPO")
    private Set<Kpontologiagrupo> kpontologiagrupoes;
    
    
	@OneToMany(mappedBy = "kpId", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_KPID")
    private Set<Token> tokens;
	

	public Set<Token> getTokens() {
        return tokens;
    }

	public void setTokens(Set<Token> tokens) {
        this.tokens = tokens;
    }
    
    public Set<Instanciakp> getInstanciakps() {
        return this.instanciakps;
    }

	public void setInstanciakps(Set<Instanciakp> instanciakps) {
        this.instanciakps = instanciakps;
    }

	public Set<Kpontologia> getKpontologias() {
        return this.kpontologias;
    }

	public void setKpontologias(Set<Kpontologia> kpontologias) {
        this.kpontologias = kpontologias;
    }

	public Set<Kpontologiagrupo> getKpontologiagrupoes() {
        return this.kpontologiagrupoes;
    }

	public void setKpontologiagrupoes(Set<Kpontologiagrupo> kpontologiagrupoes) {
        this.kpontologiagrupoes = kpontologiagrupoes;
    }

    */
	@Column(name = "ENCRYPTIONKEY",nullable = false)
    @NotNull
    @Lob
	@Type(type = "org.hibernate.type.TextType")
    @Getter @Setter private String encryptionkey;
	
    @Column(name = "METADATA")
    @Lob
	@Type(type = "org.hibernate.type.TextType")
    @Getter @Setter private String metadata;




	@Column(name = "USER_ID", length = 50)
	@Getter @Setter private String userId;

	@Column(name = "IDENTIFICATION", length = 50, unique = true,nullable = false)
    @NotNull
    @Getter @Setter private String identification;

	@Column(name = "DESCRIPTION", length = 256)
	@Getter @Setter private String description;


	
	

}
