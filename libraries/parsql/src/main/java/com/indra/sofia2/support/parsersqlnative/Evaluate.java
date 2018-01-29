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

import java.sql.SQLException;
import java.util.Vector;

@SuppressWarnings("rawtypes")
public class Evaluate {

	public boolean evaluate(Tuple tuple, Exp exp) throws SQLException {

		if(tuple == null || exp == null)  {
			throw new SQLException("Evaluate.evaluate(): null argument or operator");
		}
		if(! (exp instanceof Expression))
			throw new SQLException("Evaluate.evaluate(): only expressions are supported");

		Expression pred = (Expression)exp;
		String op = pred.getOperator();

		if(op.equals("AND")) {
			boolean and = true;
			for(int i = 0; i<pred.nbOperands(); i++) {
				and &= evaluate(tuple, pred.getOperand(i));
			}
			return and;
		} else if(op.equals("OR")) {
			boolean or = false;
			for(int i = 0; i<pred.nbOperands(); i++) {
				or |= evaluate(tuple, pred.getOperand(i));
			}
			return or;
		} else if(op.equals("NOT")) {
			return ! evaluate(tuple, pred.getOperand(0));

		} else if(op.equals("=")) {
			return evalCompare(tuple, pred.getOperands()) == 0;
		} else if(op.equals("!=")) {
			return evalCompare(tuple, pred.getOperands()) != 0;
		} else if(op.equals("<>")) {
			return evalCompare(tuple, pred.getOperands()) != 0;
		} else if(op.equals("#")) {
			throw new SQLException("Evaluate.evaluate(): Operator # not supported");
		} else if(op.equals(">")) {
			return evalCompare(tuple, pred.getOperands()) > 0;
		} else if(op.equals(">=")) {
			return evalCompare(tuple, pred.getOperands()) >= 0;
		} else if(op.equals("<")) {
			return evalCompare(tuple, pred.getOperands()) < 0;
		} else if(op.equals("<=")) {
			return evalCompare(tuple, pred.getOperands()) <= 0;

		} else if(op.equals("BETWEEN") || op.equals("NOT BETWEEN")) {

			Expression newexp = new Expression("AND", 
					new Expression(">=", pred.getOperand(0), pred.getOperand(1)),
					new Expression("<=", pred.getOperand(0), pred.getOperand(2)));

			if(op.equals("NOT BETWEEN"))
				return ! evaluate(tuple, newexp);
			else
				return evaluate(tuple, newexp);

		} else if(op.equals("LIKE") || op.equals("NOT LIKE")) {
			boolean like = evalLike(tuple, pred.getOperands());
			return op.equals("LIKE") ? like : !like;

		} else if(op.equals("IN") || op.equals("NOT IN")) {

			Expression newexp = new Expression("OR");

			for(int i = 1; i < pred.nbOperands(); i++) {
				newexp.addOperand(new Expression("=",
						pred.getOperand(0), pred.getOperand(i)));
			}

			if(op.equals("NOT IN"))
				return ! evaluate(tuple, newexp);
			else
				return evaluate(tuple, newexp);

		} else if(op.equals("IS NULL")) {

			if(pred.nbOperands() <= 0 || pred.getOperand(0) == null) return true;
			Exp x = pred.getOperand(0);
			if(x instanceof Constant) {
				return (((Constant)x).getType() == Constant.NULL);
			} else {
				throw new SQLException("Evaluate.evaluate(): can't eval IS (NOT) NULL");
			}

		} else if(op.equals("IS NOT NULL")) {

			Expression x = new Expression("IS NULL");
			x.setOperands(pred.getOperands());
			return ! evaluate(tuple, x);

		} else {
			throw new SQLException("Evaluate.evaluate(): Unknown operator " + op);
		}

	}

