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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.multipart.MultipartFile;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

@Configurable
@Entity
@Table(name = "ONTOLOGY")
@SuppressWarnings("deprecation")
public class Ontology extends AuditableEntityWithUUID{

	@Transient
    private MultipartFile file;

    @Transient
    private String fileName;


    @Column(name = "SHARD_BY")
    private Integer shardBy = 0;
    
    /*
    @OneToMany(mappedBy = "ontologiaId", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Consultaspredefinidas> consultaspredefinidass;
    
    @ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_ONTOLOGIA_PADRE")
    @JoinColumn(name = "PADRE_ID", referencedColumnName = "ID")
    private Ontologia padreId;
    
    
	public Ontologia getPadreId() {
        return this.padreId;
    }

	public void setPadreId(Ontologia padreId) {
        this.padreId = padreId;
    }
    public Set<Consultaspredefinidas> getConsultaspredefinidass() {
        return this.consultaspredefinidass;
    }

	public void setConsultaspredefinidass(Set<Consultaspredefinidas> consultaspredefinidass) {
        this.consultaspredefinidass = consultaspredefinidass;
    }
    
    

    */

    @Column(name = "JSON_SCHEMA",nullable = false)
    @NotNull
    @Lob
	@Type(type = "org.hibernate.type.TextType")
    private String jsonSchema;

    @Column(name = "XML_Diagram")
    @Lob
	@Type(type = "org.hibernate.type.TextType")
    private String xmlDiagram;

    
	@Column(name = "PARTITIONED",nullable = false)
    @NotNull
    private boolean isPartitioned;
    
    @Column(name = "IN_PARTITIONED",nullable = false)
    @NotNull
    private boolean isInPartitioned;
    
    @Column(name = "PARTITIONED_FIELDS")
    private String partitionedFields;
	

	/*
    @OneToMany(mappedBy = "idOntologia", cascade = CascadeType.ALL)
    private Set<Tipodatotimeserie> tipodatotimeseries;
    
    
    public Set<Tipodatotimeserie> getTipodatotimeseries() {
		return tipodatotimeseries;
	}

	public void setTipodatotimeseries(Set<Tipodatotimeserie> tipodatotimeseries) {
		this.tipodatotimeseries = tipodatotimeseries;
	}
    */

    @Column(name = "REDUCE")
    @NotNull
    private boolean isReduce;
    
    @Column(name = "ONTOLOGY_CLASS", length = 50)
    private String ontologyClass;
    
	@Column(name = "TIME_SERIES")
	@NotNull
	private boolean isTimeSeries;

	@Column(name = "SAMPLES", length = 10)
	private String samples;

	@Column(name = "WINDOW_UNIT", length = 10)
	private String windowUnit;

	@Column(name = "GROUPING_UNIT", length = 10)
	private String groupingUnit;
	
	
    @Column(name = "FORMULA")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String formula;
    
    @Column(name = "CRON", length = 50)
    private String cron;
    
    @Column(name = "KPI",nullable = false,columnDefinition = "boolean default false")
    @NotNull
    private boolean isKpi;
    
    @Column(name = "INITIALIZED",nullable = false,columnDefinition = "boolean default false")
    @NotNull
    private boolean isInitialized;
    
    @Column(name = "INITIAL_DATE")
    private String initialDate;
    
    @Column(name = "REGENERATE_DATA",nullable = false,columnDefinition = "boolean default false")
    @NotNull
    private boolean isRegenerateData;

