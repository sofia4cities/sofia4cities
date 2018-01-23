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
package com.indra.sofia2.support.util.authentication.token;

public class TokenVO {
	
	private String id;
	private String token;
	private boolean activo;
	private String kpId;


	public String getId() {
        return this.id;
    }

	public void setId(String id) {
        this.id = id;
    }

	public String getToken() {
        return this.token;
    }

	public void setToken(String token) {
        this.token = token;
    }

	public boolean isActivo() {
        return this.activo;
    }

	public void setActivo(boolean activo) {
        this.activo = activo;
    }

	public String getKpId() {
        return this.kpId;
    }

	public void setKpId(String kpId) {
        this.kpId = kpId;
    }
}
