package com.indracompany.sofia2.controlpanel.helper.digitaltwin.device;

import java.util.HashMap;
import java.util.Map;

public class GeneratorJavaTypesMapper {
	
	
	private static Map<String, String> types=new HashMap<String, String>();
	
	static {
		types.put("string", "String");
		types.put("int", "Integer");
		types.put("object", "Object");
		types.put("double", "Double");
		types.put("boolean", "Boolean");
	}
	

	public static String mapPropertyName(String type) {
		return types.get(type);
	}
	
	
	
}
