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
package com.indra.sofia2.support.util.sib.mail;

import java.util.Collection;

public class TemplateVariable<T> {
	
	private String name;
	private T simpleValue;
	private Collection<T> collectionValue;
	
	private TemplateVariable(String name){
		this.name = name;
	}
	
	public TemplateVariable(String name, T simpleValue){
		this(name);
		this.simpleValue = simpleValue;
	}
	
	public TemplateVariable(String name, Collection<T> collectionValue){
		this(name);
		this.collectionValue = collectionValue;
	}
	
	public String getName() {
		return name;
	}
	public T getStringValue() {
		return simpleValue;
	}
	public Collection<T> getCollectionValue() {
		return collectionValue;
	}
}
