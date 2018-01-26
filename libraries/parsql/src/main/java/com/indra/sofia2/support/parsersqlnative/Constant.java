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

public class Constant implements Exp {

	private static final long serialVersionUID = 1L;
	
	public static final int UNKNOWN = -1;
	public static final int COLUMNNAME = 0;
	public static final int NULL = 1;
	public static final int NUMBER = 2;
	public static final int STRING = 3;

	int type = Constant.UNKNOWN;
	String value = null;

	public Constant(String v, int typ) {
		value = v;
		type = typ;
	}

	public String getValue() { 
		return value; 
		}

	public int getType() { 
		return type; 
		}

	public String toString() {
		if(type == STRING) return '\'' + value + '\'';
		else return value;
	}
};

