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
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.plugins.dto.audit;

public enum AuditType {
	
	DEBUG("DEBUG"),
	INFO("INFO"),
	WARN("WARN"),
	ERROR_NoResultException("ERROR"),
	ERROR_ProcessorException("ERROR"),
	ERROR_PersistenceException("ERROR"),
	ERROR_AuthenticationException("ERROR"),
	ERROR_AuthorizationServiceException("ERROR"),
	ERROR_BDCConnectionException("ERROR"),
	ERROR("ERROR"),
	FATAL("FATAL");
		
	private String traceLevel;
	
	private AuditType(String level){
		this.traceLevel=level;
	}
	
	public String getTraceLevel(){
		return this.traceLevel;
	}
	
}
