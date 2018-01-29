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
package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CONFIGURATION_TYPE")
@Configurable
public class ConfigurationType {
	@Id
	@Column(name = "ID")
	@Getter @Setter private Integer id;    

	@Column(name = "NAME", length = 24, unique = true,nullable = false)
	@NotNull
	@Getter @Setter private String name;

	@Column(name = "DESCRIPTION", length = 255)
	@Getter @Setter private String description;
}
