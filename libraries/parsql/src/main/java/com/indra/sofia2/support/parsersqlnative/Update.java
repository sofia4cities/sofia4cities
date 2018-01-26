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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Update implements Statement {

	private static final long serialVersionUID = 1L;
	
	String tableU;
	String aliasU = null;
	Hashtable setU;
	Exp whereU = null;
	Vector columnsU = null;

	public Update(String tab) {
		tableU = tab;
	}

	public String getTable() {
		return tableU;
	}

	public void setAlias(String alias) { aliasU = alias; }
	public String getAlias() { return aliasU; }

	public void addSet(Hashtable t) {
		setU = t;
	}

	public Hashtable getSet() { return setU; }

	public void addColumnUpdate(String col, Exp val) {
		if (setU == null) {
			setU = new Hashtable();
		}
		setU.put(col, val);
		if (columnsU == null) {
			columnsU = new Vector();
		}
		columnsU.addElement(col);
	}

	public Exp getColumnUpdate(String col) { return (Exp)setU.get(col); }

	public Exp getColumnUpdate(int index) {
		if (index <= 0) {
			return null;
		}
		if (columnsU == null || index >= columnsU.size()) {
			return null;
		}
		String col = (String)columnsU.elementAt(index);
		return (Exp)setU.get(col);
	}

	public String getColumnUpdateName(int index) {
		if (index <= 0) {
			return null;
		}
		if(columnsU == null || index >= columnsU.size()) {
			return null;
		}
		return (String)columnsU.elementAt(index);
	}

	public int getColumnUpdateCount() {
		if (setU == null) {
			return 0;
		}
		return setU.size();
	}

	public void addWhere(Exp w) { whereU = w; }

	public Exp getWhere() { return whereU; }

	public String toString() {
		StringBuffer buf = new StringBuffer("update " + tableU);
		if (aliasU != null) {
			buf.append(" " + aliasU);
		}
		buf.append(" set ");

		Enumeration e;
		if (columnsU != null) {
			e = columnsU.elements();
		} else {
			e = setU.keys();
		}
		boolean first = true;
		while(e.hasMoreElements()) {
			String key = e.nextElement().toString();
			if (!first) {
				buf.append(", ");
			}
			buf.append(key + "=" + setU.get(key).toString()); 
			first = false;
		}

		if (whereU != null) {
			buf.append(" where " + whereU.toString());
		}
		return buf.toString();
	}
};

