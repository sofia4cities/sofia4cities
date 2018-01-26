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
package com.indra.sofia2.support.parsersqlnative;/*

 * This file is part of Zql.
 *
 * Zql is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Zql is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zql.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.DataInputStream;
import java.io.FileInputStream;

import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import com.indra.sofia2.support.parsersqlnative.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CustomFct {

  private static Log log = LogFactory.getLog(CustomFct.class);
  
  public static void main(String args[]) {
    try {

     Parser p = null;

      if(args.length < 1) {
        log.info("Reading SQL from stdin (quit; or exit; to quit)");
        p = new Parser(System.in);
      } else {
        p = new Parser(new DataInputStream(new FileInputStream(args[0])));
      }

      p.addCustomFunction("whynot", 1);
      p.addCustomFunction("nvl", 2);
      p.addCustomFunction("concat", Utils.VARIABLE_PLIST);
      p.addCustomFunction("curdate", 0);

      Statement st;
      while((st = p.processStatement()) != null) {

        log.info(st.toString()); 

      }

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

};

