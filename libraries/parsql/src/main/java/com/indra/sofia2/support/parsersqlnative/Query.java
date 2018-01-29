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
public class Query implements Statement, Exp {

	private static final long serialVersionUID = 1L;
	
	Vector select;
	boolean distinct = false;
	Vector from;
	Exp where = null;
	GroupBy groupby = null;
	Expression setclause = null;
	Vector orderby = null;
	boolean forupdate = false;
	int limit;
	int skip;

	public Query() {}

	public void addSelect(Vector s) {
		select = s; }

	public void addFrom(Vector f) { 
		from = f; }

	public void addWhere(Exp w) {
		where = w; }

	public void addGroupBy(GroupBy g) { 
		groupby = g; }

	public void addSet(Expression s) { 
		setclause = s; }

	public void addOrderBy(Vector v) { 
		orderby = v; }

	public void addLimit(int l){
		limit = l;
	}
	public int getLimit(){
		return limit;
	}
	public void addSkip (int s){
		skip = s;
	}
	public int getSkip (){
		return skip;
	}
	public Vector getSelect() { 
		return select; }

	public Vector getFrom() { 
		return from; }

	public Exp getWhere() { 
		return where; }

	public GroupBy getGroupBy() { 
		return groupby; }

	public Expression getSet() { 
		return setclause; }

	public Vector getOrderBy() { 
		return orderby; }

	public boolean isDistinct() {
		return distinct; }

	public boolean isForUpdate() { 
		return forupdate; }


	public String toString() {
		StringBuffer buf = new StringBuffer("select ");
		if (distinct) {
			buf.append("distinct ");
		}

		int i;
		buf.append(select.elementAt(0).toString());
		for(i=1; i<select.size(); i++) {
			buf.append(", " + select.elementAt(i).toString());
		}

		buf.append(" from ");
		buf.append(from.elementAt(0).toString());
		for(i=1; i<from.size(); i++) {
			buf.append(", " + from.elementAt(i).toString());
		}

		if(where != null) {
			buf.append(" where " + where.toString());
		}
		if(groupby != null) {
			buf.append(" " + groupby.toString());
		}
		if(setclause != null) {
			buf.append(" " + setclause.toString());
		}
		if(orderby != null) {
			buf.append(" order by ");
			buf.append(orderby.elementAt(0).toString());
			for(i=1; i<orderby.size(); i++) {
				buf.append(", " + orderby.elementAt(i).toString());
			}
		}
		if (forupdate) {
			buf.append(" for update");
		}

		if (limit != 0) {
			buf.append(" limit " + limit);
		}
		if (skip != 0) {
			buf.append(" skip " + skip);
		}
		
		return buf.toString();
	}

};