	double evalCompare(Tuple tuple, Vector operands) throws SQLException {

		if(operands.size() < 2) {
			throw new SQLException(
					"Evaluate.evalCompare(): Trying to compare less than two values");
		}
		if(operands.size() > 2) {
			throw new SQLException(
					"Evaluate.evalCompare(): Trying to compare more than two values");
		}

		Object object1 = null, object2 = null;

		object1 = evalExpValue(tuple, (Exp)operands.elementAt(0));
		object2 = evalExpValue(tuple, (Exp)operands.elementAt(1));

		if(object1 instanceof String || object2 instanceof String) {
			return(object1.equals(object2) ? 0 : -1);
		}

		if(object1 instanceof Number && object2 instanceof Number) {
			return ((Number)object1).doubleValue() - ((Number)object2).doubleValue();
		} else {
			throw new SQLException("Evaluate.evalCompare(): can't compare (" + object1.toString()
					+ ") with (" + object2.toString() + ")");
		}
	}

	private boolean evalLike(Tuple tuple, Vector operands) throws SQLException
	{
		if(operands.size() < 2) {
			throw new SQLException(
					"Evaluate.evalCompare(): Trying to compare less than two values");
		}
		if(operands.size() > 2) {
			throw new SQLException(
					"Evaluate.evalCompare(): Trying to compare more than two values");
		}

		Object o1 = evalExpValue(tuple, (Exp)operands.elementAt(0));
		Object o2 = evalExpValue(tuple, (Exp)operands.elementAt(1));

		if ( (o1 instanceof String) && (o2 instanceof String) ) {
			String s1 = (String)o1;
			String s2 = (String)o2;
			if ( s2.startsWith("%") ) {
				return s1.endsWith(s2.substring(1));
			} else if ( s2.endsWith("%") ) {
				return s1.startsWith(s2.substring(0,s2.length()-1));
			} else {
				return s1.equalsIgnoreCase(s2);
			}
		}
		else {
			throw new SQLException("Evaluate.evalLike(): LIKE can only compare strings");
		}

	}

	double evalNumericExp(Tuple tuple, Expression exp)
			throws SQLException {

		if(tuple == null || exp == null || exp.getOperator() == null)  {
			throw new SQLException("Evaluate.evaluate(): null argument or operator");
		}

		String op = exp.getOperator();

		Object o1 = evalExpValue(tuple, (Exp)exp.getOperand(0));
		if(! (o1 instanceof Double))
			throw new SQLException("Evaluate.evalNumericExp(): expression not numeric");
		Double dobj = (Double)o1;

		if(op.equals("+")) {

			double val = dobj.doubleValue();
			for(int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, (Exp)exp.getOperand(i));
				val += ((Number)obj).doubleValue();
			}
			return val;

		} else if(op.equals("-")) {

			double val = dobj.doubleValue();
			if(exp.nbOperands() == 1) return -val;
			for(int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, (Exp)exp.getOperand(i));
				val -= ((Number)obj).doubleValue();
			}
			return val;

		} else if(op.equals("*")) {

			double val = dobj.doubleValue();
			for(int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, (Exp)exp.getOperand(i));
				val *= ((Number)obj).doubleValue();
			}
			return val;

		} else if(op.equals("/")) {

			double val = dobj.doubleValue();
			for(int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, (Exp)exp.getOperand(i));
				val /= ((Number)obj).doubleValue();
			}
			return val;

		} else if(op.equals("**")) {

			double val = dobj.doubleValue();
			for(int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, (Exp)exp.getOperand(i));
				val = Math.pow(val, ((Number)obj).doubleValue());
			}
			return val;

		} else {
			throw new SQLException("Evaluate.evalNumericExp(): Unknown operator " + op);
		}
	}

	public Object evalExpValue(Tuple tuple, Exp exp) throws SQLException {

		Object o2 = null;

		if(exp instanceof Constant) {

			Constant c = (Constant)exp;

			switch(c.getType()) {

			case Constant.COLUMNNAME:

				Object o1 = tuple.getAttValue(c.getValue());
				if(o1 == null)
					throw new SQLException("Evaluate.evalExpValue(): unknown column "
							+ c.getValue());
				try {
					o2 = new Double(o1.toString());
				} catch(NumberFormatException e) {
					o2 = o1;
				}
				break;

			case Constant.NUMBER:
				o2 = new Double(c.getValue());
				break;

			case Constant.STRING:
			default:
				o2 = c.getValue();
				break;
			}
		} else if(exp instanceof Expression) {
			o2 = new Double(evalNumericExp(tuple, (Expression)exp));
		}
		return o2;
	}


}
