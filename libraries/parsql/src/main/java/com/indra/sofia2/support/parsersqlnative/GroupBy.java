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
public class GroupBy implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	Vector groupby;
	Exp having = null;

	public GroupBy(Vector exps) { 
		groupby = exps; 
		}

	public void setHaving(Exp e) { 
		having = e; 
		}

	public Vector getGroupBy() {
		return groupby; 
		}

	public Exp getHaving() { 
		return having; 
		}

	public String toString() {
		StringBuffer buf = new StringBuffer("group by ");

		buf.append(groupby.elementAt(0).toString());
		for(int i=1; i<groupby.size(); i++) {
			buf.append(", " + groupby.elementAt(i).toString());
		}
		if(having != null) {
			buf.append(" having " + having.toString());
		}
		return buf.toString();
	}
};

