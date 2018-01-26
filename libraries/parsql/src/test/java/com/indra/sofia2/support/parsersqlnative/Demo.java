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
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Vector;

import com.indra.sofia2.support.parsersqlnative.Evaluate;
import com.indra.sofia2.support.parsersqlnative.Expression;
import com.indra.sofia2.support.parsersqlnative.FromItem;
import com.indra.sofia2.support.parsersqlnative.Insert;
import com.indra.sofia2.support.parsersqlnative.Query;
import com.indra.sofia2.support.parsersqlnative.SelectItem;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.Tuple;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 * Demo is able to send SQL queries to simple CSV (comma-separated values)
 * files; the CSV syntax used here is very simple:
 *  The 1st line contains the column names
 *  Other lines contain column values (tuples)
 *  Values are separated by commas, so they can't contain commas (it's just
 *  for demo purposes).
 * Example:
 * Create a num.db text file that contains the following:
 *  a,b,c,d,e
 *  1,1,1,1,1
 *  2,2,2,2,2
 *  1,2,3,4,5
 *  5,4,3,2,1
 * You can then run ZDemo, and query it; some legal queries follow:
 *  select * from num;
 *  select a, b from num;
 *  select a+b, c from num;
 *  select * from num where a = 1 or e = 1;
 *  select * from num where a = 1 and b = 1 or e = 1;
 *  select d, e from num where a + b + c <= 3;
 *  select * from num where 3 = a + b + c;
 *  select * from num where a = b or e = d - 1;
 *  select * from num where b ** a <= 2;
 * </pre>
 */
@SuppressWarnings("rawtypes")
public class Demo {
	
	private static Log log = LogFactory.getLog(Demo.class);

  public static void main(String args[]) {
    try {

      Parser p = null;

      if(args.length < 1) {
        log.info("Reading SQL from stdin (quit; or exit; to quit)");
        p = new Parser(System.in);
      } else {
        p = new Parser(new DataInputStream(new FileInputStream(args[0])));
      }

      // Read all SQL statements from input
      Statement st;
      while((st = p.processStatement()) != null) {

        log.info(st.toString()); // Display the statement

        if(st instanceof Query) { // An SQL query: query the DB
          queryDB((Query)st);
        } else if(st instanceof Insert) { // An SQL insert
          insertDB((Insert)st);
        }
      }

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Query the database
   */
  static void queryDB(Query q) throws Exception {

    Vector sel = q.getSelect(); // SELECT part of the query
    Vector from = q.getFrom();  // FROM part of the query
    Expression where = (Expression)q.getWhere();  // WHERE part of the query

    if(from.size() > 1) {
      throw new SQLException("Joins are not supported");
    }

    // Retrieve the table name in the FROM clause
    FromItem table = (FromItem)from.elementAt(0);

    // We suppose the data is in a text file called <tableName>.db
    // <tableName> is the table name in the FROM clause
    BufferedReader db = new BufferedReader(
      new FileReader("demo\\" + table.getTable() + ".db"));
    
    // Read the column names (the 1st line of the .db file)
    Tuple tuple = new Tuple(db.readLine()); 

    Evaluate evaluator = new Evaluate();

    // Now, each line in the .db file is a tuple
    String tpl;
    while((tpl = db.readLine()) != null) {

      tuple.setRow(tpl);

      // EvaluateTest the WHERE expression for the current tuple
      // Display the tuple if the condition EvaluateTests to true

      if(where == null || evaluator.evaluate(tuple, where)) {
        displayTuple(tuple, sel);
      }

    }

    db.close();
  }

  /**
   * Display a tuple, according to a SELECT map
   */
  static void displayTuple(Tuple tuple, Vector map) throws Exception {

    // If it is a "select *", display the whole tuple
    if(((SelectItem)map.elementAt(0)).isWildcard()) {
      log.info(tuple.toString());
      return;
    }

    Evaluate evaluator = new Evaluate();

    // EvaluateTest the value of each select item
    String strOut = ""; 
    for(int i=0; i<map.size(); i++) {

      SelectItem item = (SelectItem)map.elementAt(i);
      strOut += (
        evaluator.evalExpValue(tuple, item.getExpression()).toString());

      if(i == map.size()-1) {
    	  log.info(strOut);
      } else {
    	  strOut += ", ";
      }
    }
  }

  static void insertDB(Insert ins) throws Exception {
    log.info("Should implement INSERT here");
    log.info(ins.toString());
  }

};

