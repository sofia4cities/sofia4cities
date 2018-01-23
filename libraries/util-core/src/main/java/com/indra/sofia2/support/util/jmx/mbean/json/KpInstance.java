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
package com.indra.sofia2.support.util.jmx.mbean.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class KpInstance implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String kpIdentifier;
	private String instanceIdentifier;
	private String sessionkey;
	
	public KpInstance(String kpIdentifier, String instanceIdentifier,
			String sessionkey) {
		super();
		this.kpIdentifier = kpIdentifier;
		this.instanceIdentifier = instanceIdentifier;
		this.sessionkey = sessionkey;
	}

	@Override
	public String toString() {
		return "KpInstance [kpIdentifier=" + kpIdentifier
				+ ", instanceIdentifier=" + instanceIdentifier
				+ ", sessionkey=" + sessionkey + "]";
	}
	
	
	

	

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

	public static KpInstance fromJsonToKpInstance(String json) {
        return new JSONDeserializer<KpInstance>().use(null, KpInstance.class).deserialize(json);
    }

	public static String toJsonArray(Collection<KpInstance> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<KpInstance> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<KpInstance> fromJsonArrayToKpInstances(String json) {
        return new JSONDeserializer<List<KpInstance>>().use(null, ArrayList.class).use("values", KpInstance.class).deserialize(json);
    }

	public String getKpIdentifier() {
        return this.kpIdentifier;
    }

	public void setKpIdentifier(String kpIdentifier) {
        this.kpIdentifier = kpIdentifier;
    }

	public String getInstanceIdentifier() {
        return this.instanceIdentifier;
    }

	public void setInstanceIdentifier(String instanceIdentifier) {
        this.instanceIdentifier = instanceIdentifier;
    }

	public String getSessionkey() {
        return this.sessionkey;
    }

	public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }
}
