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

import com.indra.sofia2.support.parsersqlnative.Constant;
import com.indra.sofia2.support.parsersqlnative.Expression;
import com.indra.sofia2.support.parsersqlnative.ParseException;
import com.indra.sofia2.support.parsersqlnative.Query;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestOp {

  private static Log log = LogFactory.getLog(TestOp.class);
	
  public static void main(String args[]) {
    try {

      Parser p = new Parser();

      p.initParser(new ByteArrayInputStream(args[0].getBytes()));
      Query st = (Query) p.processStatement();
      log.info(st.toString()); 
        Expression where = (Expression) st.getWhere();
        Expression prjNums = new Expression("OR");
        for (int i = 1; i < 4; i++) {
            prjNums.addOperand(
                new Expression(
                    "=",
                    new Constant("ID", Constant.COLUMNNAME),
                    new Constant("" + i, Constant.NUMBER)));
        }
 
        if (where != null) {
            Expression w = new Expression("AND");
            w.addOperand(where);
            w.addOperand(prjNums);
            where = w;
        } else {
            where = prjNums;
        }
        st.addWhere(where);
      log.info(st.toString()); 

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
}
