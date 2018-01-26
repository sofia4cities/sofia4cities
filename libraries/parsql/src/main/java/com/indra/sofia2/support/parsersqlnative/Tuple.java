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

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Tuple {


	private Vector attributes;

	private Vector values;

	private Hashtable searchTable;

	public Tuple() {
		attributes=new Vector();
		values=new Vector();
		searchTable = new Hashtable();
	}

	public Tuple(String colnames) {
		this();
		StringTokenizer st = new StringTokenizer(colnames, ",");
		while(st.hasMoreTokens()) {
			setAttribute(st.nextToken().trim(), null);
		}
	}

	public void setRow(String row) {
		StringTokenizer st = new StringTokenizer(row, ",");
		for(int i = 0; st.hasMoreTokens(); i++) {
			String val = st.nextToken().trim();
			try {
				Double d = new Double(val);
				setAttribute(getAttributeName(i), d);
			} catch(Exception e) {
				setAttribute(getAttributeName(i), val);
			}
		}
	}

	public void setRow(Vector row) {
		for(int i = 0; i < row.size(); i++) {
			setAttribute(getAttributeName(i), row.elementAt(i));
		}
	}

	public void setAttribute(String name, Object value)
	{
		if(name != null)
		{
			boolean exist = searchTable.containsKey(name);

			if(exist)
			{
				int i = ((Integer)searchTable.get(name)).intValue();
				values.setElementAt(value,i);
			}
			else
			{
				int i = attributes.size();
				attributes.addElement(name);
				values.addElement(value);
				searchTable.put(name, Integer.valueOf(i));
			}
		}
	}

	public String getAttributeName(int index)
	{
		try
		{
			return (String)attributes.elementAt(index);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public int getAttributeIndex(String name)
	{
		if(name == null) {
			return -1;
		}
		Integer index = (Integer)searchTable.get(name);
		if(index != null) {
			return index.intValue();
		} else {
			return -1;
		}
	}

	public Object getAttributeValue(int index)
	{
		try
		{
			return values.elementAt(index);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public Object getAttValue(String name)
	{
		boolean exist = false;

		if (name != null) {
			exist = searchTable.containsKey(name);
		}

		if (exist) {
			int index = ((Integer)searchTable.get(name)).intValue();
			return values.elementAt(index);
		} else {
			return null;
		}
	}

	public boolean isAttribute(String attrName)
	{
		if (attrName != null) {
			return searchTable.containsKey(attrName);
		} else {
			return false;
		}
	}

	public int getNumAttributes()
	{
		return values.size();
	}

	public String toString()
	{
		Object attribute;
		Object value;
		String attS;
		String valueS;

		StringBuffer resp = new StringBuffer();
		resp.append("[");
		if(attributes.size() > 0)
		{
			attribute = attributes.elementAt(0);
			if (attribute==null) {
				attS = "(null)";
			} else {
				attS = attribute.toString();
			}

			value = values.elementAt(0);
			if (value==null) {
				valueS = "(null)";
			} else {
				valueS = value.toString();
			}
			resp.append(attS +" = "+ valueS);
		}

		for(int i=1; i < attributes.size(); i++)
		{
			attribute = attributes.elementAt(i);
			if (attribute==null) {
				attS = "(null)";
			} else {
				attS = attribute.toString();
			}

			value = values.elementAt(i);
			if (value == null) {
				valueS = "(null)";
			} else {
				valueS = value.toString();
			}
			resp.append(", "+ attS + " = "+ valueS);
		}
		resp.append("]");
		return resp.toString();
	}

};

