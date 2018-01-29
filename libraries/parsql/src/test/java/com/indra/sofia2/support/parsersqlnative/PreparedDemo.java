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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.indra.sofia2.support.parsersqlnative.Constant;
import com.indra.sofia2.support.parsersqlnative.Delete;
import com.indra.sofia2.support.parsersqlnative.Exp;
import com.indra.sofia2.support.parsersqlnative.Expression;
import com.indra.sofia2.support.parsersqlnative.FromItem;
import com.indra.sofia2.support.parsersqlnative.Insert;
import com.indra.sofia2.support.parsersqlnative.Query;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.Update;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * <pre>
 * </pre>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PreparedDemo {

  private static Log log = LogFactory.getLog(PreparedDemo.class);
	
  public static void main(String args[]) {
    try {

      Parser p = null;

      if(args.length < 1) {
        log.info("Reading SQL from stdin (quit; or exit; to quit)");
        p = new Parser(System.in);
      } else {
        p = new Parser(new DataInputStream(new FileInputStream(args[0])));
      }

      Statement st;
      while((st = p.processStatement()) != null) {

        log.info(st.toString()); 

        if(st instanceof Query) {
          handleQuery((Query)st);
        } else if(st instanceof Insert) { 
          handleInsert((Insert)st);
        } else if(st instanceof Update) {
          handleUpdate((Update)st);
        } else if(st instanceof Delete) {
          handleDelete((Delete)st);
        }
      }

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  static void handleQuery(Query q) throws Exception {
    log.info("SELECT Statement:");

//    Vector sel = q.getSelect(); 

    Expression w = (Expression)q.getWhere();
    if(w != null) {

      Hashtable meta = null;

      Vector from = q.getFrom();  


      meta = new Hashtable();
      for(int i=0; i<from.size(); i++) {
        FromItem fi = (FromItem)from.elementAt(i);
        String alias = fi.getAlias();
        if(alias == null) alias = fi.getTable();
        meta.put(alias.toUpperCase(), fi.getTable());
      }

      handleWhere(w, meta);
    }

  }

  static void handleInsert(Insert ins) throws Exception {
    log.info("INSERT Statement:");
    String tab = ins.getTable();
    Vector values = ins.getValues();
    if(values == null) {
      log.info("no VALUES(), probably a subquery ?");
    }
    int nval = values.size();
    Vector columns = ins.getColumns();
    if(columns == null) {
      log.info("no column names, assuming _col_1"
       + (nval > 1 ? " to _col_" + nval : ""));
      columns = new Vector(nval);
      for(int i=1; i<=nval; i++) {
        columns.addElement("_col_" + i);
      }
    }

    for (int i=0; i<nval; i++) {
      Exp v = (Exp)values.elementAt(i);
      if(isPreparedColumn(v)) {
        log.info("[" + tab + "," + columns.elementAt(i) + "]");
      }
    }
  }

  static void handleUpdate(Update upd) throws Exception {
    log.info("UPDATE Statement:");

    String tab = upd.getTable();
    Hashtable set = upd.getSet();
    Enumeration k = set.keys();
    while(k.hasMoreElements()) {
      String col = (String)k.nextElement();
      Exp e = (Exp)set.get(col);
      if(isPreparedColumn(e)) {
        log.info("[" + tab + "," + col + "]");
      }
    }

    Expression w = (Expression)upd.getWhere();
    if(w != null) {
      Hashtable meta = new Hashtable(1);
      meta.put(tab, tab);
      handleWhere(w, meta);
    }

  }

  static void handleDelete(Delete del) throws Exception {
    log.info("DELETE Statement:");

    String tab = del.getTable();

    Expression w = (Expression)del.getWhere();
    if(w != null) {
      Hashtable meta = new Hashtable(1);
      meta.put(tab, tab);
      handleWhere(w, meta);
    }

  }

  static void handleWhere(Exp e, Hashtable meta) throws Exception {

    if(! (e instanceof Expression)) return;
    Expression w = (Expression)e;

    Vector operands = w.getOperands();
    if(operands == null) return;

    String prepared = null;
    for(int i=0; i<operands.size(); i++) {
      if(isPreparedColumn((Exp)operands.elementAt(i))) {
        prepared = ((Constant)operands.elementAt(0)).getValue();
        if(operands.size() != 2) {
          throw new Exception("ERROR in where clause ?? found:"
           + w.toString());
        }
        break;
      }
    }

    if(prepared != null) { 

      boolean noalias = false;

      String tbl = null;

      int pos = prepared.lastIndexOf('.');
      if(pos > 0) {  

        tbl = prepared.substring(0, pos);
        prepared = prepared.substring(pos+1); 

        if((pos = tbl.lastIndexOf('.')) > 0) { 
          tbl = tbl.substring(pos+1);
          noalias = true;
        }
      }

      if(! noalias) {
        // If tbl is an alias, resolve it
        if (tbl != null) {
        	tbl = (String)meta.get(tbl.toUpperCase());
        }

      }

      if(tbl == null && meta.size() == 1) {
        Enumeration keys = meta.keys();
        tbl = (String)keys.nextElement();
      }


      log.info("[" + (tbl == null ? "unknown" : tbl) + ","
       + prepared + "]");

    } else {  

      for(int i=0; i<operands.size(); i++) {
        handleWhere(w.getOperand(i), meta); 
      }
    }

  }

  static boolean isPreparedColumn(Exp v) {
    return
     (v instanceof Expression && ((Expression)v).getOperator().equals("?"));
  }

}

