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
package com.indracompany.sofia2.flowengine.nodered.communication.dto;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class FlowEngineDomainStatus {

	@Getter
	@Setter
	private String domain;

	@Getter
	@Setter
	private int port;

	@Getter
	@Setter
	private String home;

	@Getter
	@Setter
	private int servicePort;

	@Getter
	@Setter
	private String state;

	@Getter
	@Setter
	private String runtimeState;

	@Getter
	@Setter
	private String cpu;

	@Getter
	@Setter
	private String memory;

	public static Collection<FlowEngineDomainStatus> fromJsonArrayToDomainStatus(String json)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		return mapper.readValue(json, new TypeReference<List<FlowEngineDomainStatus>>() {
		});

	}
}
