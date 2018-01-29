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
package com.indra.sofia2.support.parsersqlnative.util;

import java.util.Hashtable;



@SuppressWarnings({"rawtypes", "unchecked"})
public class Utils {

private static final String ST_POINT = "ST_POINT";
private static final String S_NEAR = "S_NEAR";

private static Hashtable customs = null;

  public static final int VARIABLE_PLIST = 10000;

  public static void addCustomFunction(String fct, int nparm) {
    if(customs == null) {
    	customs = new Hashtable();
    }
    int myNparm = nparm;
    if(myNparm < 0) {
    	myNparm = 1;
    }
    customs.put(fct.toUpperCase(), Integer.valueOf(myNparm));
  }

  public static int isCustomFunction(String fct) {
    Integer nparm;
    if(fct == null || fct.length()<1 || customs == null
      || (nparm = (Integer)customs.get(fct.toUpperCase())) == null) {
       return -1;
    }
    return nparm.intValue();
  }

  public static boolean isAggregate(String op) {
    String tmp = op.toUpperCase().trim();
    return tmp.equals("SUM") || tmp.equals("AVG")
        || tmp.equals("MAX") || tmp.equals("MIN")
        || tmp.equals("COUNT") || (customs != null && customs.get(tmp) != null);
  }

  public static String getAggregateCall(String c) {
    int pos = c.indexOf('(');
    if (pos <= 0) {
    	return null;
    }
    String call = c.substring(0,pos);
    if (Utils.isAggregate(call)) {
    	return call.trim();
    } else {
    	return null;
    }
  }
  
  public static boolean isGeospatial(String query){
	  return  query.toUpperCase().contains(S_NEAR);
  }

public static boolean checkGeospatialSyntax(String query) {
	
	boolean result= 
			countOccurrencesOf(query.toUpperCase(),S_NEAR) == 1
			&& countOccurrencesOf(query.toUpperCase(), ST_POINT) == 1
			&& countOccurrencesOf(query.toUpperCase(), "(") == 2
			&& countOccurrencesOf(query.toUpperCase(), ")") == 2
			&& countOccurrencesOf(query.toUpperCase(), ",") == 2; 
	
	return result;
}


public static int countOccurrencesOf(String str, String sub) {
	if (!hasLength(str) || !hasLength(sub)) {
		return 0;
	}

	int count = 0;
	int pos = 0;
	int idx;
	while ((idx = str.indexOf(sub, pos)) != -1) {
		++count;
		pos = idx + sub.length();
	}
	return count;
}


public static boolean hasLength(String str) {
	return (str != null && !str.isEmpty());
}
  

};

