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

import com.indra.sofia2.support.parsersqlnative.Insert;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("rawtypes")
public class InsertTest {

  private static Log log = LogFactory.getLog(InsertTest.class);
  
  public static void main(String args[]) {
    try {

      Parser p = new Parser();

      p.initParser(new ByteArrayInputStream(args[0].getBytes()));
      Statement st = p.processStatement();

      if(st instanceof Insert) {
        Insert ins = (Insert)st;
        Vector columns = ins.getColumns();
        Vector values = ins.getValues();
        log.info("Insert: Table=" + ins.getTable());
        for(int i=0; i<columns.size(); i++) {
          log.info(
           "  " + columns.elementAt(i) + "=" + values.elementAt(i));
        }
      }

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

};

