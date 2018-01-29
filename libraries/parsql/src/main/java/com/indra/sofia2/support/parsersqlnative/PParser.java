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
import java.util.Vector;

import com.indra.sofia2.support.parsersqlnative.util.ParserConstants;
import com.indra.sofia2.support.parsersqlnative.util.Utils;

@SuppressWarnings({"unused", "rawtypes", "unchecked", "serial"})
public class PParser implements ParserConstants {

	public ParserTokenManager tokenSource;
	SimpleCharStream pInputStream;
	public Token token, pNextToken;
	private int pntk;
	private Token pScanPos, plastPos;
	private int plineAnalice;
	public boolean lookingAhead = false;
	private int pGen;
	final private int[] plineanalizce1 = new int[101];
	int limit = 0;
	int skip = 0;
	public String query;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public PParser(java.io.InputStream stream) {
		this(stream, null);
	}
	
	public PParser(java.io.InputStream stream, String encoding) {
		try { 
			pInputStream = new SimpleCharStream(stream, encoding, 1, 1); 
		} catch(java.io.UnsupportedEncodingException e) { 
			throw new RuntimeException(e); 
		}
		tokenSource = new ParserTokenManager(pInputStream);
		token = new Token();
		pntk = -1;
		pGen = 0;
		for (int i = 0; i < 101; i++) 
			plineanalizce1[i] = -1;
		for (int i = 0; i < p2Returns.length; i++) 
			p2Returns[i] = new PCalls();
	}

	public void reInit(java.io.InputStream stream) {
		reInit(stream, null);
	}
	
