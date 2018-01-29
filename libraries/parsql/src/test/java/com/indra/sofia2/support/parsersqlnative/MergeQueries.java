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

import java.io.ByteArrayInputStream;
import java.util.Vector;

import com.indra.sofia2.support.parsersqlnative.Constant;
import com.indra.sofia2.support.parsersqlnative.Expression;
import com.indra.sofia2.support.parsersqlnative.Query;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MergeQueries {

  private static Log log = LogFactory.getLog(MergeQueries.class);
  
  public static void main(String args[]) {
    try {

      String q1 = "select a.id, a.descr from acce.producto a;";
      String q2 = "select b.id, b.price from info.ventas b;";

      Parser p = new Parser();

      p.initParser(new ByteArrayInputStream(q1.getBytes()));
      Query st1 = (Query)p.processStatement();
      log.info(st1.toString()); 

      p.initParser(new ByteArrayInputStream(q2.getBytes()));
      Query st2 = (Query)p.processStatement();
      log.info(st2.toString()); 

      Vector cols = st1.getSelect();
      Vector from = st1.getFrom();

      Vector cols2 = st2.getSelect();
      Vector f2 = st2.getFrom();

      // Append "select" parts
      for(int i=0; i<cols2.size(); i++) {
    	  cols.addElement(cols2.elementAt(i));
      }

      // Append "from" parts
      for(int i=0; i<f2.size(); i++) {
    	  from.addElement(f2.elementAt(i));
      }

      // new query with where
      Query q = new Query();
      q.addSelect(cols);
      q.addFrom(from);
      Expression where = new Expression("=",
       new Constant("a.id", Constant.COLUMNNAME),
       new Constant("b.id", Constant.COLUMNNAME));
      q.addWhere(where);

      log.info(q);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

};

