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

public class SIBConnectedKP implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sibName;
	private List<ConnectedKP> connectedKps;


	public String getSibName() {
        return this.sibName;
    }

	public void setSibName(String sibName) {
        this.sibName = sibName;
    }

	public List<ConnectedKP> getConnectedKps() {
        return this.connectedKps;
    }

	public void setConnectedKps(List<ConnectedKP> connectedKps) {
        this.connectedKps = connectedKps;
    }

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

	public static SIBConnectedKP fromJsonToSIBConnectedKP(String json) {
        return new JSONDeserializer<SIBConnectedKP>().use(null, SIBConnectedKP.class).deserialize(json);
    }

	public static String toJsonArray(Collection<SIBConnectedKP> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<SIBConnectedKP> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<SIBConnectedKP> fromJsonArrayToSIBConnectedKPs(String json) {
        return new JSONDeserializer<List<SIBConnectedKP>>().use(null, ArrayList.class).use("values", SIBConnectedKP.class).deserialize(json);
    }
}
