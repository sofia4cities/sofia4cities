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

import com.indra.sofia2.support.parsersqlnative.util.Utils;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Expression implements Exp {

	private static final long serialVersionUID = 1L;
	
	String operator = null;
	Vector operands = null;

	public Expression(String op) {
		operator = op;
	}

	public Expression(String op, Exp o1) {
		operator = op;
		addOperand(o1);
	}

	public Expression(String op, Exp o1, Exp o2) {
		operator = op;
		addOperand(o1);
		addOperand(o2);
	}

	public String getOperator() { return operator; }

	public void setOperands(Vector v) {
		operands = v;
	}

	public Vector getOperands() {
		return operands;
	}

	public void addOperand(Exp o) {
		if(operands == null) operands = new Vector();
		operands.addElement(o);
	}

	public Exp getOperand(int pos) {
		if(operands == null || pos >= operands.size()) return null;
		return (Exp)operands.elementAt(pos);
	}

	public int nbOperands() {
		if(operands == null) return 0;
		return operands.size();
	}

	public String toReversePolish() {
		StringBuffer buf = new StringBuffer("(");
		buf.append(operator);
		for(int i = 0; i < nbOperands(); i++) {
			Exp opr = getOperand(i);
			if(opr instanceof Expression)
				buf.append(" " + ((Expression)opr).toReversePolish()); 
			else if(opr instanceof Query)
				buf.append(" (" + opr.toString() + ")");
			else
				buf.append(" " + opr.toString());
		}
		buf.append(")");
		return buf.toString();
	}

	public String toString() {

		if(operator.equals("?")) return operator;

		if(Utils.isCustomFunction(operator) >= 0)
			return formatFunction();

		StringBuffer buf = new StringBuffer();
		if(needPar(operator)) 
			buf.append("(");

		Exp operand;
		switch(nbOperands()) {

		case 1:
			operand = getOperand(0);
			if(operand instanceof Constant) {
				// Operator may be an aggregate function (MAX, SUM...)
				if(Utils.isAggregate(operator))
					buf.append(operator + "(" + operand.toString() + ")");
				else if(operator.equals("IS NULL") || operator.equals("IS NOT NULL"))
					buf.append(operand.toString() + " " + operator);
				// "," = list of values, here just one single value
				else if(operator.equals(",")) buf.append(operand.toString());
				else buf.append(operator + " " + operand.toString());
			} else if(operand instanceof Query) {
				buf.append(operator + " (" + operand.toString() + ")");
			} else {
				if(operator.equals("IS NULL") || operator.equals("IS NOT NULL"))
					buf.append(operand.toString() + " " + operator);
				// "," = list of values, here just one single value
				else if(operator.equals(",")) buf.append(operand.toString());
				else buf.append(operator + " " + operand.toString());
			}
			break;

		case 3:
			if(operator.toUpperCase().endsWith("BETWEEN")) {
				buf.append(getOperand(0).toString() + " " + operator + " "
						+ getOperand(1).toString()
						+ " AND " + getOperand(2).toString()); 
				break;
			}

		default:

			boolean in_op = operator.equals("IN") || operator.equals("NOT IN");

			int nb = nbOperands();
			for(int i = 0; i < nb; i++) {

				if(in_op && i==1) buf.append(" " + operator + " (");

				operand = getOperand(i);
				if(operand instanceof Query && !in_op) {
					buf.append("(" + operand.toString() + ")");
				} else {
					buf.append(operand.toString());
				}
				if(i < nb-1) {
					if(operator.equals(",") || (in_op && i>0)) buf.append(", ");
					else if(!in_op) buf.append(" " + operator + " ");
				}
			}
			if(in_op) buf.append(")");
			break;
		}

		if(needPar(operator)) buf.append(")");
		return buf.toString();
	}

	private boolean needPar(String op) {
		String tmp = op.toUpperCase();
		return ! (tmp.equals("ANY") || tmp.equals("ALL")
				|| tmp.equals("UNION") || Utils.isAggregate(tmp));
	}

	private String formatFunction() {
		StringBuffer b = new StringBuffer(operator + "(");
		int nb = nbOperands();
		for(int i = 0; i < nb; i++) {
			b.append(getOperand(i).toString() + (i < nb-1 ? "," : ""));
		}
		b.append(")");
		return b.toString();
	}
}

