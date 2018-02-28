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
package com.indracompany.sofia2.flowengine.api.rest.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@ToString()
public class DeployRequestRecord {

	@Getter
	@Setter
	private String domain;
	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String type;
	@Getter
	@Setter
	private String label;
	@Getter
	@Setter
	private String z;
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String topic;
	@Getter
	@Setter
	@JsonProperty("direccion")
	private String direction;
	@Getter
	@Setter
	@JsonProperty("tipomensaje")
	private String meassageType;
	@Getter
	@Setter
	private String ontology;
	@Getter
	@Setter
	private String token;
	@Getter
	@Setter
	private String thinKp;
	@Getter
	@Setter
	@JsonProperty("instanciakp")
	private String kpInstance;
	@Getter
	@Setter
	private String query;
	@Getter
	@Setter
	@JsonProperty("tipoQuery")
	private String QueryType;
	@Getter
	@Setter
	private String msRefresh;
	@Getter
	@Setter
	private String pingQuery;
	@Getter
	@Setter
	private String pingType;
	@Getter
	@Setter
	private String pingTimer;
	@Getter
	@Setter
	private String url;
}
