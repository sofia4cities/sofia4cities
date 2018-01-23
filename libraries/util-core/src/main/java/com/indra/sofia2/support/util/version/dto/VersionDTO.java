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
package com.indra.sofia2.support.util.version.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;
import com.indra.sofia2.support.util.version.VersionProperties;

@ApiObject(name = "version")
@XmlRootElement(name = "version")
public class VersionDTO {

	@ApiObjectField(description = "Version")
	private String version;

	@ApiObjectField(description = "Timestamp")
	private String compilationDate;

	@ApiObjectField(description = "Nombre del módulo")
	private String moduleName;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCompilationDate() {
		return compilationDate;
	}

	public void setCompilationDate(String timestamp) {
		this.compilationDate = timestamp;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public static VersionDTO getInstance(){
		VersionDTO result = new VersionDTO();
		result.setVersion(ArqSpringContext.getVersionModulo());
		result.setModuleName(ArqSpringContext.getModulo());
		try {
			result.setCompilationDate(ArqSpringContext.getPropiedad(VersionProperties.COMPILATION_DATE));
		} catch (Exception e){}
		return result;
	}
}