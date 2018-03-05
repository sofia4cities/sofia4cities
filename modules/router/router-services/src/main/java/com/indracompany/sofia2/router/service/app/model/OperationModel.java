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

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


public class OperationModel implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static enum OperationType {
		INSERT, UPDATE, DELETE, QUERY, SUBSCRIBE, GET, PUT, POST;
	}
	
	public static enum QueryType {
		SQLLIKE,NATIVE,NONE;
	}

	@Getter
	@Setter
	private String ontologyName;
	
	@Getter
	@Setter
	private OperationType operationType;
	
	@Getter
	@Setter
	private QueryType queryType;
	
	@Getter
	@Setter
	private String body;
	
	@Getter
	@Setter
	private String objectId;
	
	@Getter
	@Setter
	private String user;

	@Override
	public String toString() {
		return "OperationModel [ontologyName=" + ontologyName + ", operationType=" + operationType + ", queryType="
				+ queryType + ", body=" + body + ", objectId=" + objectId + ", user=" + user + "]";
	}

	


	

	
}