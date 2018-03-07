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
package com.indracompany.sofia2.router.service.app.model;

import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;

import lombok.Getter;
import lombok.Setter;

public class SuscriptionModel {
	
	public static enum OperationType {
		SUSCRIBE,UNSUSCRIBE;
	}
	
	public static enum QueryType {
		SQLLIKE,NATIVE;
	}
	
	@Getter
	@Setter
	private OperationType operationType;
	
	@Getter
	@Setter
	private String ontologyName;
	
	@Getter
	@Setter
	private String sessionKey;
	
	@Getter
	@Setter
	private String suscriptionId;
	
	@Getter
	@Setter
	private String query;
	
	@Getter
	@Setter
	private QueryType queryType;
	
	@Getter
	@Setter
	private String user;
}
