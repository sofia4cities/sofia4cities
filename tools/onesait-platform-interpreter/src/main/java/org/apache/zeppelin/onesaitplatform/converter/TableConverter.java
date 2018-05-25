package org.apache.zeppelin.onesaitplatform.converter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class TableConverter {
	
	private static String fieldSeparator = "\t";
	private static char rowSeparator = '\n';
	
	public static List<String> fromListStr(List<String> l) {
		List<String> tableView = new LinkedList<String>(); 
		appendHeaderTable(tableView);
		appendJsonHeaderValuesTable(tableView,l);
	    return tableView;
	}
	
	
    
    private static List<String> appendHeaderTable(List<String> l) {
    	l.add("%table");
    	return l;
    }
    
    
    
    private static List<String> appendJsonHeaderValuesTable(List<String> tableView, List<String> jsonStrInstances) {
    	List<String> jsonPlannedFields = getStringJsonKeys(jsonStrInstances.get(0));
    	//Headers
    	StringBuilder strb = new StringBuilder();
    	for(String field: jsonPlannedFields) {
			strb.append(field);
			strb.append(fieldSeparator);
    	}
		int size = strb.length();
		strb.setCharAt(size-1,rowSeparator);
		tableView.add(strb.toString());
		
		//Values
    	for(String instance: jsonStrInstances) {
    		strb = new StringBuilder();
    		
    		for(String field: jsonPlannedFields) {
    			String value = getStringJsonValue(instance, field);
    			strb.append(value);
    			strb.append(fieldSeparator);
        	}
    		size = strb.length();
    		strb.setCharAt(size-1,rowSeparator);
    		tableView.add(strb.toString());
    	}
    	
    	return tableView;
    }
    
    private static List<String> getStringJsonKeys(String json) {
    	JsonObject object =  parseDataObj(json);
    	List<String> l = new LinkedList<String>();
    	for (Map.Entry<String,JsonElement> entry : object.entrySet()) {
    	    l.add(entry.getKey());
    	}
    	return l;
    }
    
    private static String getStringJsonValue(String json, String key) {
    	JsonObject object =  parseDataObj(json);
    	return object.get(key).toString();
    }
    
    
    private static JsonObject parseDataObj(String json){
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonTree = jsonParser.parse(json).getAsJsonObject();
        return jsonTree;
    }
}
