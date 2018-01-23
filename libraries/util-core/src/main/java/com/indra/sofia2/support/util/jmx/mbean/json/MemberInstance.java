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


public class MemberInstance implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private String port;
	private String memberIdentifier;
	
	public MemberInstance(String ip, String port, String memberIdentifier) {
		this.ip = ip;
		this.port = port;
		this.memberIdentifier = memberIdentifier;
	}


	public String getIp() {
        return this.ip;
    }

	public void setIp(String ip) {
        this.ip = ip;
    }

	public String getPort() {
        return this.port;
    }

	public void setPort(String port) {
        this.port = port;
    }

	public String getMemberIdentifier() {
        return this.memberIdentifier;
    }

	public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

	public static MemberInstance fromJsonToMemberInstance(String json) {
        return new JSONDeserializer<MemberInstance>().use(null, MemberInstance.class).deserialize(json);
    }

	public static String toJsonArray(Collection<MemberInstance> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<MemberInstance> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<MemberInstance> fromJsonArrayToMemberInstances(String json) {
        return new JSONDeserializer<List<MemberInstance>>().use(null, ArrayList.class).use("values", MemberInstance.class).deserialize(json);
    }
}
