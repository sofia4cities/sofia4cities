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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;

import com.indra.sofia2.support.parsersqlnative.Evaluate;
import com.indra.sofia2.support.parsersqlnative.Exp;
import com.indra.sofia2.support.parsersqlnative.Tuple;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EvaluateTest {

	  private static Log log = LogFactory.getLog(EvaluateTest.class);
	
	  public static void main(String args[]) {
	    try {
	      BufferedReader db = new BufferedReader(new FileReader("test.db"));
	      String tpl = db.readLine();
	      Tuple t = new Tuple(tpl);

	      Parser parser = new Parser();
	      Evaluate evaluator = new Evaluate();

	      while((tpl = db.readLine()) != null) {
	        t.setRow(tpl);
	        BufferedReader sql = new BufferedReader(new FileReader("test.sql")); 
	        String query;
	        while((query = sql.readLine()) != null) {
	          parser.initParser(new ByteArrayInputStream(query.getBytes()));
	          Exp exp = parser.readExpression();
	          log.info(tpl + ", " + query + ", " + evaluator.evaluate(t, exp));
	        }
	        sql.close();
	      }
	      db.close();
	    } catch(Exception e) {
	      e.printStackTrace();
	    }
	  }
  
};

