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

import java.util.StringTokenizer;

public class AliasedName implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	String strform = "";
	String schema = null;
	String table = null;
	String column = null;
	String alias = null;

	public static final int FORM_TABLE = 1;
	public static final int FORM_COLUMN = 2;

	int form = FORM_COLUMN;

	public AliasedName() {}

	public AliasedName(String fullname, int form) {

		this.form = form;
		strform = fullname;

		StringTokenizer st = new StringTokenizer(fullname, ".");
		switch(st.countTokens()) {
		case 1:
			if(form == FORM_TABLE) {
				table = st.nextToken();
			} else {
				column = st.nextToken();
			}
			break;
		case 2:
			if(form == FORM_TABLE) {
				schema = st.nextToken();
				table = st.nextToken();
			} else {
				table = st.nextToken();
				column = st.nextToken();
			}
			break;
		case 3:
		default:
			schema = st.nextToken();
			table = st.nextToken();
			column = st.nextToken();
			break;
		}
		schema = postProcess(schema);
		table = postProcess(table);
		column = postProcess(column);
	}

	private String postProcess(String val) {
		if(val == null) {
			return null;
		}
		String myVal = val;
		if(myVal.indexOf("(") >= 0) {
			myVal = myVal.substring(myVal.lastIndexOf("(") + 1);
		}
		if(myVal.indexOf(")") >= 0) {
			myVal = myVal.substring(0, myVal.indexOf(")"));
		}
		return myVal.trim();
	}

	public String toString() {
		if(alias == null) {
			return strform;
		} else {
			return strform + " " + alias;
		}
	}

	public String getSchema() {
		return schema; 
	}

	public String getTable() {
		return table; 
	}

	public String getColumn() {
		return column; 
	}

	public boolean isWildcard() {
		if(form == FORM_TABLE) {
			return table != null && table.equals("*");
		} else {
			return column != null && column.indexOf('*') >= 0;
		}
	}

	public String getAlias() {
		return alias; 
	}

	public void setAlias(String a) { 
		alias = a; 
	}
}