	public void reInit(java.io.InputStream stream, String encoding) {
		try { pInputStream.reInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
		tokenSource.reInit(pInputStream);
		token = new Token();
		pntk = -1;
		pGen = 0;
		for (int i = 0; i < 101; i++) plineanalizce1[i] = -1;
		for (int i = 0; i < p2Returns.length; i++) p2Returns[i] = new PCalls();
	}

	public PParser(java.io.Reader stream) {
		pInputStream = new SimpleCharStream(stream, 1, 1);
		tokenSource = new ParserTokenManager(pInputStream);
		token = new Token();
		pntk = -1;
		pGen = 0;
		for (int i = 0; i < 101; i++) plineanalizce1[i] = -1;
		for (int i = 0; i < p2Returns.length; i++) p2Returns[i] = new PCalls();
	}

	public void reInit(java.io.Reader stream) {
		pInputStream.reInit(stream, 1, 1);
		tokenSource.reInit(pInputStream);
		token = new Token();
		pntk = -1;
		pGen = 0;
		for (int i = 0; i < 101; i++) plineanalizce1[i] = -1;
		for (int i = 0; i < p2Returns.length; i++) p2Returns[i] = new PCalls();
	}

	public PParser(ParserTokenManager tm) {
		tokenSource = tm;
		token = new Token();
		pntk = -1;
		pGen = 0;
		for (int i = 0; i < 101; i++) plineanalizce1[i] = -1;
		for (int i = 0; i < p2Returns.length; i++) p2Returns[i] = new PCalls();
	}

	public void reInit(ParserTokenManager tm) {
		tokenSource = tm;
		token = new Token();
		pntk = -1;
		pGen = 0;
		for (int i = 0; i < 101; i++) plineanalizce1[i] = -1;
		for (int i = 0; i < p2Returns.length; i++) p2Returns[i] = new PCalls();
	}
	
	final public void basicDataTypeDeclaration() throws ParseException {
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_CHAR:
		case K_FLOAT:
		case K_INTEGER:
		case K_NATURAL:
		case K_NUMBER:
		case K_REAL:
		case K_VARCHAR2:
		case K_VARCHAR:
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_CHAR:
				pconsumeToken(K_CHAR);
				break;
			case K_VARCHAR:
				pconsumeToken(K_VARCHAR);
				break;
			case K_VARCHAR2:
				pconsumeToken(K_VARCHAR2);
				break;
			case K_INTEGER:
				pconsumeToken(K_INTEGER);
				break;
			case K_NUMBER:
				pconsumeToken(K_NUMBER);
				break;
			case K_NATURAL:
				pconsumeToken(K_NATURAL);
				break;
			case K_REAL:
				pconsumeToken(K_REAL);
				break;
			case K_FLOAT:
				pconsumeToken(K_FLOAT);
				break;
			default:
				plineanalizce1[0] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
			if (((pntk==-1)?pNextToken():pntk) == 88) {
				pconsumeToken(88);
				pconsumeToken(S_NUMBER);
				if (((pntk==-1)?pNextToken():pntk) == 89) {
					pconsumeToken(89);
					pconsumeToken(S_NUMBER);
				} else {
					plineanalizce1[1] = pGen;
					;
				}
				pconsumeToken(90);
			} else {
				plineanalizce1[2] = pGen;
				;
			}
			break;
		case K_DATE:
			pconsumeToken(K_DATE);
			break;
		case K_BINARY_INTEGER:
			pconsumeToken(K_BINARY_INTEGER);
			break;
		case K_BOOLEAN:
			pconsumeToken(K_BOOLEAN);
			break;
		default:
			plineanalizce1[3] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
	}

	final public Vector sQLStatements() throws ParseException {
		Vector v = new Vector();
		Statement s;
		label_1:
			while (true) {
				s = sQLStatement();
				
				if(s == null) {
					if (true) 
						return v;
				}
				else 
					v.addElement(s);
				
				switch ((pntk==-1)?pNextToken():pntk) {
				case K_COMMIT:
				case K_DELETE:
				case K_EXIT:
				case K_INSERT:
				case K_LOCK:
				case K_QUIT:
				case K_ROLLBACK:
				case K_SELECT:
				case K_SET:
				case K_UPDATE:
				case K_LIMIT:
				case K_SKIP:
					;
					break;
				default:
					plineanalizce1[4] = pGen;
					break label_1;
				}
			}
		
		if (true) 
			return v;
	
		throw new Error("Missing return statement in function");
	}

	final public Statement sQLStatement() throws ParseException {
		Statement statement = null;
		
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_COMMIT:
			statement = commitStatement();
			{if (true) return statement;}
			break;
		case K_DELETE:
			statement = deleteStatement();
			{if (true) return statement;}
			break;
		case K_INSERT:
			statement = insertStatement();
			{if (true) return statement;}
			break;
		case K_LOCK:
			statement = lockTableStatement();
			{if (true) return statement;}
			break;
		case K_ROLLBACK:
			statement = rollbackStatement();
			{if (true) return statement;}
			break;
		case K_SELECT:
			statement = queryStatement();
			{if (true) return statement;}
			break;
		case K_SET:
			statement = setTransactionStatement();
			{if (true) return statement;}
			break;
		case K_UPDATE:
			statement = updateStatement();
			{if (true) return statement;}
			break;
		case K_EXIT:
		case K_QUIT:
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_EXIT:
				pconsumeToken(K_EXIT);
				break;
			case K_QUIT:
				pconsumeToken(K_QUIT);
				break;
			default:
				plineanalizce1[5] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
			pconsumeToken(91);
			{if (true) return null;}
			break;
		default:
			plineanalizce1[6] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public TransactStatement commitStatement() throws ParseException {
		Token tk;
		TransactStatement t = new TransactStatement("COMMIT");
		pconsumeToken(K_COMMIT);
		if (((pntk==-1)?pNextToken():pntk) == K_WORK) {
			pconsumeToken(K_WORK);
		} else {
			plineanalizce1[7] = pGen;
			;
		}
		if (((pntk==-1)?pNextToken():pntk) == K_COMMENT) {
			pconsumeToken(K_COMMENT);
			tk = pconsumeToken(S_CHAR_LITERAL);
			t.setComment(tk.toString());
		} else {
			plineanalizce1[8] = pGen;
			;
		}
		pconsumeToken(91);
		{if (true) return t;}
		throw new Error("Missing return statement in function");
	}

	final public LockTable lockTableStatement() throws ParseException {
		LockTable lck = new LockTable();
		Vector v = new Vector();
		String s;
		pconsumeToken(K_LOCK);
		pconsumeToken(K_TABLE);
		s = tableReference();
		v.addElement(s);
		label_2:
			while (true) {
				if (((pntk==-1)?pNextToken():pntk) == 89) {
					;
				} else {
					plineanalizce1[9] = pGen;
					break label_2;
				}
				pconsumeToken(89);
				s = tableReference();
				v.addElement(s);
			}
		pconsumeToken(K_IN);
		s = lockMode();
		lck.setLockMode(s);
		pconsumeToken(K_MODE);
		if (((pntk==-1)?pNextToken():pntk) == K_NOWAIT) {
			pconsumeToken(K_NOWAIT);
			lck.nowait = true;
		} else {
			plineanalizce1[10] = pGen;
			;
		}
		pconsumeToken(91);
		lck.addTables(v); {if (true) return lck;}
		throw new Error("Missing return statement in function");
	}

	final public TransactStatement rollbackStatement() throws ParseException {
		Token tk;
		TransactStatement t = new TransactStatement("ROLLBACK");
		pconsumeToken(K_ROLLBACK);
		if (((pntk==-1)?pNextToken():pntk) == K_WORK) {
			pconsumeToken(K_WORK);
		} else {
			plineanalizce1[11] = pGen;
			;
		}
		if (((pntk==-1)?pNextToken():pntk) == K_COMMENT) {
			pconsumeToken(K_COMMENT);
			tk = pconsumeToken(S_CHAR_LITERAL);
			t.setComment(tk.toString());
		} else {
			plineanalizce1[12] = pGen;
			;
		}
		pconsumeToken(91);
		{if (true) return t;}
		throw new Error("Missing return statement in function");
	}

	final public TransactStatement setTransactionStatement() throws ParseException {
		StringBuffer b;
		TransactStatement t = new TransactStatement("SET TRANSACTION");
		boolean rdonly = false;
		pconsumeToken(K_SET);
		pconsumeToken(K_TRANSACTION);
		pconsumeToken(K_READ);
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_ONLY:
			pconsumeToken(K_ONLY);
			rdonly = true;
			break;
		case K_WRITE:
			pconsumeToken(K_WRITE);
			break;
		default:
			plineanalizce1[13] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		pconsumeToken(91);
		t.readOnly = rdonly; {if (true) return t;}
		throw new Error("Missing return statement in function");
	}

	final public String lockMode() throws ParseException {
		StringBuffer b = new StringBuffer();
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_ROW:
			pconsumeToken(K_ROW);
			b.append("ROW ");
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_SHARE:
				pconsumeToken(K_SHARE);
				b.append("SHARE");
				break;
			case K_EXCLUSIVE:
				pconsumeToken(K_EXCLUSIVE);
				b.append("EXCLUSIVE");
				break;
			default:
				plineanalizce1[14] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
			{if (true) return b.toString();}
			break;
		case K_SHARE:
			pconsumeToken(K_SHARE);
			b.append("SHARE");
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_ROW:
			case K_UPDATE:
				switch ((pntk==-1)?pNextToken():pntk) {
				case K_UPDATE:
					pconsumeToken(K_UPDATE);
					b.append(" UPDATE");
					break;
				case K_ROW:
					pconsumeToken(K_ROW);
					pconsumeToken(K_EXCLUSIVE);
					b.append(" ROW EXCLUSIVE");
					break;
				default:
					plineanalizce1[15] = pGen;
					pconsumeToken(-1);
					throw new ParseException();
				}
				break;
			default:
				plineanalizce1[16] = pGen;
				;
			}
			{if (true) return b.toString();}
			break;
		case K_EXCLUSIVE:
			pconsumeToken(K_EXCLUSIVE);
			{if (true) return "EXCLUSIVE";}
			break;
		default:
			plineanalizce1[17] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public Update updateStatement() throws ParseException {
		Update u;
		Exp e;
		Hashtable t;
		String s;
		Token tk;
		pconsumeToken(K_UPDATE);
		s = tableReference();
		u = new Update(s);
		if (((pntk==-1)?pNextToken():pntk) == S_IDENTIFIER) {
			tk = pconsumeToken(S_IDENTIFIER);
			u.setAlias(tk.toString());
		} else {
			plineanalizce1[18] = pGen;
			;
		}
		pconsumeToken(K_SET);
		columnValues(u);

		if (((pntk==-1)?pNextToken():pntk) == K_WHERE) {
			pconsumeToken(K_WHERE);
			e = sQLExpression();
			u.addWhere(e);
		} else {
			plineanalizce1[19] = pGen;
			;
		}
		pconsumeToken(91);
		{if (true) return u;}
		throw new Error("Missing return statement in function");
	}

	final public void columnValues(Update u) throws ParseException {
		String key;
		Exp val;
		key = tableColumn();
		pconsumeToken(92);
		val = updatedValue();
		u.addColumnUpdate(key, val);
		label_3:
			while (true) {
				if (((pntk==-1)?pNextToken():pntk) == 89) {
					;
				} else {
					plineanalizce1[20] = pGen;
					break label_3;
				}
				pconsumeToken(89);
				key = tableColumn();
				pconsumeToken(92);
				val = updatedValue();
				u.addColumnUpdate(key, val);
			}

	}

	final public Exp updatedValue() throws ParseException {
		Exp e;
		if (p2x1(2147483647)) {
			pconsumeToken(88);
			e = selectStatement();
			pconsumeToken(90);
			{if (true) return e;}
		} else {
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_AVG:
			case K_COUNT:
			case K_EXISTS:
			case K_MAX:
			case K_MIN:
			case K_NOT:
			case K_NULL:
			case K_PRIOR:
			case K_SUM:
			case S_NUMBER:
			case S_IDENTIFIER:
			case S_BIND:
			case S_CHAR_LITERAL:
			case S_QUOTED_IDENTIFIER:
			case 88:
			case 101:
			case 102:
				e = sQLExpression();
				{if (true) return e;}
				break;
			case 105:
				e = preparedCol();
				{if (true) return e;}
				break;
			default:
				plineanalizce1[21] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public Insert insertStatement() throws ParseException {
		Insert insert;
		String s;
		Vector value;
		Query query;
		pconsumeToken(K_INSERT);
		pconsumeToken(K_INTO);
		s = tableReference();
		insert = new Insert(s);
		if (((pntk==-1)?pNextToken():pntk) == 88) {
			pconsumeToken(88);
			s = tableColumn();
			value = new Vector();
			value.addElement(s);
			label_4:
				while (true) {
					if (((pntk==-1)?pNextToken():pntk) == 89) {
						;
					} else {
						plineanalizce1[22] = pGen;
						break label_4;
					}
					pconsumeToken(89);
					s = tableColumn();
					value.addElement(s);
				}
			pconsumeToken(90);
			insert.addColumns(value);
		} else {
			plineanalizce1[23] = pGen;
			;
		}
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_VALUES:
			pconsumeToken(K_VALUES);
			pconsumeToken(88);
			value = sQLExpressionList();
			pconsumeToken(90);
			Expression e = new Expression(",");
			e.setOperands(value); insert.addValueSpec(e);
			break;
		case K_SELECT:
			query = selectStatement();
			insert.addValueSpec(query);
			break;
		default:
			plineanalizce1[24] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		pconsumeToken(91);
		{if (true) return insert;}
		throw new Error("Missing return statement in function");
	}

	final public Delete deleteStatement() throws ParseException {
		Delete d;
		Exp e;
		String s;
		pconsumeToken(K_DELETE);
		if (((pntk==-1)?pNextToken():pntk) == K_FROM) {
			pconsumeToken(K_FROM);
		} else {
			plineanalizce1[25] = pGen;
			;
		}
		s = tableReference();
		d = new Delete(s);
		if (((pntk==-1)?pNextToken():pntk) == K_WHERE) {
			pconsumeToken(K_WHERE);
			e = sQLExpression();
			d.addWhere(e);
		} else {
			plineanalizce1[26] = pGen;
			;
		}
		pconsumeToken(91);
		{if (true) return d;}
		throw new Error("Missing return statement in function");
	}

	final public Query queryStatement() throws ParseException {
		Query q;
		q = selectStatement();
		if(limit!=0)
			q.addLimit(limit);
		if(skip != 0)
			q.addSkip(skip);
		if(! Utils.isGeospatial(query)){
			try{
				pconsumeToken(91);
			}catch(Exception e){//Impala impone limits para order by
				pconsumeToken(88);
			}
		}
		{if (true) return q;}
		throw new Error("Missing return statement in function");
	}

	final public String tableColumn() throws ParseException {
		StringBuffer buf = new StringBuffer();
		String s;
		s = oracleObjectName();
		buf.append(s);
		if (((pntk==-1)?pNextToken():pntk) == 93) {
			pconsumeToken(93);
			s = oracleObjectName();
			buf.append("." + s);
			if (((pntk==-1)?pNextToken():pntk) == 93) {
				pconsumeToken(93);
				s = oracleObjectName();
				buf.append("." + s);
			} else {
				plineanalizce1[27] = pGen;
				;
			}
		} else {
			plineanalizce1[28] = pGen;
			;
		}
		{if (true) return buf.toString();}
		throw new Error("Missing return statement in function");
	}

	final public String oracleObjectName() throws ParseException {
		Token t;
		switch ((pntk==-1)?pNextToken():pntk) {
		case S_IDENTIFIER:
			t = pconsumeToken(S_IDENTIFIER);
			{if (true) return t.toString();}
			break;
		case S_QUOTED_IDENTIFIER:
			t = pconsumeToken(S_QUOTED_IDENTIFIER);
			{if (true) return t.toString();}
			break;
		default:
			plineanalizce1[29] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public String relop() throws ParseException {
		Token op;
		switch ((pntk==-1)?pNextToken():pntk) {
		case 92:
			op = pconsumeToken(92);
			{if (true) return op.toString();}
			break;
		case 94:
			op = pconsumeToken(94);
			{if (true) return op.toString();}
			break;
		case 95:
			op = pconsumeToken(95);
			{if (true) return op.toString();}
			break;
		case 96:
			op = pconsumeToken(96);
			{if (true) return op.toString();}
			break;
		case 97:
			op = pconsumeToken(97);
			{if (true) return op.toString();}
			break;
		case 98:
			op = pconsumeToken(98);
			{if (true) return op.toString();}
			break;
		case 99:
			op = pconsumeToken(99);
			{if (true) return op.toString();}
			break;
		case 100:
			op = pconsumeToken(100);
			{if (true) return op.toString();}
			break;
		default:
			plineanalizce1[30] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public String tableReference() throws ParseException {
		StringBuffer buf = new StringBuffer();
		String s;
		s = oracleObjectName();
		buf.append(s);
		int n=0;
		if(pntk==-1)
			n=pNextToken();
		else
			n= pntk;
		if (n == 93) {
			pconsumeToken(93);
			s = oracleObjectName();
			buf.append("." + s);
		} else {
			plineanalizce1[31] = pGen;
			;
		}
		{if (true) return buf.toString();}
		throw new Error("Missing return statement in function");
	}

	final public void numOrID() throws ParseException {
		switch ((pntk==-1)?pNextToken():pntk) {
		case S_IDENTIFIER:
			pconsumeToken(S_IDENTIFIER);
			break;
		case S_NUMBER:
		case 101:
		case 102:
			switch ((pntk==-1)?pNextToken():pntk) {
			case 101:
			case 102:
				switch ((pntk==-1)?pNextToken():pntk) {
				case 101:
					pconsumeToken(101);
					break;
				case 102:
					pconsumeToken(102);
					break;
				default:
					plineanalizce1[32] = pGen;
					pconsumeToken(-1);
					throw new ParseException();
				}
				break;
			default:
				plineanalizce1[33] = pGen;
				;
			}
			pconsumeToken(S_NUMBER);
			break;
		default:
			plineanalizce1[34] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
	}

	final public Query selectStatement() throws ParseException {
		Query q;
		Vector v;
		q = selectWithoutOrder();
		if(! Utils.isGeospatial(query)){
			if (((pntk==-1)?pNextToken():pntk) == K_ORDER) {
				v = orderByClause();
				q.addOrderBy(v);
			} else {
				plineanalizce1[35] = pGen;
				;
			}
			if (((pntk==-1)?pNextToken():pntk) == K_FOR) {
				forUpdateClause();
				q.forupdate = true;
			} else {
				plineanalizce1[36] = pGen;
				;
			}
		}
		
		{if (true) return q;}
		throw new Error("Missing return statement in function");
	}

	final public Query selectWithoutOrder() throws ParseException {
		Query q = new Query();
		Vector select;
		Vector from;
		int limit=0;
		int skip = 0;
		Exp where = null;
		GroupBy groupby = null;
		Expression setclause = null;
		pconsumeToken(K_SELECT);
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_ALL:
		case K_DISTINCT:
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_ALL:
				pconsumeToken(K_ALL);
				break;
			case K_DISTINCT:
				pconsumeToken(K_DISTINCT);
				q.distinct = true;
				break;
			default:
				plineanalizce1[37] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
			break;
		default:
			plineanalizce1[38] = pGen;
			;
		}
		select = selectList();
		from = fromClause();
		int n=0;
		if(pntk==-1)
			n=pNextToken();
		else
			n= pntk;
		switch (n){//(pntk==-1)?pNextToken():pntk) {
		case K_LIMIT:
			limit = limitClause();
			break;
		case K_SKIP:
			skip = skipClause();
			break;
		case K_WHERE:
			where = whereClause();
			break;
		default:
			plineanalizce1[39] = pGen;
			;
		}
		
		if(! Utils.isGeospatial(query)){
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_CONNECT:
			case K_START:
				connectClause();
				break;
			default:
				plineanalizce1[40] = pGen;
				;
			}
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_GROUP:
				groupby = groupByClause();
				break;
			case K_SKIP:
				skip = skipClause();
				break;
			default:
				plineanalizce1[41] = pGen;
				;
			}
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_INTERSECT:
			case K_MINUS:
			case K_UNION:
				setclause = setClause();
				break;
			default:
				plineanalizce1[42] = pGen;
				;
			}
		}
		
		
		q.addSelect(select);
		q.addFrom(from);
		q.addWhere(where);
		q.addGroupBy(groupby);
		q.addSet(setclause);
		q.addLimit(limit);
		q.addSkip(skip);
		{if (true) return q;}
		throw new Error("Missing return statement in function");
	}

	final public Vector selectList() throws ParseException {
		Vector v = new Vector(8);
		SelectItem elem;
		switch ((pntk==-1)?pNextToken():pntk) {
		case 103:
			pconsumeToken(103);
			v.addElement(new SelectItem("*")); {if (true) return v;}
			break;
		case K_AVG:
		case K_COUNT:
		case K_MAX:
		case K_MIN:
		case K_NULL:
		case K_SUM:
		case S_NUMBER:
		case S_IDENTIFIER:
		case S_BIND:
		case S_CHAR_LITERAL:
		case S_QUOTED_IDENTIFIER:
		case 88:
		case 101:
		case 102:
			elem = selectItem();
			v.addElement(elem);
			label_5:
				while (true) {
					if (((pntk==-1)?pNextToken():pntk) == 89) {
						;
					} else {
						plineanalizce1[43] = pGen;
						break label_5;
					}
					pconsumeToken(89);
					elem = selectItem();
					v.addElement(elem);
				}
			{if (true) return v;}
			break;
		default:
			plineanalizce1[44] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public SelectItem selectItem() throws ParseException {
		String s;
		SelectItem it;
		Exp e;
		if (p2x2(2147483647)) {
			s = selectStar();
			{if (true) return new SelectItem(s);}
		} else {
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_AVG:
			case K_COUNT:
			case K_MAX:
			case K_MIN:
			case K_NULL:
			case K_SUM:
			case S_NUMBER:
			case S_IDENTIFIER:
			case S_BIND:
			case S_CHAR_LITERAL:
			case S_QUOTED_IDENTIFIER:
			case 88:
			case 101:
			case 102:
				e = sQLSimpleExpression();
				it = new SelectItem(e.toString());
				it.setExpression(e);
				switch ((pntk==-1)?pNextToken():pntk) {
				case K_AS:
				case S_IDENTIFIER:
				case S_QUOTED_IDENTIFIER:
					s = selectAlias();
					it.setAlias(s);
					break;
				default:
					plineanalizce1[45] = pGen;
					;
				}
				{if (true) return it;}
				break;
			default:
				plineanalizce1[46] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public String selectAlias() throws ParseException {
		Token tk;
		StringBuffer b = null;
		if (((pntk==-1)?pNextToken():pntk) == K_AS) {
			pconsumeToken(K_AS);
		} else {
			plineanalizce1[47] = pGen;
			;
		}
		switch ((pntk==-1)?pNextToken():pntk) {
		case S_QUOTED_IDENTIFIER:
			tk = pconsumeToken(S_QUOTED_IDENTIFIER);
			{if (true) return tk.toString().trim();}
			break;
		case S_IDENTIFIER:
			label_6:
				while (true) {
					tk = pconsumeToken(S_IDENTIFIER);
					if(b == null) b = new StringBuffer(tk.toString().trim());
					else b.append(" " + tk.toString().trim());
					if (((pntk==-1)?pNextToken():pntk) == S_IDENTIFIER) {
						;
					} else {
						plineanalizce1[48] = pGen;
						break label_6;
					}
				}
		break;
		default:
			plineanalizce1[49] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		{if (true) return b.toString().trim();}
		throw new Error("Missing return statement in function");
	}

	final public String selectStar() throws ParseException {
		String s, s2;
		if (p2x3(2)) {
			s = oracleObjectName();
			pconsumeToken(104);
			{if (true) return new String(s + ".*");}
		} else if (p2x4(4)) {
			s = oracleObjectName();
			pconsumeToken(93);
			s2 = oracleObjectName();
			pconsumeToken(104);
			{if (true) return new String(s + "." + s2 + ".*");}
		} else {
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public Vector fromClause() throws ParseException {
		Vector v = new Vector(8);
		FromItem f;
		pconsumeToken(K_FROM);
		f = fromItem();
		v.addElement(f);
		label_7:
			while (true) {
				switch ((pntk==-1)?pNextToken():pntk) {
				case 89:
					;
					break;
				case 78:
					plineanalizce1[50] = pGen;
					break label_7;
				default:
					plineanalizce1[50] = pGen;
					break label_7;
				}
				pconsumeToken(89);
				f = fromItem();
				v.addElement(f);
			}
		{if (true) return v;}
		throw new Error("Missing return statement in function");
	}

	final public FromItem fromItem() throws ParseException {
		FromItem f;
		String s;
		Token tk;
		s = tableReference();
		f = new FromItem(s);
		int n=0;
		if(pntk==-1)
			n = pNextToken();
		else 
			n = pntk;
		switch (n){ //(pntk==-1)?pNextToken():pntk) {
		case S_IDENTIFIER:
			tk = pconsumeToken(S_IDENTIFIER);
			f.setAlias(tk.toString());
			break;
		case K_LIMIT:
		case K_SKIP:
		default:
			plineanalizce1[51] = pGen;
			;
		}
		{if (true) return f;}
		throw new Error("Missing return statement in function");
	}

	final public int limitClause() throws ParseException {
	
		Token tk =pconsumeToken(K_LIMIT);
		int limit=0;
		int n=0;
		if(pntk==-1)
			n= pNextToken();
		else
			n= pntk;
		if (n == S_NUMBER) {
			tk = pconsumeToken(S_NUMBER);
			limit = Integer.parseInt(tk.toString());
		}
		if(true)
			return limit;
		throw new Error("Missing return statement in function");
		
	}
	
	final public int skipClause() throws ParseException {
		Token tk = pconsumeToken(K_SKIP);
		int skip = 0;
		int n = 0;
		if(pntk == -1)
			n = pNextToken();
		else
			n = pntk;
		if (n == S_NUMBER) {
			tk = pconsumeToken(S_NUMBER);
			skip = Integer.parseInt(tk.toString());
		}
		if(true)
			return skip;
		throw new Error("Missing return statement in function");
	}
	
	final public Exp whereClause() throws ParseException {
		Exp e;
		pconsumeToken(K_WHERE);
		e = sQLExpression();
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public void connectClause() throws ParseException {
		if (((pntk==-1)?pNextToken():pntk) == K_START) {
			pconsumeToken(K_START);
			pconsumeToken(K_WITH);
			sQLExpression();
		} else {
			plineanalizce1[52] = pGen;
			;
		}
		pconsumeToken(K_CONNECT);
		pconsumeToken(K_BY);
		sQLExpression();
		if (((pntk==-1)?pNextToken():pntk) == K_START) {
			pconsumeToken(K_START);
			pconsumeToken(K_WITH);
			sQLExpression();
		} else {
			plineanalizce1[53] = pGen;
			;
		}
	}

	final public GroupBy groupByClause() throws ParseException {
		GroupBy g = null;
		Vector v;
		Exp e;
		pconsumeToken(K_GROUP);
		pconsumeToken(K_BY);
		v = sQLExpressionList();
		g = new GroupBy(v);
		if (((pntk==-1)?pNextToken():pntk) == K_HAVING) {
			pconsumeToken(K_HAVING);
			e = sQLExpression();
			g.setHaving(e);
		} else {
			plineanalizce1[54] = pGen;
			;
		}
		{if (true) return g;}
		throw new Error("Missing return statement in function");
	}

	// SetClause ::= UNION [ALL] Qry | INTERSECT Qry | MINUS Qry
	// Qry ::= SelectWithoutOrder | ( SelectWithoutOrder )
	final public Expression setClause() throws ParseException {
		Expression e;
		Query q;
		Token t;
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_UNION:
			t = pconsumeToken(K_UNION);
			if (((pntk==-1)?pNextToken():pntk) == K_ALL) {
				pconsumeToken(K_ALL);
			} else {
				plineanalizce1[55] = pGen;
				;
			}
			break;
		case K_INTERSECT:
			t = pconsumeToken(K_INTERSECT);
			break;
		case K_MINUS:
			t = pconsumeToken(K_MINUS);
			break;
		default:
			plineanalizce1[56] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		e = new Expression(t.toString());
		if (p2x5(2147483647)) {
			pconsumeToken(88);
			q = selectWithoutOrder();
			e.addOperand(q);
			pconsumeToken(90);
		} else {
			if (((pntk==-1)?pNextToken():pntk) == K_SELECT) {
				q = selectWithoutOrder();
				e.addOperand(q);
			} else {
				plineanalizce1[57] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
		}
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public Vector orderByClause() throws ParseException {
		Vector v = new Vector();
		Exp e;
		OrderBy ob;
		pconsumeToken(K_ORDER);
		pconsumeToken(K_BY);
		e = sQLSimpleExpression();
		ob = new OrderBy(e);
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_ASC:
		case K_DESC:
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_ASC:
				pconsumeToken(K_ASC);
				if (((pntk==-1)?pNextToken():pntk) == K_LIMIT) {
					limit = limitClause();
				}
				break;
			case K_DESC:
				pconsumeToken(K_DESC);
				ob.setAscOrder(false);
				if (((pntk==-1)?pNextToken():pntk) == K_LIMIT) {
					limit = limitClause();
				}
				
				break;
			default:
				plineanalizce1[58] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
			break;
		default:
			plineanalizce1[59] = pGen;
			;
		}
		v.addElement(ob);
		label_8:
			while (true) {
				if (((pntk==-1)?pNextToken():pntk) == 89) {
					;
				} else {
					plineanalizce1[60] = pGen;
					break label_8;
				}
				pconsumeToken(89);
				e = sQLSimpleExpression();
				ob = new OrderBy(e);
				switch ((pntk==-1)?pNextToken():pntk) {
				case K_ASC:
				case K_DESC:
					switch ((pntk==-1)?pNextToken():pntk) {
					case K_ASC:
						pconsumeToken(K_ASC);
						break;
					case K_DESC:
						pconsumeToken(K_DESC);
						ob.setAscOrder(false);
						break;
					default:
						plineanalizce1[61] = pGen;
						pconsumeToken(-1);
						throw new ParseException();
					}
					break;
				default:
					plineanalizce1[62] = pGen;
					;
				}
				v.addElement(ob);
			}
		{if (true) return v;}
		throw new Error("Missing return statement in function");
	}

	final public void forUpdateClause() throws ParseException {
		pconsumeToken(K_FOR);
		pconsumeToken(K_UPDATE);
		if (((pntk==-1)?pNextToken():pntk) == K_OF) {
			pconsumeToken(K_OF);
			tableColumn();
			label_9:
				while (true) {
					if (((pntk==-1)?pNextToken():pntk) == 89) {
						;
					} else {
						plineanalizce1[63] = pGen;
						break label_9;
					}
					pconsumeToken(89);
					tableColumn();
				}
		} else {
			plineanalizce1[64] = pGen;
			;
		}
	}

	
	
	
	final public Exp sQLExpression() throws ParseException {
		Exp e1, e2;
		Expression e = null;
		boolean single = true;


		/*
		 * 
		 * 
		 */
		if(Utils.isGeospatial(query)){
			
//			if (! Utils.checkGeospatialSyntax(query)){
//				throw new ParseException("Malformed where statement. Expected: where + column name + S_near + (ST_Point(longitude, latitude), max_distance);");
//			}
			
			String where = query.substring(query.toUpperCase().indexOf("WHERE"));
			String operator = "S_NEAR";
			String shapeExpression = where.substring(where.indexOf('(') +1, where.indexOf(')')+1);
			String columnExpression = where.substring(where.toUpperCase().indexOf("WHERE") +5 , where.toUpperCase().indexOf("S_NEAR"));
			String[] split = columnExpression.trim().split(" ");
			if(query.contains("limit")){
				try{
				limit = Integer.parseInt(query.substring(query.indexOf("limit")+5));
				}catch(NumberFormatException e11){
					String aux = query.substring(query.indexOf("limit")+5);
					aux = aux.replace(";","");
					aux = aux.trim();
					try{
						limit = Integer.parseInt(aux);
					}catch(NumberFormatException e22){
						throw new ParseException("Problems parsing query. Review it");
					}
				}
			}
			if (split.length != 1 || columnExpression.trim().length() == 0){
				throw new ParseException("Malformed query, wrong column name");
			}
			
			Constant columnName = new Constant(split[0],  Constant.COLUMNNAME);
			String distance = where.substring(where.lastIndexOf(",") + 1, where.lastIndexOf(")") );
			//TODO: lanzar excepción con mensaje correcto
			if(distance.trim().length() == 0 ){
				throw new ParseException ("Malformed where statement,Wrong distance. Expected: where + column name + S_near + (ST_Point(longitude, latitude), max_distance);");
			}
			
			String shape = shapeExpression.substring(0, shapeExpression.indexOf('(')).trim();
			
			Expression pointExpression;
			
			if(! "ST_POINT".equals(shape.toUpperCase())){
				throw new ParseException("not supported");
				
			}else{
				String shapeFunction = "ST_POINT";
				String longitude;
				String latitude;
				try{
					longitude = shapeExpression.substring(shapeExpression.indexOf('(') + 1, shapeExpression.indexOf(','));
					latitude = shapeExpression.substring(shapeExpression.indexOf(',') + 1, shapeExpression.indexOf(')'));
				}catch(IndexOutOfBoundsException e21){
					throw new ParseException("ERROR - Wrong coordinates. Expected:(longitude,latitude). ");
				}
				//TODO: lanzar excepción con mensaje correcto
				if(longitude.trim().length() == 0 || latitude.trim().length() == 0){
					throw new ParseException ("ERROR - Wrong coordinates. Expected: FIELD + Comparison Operator + VALUE + Logic Operator + FIELD + Comparison Operator + VALUE ");
				}
				pointExpression = new Expression(shapeFunction,new Constant(latitude,Constant.COLUMNNAME), new Constant(longitude,Constant.COLUMNNAME));
			}
			
			e = new Expression(operator, new Constant(distance, Constant.COLUMNNAME), pointExpression);
			e.addOperand(columnName);
			return e;
		}
		
		e1 = sQLAndExpression();
		label_10:
			while (true) {
				if (((pntk==-1)?pNextToken():pntk) == K_OR) {
					;
				} else {
					plineanalizce1[65] = pGen;
					break label_10;
				}
				pconsumeToken(K_OR);
				e2 = sQLAndExpression();
				if(single) { e = new Expression("OR", e1); }
				single=false;
				e.addOperand(e2);
			}
		{if (true) return (single ? e1 : e);}
		throw new Error("Missing return statement in function");
	}

	final public Exp sQLAndExpression() throws ParseException {
		Exp e1, e2;
		Expression e = null;
		boolean single = true;
		e1 = sQLUnaryLogicalExpression();
		label_11:
			while (true) {
				if (((pntk==-1)?pNextToken():pntk) == K_AND) {
					;
				} else {
					plineanalizce1[66] = pGen;
					break label_11;
				}
				pconsumeToken(K_AND);
				e2 = sQLUnaryLogicalExpression();
				if(single) { e = new Expression("AND", e1); }
				single=false;
				e.addOperand(e2);
			}
		{if (true) return (single ? e1 : e);}
		throw new Error("Missing return statement in function");
	}

	final public Exp sQLUnaryLogicalExpression() throws ParseException {
		Exp e1, e;
		boolean not = false;
		if (p2x6(2)) {
			e = existsClause();
			{if (true) return e;}
		} else {
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_AVG:
			case K_COUNT:
			case K_MAX:
			case K_MIN:
			case K_NOT:
			case K_NULL:
			case K_PRIOR:
			case K_SUM:
			case S_NUMBER:
			case S_IDENTIFIER:
			case S_BIND:
			case S_CHAR_LITERAL:
			case S_QUOTED_IDENTIFIER:
			case 88:
			case 101:
			case 102:
				if (((pntk==-1)?pNextToken():pntk) == K_NOT) {
					pconsumeToken(K_NOT);
					not = true;
				} else {
					plineanalizce1[67] = pGen;
					;
				}
				e1 = sQLRelationalExpression();
				if(not) e = new Expression("NOT", e1);
				else e = e1;
				{if (true) return e;}
				break;
			default:
				plineanalizce1[68] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public Expression existsClause() throws ParseException {
		Expression e;
		Query q;
		boolean not = false;
		if (((pntk==-1)?pNextToken():pntk) == K_NOT) {
			pconsumeToken(K_NOT);
			not = true;
		} else {
			plineanalizce1[69] = pGen;
			;
		}
		pconsumeToken(K_EXISTS);
		pconsumeToken(88);
		q = subQuery();
		pconsumeToken(90);
		Expression e1 = new Expression("EXISTS", q);
		if(not) e = new Expression("NOT", e1);
		else e = e1;
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	// SQLRelationalExpression ::=
	//    ( [PRIOR] SQLSimpleExpression | SQLExpressionList )
	//    ( SQLRelationalOperatorExpression
	//      | SQLInClause | SQLBetweenClause | SQLLikeClause | IsNullClause )?
	final public Exp sQLRelationalExpression() throws ParseException {
		Exp e1, eleft;
		Expression eright = null;
		Vector v;
		boolean prior = false;
		if (p2x7(2147483647)) {
			pconsumeToken(88);
			v = sQLExpressionList();
			pconsumeToken(90);
			eleft = new Expression(",");
			((Expression)eleft).setOperands(v);
		} else {
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_AVG:
			case K_COUNT:
			case K_MAX:
			case K_MIN:
			case K_NULL:
			case K_PRIOR:
			case K_SUM:
			case S_NUMBER:
			case S_IDENTIFIER:
			case S_BIND:
			case S_CHAR_LITERAL:
			case S_QUOTED_IDENTIFIER:
			case 88:
			case 101:
			case 102:
				if (((pntk==-1)?pNextToken():pntk) == K_PRIOR) {
					pconsumeToken(K_PRIOR);
					prior = true;
				} else {
					plineanalizce1[70] = pGen;
					;
				}
				e1 = sQLSimpleExpression();
				if(prior) eleft = new Expression("PRIOR", e1);
				else eleft = e1;
				break;
			default:
				plineanalizce1[71] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
		}
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_BETWEEN:
		case K_IN:
		case K_IS:
		case K_LIKE:
		case K_NOT:
		case 92:
		case 94:
		case 95:
		case 96:
		case 97:
		case 98:
		case 99:
		case 100:
			switch ((pntk==-1)?pNextToken():pntk) {
			case 92:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
				eright = sQLRelationalOperatorExpression();
				break;
			default:
				plineanalizce1[72] = pGen;
				if (p2x8(2)) {
					eright = sQLInClause();
				} else if (p2x9(2)) {
					eright = sQLBetweenClause();
				} else if (p2x10(2)) {
					eright = sQLLikeClause();
				} else {
					if (((pntk==-1)?pNextToken():pntk) == K_IS) {
						eright = isNullClause();
					} else {
						plineanalizce1[73] = pGen;
						pconsumeToken(-1);
						throw new ParseException();
					}
				}
			}
			break;
		default:
			plineanalizce1[74] = pGen;
			;
		}
		if(eright == null) {if (true) return eleft;}
		Vector v2 = eright.getOperands();
		if(v2 == null) v2 = new Vector(); //For IS NULL, which is unary!
		v2.insertElementAt(eleft, 0);
		eright.setOperands(v2);
		{if (true) return eright;}
		throw new Error("Missing return statement in function");
	}

	final public Vector sQLExpressionList() throws ParseException {
		Vector v = new Vector(8);
		Exp e;
		e = sQLSimpleExpressionOrPreparedCol();
		v.addElement(e);
		label_12:
			while (true) {
				if (((pntk==-1)?pNextToken():pntk) == 89) {
					;
				} else {
					plineanalizce1[75] = pGen;
					break label_12;
				}
				pconsumeToken(89);
				e = sQLSimpleExpressionOrPreparedCol();
				v.addElement(e);
			}
		{if (true) return v;}
		throw new Error("Missing return statement in function");
	}

	final public Expression sQLRelationalOperatorExpression() throws ParseException {
		Expression e;
		Exp e1, eright;
		String op;
		String unaryOp = null;
		op = relop();
		e = new Expression(op);
		if (p2x11(2147483647)) {
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_ALL:
			case K_ANY:
				switch ((pntk==-1)?pNextToken():pntk) {
				case K_ALL:
					pconsumeToken(K_ALL);
					unaryOp = "ALL";
					break;
				case K_ANY:
					pconsumeToken(K_ANY);
					unaryOp = "ANY";
					break;
				default:
					plineanalizce1[76] = pGen;
					pconsumeToken(-1);
					throw new ParseException();
				}
				break;
			default:
				plineanalizce1[77] = pGen;
				;
			}
			pconsumeToken(88);
			e1 = subQuery();
			pconsumeToken(90);
			if(unaryOp == null) eright = e1;
			else eright = new Expression(unaryOp, e1);
		} else {
			switch ((pntk==-1)?pNextToken():pntk) {
			case K_AVG:
			case K_COUNT:
			case K_MAX:
			case K_MIN:
			case K_NULL:
			case K_PRIOR:
			case K_SUM:
			case S_NUMBER:
			case S_IDENTIFIER:
			case S_BIND:
			case S_CHAR_LITERAL:
			case S_QUOTED_IDENTIFIER:
			case 88:
			case 101:
			case 102:
			case 105:
				if (((pntk==-1)?pNextToken():pntk) == K_PRIOR) {
					pconsumeToken(K_PRIOR);
					unaryOp = "PRIOR";
				} else {
					plineanalizce1[78] = pGen;
					;
				}
				e1 = sQLSimpleExpressionOrPreparedCol();
				if(unaryOp == null) eright = e1;
				else eright = new Expression(unaryOp, e1);
				break;
			default:
				plineanalizce1[79] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
		}
		e.addOperand(eright); {if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public Exp sQLSimpleExpressionOrPreparedCol() throws ParseException {
		Exp e;
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_AVG:
		case K_COUNT:
		case K_MAX:
		case K_MIN:
		case K_NULL:
		case K_SUM:
		case S_NUMBER:
		case S_IDENTIFIER:
		case S_BIND:
		case S_CHAR_LITERAL:
		case S_QUOTED_IDENTIFIER:
		case 88:
		case 101:
		case 102:
			e = sQLSimpleExpression();
			{if (true) return e;}
			break;
		case 105:
			e = preparedCol();
			{if (true) return e;}
			break;
		default:
			plineanalizce1[80] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	// For prepared columns ("?")
	final public Exp preparedCol() throws ParseException {
		pconsumeToken(105);
		{if (true) return new Expression("?");}
		throw new Error("Missing return statement in function");
	}

	final public Expression sQLInClause() throws ParseException {
		Expression e;
		Query q;
		boolean not = false;
		Vector v;
		if (((pntk==-1)?pNextToken():pntk) == K_NOT) {
			pconsumeToken(K_NOT);
			not = true;
		} else {
			plineanalizce1[81] = pGen;
			;
		}
		pconsumeToken(K_IN);
		e = new Expression(not ? "NOT IN" : "IN");
		pconsumeToken(88);
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_AVG:
		case K_COUNT:
		case K_MAX:
		case K_MIN:
		case K_NULL:
		case K_SUM:
		case S_NUMBER:
		case S_IDENTIFIER:
		case S_BIND:
		case S_CHAR_LITERAL:
		case S_QUOTED_IDENTIFIER:
		case 88:
		case 101:
		case 102:
		case 105:
			v = sQLExpressionList();
			e.setOperands(v);
			break;
		case K_SELECT:
			q = subQuery();
			e.addOperand(q);
			break;
		default:
			plineanalizce1[82] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		pconsumeToken(90);
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public Expression sQLBetweenClause() throws ParseException {
		Expression e;
		Exp  e1, e2;
		boolean not = false;
		if (((pntk==-1)?pNextToken():pntk) == K_NOT) {
			pconsumeToken(K_NOT);
			not = true;
		} else {
			plineanalizce1[83] = pGen;
			;
		}
		pconsumeToken(K_BETWEEN);
		e1 = sQLSimpleExpressionOrPreparedCol();
		pconsumeToken(K_AND);
		e2 = sQLSimpleExpressionOrPreparedCol();
		if(not) e = new Expression("NOT BETWEEN", e1, e2);
		else e = new Expression("BETWEEN", e1, e2);
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public Expression sQLLikeClause() throws ParseException {
		Exp eright;
		Expression e;
		boolean not = false;
		if (((pntk==-1)?pNextToken():pntk) == K_NOT) {
			pconsumeToken(K_NOT);
			not = true;
		} else {
			plineanalizce1[84] = pGen;
			;
		}
		pconsumeToken(K_LIKE);
		eright = sQLSimpleExpressionOrPreparedCol();
		if(not) e = new Expression("NOT LIKE", eright);
		else e = new Expression("LIKE", eright);
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public Expression isNullClause() throws ParseException {
		boolean not = false;
		pconsumeToken(K_IS);
		if (((pntk==-1)?pNextToken():pntk) == K_NOT) {
			pconsumeToken(K_NOT);
			not = true;
		} else {
			plineanalizce1[85] = pGen;
			;
		}
		pconsumeToken(K_NULL);
		{if (true) return(not ? new Expression("IS NOT NULL") : new Expression("IS NULL"));}
		throw new Error("Missing return statement in function");
	}

	// SQLSimpleExpression
	//    ::= SQLMultiplicativeExpression (OP SQLMultiplicativeExpression)*
	// OP ::= + | - | "||"
	final public Exp sQLSimpleExpression() throws ParseException {
		Token op;
		Exp e1, e2;
		Expression e = null;
		e1 = sQLMultiplicativeExpression();
		label_13:
			while (true) {
				switch ((pntk==-1)?pNextToken():pntk) {
				case 101:
				case 102:
				case 106:
					;
					break;
				default:
					plineanalizce1[86] = pGen;
					break label_13;
				}
				switch ((pntk==-1)?pNextToken():pntk) {
				case 101:
					op = pconsumeToken(101);
					break;
				case 102:
					op = pconsumeToken(102);
					break;
				case 106:
					op = pconsumeToken(106);
					break;
				default:
					plineanalizce1[87] = pGen;
					pconsumeToken(-1);
					throw new ParseException();
				}
				e2 = sQLMultiplicativeExpression();
				e = new Expression(op.toString(), e1);
				e.addOperand(e2);
				e1 = e;
			}
		{if (true) return e1;}
		throw new Error("Missing return statement in function");
	}

	final public Exp sQLMultiplicativeExpression() throws ParseException {
		Token op;
		Exp e1, e2;
		Expression e = null;
		e1 = sQLExpotentExpression();
		label_14:
			while (true) {
				switch ((pntk==-1)?pNextToken():pntk) {
				case 103:
				case 107:
					;
					break;
				default:
					plineanalizce1[88] = pGen;
					break label_14;
				}
				switch ((pntk==-1)?pNextToken():pntk) {
				case 103:
					op = pconsumeToken(103);
					break;
				case 107:
					op = pconsumeToken(107);
					break;
				default:
					plineanalizce1[89] = pGen;
					pconsumeToken(-1);
					throw new ParseException();
				}
				e2 = sQLExpotentExpression();
				e = new Expression(op.toString(), e1);
				e.addOperand(e2);
				e1 = e;
			}
		{if (true) return e1;}
		throw new Error("Missing return statement in function");
	}

	final public Exp sQLExpotentExpression() throws ParseException {
		Token op;
		Exp e1, e2;
		Expression e = null;
		boolean single = true;
		e1 = sQLUnaryExpression();
		label_15:
			while (true) {
				switch ((pntk==-1)?pNextToken():pntk) {
				case K_LIMIT:
					limit = limitClause();
					break;
				case 108:
					;
					break;
				default:
					plineanalizce1[90] = pGen;
					break label_15;
				}
				if(limit == 0){
					op = pconsumeToken(108);
					e2 = sQLUnaryExpression();
					if(single) e = new Expression(op.toString(), e1);
					single = false;
					e.addOperand(e2);
				}
				
			}
		{if (true) return (single ? e1 : e);}
		throw new Error("Missing return statement in function");
	}

	final public Exp sQLUnaryExpression() throws ParseException {
		Token op = null;
		Exp e1, e;
		switch ((pntk==-1)?pNextToken():pntk) {
		case 101:
		case 102:
			switch ((pntk==-1)?pNextToken():pntk) {
			case 101:
				op = pconsumeToken(101);
				break;
			case 102:
				op = pconsumeToken(102);
				break;
			default:
				plineanalizce1[91] = pGen;
				pconsumeToken(-1);
				throw new ParseException();
			}
			break;
		default:
			plineanalizce1[92] = pGen;
			;
		}
		e1 = sQLPrimaryExpression();
		if(op == null) e = e1;
		else e = new Expression(op.toString(), e1);
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public Exp sQLPrimaryExpression() throws ParseException {
		Token t;
		String s, s2, modifier="";
		Exp e;
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_NULL:
			pconsumeToken(K_NULL);
			{if (true) return new Constant("NULL", Constant.NULL);}
			break;
		default:
			plineanalizce1[94] = pGen;
			if (p2x12(2147483647)) {
				s = outerJoinExpression();
				{if (true) return new Constant(s, Constant.COLUMNNAME);}
				//return new ZExpression("_NOT_SUPPORTED"); //TBD

			} else if (p2x13(3)) {
				pconsumeToken(K_COUNT);
				pconsumeToken(88);
				pconsumeToken(103);
				pconsumeToken(90);
				{if (true) return new Expression("COUNT",
						new Constant("*", Constant.COLUMNNAME));}
			} else if (p2x14(3)) {
				s = aggregateFunc();
				pconsumeToken(88);
				switch ((pntk==-1)?pNextToken():pntk) {
				case K_ALL:
					pconsumeToken(K_ALL);
					modifier="all ";
					break;
				case K_DISTINCT:
					pconsumeToken(K_DISTINCT);
					modifier="distinct ";
					break;
				default:
					plineanalizce1[93] = pGen;
					pconsumeToken(-1);
					throw new ParseException();
				}
				s2 = tableColumn();
				pconsumeToken(90);
				{if (true) return new Expression(s, new Constant(modifier + s2, Constant.COLUMNNAME));}
			} else if (p2x15(2)) {
				e = functionCall();
				{if (true) return e;}
			} else {
				switch ((pntk==-1)?pNextToken():pntk) {
				case S_IDENTIFIER:
				case S_QUOTED_IDENTIFIER:
					s = tableColumn();
					{if (true) return new Constant(s, Constant.COLUMNNAME);}
					break;
				case S_NUMBER:
					t = pconsumeToken(S_NUMBER);
					{if (true) return new Constant(t.toString(), Constant.NUMBER);}
					break;
				case S_CHAR_LITERAL:
					t = pconsumeToken(S_CHAR_LITERAL);
					s = t.toString();
					if(s.startsWith("\'")) s = s.substring(1);
					if(s.endsWith("\'")) s = s.substring(0, s.length()-1);
					{if (true) return new Constant(s, Constant.STRING);}
					break;
				case S_BIND:
					t = pconsumeToken(S_BIND);
					{if (true) return new Constant(t.toString(), Constant.STRING);}
					break;
				case 88:
					pconsumeToken(88);
					e = sQLExpression();
					pconsumeToken(90);
					{if (true) return e;}
					break;
				default:
					plineanalizce1[95] = pGen;
					pconsumeToken(-1);
					throw new ParseException();
				}
			}
		}
		throw new Error("Missing return statement in function");
	}

	final public String aggregateFunc() throws ParseException {
		Token t;
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_SUM:
			t = pconsumeToken(K_SUM);
			{if (true) return t.toString();}
			break;
		case K_AVG:
			t = pconsumeToken(K_AVG);
			{if (true) return t.toString();}
			break;
		case K_MAX:
			t = pconsumeToken(K_MAX);
			{if (true) return t.toString();}
			break;
		case K_MIN:
			t = pconsumeToken(K_MIN);
			{if (true) return t.toString();}
			break;
		case K_COUNT:
			t = pconsumeToken(K_COUNT);
			{if (true) return t.toString();}
			break;
		default:
			plineanalizce1[96] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public Expression functionCall() throws ParseException {
		Token t;
		String s;
		Expression e;
		Vector parm = null;
		switch ((pntk==-1)?pNextToken():pntk) {
		case S_IDENTIFIER:
			t = pconsumeToken(S_IDENTIFIER);
			s = t.toString();
			break;
		case K_AVG:
		case K_COUNT:
		case K_MAX:
		case K_MIN:
		case K_SUM:
			s = aggregateFunc();
			break;
		default:
			plineanalizce1[97] = pGen;
			pconsumeToken(-1);
			throw new ParseException();
		}
		pconsumeToken(88);
		switch ((pntk==-1)?pNextToken():pntk) {
		case K_AVG:
		case K_COUNT:
		case K_MAX:
		case K_MIN:
		case K_NULL:
		case K_SUM:
		case S_NUMBER:
		case S_IDENTIFIER:
		case S_BIND:
		case S_CHAR_LITERAL:
		case S_QUOTED_IDENTIFIER:
		case 88:
		case 101:
		case 102:
		case 105:
			parm = sQLExpressionList();
			break;
		default:
			plineanalizce1[98] = pGen;
			;
		}
		pconsumeToken(90);
		int nparm = Utils.isCustomFunction(s);
		if(nparm < 0) nparm = (Utils.isAggregate(s) ? 1 : -1);
		if(nparm < 0)
		{if (true) throw new ParseException("Undefined function: " + s);}
		if(nparm != Utils.VARIABLE_PLIST && nparm > 0) {
			if(parm == null || parm.size() != nparm)
			{if (true) throw new ParseException("Function " + s + " should have "
					+ nparm + " parameter(s)");}
		}

		e = new Expression(s);
		e.setOperands(parm);
		{if (true) return e;}
		throw new Error("Missing return statement in function");
	}

	final public String outerJoinExpression() throws ParseException {
		String s = null;
		String c = "";
		// user.table.col
		s = oracleObjectName();
		if (((pntk==-1)?pNextToken():pntk) == 93) {
			pconsumeToken(93);
			c = oracleObjectName();
			s += "." + c;
			if (((pntk==-1)?pNextToken():pntk) == 93) {
				pconsumeToken(93);
				c = oracleObjectName();
				s += "." + c;
			} else {
				plineanalizce1[99] = pGen;
				;
			}
		} else {
			plineanalizce1[100] = pGen;
			;
		}
		pconsumeToken(88);
		pconsumeToken(101);
		pconsumeToken(90);
		{if (true) return s + "(+)";}
		throw new Error("Missing return statement in function");
	}

	final public Query subQuery() throws ParseException {
		Query q;
		q = selectWithoutOrder();
		{if (true) return q;}
		throw new Error("Missing return statement in function");
	}

	final private boolean p2x1(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x1(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(0, xla); }
	}

	final private boolean p2x2(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x2(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(1, xla); }
	}

	final private boolean p2x3(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x3(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(2, xla); }
	}

	final private boolean p2x4(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x4(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(3, xla); }
	}

	final private boolean p2x5(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !px3x5(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(4, xla); }
	}

	final private boolean p2x6(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x6(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(5, xla); }
	}

	final private boolean p2x7(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x7(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(6, xla); }
	}

	final private boolean p2x8(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x8(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(7, xla); }
	}

	final private boolean p2x9(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x9(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(8, xla); }
	}

	final private boolean p2x10(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x10(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(9, xla); }
	}

	final private boolean p2x11(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x11(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(10, xla); }
	}

	final private boolean p2x12(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x12(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(11, xla); }
	}

	final private boolean p2x13(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x13(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(12, xla); }
	}

	final private boolean p2x14(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x14(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(13, xla); }
	}

	final private boolean p2x15(int xla) {
		plineAnalice = xla; plastPos = pScanPos = token;
		try { return !p3x15(); }
		catch(LookaheadSuccess ls) { return true; }
		finally { save(14, xla); }
	}

	final private boolean p3x1() {
		Token xsp;
		if (pScanToken(88)) return true;
		while (true) {
			xsp = pScanPos;
			if (pScanToken(88)) { pScanPos = xsp; break; }
		}
		if (pScanToken(K_SELECT)) return true;
		return false;
	}

	final private boolean p3Rx134() {
		if (p3Rx135()) return true;
		return false;
	}

	final private boolean p3x2() {
		if (p3Rx16()) return true;
		return false;
	}

	final private boolean p3Rx47() {
		Token xsp;
		xsp = pScanPos;
		if (pScanToken(103)) {
			pScanPos = xsp;
			if (pScanToken(107)) return true;
		}
		if (p3Rx46()) return true;
		return false;
	}

	final private boolean p3Rx32() {
		if (p3Rx46()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx47()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx130() {
		if (p3Rx19()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx134()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx31() {
		if (pScanToken(K_NOT)) return true;
		return false;
	}

	final private boolean p3Rx129() {
		if (p3Rx16()) return true;
		return false;
	}

	final private boolean p3Rx109() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx129()) {
			pScanPos = xsp;
			if (p3Rx130()) return true;
		}
		return false;
	}

	final private boolean p3Rx18() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx31()) pScanPos = xsp;
		if (pScanToken(K_EXISTS)) return true;
		if (pScanToken(88)) return true;
		if (p3Rx77()) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx33() {
		Token xsp;
		xsp = pScanPos;
		if (pScanToken(101)) {
			pScanPos = xsp;
			if (pScanToken(102)) {
				pScanPos = xsp;
				if (pScanToken(106)) return true;
			}
		}
		if (p3Rx32()) return true;
		return false;
	}

	final private boolean p3Rx75() {
		if (pScanToken(K_NOT)) return true;
		return false;
	}

	final private boolean p3Rx19() {
		if (p3Rx32()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx33()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx110() {
		if (pScanToken(89)) return true;
		if (p3Rx109()) return true;
		return false;
	}

	final private boolean p3Rx74() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx75()) pScanPos = xsp;
		if (p3Rx76()) return true;
		return false;
	}

	final private boolean p3Rx96() {
		if (p3Rx109()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx110()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3x6() {
		if (p3Rx18()) return true;
		return false;
	}

	final private boolean p3Rx71() {
		Token xsp;
		xsp = pScanPos;
		if (p3x6()) {
			pScanPos = xsp;
			if (p3Rx74()) return true;
		}
		return false;
	}

	final private boolean p3Rx95() {
		if (pScanToken(103)) return true;
		return false;
	}

	final private boolean p3Rx86() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx95()) {
			pScanPos = xsp;
			if (p3Rx96()) return true;
		}
		return false;
	}

	final private boolean p3Rx108() {
		if (pScanToken(K_NOT)) return true;
		return false;
	}

	final private boolean p3Rx72() {
		if (pScanToken(K_AND)) return true;
		if (p3Rx71()) return true;
		return false;
	}

	final private boolean p3Rx94() {
		if (pScanToken(K_DISTINCT)) return true;
		return false;
	}

	final private boolean p3Rx93() {
		if (pScanToken(K_IS)) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx108()) pScanPos = xsp;
		if (pScanToken(K_NULL)) return true;
		return false;
	}

	final private boolean p3Rx67() {
		if (p3Rx71()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx72()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx45() {
		if (p3Rx25()) return true;
		return false;
	}

	final private boolean p3Rx77() {
		if (p3Rx81()) return true;
		return false;
	}

	final private boolean p3Rx85() {
		Token xsp;
		xsp = pScanPos;
		if (pScanToken(5)) {
			pScanPos = xsp;
			if (p3Rx94()) return true;
		}
		return false;
	}

	final private boolean p3Rx91() {
		if (p3Rx102()) return true;
		return false;
	}

	final private boolean p3Rx90() {
		if (p3Rx101()) return true;
		return false;
	}

	final private boolean p3Rx37() {
		if (pScanToken(K_NOT)) return true;
		return false;
	}

	final private boolean p3Rx89() {
		if (p3Rx100()) return true;
		return false;
	}

	final private boolean p3Rx22() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx37()) pScanPos = xsp;
		if (pScanToken(K_LIKE)) return true;
		if (p3Rx36()) return true;
		return false;
	}

	final private boolean p3Rx88() {
		if (p3Rx99()) return true;
		return false;
	}

	final private boolean p3Rx68() {
		if (pScanToken(K_OR)) return true;
		if (p3Rx67()) return true;
		return false;
	}

	final private boolean p3Rx38() {
		if (pScanToken(93)) return true;
		if (p3Rx17()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx50()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx50() {
		if (pScanToken(93)) return true;
		if (p3Rx17()) return true;
		return false;
	}

	final private boolean p3Rx64() {
		if (p3Rx67()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx68()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx24() {
		if (p3Rx17()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx38()) pScanPos = xsp;
		if (pScanToken(88)) return true;
		if (pScanToken(101)) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx81() {
		if (pScanToken(K_SELECT)) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx85()) pScanPos = xsp;
		if (p3Rx86()) return true;
		if (p3Rx87()) return true;
		xsp = pScanPos;
		if (p3Rx88()) pScanPos = xsp;
		xsp = pScanPos;
		if (p3Rx89()) pScanPos = xsp;
		xsp = pScanPos;
		if (p3Rx90()) pScanPos = xsp;
		xsp = pScanPos;
		if (p3Rx91()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx35() {
		if (pScanToken(K_NOT)) return true;
		return false;
	}

	final private boolean p3Rx21() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx35()) pScanPos = xsp;
		if (pScanToken(K_BETWEEN)) return true;
		if (p3Rx36()) return true;
		if (pScanToken(K_AND)) return true;
		if (p3Rx36()) return true;
		return false;
	}

	final private boolean p3Rx106() {
		if (p3Rx70()) return true;
		return false;
	}

	final private boolean p3Rx30() {
		if (pScanToken(S_QUOTED_IDENTIFIER)) return true;
		return false;
	}

	final private boolean p3Rx107() {
		if (p3Rx77()) return true;
		return false;
	}

	final private boolean px3x5() {
		if (pScanToken(88)) return true;
		return false;
	}

	final private boolean p3Rx34() {
		if (pScanToken(K_NOT)) return true;
		return false;
	}

	final private boolean p3Rx20() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx34()) pScanPos = xsp;
		if (pScanToken(K_IN)) return true;
		if (pScanToken(88)) return true;
		xsp = pScanPos;
		if (p3Rx106()) {
			pScanPos = xsp;
			if (p3Rx107()) return true;
		}
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx66() {
		if (p3Rx70()) return true;
		return false;
	}

	final private boolean p3Rx133() {
		if (pScanToken(K_ANY)) return true;
		return false;
	}

	final private boolean p3Rx118() {
		if (p3Rx81()) return true;
		return false;
	}

	final private boolean p3Rx44() {
		if (pScanToken(S_IDENTIFIER)) return true;
		return false;
	}

	final private boolean p3Rx28() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx44()) {
			pScanPos = xsp;
			if (p3Rx45()) return true;
		}
		if (pScanToken(88)) return true;
		xsp = pScanPos;
		if (p3Rx66()) pScanPos = xsp;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx117() {
		if (pScanToken(88)) return true;
		if (p3Rx81()) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx23() {
		if (pScanToken(88)) return true;
		if (pScanToken(K_SELECT)) return true;
		return false;
	}

	final private boolean p3Rx116() {
		if (pScanToken(K_UNION)) return true;
		Token xsp;
		xsp = pScanPos;
		if (pScanToken(5)) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx53() {
		if (pScanToken(105)) return true;
		return false;
	}

	final private boolean p3Rx131() {
		if (pScanToken(93)) return true;
		if (p3Rx17()) return true;
		return false;
	}

	final private boolean p3Rx102() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx116()) {
			pScanPos = xsp;
			if (pScanToken(35)) {
				pScanPos = xsp;
				if (pScanToken(42)) return true;
			}
		}
		xsp = pScanPos;
		if (p3Rx117()) {
			pScanPos = xsp;
			if (p3Rx118()) return true;
		}
		return false;
	}

	final private boolean p3Rx27() {
		if (pScanToken(K_DISTINCT)) return true;
		return false;
	}

	final private boolean p3Rx111() {
		if (p3Rx17()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx131()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx43() {
		if (pScanToken(K_COUNT)) return true;
		return false;
	}

	final private boolean p3Rx49() {
		if (p3Rx53()) return true;
		return false;
	}

	final private boolean p3Rx42() {
		if (pScanToken(K_MIN)) return true;
		return false;
	}

	final private boolean p3Rx36() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx48()) {
			pScanPos = xsp;
			if (p3Rx49()) return true;
		}
		return false;
	}

	final private boolean p3Rx48() {
		if (p3Rx19()) return true;
		return false;
	}

	final private boolean p3Rx41() {
		if (pScanToken(K_MAX)) return true;
		return false;
	}

	final private boolean p3Rx40() {
		if (pScanToken(K_AVG)) return true;
		return false;
	}

	final private boolean p3Rx25() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx39()) {
			pScanPos = xsp;
			if (p3Rx40()) {
				pScanPos = xsp;
				if (p3Rx41()) {
					pScanPos = xsp;
					if (p3Rx42()) {
						pScanPos = xsp;
						if (p3Rx43()) return true;
					}
				}
			}
		}
		return false;
	}

	final private boolean p3Rx39() {
		if (pScanToken(K_SUM)) return true;
		return false;
	}

	final private boolean p3Rx115() {
		if (pScanToken(K_HAVING)) return true;
		if (p3Rx64()) return true;
		return false;
	}

	final private boolean p3x11() {
		Token xsp;
		xsp = pScanPos;
		if (pScanToken(7)) {
			pScanPos = xsp;
			if (pScanToken(5)) {
				pScanPos = xsp;
				if (p3Rx23()) return true;
			}
		}
		return false;
	}

	final private boolean p3Rx128() {
		if (pScanToken(K_PRIOR)) return true;
		return false;
	}

	final private boolean p3Rx126() {
		if (pScanToken(100)) return true;
		return false;
	}

	final private boolean p3Rx125() {
		if (pScanToken(99)) return true;
		return false;
	}

	final private boolean p3Rx105() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx128()) pScanPos = xsp;
		if (p3Rx36()) return true;
		return false;
	}

	final private boolean p3Rx101() {
		if (pScanToken(K_GROUP)) return true;
		if (pScanToken(K_BY)) return true;
		if (p3Rx70()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx115()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx124() {
		if (pScanToken(98)) return true;
		return false;
	}

	final private boolean p3Rx123() {
		if (pScanToken(97)) return true;
		return false;
	}

	final private boolean p3Rx62() {
		if (pScanToken(88)) return true;
		if (p3Rx64()) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx122() {
		if (pScanToken(96)) return true;
		return false;
	}

	final private boolean p3Rx61() {
		if (pScanToken(S_BIND)) return true;
		return false;
	}

	final private boolean p3Rx121() {
		if (pScanToken(95)) return true;
		return false;
	}

	final private boolean p3Rx132() {
		if (pScanToken(K_ALL)) return true;
		return false;
	}

	final private boolean p3Rx127() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx132()) {
			pScanPos = xsp;
			if (p3Rx133()) return true;
		}
		return false;
	}

	final private boolean p3Rx120() {
		if (pScanToken(94)) return true;
		return false;
	}

	final private boolean p3Rx119() {
		if (pScanToken(92)) return true;
		return false;
	}

	final private boolean p3Rx103() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx119()) {
			pScanPos = xsp;
			if (p3Rx120()) {
				pScanPos = xsp;
				if (p3Rx121()) {
					pScanPos = xsp;
					if (p3Rx122()) {
						pScanPos = xsp;
						if (p3Rx123()) {
							pScanPos = xsp;
							if (p3Rx124()) {
								pScanPos = xsp;
								if (p3Rx125()) {
									pScanPos = xsp;
									if (p3Rx126()) return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	final private boolean p3Rx114() {
		if (pScanToken(K_START)) return true;
		if (pScanToken(K_WITH)) return true;
		if (p3Rx64()) return true;
		return false;
	}

	final private boolean p3Rx113() {
		if (pScanToken(K_START)) return true;
		if (pScanToken(K_WITH)) return true;
		if (p3Rx64()) return true;
		return false;
	}

	final private boolean p3Rx104() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx127()) pScanPos = xsp;
		if (pScanToken(88)) return true;
		if (p3Rx77()) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx60() {
		if (pScanToken(S_CHAR_LITERAL)) return true;
		return false;
	}

	final private boolean p3Rx59() {
		if (pScanToken(S_NUMBER)) return true;
		return false;
	}

	final private boolean p3Rx100() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx113()) pScanPos = xsp;
		if (pScanToken(K_CONNECT)) return true;
		if (pScanToken(K_BY)) return true;
		if (p3Rx64()) return true;
		xsp = pScanPos;
		if (p3Rx114()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx58() {
		if (p3Rx63()) return true;
		return false;
	}

	final private boolean p3Rx29() {
		if (pScanToken(S_IDENTIFIER)) return true;
		return false;
	}

	final private boolean p3Rx17() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx29()) {
			pScanPos = xsp;
			if (p3Rx30()) return true;
		}
		return false;
	}

	final private boolean p3x15() {
		if (p3Rx28()) return true;
		return false;
	}

	final private boolean p3Rx26() {
		if (pScanToken(K_ALL)) return true;
		return false;
	}

	final private boolean p3x12() {
		if (p3Rx24()) return true;
		return false;
	}

	final private boolean p3Rx92() {
		if (p3Rx103()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx104()) {
			pScanPos = xsp;
			if (p3Rx105()) return true;
		}
		return false;
	}

	final private boolean p3Rx99() {
		if (pScanToken(K_WHERE)) return true;
		if (p3Rx64()) return true;
		return false;
	}

	final private boolean p3x14() {
		if (p3Rx25()) return true;
		if (pScanToken(88)) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx26()) {
			pScanPos = xsp;
			if (p3Rx27()) return true;
		}
		if (p3Rx63()) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx69() {
		if (pScanToken(93)) return true;
		if (p3Rx17()) return true;
		return false;
	}

	final private boolean p3Rx65() {
		if (pScanToken(93)) return true;
		if (p3Rx17()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx69()) pScanPos = xsp;
		return false;
	}

	final private boolean p3x13() {
		if (pScanToken(K_COUNT)) return true;
		if (pScanToken(88)) return true;
		if (pScanToken(103)) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx63() {
		if (p3Rx17()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx65()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx57() {
		if (p3Rx24()) return true;
		return false;
	}

	final private boolean p3Rx112() {
		if (pScanToken(S_IDENTIFIER)) return true;
		return false;
	}

	final private boolean p3Rx55() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx56()) {
			pScanPos = xsp;
			if (p3Rx57()) {
				pScanPos = xsp;
				if (p3x13()) {
					pScanPos = xsp;
					if (p3x14()) {
						pScanPos = xsp;
						if (p3x15()) {
							pScanPos = xsp;
							if (p3Rx58()) {
								pScanPos = xsp;
								if (p3Rx59()) {
									pScanPos = xsp;
									if (p3Rx60()) {
										pScanPos = xsp;
										if (p3Rx61()) {
											pScanPos = xsp;
											if (p3Rx62()) return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	final private boolean p3Rx56() {
		if (pScanToken(K_NULL)) return true;
		return false;
	}

	final private boolean p3Rx73() {
		if (pScanToken(89)) return true;
		if (p3Rx36()) return true;
		return false;
	}

	final private boolean p3Rx97() {
		if (p3Rx111()) return true;
		Token xsp;
		xsp = pScanPos;
		if (p3Rx112()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx70() {
		if (p3Rx36()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx73()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx98() {
		if (pScanToken(89)) return true;
		if (p3Rx97()) return true;
		return false;
	}

	final private boolean p3Rx87() {
		if (pScanToken(K_FROM)) return true;
		if (p3Rx97()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx98()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx84() {
		if (p3Rx93()) return true;
		return false;
	}

	final private boolean p3x10() {
		if (p3Rx22()) return true;
		return false;
	}

	final private boolean p3x9() {
		if (p3Rx21()) return true;
		return false;
	}

	final private boolean p3x8() {
		if (p3Rx20()) return true;
		return false;
	}

	final private boolean p3Rx54() {
		Token xsp;
		xsp = pScanPos;
		if (pScanToken(101)) {
			pScanPos = xsp;
			if (pScanToken(102)) return true;
		}
		return false;
	}

	final private boolean p3Rx83() {
		if (p3Rx92()) return true;
		return false;
	}

	final private boolean p3Rx80() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx83()) {
			pScanPos = xsp;
			if (p3x8()) {
				pScanPos = xsp;
				if (p3x9()) {
					pScanPos = xsp;
					if (p3x10()) {
						pScanPos = xsp;
						if (p3Rx84()) return true;
					}
				}
			}
		}
		return false;
	}

	final private boolean p3Rx51() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx54()) pScanPos = xsp;
		if (p3Rx55()) return true;
		return false;
	}

	final private boolean p3x4() {
		if (p3Rx17()) return true;
		if (pScanToken(93)) return true;
		if (p3Rx17()) return true;
		if (pScanToken(104)) return true;
		return false;
	}

	final private boolean p3x3() {
		if (p3Rx17()) return true;
		if (pScanToken(104)) return true;
		return false;
	}

	final private boolean p3Rx16() {
		Token xsp;
		xsp = pScanPos;
		if (p3x3()) {
			pScanPos = xsp;
			if (p3x4()) return true;
		}
		return false;
	}

	final private boolean p3Rx82() {
		if (pScanToken(K_PRIOR)) return true;
		return false;
	}

	final private boolean p3x7() {
		if (pScanToken(88)) return true;
		if (p3Rx19()) return true;
		if (pScanToken(89)) return true;
		return false;
	}

	final private boolean p3Rx136() {
		if (pScanToken(S_QUOTED_IDENTIFIER)) return true;
		return false;
	}

	final private boolean p3Rx79() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx82()) pScanPos = xsp;
		if (p3Rx19()) return true;
		return false;
	}

	final private boolean p3Rx52() {
		if (pScanToken(108)) return true;
		if (p3Rx51()) return true;
		return false;
	}

	final private boolean p3Rx138() {
		if (pScanToken(S_IDENTIFIER)) return true;
		return false;
	}

	final private boolean p3Rx46() {
		if (p3Rx51()) return true;
		Token xsp;
		while (true) {
			xsp = pScanPos;
			if (p3Rx52()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx137() {
		Token xsp;
		if (p3Rx138()) return true;
		while (true) {
			xsp = pScanPos;
			if (p3Rx138()) { pScanPos = xsp; break; }
		}
		return false;
	}

	final private boolean p3Rx78() {
		if (pScanToken(88)) return true;
		if (p3Rx70()) return true;
		if (pScanToken(90)) return true;
		return false;
	}

	final private boolean p3Rx76() {
		Token xsp;
		xsp = pScanPos;
		if (p3Rx78()) {
			pScanPos = xsp;
			if (p3Rx79()) return true;
		}
		xsp = pScanPos;
		if (p3Rx80()) pScanPos = xsp;
		return false;
	}

	final private boolean p3Rx135() {
		Token xsp;
		xsp = pScanPos;
		if (pScanToken(8)) pScanPos = xsp;
		xsp = pScanPos;
		if (p3Rx136()) {
			pScanPos = xsp;
			if (p3Rx137()) return true;
		}
		return false;
	}

	static private int[] pLa1_0;
	
	static private int[] pLa1_1;
	
	static private int[] pLa1_2;
	
	static private int[] pLa1_3;
	
	static {
		pLa1x0();
		pLa1x1();
		pLa1x2();
		pLa1x3();
	}
	
	private static void pLa1x0() {
		pLa1_0 = new int[] {0x8008000,0x0,0x0,0x810b000,0x4220000,0x4000000,0x4220000,0x0,0x10000,0x0,0x0,0x0,0x10000,0x0,0x1000000,0x0,0x0,0x1000000,0x0,0x0,0x0,0x2080400,0x0,0x0,0x0,0x20000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x10000000,0x800020,0x800020,0x0,0x40000,0x40000000,0x0,0x0,0x80400,0x100,0x80400,0x100,0x0,0x0,0x0,0x0,0x0,0x0,0x80000000,0x20,0x0,0x0,0x400200,0x400200,0x0,0x400200,0x400200,0x0,0x0,0x0,0x40,0x0,0x80400,0x0,0x0,0x80400,0x0,0x0,0x800,0x0,0xa0,0xa0,0x0,0x80400,0x80400,0x0,0x80400,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x800020,0x0,0x0,0x80400,0x80400,0x80400,0x0,0x0,};
	}
	
	private static void pLa1x1() {
		pLa1_1 = new int[] {0x1011004,0x0,0x0,0x1011004,0x1a400082,0x400000,0x1a400082,0x0,0x0,0x0,0x4000,0x0,0x0,0x40000,0x20000000,0x4000000,0x4000000,0x24000000,0x0,0x0,0x0,0x20a300,0x0,0x0,0x8000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x100000,0x0,0x0,0x0,0x0,0x80000000,0x0,0x408,0x0,0x8300,0x0,0x8300,0x0,0x0,0x0,0x0,0x0,0x80000000,0x80000000,0x0,0x0,0x408,0x8000000,0x0,0x0,0x0,0x0,0x0,0x0,0x20000,0x80000,0x0,0x2000,0x20a300,0x2000,0x200000,0x208300,0x0,0x20,0x2061,0x0,0x0,0x0,0x200000,0x208300,0x8300,0x2000,0x8008300,0x2000,0x2000,0x2000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x8000,0x0,0x300,0x300,0x8300,0x0,0x0,};
	}
	
	private static void pLa1x2() {
		pLa1_2 = new int[] {0xc0,0x2000000,0x1000000,0xc0,0x10,0x0,0x10,0x400,0x0,0x2000000,0x0,0x400,0x0,0x800,0x0,0x10,0x10,0x0,0x40000,0x100,0x2000000,0x1e41001,0x2000000,0x1000000,0x20,0x0,0x100,0x20000000,0x20000000,0x840000,0xd0000000,0x20000000,0x0,0x0,0x41000,0x0,0x0,0x0,0x0,0x100,0x0,0x0,0x8,0x2000000,0x1e41001,0x840000,0x1e41001,0x0,0x40000,0x840000,0x2000000,0x40000,0x0,0x0,0x0,0x0,0x8,0x0,0x0,0x0,0x2000000,0x0,0x0,0x2000000,0x0,0x0,0x0,0x0,0x1e41001,0x0,0x0,0x1e41001,0xd0000000,0x0,0xd0000000,0x2000000,0x0,0x0,0x0,0x1e41001,0x1e41001,0x0,0x1e41001,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1e41000,0x1,0x40001,0x1e41001,0x20000000,0x20000000,};
	}
	
	private static void pLa1x3() {
		pLa1_3 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x260,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1f,0x0,0x60,0x60,0x60,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xe0,0x0,0x60,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x60,0x0,0x0,0x60,0x1f,0x0,0x1f,0x0,0x0,0x0,0x0,0x260,0x260,0x0,0x260,0x0,0x0,0x0,0x460,0x460,0x880,0x880,0x1000,0x60,0x60,0x0,0x0,0x0,0x0,0x0,0x260,0x0,0x0,};
	}
	
	final private PCalls[] p2Returns = new PCalls[15];
	
	private boolean pReScan = false;
	
	private int pgc = 0;

	final private Token pconsumeToken(int kind) throws ParseException {
		Token oldToken;
		if ((oldToken = token).next != null) 
			token = token.next;
		else 
			token = token.next = tokenSource.getNextToken();
		pntk = -1;
		if (token.kind == kind) {
			pGen++;
			if (++pgc > 100) {
				pgc = 0;
				for (int i = 0; i < p2Returns.length; i++) {
					PCalls c = p2Returns[i];
					while (c != null) {
						if (c.gen < pGen) c.first = null;
						c = c.next;
					}
				}
			}
			return token;
		}
		token = oldToken;
		pKind = kind;
		String b = String.valueOf(tokenSource.inputStream.buffer);
		if(b.contains("skip") && !b.contains("limit"))
			throw generateParseException("Always be used to LIMIT with SKIP. For example select * from users limit 10 skip 3;" );
		else
			throw generateParseException();
	}

	static private final class LookaheadSuccess extends java.lang.Error { }
	
	final private LookaheadSuccess pls = new LookaheadSuccess();
	
	final private boolean pScanToken(int kind) {
		if (pScanPos == plastPos) {
			plineAnalice--;
			if (pScanPos.next == null) {
				plastPos = pScanPos = pScanPos.next = tokenSource.getNextToken();
			} else {
				plastPos = pScanPos = pScanPos.next;
			}
		} else {
			pScanPos = pScanPos.next;
		}
		if (pReScan) {
			int i = 0; Token tok = token;
			while (tok != null && tok != pScanPos) { i++; tok = tok.next; }
			if (tok != null) pAddErrorToken(kind, i);
		}
		if (pScanPos.kind != kind) return true;
		if (plineAnalice == 0 && pScanPos == plastPos) throw pls;
		return false;
	}

	final public Token getNextToken() {
		if (token.next != null) token = token.next;
		else token = token.next = tokenSource.getNextToken();
		pntk = -1;
		pGen++;
		return token;
	}

	final public Token getToken(int index) {
		Token t = lookingAhead ? pScanPos : token;
		for (int i = 0; i < index; i++) {
			if (t.next != null) t = t.next;
			else t = t.next = tokenSource.getNextToken();
		}
		return t;
	}

	final private int pNextToken() {
		if ((pNextToken=token.next) == null)
			return (pntk = (token.next=tokenSource.getNextToken()).kind);
		else
			return (pntk = pNextToken.kind);	
	}

	private java.util.Vector pExpEntries = new java.util.Vector();
	private int[] pExpEntry;
	private int pKind = -1;
	private int[] pLastTokens = new int[100];
	private int pEndPos;

	private void pAddErrorToken(int kind, int pos) {
		if (pos >= 100) return;
		if (pos == pEndPos + 1) {
			pLastTokens[pEndPos++] = kind;
		} else if (pEndPos != 0) {
			pExpEntry = new int[pEndPos];
			for (int i = 0; i < pEndPos; i++) {
				pExpEntry[i] = pLastTokens[i];
			}
			boolean exists = false;
			for (java.util.Enumeration e = pExpEntries.elements(); e.hasMoreElements();) {
				int[] oldentry = (int[])(e.nextElement());
				if (oldentry.length == pExpEntry.length) {
					exists = true;
					for (int i = 0; i < pExpEntry.length; i++) {
						if (oldentry[i] != pExpEntry[i]) {
							exists = false;
							break;
						}
					}
					if (exists) break;
				}
			}
			if (!exists) pExpEntries.addElement(pExpEntry);
			if (pos != 0) pLastTokens[(pEndPos = pos) - 1] = kind;
		}
	}
	
	public ParseException generateParseException (String exception){
		pExpEntries.removeAllElements();
		boolean[] la1tokens = new boolean[109];
		for (int i = 0; i < 109; i++) {
			la1tokens[i] = false;
		}
		if (pKind >= 0) {
			la1tokens[pKind] = true;
			pKind = -1;
		}
		for (int i = 0; i < 101; i++) {
			if (plineanalizce1[i] == pGen) {
				for (int j = 0; j < 32; j++) {
					if ((pLa1_0[i] & (1<<j)) != 0) {
						la1tokens[j] = true;
					}
					if ((pLa1_1[i] & (1<<j)) != 0) {
						la1tokens[32+j] = true;
					}
					if ((pLa1_2[i] & (1<<j)) != 0) {
						la1tokens[64+j] = true;
					}
					if ((pLa1_3[i] & (1<<j)) != 0) {
						la1tokens[96+j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 109; i++) {
			if (la1tokens[i]) {
				pExpEntry = new int[1];
				pExpEntry[0] = i;
				pExpEntries.addElement(pExpEntry);
			}
		}
		pEndPos = 0;
		pRescanToken();
		pAddErrorToken(0, 0);
		int[][] exptokseq = new int[pExpEntries.size()][];
		for (int i = 0; i < pExpEntries.size(); i++) {
			exptokseq[i] = (int[])pExpEntries.elementAt(i);
		}
		return new ParseException( exception);
	}

	public ParseException generateParseException() {
		
		pExpEntries.removeAllElements();
		boolean[] la1tokens = new boolean[109];
		for (int i = 0; i < 109; i++) {
			la1tokens[i] = false;
		}
		if (pKind >= 0) {
			la1tokens[pKind] = true;
			pKind = -1;
		}
		for (int i = 0; i < 101; i++) {
			if (plineanalizce1[i] == pGen) {
				for (int j = 0; j < 32; j++) {
					if ((pLa1_0[i] & (1<<j)) != 0) {
						la1tokens[j] = true;
					}
					if ((pLa1_1[i] & (1<<j)) != 0) {
						la1tokens[32+j] = true;
					}
					if ((pLa1_2[i] & (1<<j)) != 0) {
						la1tokens[64+j] = true;
					}
					if ((pLa1_3[i] & (1<<j)) != 0) {
						la1tokens[96+j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 109; i++) {
			if (la1tokens[i]) {
				pExpEntry = new int[1];
				pExpEntry[0] = i;
				pExpEntries.addElement(pExpEntry);
			}
		}
		pEndPos = 0;
		pRescanToken();
		pAddErrorToken(0, 0);
		int[][] exptokseq = new int[pExpEntries.size()][];
		for (int i = 0; i < pExpEntries.size(); i++) {
			exptokseq[i] = (int[])pExpEntries.elementAt(i);
		}
		return new ParseException(token, exptokseq, tokenImage);
	}

	final public void enableTracing() {
	}

	final public void disableTracing() {
	}

	final private void pRescanToken() {
		pReScan = true;
		for (int i = 0; i < 15; i++) {
			try {
				PCalls p = p2Returns[i];
				do {
					if (p.gen > pGen) {
						plineAnalice = p.arg; plastPos = pScanPos = p.first;
						switch (i) {
						case 0: p3x1(); break;
						case 1: p3x2(); break;
						case 2: p3x3(); break;
						case 3: p3x4(); break;
						case 4: px3x5(); break;
						case 5: p3x6(); break;
						case 6: p3x7(); break;
						case 7: p3x8(); break;
						case 8: p3x9(); break;
						case 9: p3x10(); break;
						case 10: p3x11(); break;
						case 11: p3x12(); break;
						case 12: p3x13(); break;
						case 13: p3x14(); break;
						case 14: p3x15(); break;
						}
					}
					p = p.next;
				} while (p != null);
			} catch(LookaheadSuccess ls) { }
		}
		pReScan = false;
	}

	final private void save(int index, int xla) {
		PCalls pcalls = p2Returns[index];
		while (pcalls.gen > pGen) {
			if (pcalls.next == null) { pcalls = pcalls.next = new PCalls(); break; }
			pcalls = pcalls.next;
		}
		pcalls.gen = pGen + xla - plineAnalice; pcalls.first = token; pcalls.arg = xla;
	}

	static final class PCalls {
		int gen;
		Token first;
		int arg;
		PCalls next;
	}


}
