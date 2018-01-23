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

public class SIBInstance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private String hostName;
	private String instanceIdentificer;
	private InstanceStatus status;
	
	public SIBInstance(String ip, String instanceIdentificer, InstanceStatus status){
		this.ip=ip;
		this.instanceIdentificer=instanceIdentificer;
		this.status=status;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

	public static SIBInstance fromJsonToSIBInstance(String json) {
        return new JSONDeserializer<SIBInstance>().use(null, SIBInstance.class).deserialize(json);
    }

	public static String toJsonArray(Collection<SIBInstance> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<SIBInstance> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<SIBInstance> fromJsonArrayToSIBInstances(String json) {
        return new JSONDeserializer<List<SIBInstance>>().use(null, ArrayList.class).use("values", SIBInstance.class).deserialize(json);
    }

	public String getIp() {
        return this.ip;
    }

	public void setIp(String ip) {
        this.ip = ip;
    }

	public String getHostName() {
        return this.hostName;
    }

	public void setHostName(String hostName) {
        this.hostName = hostName;
    }

	public String getInstanceIdentificer() {
        return this.instanceIdentificer;
    }

	public void setInstanceIdentificer(String instanceIdentificer) {
        this.instanceIdentificer = instanceIdentificer;
    }

	public InstanceStatus getStatus() {
        return this.status;
    }

	public void setStatus(InstanceStatus status) {
        this.status = status;
    }
}
