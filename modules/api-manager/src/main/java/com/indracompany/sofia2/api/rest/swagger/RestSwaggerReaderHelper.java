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
package com.indracompany.sofia2.api.rest.swagger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

public class RestSwaggerReaderHelper {
	
	public static Parameter populateParameter(Swagger swagger, String name, String description, boolean required, String parameterType, String dataType, String arrayType, String value)
	{
		Parameter parameter = null;
		if (parameterType.equals("body")) {
			parameter = new BodyParameter();
		} else if (parameterType.equals("formData")) {
			parameter = new FormParameter();
		} else if (parameterType.equals("header")) {
			parameter = new HeaderParameter();
		} else if (parameterType.equals("path")) {
			parameter = new PathParameter();
		} else if (parameterType.equals("query")) {
			parameter = new QueryParameter();
		}
		
		if (parameter != null) {
            parameter.setName(name);
            parameter.setDescription(description);
            parameter.setRequired(required);
            

            if (parameter instanceof SerializableParameter) {
                SerializableParameter serializableParameter = (SerializableParameter) parameter;
                
                if (value!=null && (!value.equals(""))) {
                	 List<String> enumValue=new ArrayList<>();
                     enumValue.add(value);
     				((SerializableParameter) parameter).setEnumValue(enumValue);
                }
               

                if (dataType != null) {
                    serializableParameter.setType(dataType);
                   
                    if (dataType.equalsIgnoreCase("array")) {
                        if (arrayType != null) {
                            if (arrayType.equalsIgnoreCase("string")) {
                                serializableParameter.setItems(new StringProperty());
                            }
                            if (arrayType.equalsIgnoreCase("int") || arrayType.equalsIgnoreCase("integer")) {
                                serializableParameter.setItems(new IntegerProperty());
                            }
                            if (arrayType.equalsIgnoreCase("long")) {
                                serializableParameter.setItems(new LongProperty());
                            }
                            if (arrayType.equalsIgnoreCase("float")) {
                                serializableParameter.setItems(new FloatProperty());
                            }
                            if (arrayType.equalsIgnoreCase("double")) {
                                serializableParameter.setItems(new DoubleProperty());
                            }
                            if (arrayType.equalsIgnoreCase("boolean")) {
                                serializableParameter.setItems(new BooleanProperty());
                            }
                        }
                    }
                    if (dataType.equalsIgnoreCase("date") || 
                    		dataType.equalsIgnoreCase("date-time") || 
                    		dataType.equalsIgnoreCase("password") ||
                    		dataType.equalsIgnoreCase("byte") || 
                    		dataType.equalsIgnoreCase("binary") || 
                    		dataType.equalsIgnoreCase("email") || 
                    		dataType.equalsIgnoreCase("uuid") || 
                    		dataType.equalsIgnoreCase("uri") || 
                    		dataType.equalsIgnoreCase("hostname") || 
                    		dataType.equalsIgnoreCase("ipv4") 
                    		) 
                    {
                    	serializableParameter.setType("string");
                    	serializableParameter.setFormat(dataType); 
                    }

                }
               
            }
            

            // set schema on body parameter
            if (parameter instanceof BodyParameter) {
                BodyParameter bp = (BodyParameter) parameter;

                if (dataType != null) {
                    if (dataType.endsWith("[]")) {
                        String typeName = dataType;
                        typeName = typeName.substring(0, typeName.length() - 2);
                        Property prop = modelTypeAsProperty(typeName, swagger);
                        if (prop != null) {
                            ArrayModel arrayModel = new ArrayModel();
                            arrayModel.setItems(prop);
                            bp.setSchema(arrayModel);
                        }
                    } else {
                        String ref = modelTypeAsRef(dataType, swagger);
                        if (ref != null) {
                            bp.setSchema(new RefModel(ref));
                        }
                        else {
                        	 ArrayModel arrayModel = new ArrayModel();
                             //arrayModel.setItems(prop);
                             bp.setSchema(arrayModel);
                        	// bp.setSchema(new RefModel("#/definitions/String"));
                        	
                        	
                        }
                    }
                }
            }
            
		}
		
		return parameter;

	}

	
	  private static Property modelTypeAsProperty(String typeName, Swagger swagger) {
	        boolean array = typeName.endsWith("[]");
	        if (array) {
	            typeName = typeName.substring(0, typeName.length() - 2);
	        }

	        String ref = modelTypeAsRef(typeName, swagger);

	        Property prop;

	        if (ref != null) {
	            prop = new RefProperty(ref);
	        } else {
	            // special for byte arrays
	            if (array && ("byte".equals(typeName) || "java.lang.Byte".equals(typeName))) {
	                prop = new ByteArrayProperty();
	                array = false;
	            } else if ("java.lang.String".equals(typeName)) {
	                prop = new StringProperty();
	            } else if ("int".equals(typeName) || "java.lang.Integer".equals(typeName)) {
	                prop = new IntegerProperty();
	            } else if ("long".equals(typeName) || "java.lang.Long".equals(typeName)) {
	                prop = new LongProperty();
	            } else if ("float".equals(typeName) || "java.lang.Float".equals(typeName)) {
	                prop = new FloatProperty();
	            } else if ("double".equals(typeName) || "java.lang.Double".equals(typeName)) {
	                prop = new DoubleProperty();
	            } else if ("boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)) {
	                prop = new BooleanProperty();
	            } else {
	                prop = new StringProperty(typeName);
	            }
	        }

	        if (array) {
	            return new ArrayProperty(prop);
	        } else {
	            return prop;
	        }
	    }
	  
	  private static String modelTypeAsRef(String typeName, Swagger swagger) {
	        boolean array = typeName.endsWith("[]");
	        if (array) {
	            typeName = typeName.substring(0, typeName.length() - 2);
	        }

	        Model model = asModel(typeName, swagger);
	        if (model != null) {
	            typeName = ((ModelImpl) model).getName();
	            return typeName;
	        }

	        return null;
	    }
	  
	  private static Model asModel(String typeName, Swagger swagger) {
	        boolean array = typeName.endsWith("[]");
	        if (array) {
	            typeName = typeName.substring(0, typeName.length() - 2);
	        }

	        if (swagger.getDefinitions() != null) {
	            for (Model model : swagger.getDefinitions().values()) {
	                StringProperty modelType = (StringProperty) model.getVendorExtensions().get("x-className");
	                if (modelType != null && typeName.equals(modelType.getFormat())) {
	                    return model;
	                }
	            }
	        }
	        return null;
	    }
	  
	    public static void appendModels(Class clazz, Swagger swagger) {
	        RestModelConverters converters = new RestModelConverters();
	        final Map<String, Model> models = converters.readClass(clazz);
	        for (Map.Entry<String, Model> entry : models.entrySet()) {

	            // favor keeping any existing model that has the vendor extension in the model
	            boolean oldExt = false;
	            if (swagger.getDefinitions() != null && swagger.getDefinitions().get(entry.getKey()) != null) {
	                Model oldModel = swagger.getDefinitions().get(entry.getKey());
	                if (oldModel.getVendorExtensions() != null && !oldModel.getVendorExtensions().isEmpty()) {
	                    oldExt = oldModel.getVendorExtensions().get("x-className") != null;
	                }
	            }

	            if (!oldExt) {
	                swagger.model(entry.getKey(), entry.getValue());
	            }
	        }
	}
}
