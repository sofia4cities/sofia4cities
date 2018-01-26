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
import java.util.Vector;

import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("rawtypes")
public class Test {

  private static Log log = LogFactory.getLog(Test.class);
	
  public static void main(String args[]) {
    try {

      Parser p = null;

      if(args.length < 1) {
        log.info("Reading SQL from stdin (quit; or exit; to quit)");
        p = new Parser(System.in);
      } else {
        p = new Parser(new DataInputStream(new FileInputStream(args[0])));
      }
      Vector v = p.readStatements();

      for(int i=0; i<v.size(); i++) {
        Statement st = (Statement)v.elementAt(i);
        log.info(st.toString());
      }

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

};

