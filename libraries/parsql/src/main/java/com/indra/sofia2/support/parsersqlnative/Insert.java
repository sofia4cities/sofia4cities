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

import java.util.Vector;

@SuppressWarnings("rawtypes")
public class Insert implements Statement {

	private static final long serialVersionUID = 1L;
	
	String table;
	Vector columns = null;
	Exp valueSpec = null;

	public Insert(String tab) {
		table = tab;
	}

	public String getTable() {
		return table;
	}

	public Vector getColumns() {
		return columns;
	}

	public void addColumns(Vector c) { columns = c; }

	public void addValueSpec(Exp e) { valueSpec = e; }

	public Vector getValues() {
		if(! (valueSpec instanceof Expression)) return null;
		return ((Expression)valueSpec).getOperands();
	}

	public Query getQuery() {
		if(! (valueSpec instanceof Query)) return null;
		return (Query)valueSpec;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("insert into " + table);
		if(columns != null && columns.size() > 0) {
			buf.append("(" + columns.elementAt(0));
			for(int i=1; i<columns.size(); i++) {
				buf.append("," + columns.elementAt(i));
			}
			buf.append(")");
		}

		String vlist = valueSpec.toString();
		buf.append(" ");
		if(getValues() != null)
			buf.append("values ");
		if(vlist.startsWith("(")) buf.append(vlist);
		else buf.append(" (" + vlist + ")");

		return buf.toString();
	}
};

