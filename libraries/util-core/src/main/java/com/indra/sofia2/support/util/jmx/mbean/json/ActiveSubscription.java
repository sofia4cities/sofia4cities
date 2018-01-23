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

public class ActiveSubscription implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String query;
	private List<KpInstance> kpInstances;
	private String sibIdentifier;
	private int numberOfKpsSubscribed;
	private int totalNumberOfSubscription;
	
	public ActiveSubscription(String query, List<KpInstance> kpInstances,
			String sibIdentifier, int numberOfKpsSubscribed,
			int totalNumberOfSubscription) {
		super();
		this.query = query;
		this.kpInstances = kpInstances;
		this.sibIdentifier = sibIdentifier;
		this.numberOfKpsSubscribed = numberOfKpsSubscribed;
		this.totalNumberOfSubscription = totalNumberOfSubscription;
	}

	@Override
	public String toString() {
		return "ActiveSubscription [query=" + query + ", kpInstances="
				+ kpInstances + ", sibIdentifier=" + sibIdentifier
				+ ", numberOfKpsSubscribed=" + numberOfKpsSubscribed
				+ ", totalNumberOfSubscription=" + totalNumberOfSubscription
				+ "]";
	}
	
	
	

	public String getQuery() {
        return this.query;
    }

	public void setQuery(String query) {
        this.query = query;
    }

	public List<KpInstance> getKpInstances() {
        return this.kpInstances;
    }

	public void setKpInstances(List<KpInstance> kpInstances) {
        this.kpInstances = kpInstances;
    }

	public String getSibIdentifier() {
        return this.sibIdentifier;
    }

	public void setSibIdentifier(String sibIdentifier) {
        this.sibIdentifier = sibIdentifier;
    }

	public int getNumberOfKpsSubscribed() {
        return this.numberOfKpsSubscribed;
    }

	public void setNumberOfKpsSubscribed(int numberOfKpsSubscribed) {
        this.numberOfKpsSubscribed = numberOfKpsSubscribed;
    }

	public int getTotalNumberOfSubscription() {
        return this.totalNumberOfSubscription;
    }

	public void setTotalNumberOfSubscription(int totalNumberOfSubscription) {
        this.totalNumberOfSubscription = totalNumberOfSubscription;
    }

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

	public static ActiveSubscription fromJsonToActiveSubscription(String json) {
        return new JSONDeserializer<ActiveSubscription>().use(null, ActiveSubscription.class).deserialize(json);
    }

	public static String toJsonArray(Collection<ActiveSubscription> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<ActiveSubscription> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<ActiveSubscription> fromJsonArrayToActiveSubscriptions(String json) {
        return new JSONDeserializer<List<ActiveSubscription>>().use(null, ArrayList.class).use("values", ActiveSubscription.class).deserialize(json);
    }
}