    /*
    @ManyToOne
   	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_bdtrdatasource")
    @JoinColumn(name = "bdtrdatasourceid", referencedColumnName = "ID")
    private Bdtrdatasource bdtrdatasourceid;
     
	public Bdtrdatasource getBdtrdatasourceid() {
		return bdtrdatasourceid;
	}

	public void setBdtrdatasourceid(Bdtrdatasource bdtrdatasourceid) {
		this.bdtrdatasourceid = bdtrdatasourceid;
	}
   
    */
    
    
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


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setFile() {
        this.file = new MultipartFile() {

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }
        };
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /*

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_SO_TO_ONTOLOGIA")
    private Set<Scriptontologia> scriptontologias;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
    @ForeignKey(name = "FK_CEP_EVNT_ONT")
    private Set<CepEvento> cepEventoes;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_ONTOLOGIA_INSTANCIAONTOLOGIAGRUPOONTOLOGIA")
    private Set<Instanciaontgront> instanciaontgronts;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_KPO_ONTOLOGIA")
    private Set<Kpontologia> kpontologias;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_ONTOLOGIA_ONTOLOGIAGRUPOONTOLOGIA")
    private Set<Ontologiagrupoontologia> ontologiagrupoontologias;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_USUARIO_USUARIOONTOLOGIA")
    private Set<Usuarioontologia> usuarioontologias;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_simuladorontologia_ontologia")
    private Set<Simuladorontologia> simuladorontologias;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_GRUPOSONTOLOGIA_ONTOLOGIA_ID")
    private Set<Gruposontologia> gruposontologias;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_COMENTARIO_ONTOLOGIA")
    private Set<Comentario> comentarios;

	@OneToMany(mappedBy = "ontologiaId", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_VALORACION_ONTOLOGIA")
    private Set<Valoracionesusuario> valoracionesusuarios;
    
    
    
	public Set<Scriptontologia> getScriptontologias() {
        return scriptontologias;
    }

	public void setScriptontologias(Set<Scriptontologia> scriptontologias) {
        this.scriptontologias = scriptontologias;
    }

	public Set<CepEvento> getCepEventoes() {
        return cepEventoes;
    }

	public void setCepEventoes(Set<CepEvento> cepEventoes) {
        this.cepEventoes = cepEventoes;
    }

	public Set<Instanciaontgront> getInstanciaontgronts() {
        return instanciaontgronts;
    }

	public void setInstanciaontgronts(Set<Instanciaontgront> instanciaontgronts) {
        this.instanciaontgronts = instanciaontgronts;
    }

	public Set<Kpontologia> getKpontologias() {
        return kpontologias;
    }

	public void setKpontologias(Set<Kpontologia> kpontologias) {
        this.kpontologias = kpontologias;
    }

	public Set<Ontologiagrupoontologia> getOntologiagrupoontologias() {
        return ontologiagrupoontologias;
    }

	public void setOntologiagrupoontologias(Set<Ontologiagrupoontologia> ontologiagrupoontologias) {
        this.ontologiagrupoontologias = ontologiagrupoontologias;
    }

	public Set<Usuarioontologia> getUsuarioontologias() {
        return usuarioontologias;
    }

	public void setUsuarioontologias(Set<Usuarioontologia> usuarioontologias) {
        this.usuarioontologias = usuarioontologias;
    }

	public Set<Simuladorontologia> getSimuladorontologias() {
        return simuladorontologias;
    }

	public void setSimuladorontologias(Set<Simuladorontologia> simuladorontologias) {
        this.simuladorontologias = simuladorontologias;
    }

	public Set<Gruposontologia> getGruposontologias() {
        return gruposontologias;
    }

	public void setGruposontologias(Set<Gruposontologia> gruposontologias) {
        this.gruposontologias = gruposontologias;
    }

	public Set<Comentario> getComentarios() {
        return comentarios;
    }

	public void setComentarios(Set<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

	public Set<Valoracionesusuario> getValoracionesusuarios() {
        return valoracionesusuarios;
    }

	public void setValoracionesusuarios(Set<Valoracionesusuario> valoracionesusuarios) {
        this.valoracionesusuarios = valoracionesusuarios;
    }
	

	*/
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "DATA_MODEL_ID", referencedColumnName = "ID")
    private DataModel dataModelId;

	@Column(name = "USER_ID", length = 50)
    private String userId;

	@Column(name = "IDENTIFICATION", length = 50, unique = true,nullable = false)
    @NotNull
    private String identification;

	@Column(name = "ACTIVE",nullable = false)
    @NotNull
    private boolean isActive;

	@Column(name = "RTDBCLEAN",nullable = false)
    @NotNull
    private boolean isRtdbClean;

	@Column(name = "RTDBBDH", length = 50,nullable = false)
    @NotNull
    private String isRtdbBdh;

	@Column(name = "PUBLIC",nullable = false)
    @NotNull
    private boolean isPublic;

	@Column(name = "DESCRIPTION", length = 512)
    private String description;

	@Column(name = "METAINF", length = 1024)
    private String metainf;

	@Column(name = "DATA_MODEL_VERSION", length = 50)
    private String dataModelVersion;

	/*
	@Column(name = "PADRE",nullable = false,columnDefinition = "boolean default false")
    @NotNull
    private boolean padre;
    	
	public boolean isPadre() {
        return padre;
    }

	public void setPadre(boolean padre) {
        this.padre = padre;
    }

	*/
	@Column(name = "MASSIVE_SELECTALL_SUBS",nullable = false,columnDefinition = "boolean default false")
    @NotNull
    private boolean massiveSelectallSubs;

	@Column(name = "SCORE")
    private Double score;


	public boolean isMassiveSelectallSubs() {
        return massiveSelectallSubs;
    }

	public void setMassiveSelectallSubs(boolean massiveSelectallSubs) {
        this.massiveSelectallSubs = massiveSelectallSubs;
    }

	
}
