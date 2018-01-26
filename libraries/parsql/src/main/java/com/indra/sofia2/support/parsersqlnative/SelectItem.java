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

import com.indra.sofia2.support.parsersqlnative.util.Utils;

public class SelectItem extends AliasedName {

	private static final long serialVersionUID = 1L;
	
	Exp expression = null;
	String aggregate = null;

	public SelectItem() { super(); }

	public SelectItem(String fullname) {
		super(fullname, AliasedName.FORM_COLUMN);
		setAggregate(Utils.getAggregateCall(fullname)); 
	}

	public Exp getExpression() {
		if (isExpression()) {
			return expression;
		} else if(isWildcard()) {
			return null;
		} else {
			return new Constant(getColumn(), Constant.COLUMNNAME);
		}
	}

	public void setExpression(Exp e) {
		expression = e;
		strform = expression.toString();
	}

	public boolean isExpression() { return (expression != null && expression instanceof Expression); }

	public void setAggregate(String a) { aggregate = a; }

	public String getAggregate() { return aggregate; }

}

