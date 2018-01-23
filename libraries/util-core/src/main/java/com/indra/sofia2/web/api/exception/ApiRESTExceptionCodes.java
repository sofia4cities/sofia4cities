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
package com.indra.sofia2.web.api.exception;

public enum ApiRESTExceptionCodes {
	
	/*
	 * Response error codes
	 */
	MAC_NOT_EXISTS (1000),
	NUMSERIE_NOT_EXISTS (1001),
	ASSET_NOT_EXISTS (1002),
	ASSET_HAS_NO_HIERARCHY (1003),
	ASSET_DISABLED (1004),
	USER_NOT_EXISTS (1005),
	USER_NOT_ASSOCIATED_TO_ASSET (1006),
	USER_HAS_NO_ASSETS (1007),
	PERSISTENCE_ERROR (1008),
	ILLEGALARGUMENTEXCEPTION(1009),
	AUTHORIZATIONSERVICEEXCEPTION(1010),
	ASSET_WITHOUT_USER(1011),
	NO_ASSETS_TAGGED(1012),
	NO_HIERARCHY_TAGS(1013),
	NO_ASSETS_FOUND_WITH_TAG(1014),
	NO_TAGS_ASSOCIATED_TO_ASSET(1015),
    ALARM_NOT_EXISTS(1016),
    CACHE_UPDATE_ERROR(1017),
	RESOURCE_LIMIT_REACHED(1021),
	INVALID_IDENTIFIER(1022),
	ROLLBACK_ERROR(1024),
	NOTIFICATION_TIMEOUT(1025),
	ONTOLOGY_DOES_NOT_EXIST(1026),
	SCRIPT_NOT_FOUND(1027),
	INVALID_CACHE_STATE(1028),
	INVALID_QUERY(1029), 
	ASSET_ALREADY_EXISTS(1030),
	ILLEGAL_ARGUMENT(1031);
	
	
	private final int code;
	
	public int getCode() {
		return this.code;
	}
	
	public String getStringCode() {
		return String.valueOf(this.code);
	}
	
	private ApiRESTExceptionCodes(int code) {
		this.code = code;
	}
}
