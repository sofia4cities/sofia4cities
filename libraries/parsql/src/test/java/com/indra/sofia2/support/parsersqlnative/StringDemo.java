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

import com.indra.sofia2.support.parsersqlnative.ParseException;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringDemo {

  private static Log log = LogFactory.getLog(StringDemo.class);
	
  public static void main(String args[]) {
    try {

      Parser p = new Parser();

      p.initParser(new ByteArrayInputStream(args[0].getBytes()));
      Statement st = p.processStatement();
      log.info(st.toString()); // Display the statement

    } catch(ParseException e) {
    	log.error("PARSE EXCEPTION:");
      e.printStackTrace(System.err);
    } catch(Error e) {
      log.error("ERROR");
    } catch(Exception e) {
      log.error("CLASS" + e.getClass());
      e.printStackTrace(System.err);
    }
  }

};
