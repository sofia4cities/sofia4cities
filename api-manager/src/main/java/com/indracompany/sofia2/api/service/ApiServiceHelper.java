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
