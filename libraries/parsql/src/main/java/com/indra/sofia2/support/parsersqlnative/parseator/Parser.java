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
package com.indra.sofia2.support.parsersqlnative.parseator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.indra.sofia2.support.parsersqlnative.Exp;
import com.indra.sofia2.support.parsersqlnative.PParser;
import com.indra.sofia2.support.parsersqlnative.ParseException;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.util.Utils;

@SuppressWarnings("rawtypes")
public class Parser {

	PParser parser = null;
	

	public Parser (String sql ){
		try {
			initParser( new ByteArrayInputStream(sql.getBytes("UTF-8")));
			parser.setQuery(sql);
		} catch (UnsupportedEncodingException e) {
		
		}
	}
	public Parser(InputStream in) {
		initParser(in);
	}

	public Parser() {};

	public void initParser(InputStream in) {
		if(parser == null) {
			parser = new PParser(in);
		} else {
			parser.reInit(in);
		}
	}

	public void addCustomFunction(String fct, int nparm) {
		Utils.addCustomFunction(fct, nparm);
	}

	public Statement processStatement() throws ParseException {
		if(parser == null) {
			throw new ParseException("Parser not initialized: use initParser(InputStream);");
		}
		
		return parser.sQLStatement();
	}

	public Vector readStatements() throws ParseException {
		if(parser == null) {
			throw new ParseException("Parser not initialized: use initParser(InputStream);");
		}
		
		return parser.sQLStatements();
	}

	public Exp readExpression() throws ParseException {
		if(parser == null) {
			throw new ParseException("Parser not initialized: use initParser(InputStream);");
		}
		
		return parser.sQLExpression();
	}
}
