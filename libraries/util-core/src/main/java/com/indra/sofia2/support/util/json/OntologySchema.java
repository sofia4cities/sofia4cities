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
package com.indra.sofia2.support.util.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntologySchema implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, String> attributes;
	private ArrayList<String> attributeNames;
	
	public static final String OID_KEY = "$oid";
	
	private final static Logger LOG = LoggerFactory.getLogger(OntologySchema.class);
	
	public OntologySchema(){
		this.attributeNames = new ArrayList<String>();
		this.attributes = new HashMap<String, String>();
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public ArrayList<String> getAttributeNames() {
		return attributeNames;
	}
	
	public void addAttribute(String name, String value){
		this.attributeNames.add(name);
		this.attributes.put(name, value);
	}
	
	public String getAttributeValue(String name){
		return this.attributes.get(name);
	}
	
	public boolean containsAttribute(String name){
		return this.attributes.containsKey(name);
	}
	
	public void convertToOntologyMap(){
		if (this.attributeNames.contains(OID_KEY)){
			LOG.debug(String.format("The %s attribute is already referred by the attribute id", OID_KEY));
			this.attributes.remove(OID_KEY);
			this.attributeNames.remove(OID_KEY);
		}
	}
}
