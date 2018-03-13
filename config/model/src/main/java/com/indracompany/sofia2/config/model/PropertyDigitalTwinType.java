/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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

@Configurable
@Entity
@Table(name = "PROPERTY_DIGITAL_TWIN_TYPE")
public class PropertyDigitalTwinType extends AuditableEntityWithUUID{
	
	private static final long serialVersionUID = 1L;
	
	public static enum Direction {
		IN,OUT,IN_OUT
	}
	
	@ManyToOne
	@JoinColumn(name = "TYPE_ID", referencedColumnName = "ID", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@Getter
	@Setter
	private DigitalTwinType typeId;

	@Column(name = "NAME", length = 50, unique = true, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String name;
	
	@Column(name = "TYPE", length = 50, nullable = false)
	@NotNull
	@Getter
	@Setter
	private String type;
	
	@Column(name = "DESCRIPTION", length = 512)
	@Getter
	@Setter
	private String description;
	
	@Column(name = "UNIT", length = 50)
	@Getter
	@Setter
	private String unit;
	
	@Column(name = "DIRECTION", length = 50)
	@Getter
	private String direction;
	
	@Column(name = "HREF", length = 500)
	@Getter
	@Setter
	private String href;
	
	public void setDirection(PropertyDigitalTwinType.Direction direction) {
		this.direction = direction.toString();
	}
}
