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
package com.indracompany.sofia2.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public class ApiServiceHelper {
	
	public static String buildQuery(String queryDb, HashMap<String, String> queryParametersValues) {
		for (String param : queryParametersValues.keySet()) {
			queryDb = queryDb.replace("{$" + param + "}", queryParametersValues.get(param));
		}
		return queryDb;
	}
	
	
	
	
	public static boolean isSQLLIKE(String query, String queryType) {
		if (query!=null && query.length()>0 && queryType!=null && queryType.length()>0){
			return queryType.equals("SQLLIKE");
		}
		return false;
	}
	
	public static String readPayload(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader;
		try {
			reader = request.getReader();
	        String line;
	        while((line = reader.readLine()) != null){
	            buffer.append(line);
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	

}
