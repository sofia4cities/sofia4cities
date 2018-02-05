/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "DATA_MODEL")
@Configurable
public class DataModel extends AuditableEntityWithUUID {

	public static enum MainType {
		IoT, SmartCities, General, SocialMedia, Twitter, SmartHome, SmartEnergy, SmartRetail, SmartIndustry, GSMA, FiwareDataModel
	}

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@Getter
	@Setter
	private User user;

	@Column(name = "JSON_SCHEMA", nullable = false)
	@NotNull
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Setter
	private String jsonSchema;

	@Column(name = "NAME", length = 45, unique = true, nullable = false)
	@NotNull
	@Setter
	@Getter
	private String name;

	@Column(name = "TYPE", length = 45, nullable = false)
	@NotNull
	@Setter
	@Getter
	private String type;

	public void setTypeEnum(DataModel.MainType type) {
		this.type = type.toString();
	}

	@Column(name = "DESCRIPTION", length = 255)
	@Setter
	@Getter
	private String description;

	@Column(name = "LABELS", length = 255)
	@Setter
	@Getter
	private String labels;

	public String getSchema() {
		String schema = this.jsonSchema.toString();
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

	public String prepareSchema(String jsonschema) {
		String myjsonschema = jsonschema;
		if (myjsonschema != null) {
			myjsonschema = myjsonschema.replace("\t", "");
			myjsonschema = myjsonschema.replace("\r", "");
			myjsonschema = myjsonschema.replace("\n", "");
		}
		return myjsonschema;
	}

}
