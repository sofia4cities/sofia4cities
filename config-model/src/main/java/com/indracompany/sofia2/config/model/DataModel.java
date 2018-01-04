/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TEMPLATE")
@Configurable
@SuppressWarnings("deprecation")
public class DataModel extends AuditableEntityWithUUID {
	
	 @Column(name = "JSONSCHEMA",nullable = false)
	 @NotNull
	 @Lob
	 @Type(type = "org.hibernate.type.TextType")
	 @Setter private String jsonschema;

	 @Column(name = "ISRELATIONAL",nullable = false)
	 @NotNull
	 @Setter @Getter private boolean isrelational;

	 @Column(name = "IDENTIFICATION", length = 45, unique = true,nullable = false)
	 @NotNull
	 @Setter @Getter private String identification;

	 @Column(name = "TYPE", length = 45,nullable = false)
	 @NotNull
	 @Setter @Getter private String type;

	 @Column(name = "DESCRIPTION", length = 512)
	 @Setter @Getter private String description;

	 @Column(name = "CATEGORY", length = 512)
	 @Setter @Getter private String category;
	 
	 
	 public String getschema() {
		 String schema = this.jsonschema.toString();
		 if (schema != null && schema.length() > 0) {
			 schema = schema.replaceAll("\\<.*?>", "");
			 schema = schema.replaceAll("&nbsp;", "");
			 schema = schema.replaceAll("&amp;", "");
			 schema = schema.replaceAll("&quot;", "\"");
			 schema = schema.replaceAll("\"", "'");
			 schema = schema.replaceAll("\n", "");
		 }
		 return schema;
	 }

	 private String prepareschema(String jsonschema) {
		 String myjsonschema = jsonschema;
		 if (myjsonschema != null) {
			 myjsonschema = myjsonschema.replace("\t", "");
			 myjsonschema = myjsonschema.replace("\r", "");
			 myjsonschema = myjsonschema.replace("\n", "");
		 }
		 return myjsonschema;
	 }

	



}
