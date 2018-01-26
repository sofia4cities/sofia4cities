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
package com.indra.sofia2.support.parsersqlnative;


public class Delete implements Statement {

	private static final long serialVersionUID = 1L;
	
	String table;
	Exp where = null;

	public Delete(String tab) {
		table = tab;
	}

	public void addWhere(Exp w) { 
		where = w; 
		}

	public String getTable() { 
		return table; 
		}

	public Exp getWhere() { 
		return where; 
		}

	public String toString() {
		StringBuffer buf = new StringBuffer("delete ");
		if(where != null) 
			buf.append("from ");
		buf.append(table);
		if(where != null) 
			buf.append(" where " + where.toString());
		return buf.toString();
	}
};

