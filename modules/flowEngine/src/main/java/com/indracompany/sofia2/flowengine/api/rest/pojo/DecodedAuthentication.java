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

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class DecodedAuthentication {

	@Getter
	@Setter
	@JsonProperty("id_usuario")
	private String userId;

	@Getter
	@Setter
	private String password;

	public DecodedAuthentication(String authentication) {
		String auth = new String(Base64.getDecoder().decode(authentication));
		this.userId = auth.split(":")[0];
		this.password = auth.split(":")[1];
	}
}
