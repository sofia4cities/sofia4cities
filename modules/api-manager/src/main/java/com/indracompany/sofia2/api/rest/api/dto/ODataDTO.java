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
package com.indracompany.sofia2.api.rest.api.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ODataDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String queryMongo;
	private String codigo;
	private String odataContext;
	private String odataNextLink;
	private String odataId;
	private String odataEtag;
	private String odataEditLink;	
	private Boolean projection;
	
	public ODataDTO(String apiName,String ontologyName, String pathInfo, Map<?, ?> queryParams) throws Exception{
		String ontologyId = null;
		String filterQueryParams = null;
		String projectQueryParams = null;
		String sortQueryParams = null;
		Boolean noContextData= false;

		//Construimos la query mongo
		if (pathInfo.endsWith("\\") || pathInfo.endsWith("/")){
			pathInfo=pathInfo.substring(0,pathInfo.length()-1);
		}
		pathInfo = pathInfo.trim();
		int indexFrom = pathInfo.indexOf(ontologyName,pathInfo.indexOf(apiName) +apiName.length()+1);
		int indexTo = pathInfo.length();
		if (indexFrom==-1){
			throw new Exception("Error en PathInfo");
		}
		indexTo = pathInfo.indexOf("/", indexFrom);
		if (indexTo==-1){
			indexTo = pathInfo.indexOf("?", indexFrom);
			if (indexTo==-1){
				indexTo=pathInfo.length();	
			}
		}			
		ontologyId = pathInfo.substring(indexFrom, indexTo).trim(); 
		
		if (pathInfo.contains("$count") || pathInfo.contains("$value")){
			noContextData= true;
		}
		if (queryParams.containsKey("$filter")){
			filterQueryParams = ((String[])queryParams.get("$filter"))[0];
		}
		if (queryParams.containsKey("$select")){
			projectQueryParams = ((String[])queryParams.get("$select"))[0];
			this.projection=true;
		}else{
			this.projection=false;
		}
		if (queryParams.containsKey("$orderby")){
			sortQueryParams = ((String[])queryParams.get("$orderby"))[0];
		}
		this.queryMongo=mongoQueryParser(ontologyName,ontologyId,indexFrom,indexTo,pathInfo,filterQueryParams,projectQueryParams,sortQueryParams);		
			
		//Informamos codigo
		String codigoAleat=RandomStringUtils.randomAlphanumeric(24); 
		this.codigo = "(S("+codigoAleat.toLowerCase()+"))";
		
		//Informamos odataContext
		if (noContextData){
			this.odataContext=null;			
		}else{
			this.odataContext=pathInfo.substring(0, indexFrom)+"$metadata#"+ontologyId;
			if (pathInfo.length()>indexTo+1 && pathInfo.substring(indexTo+1).trim().length()>0){
				this.odataContext+=	pathInfo.substring(indexTo).replaceAll("\\$.*", "");
			}else if (!ontologyId.equalsIgnoreCase(ontologyName) && filterQueryParams==null && projectQueryParams==null && sortQueryParams==null){
				this.odataContext+="/$entity";
			}else if(!ontologyId.equalsIgnoreCase(ontologyName) && queryParams.containsKey("$select")){
				this.odataContext=(this.odataContext).replace(ontologyId, ontologyName);
				this.odataContext+="("+projectQueryParams+")/$entity";
			}else if (ontologyId.equalsIgnoreCase(ontologyName) && queryParams.containsKey("$select")){
				this.odataContext+="("+projectQueryParams+")";
			}
		}		
		
		//Informamos odataId
		if(noContextData || indexTo<pathInfo.length()){
			this.odataId=null;
		}else{
			this.odataId = pathInfo.substring(0, indexFrom)+ontologyName; //+"(id de la instancia)"
		}
		
		//Informamos odataEtag
		if (this.odataId != null){
			String aleat=RandomStringUtils.randomAlphanumeric(16);
			this.odataEtag= "W/'"+aleat.toUpperCase()+"'";
		}else{
			this.odataEtag=null;
		}	
		
		//Informamos odataEditLink
		this.odataEditLink= this.odataId;		
	}

	private String mongoQueryParser(String ontologyName, String ontologyId, int indexFrom, int indexTo,
			String pathInfo, String filterQueryParams,
			String projectQueryParams, String sortQueryParams) throws Exception {		
		
		String result="";
		boolean tipoFind=true;
		String oid=null;
		
		//--tipo de query		
		if (pathInfo.contains("$count")){
			tipoFind = false;
			result = "db."+ontologyName+".count({";
		}else {
			result = "db."+ontologyName+".find({";
		}
		
		//--filtrado de la query		
		if (ontologyId.contains("(") && ontologyId.contains(")") && ontologyId.contains("'")){
			oid = ontologyId.substring(ontologyId.indexOf("'")+1, ontologyId.lastIndexOf("'"));
			if (oid.length()>0){
				result +="\"_id\":ObjectId(\""+oid+"\")";
			}else{
				oid=null;
			}			
		}
		if (oid !=null && filterQueryParams !=null){ //No se puede filtrar una instancia
			throw new Exception ("Error en el $filter");
		}
		if (filterQueryParams !=null){
			String filterQuery = getFilterExpression(filterQueryParams);
			if (filterQuery!=null && filterQuery.length()>0){
				result +=filterQuery;
			}
		}
		result +="}";
		
		//--Proyeccion de la query
		//Validamos esta parte de la query
		if (pathInfo.contains("$value") && oid==null ){
			throw new Exception("Error en el $value");
		}			
		if (tipoFind && indexTo<pathInfo.length()){ //Se pregunta por un campo en concreto en pathinfo
			if (projectQueryParams !=null){ //No son combinables un preguntar por un campo concreto en pathInfo + $select
				throw new Exception("Error en el $select");
			}
			if (oid ==null){ //Solo se puede seleccionar un campo por pathinfo para oid informado
				throw new Exception("Error en pathInfo");
			}
			String projectField=pathInfo.substring(indexTo+1).trim();
			result +=",{\"_id\":0,";						
			projectField =projectField.replaceAll("\\$.*", "").replace("/", ".").replace(" ", "");//Construimos el jsonPath del campo
			if (projectField.endsWith(".")){
				projectField = projectField.substring(0, projectField.length()-1);
			}
			result +="\""+projectField+"\":1}";
		}else if (tipoFind && projectQueryParams !=null){
			projectQueryParams = projectQueryParams.replace(" ", "");
			String[] projectFields = projectQueryParams.split(",");
			if(projectFields!=null && projectFields.length>0){
				result +=",{";
				for (int i=0;i<projectFields.length;i++){
					result += "\""+projectFields[i]+"\":1,";
				}
				result=result.substring(0, result.length()-1)+"}";					
			}
		}
		
		//--Sort de la query
		if (tipoFind && sortQueryParams !=null){
			String sortQuery = getSortExpression(sortQueryParams);
			if (sortQuery!=null && sortQuery.length()>0){
				result +=").sort({"+sortQuery+"}";
			}			
		}	
		
		//--Cerrar la query
		result +=")";
		return result;
	}
	
	private String getFilterExpression(String filterQuery) throws Exception {
		String result = null;
		filterQuery = filterQuery.trim();
		if (filterQuery.length()==0){
			throw new Exception("Error en el $filter");
		}
		String[] sentences = filterQuery.split("( or | and | OR | AND )");
		for (int i = 0; i < sentences.length; i++) {
			String expression = "( eq | EQ | ne | NE | ge | GE | gt | GT | lt | LT | le | LE )";
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(sentences[i]);			
			if (matcher.find()){
				String key = (String) (sentences[i]).subSequence(0, matcher.start());
				String operator =  matcher.group(1);
				String value = (String) (sentences[i]).subSequence(matcher.end(),sentences[i].length());
			    String jexp = getJsonExpression(key,operator,value);
			    if(jexp==null || jexp.length()==0){
			    	throw new Exception("Error en el $filter");
			    }
			    if (i>0){
			    	String condition = ((String) filterQuery.subSequence(filterQuery.indexOf(sentences[i-1])+sentences[i-1].length(), filterQuery.indexOf(sentences[i]))).trim();
			    	if (result !=null && condition.equalsIgnoreCase("and")){
						result = "$and:[{"+result+"},{"+jexp+"}]";				
					}else if (result !=null && condition.equalsIgnoreCase("or")){
						result = "$or:[{"+result+"},{"+jexp+"}]";
					}else{
						result = jexp;
					}		    
			    }else{
			    	result = jexp;
			    }			    
			}else{
				throw new Exception("Error en el $filter");
			}			
		}
		return result;		
	}


	private String getSortExpression(String sortQuery) throws Exception {
		String result="";
		sortQuery = sortQuery.trim();
		if(sortQuery.length()==0){
			throw new Exception("Error en el $orderby");
		}
		String expression = "((([^,\\s]*)(\\s*|),(\\s*|)|)*)([^,\\s]*)\\s+(asc|desc|ASC|DESC)";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(sortQuery);
		int operatorValue=1;

		while (matcher.find()) {   
			String fieldsChain = matcher.group(1).replace(" ", ""); //Cuando hay mas de un campo separado por ","
			String field = matcher.group(6); 
		    String operator = matcher.group(7);
		    if (operator.equalsIgnoreCase("asc")){
		    	operatorValue = 1;
		    }else if (operator.equalsIgnoreCase("desc")){
		    	operatorValue = -1;
		    }else{
		    	throw new Exception("Error en el $orderby");
		    }
		    
		    if (fieldsChain.length()>0){
		    	String[] fields = fieldsChain.split(",");
		    	for (int i=0;i<fields.length;i++){
		    		if (fields[i].length()>0){
		    			result +="\""+fields[i]+"\":"+operatorValue+",";
		    		}		    		
		    	}		    	
		    }
		    result +="\""+field+"\":"+operatorValue+",";
		}
		if (result.length()>0){
			result = result.substring(0,result.length()-1);
		}else{
			throw new Exception("Error en el $orderby");
		}

		return result;
	}



	private String getJsonExpression(String key, String operator, String value) throws Exception {	
		//Validamos los datos
		key = key.trim();
		operator = operator.trim();
		value = value.trim();
		if(key.length()==0 || operator.length()==0 || value.length()==0){
			throw new Exception("Error en el operador del filter");
		}		
		
		if(operator.equalsIgnoreCase("eq")){
			return "\""+key+"\""+":"+value;
		}else if(operator.equalsIgnoreCase("ne")){
			return "\""+key+"\""+":{ $ne: "+value+" }";
		}else if(operator.equalsIgnoreCase("gt")){
			return "\""+key+"\""+":{ $gt: "+value+" }";
		}else if(operator.equalsIgnoreCase("ge")){
			return "\""+key+"\""+":{ $gte: "+value+" }";
		}else if(operator.equalsIgnoreCase("lt")){
			return "\""+key+"\""+":{ $lt: "+value+" }";
		}else if(operator.equalsIgnoreCase("le")){
			return "\""+key+"\""+":{ $lte: "+value+" }";
		}else{
			throw new Exception("Error en el operador del filter");
		}			
	}
	public String processData(String msgResponse) throws Exception {
		String result = null;
		JSONObject json;
		if (msgResponse!=null){
			try {
				json = (JSONObject) new JSONParser().parse(msgResponse);
			} catch (ParseException e) {
				throw new Exception("Error al recuperar data");
			}	

			String instanciaError = (String) json.get("error");
			
			if (instanciaError == null) {
				try{
					JSONArray data = (JSONArray)new JSONParser().parse(json.get("data").toString());
					if(data!=null && data.size()>0){//Caso favorable
						//Informamos contextData: 
						if (odataContext != null){
							String contextData="\"@odata.context\":\""+this.odataContext+"\",";
							String instances = "";
							int numInstances=0;
							for (int i=0;i<data.size();i++){
								JSONObject instanciaOnt=(JSONObject) data.get(i);
								String odataIdComplete="";
								String oid = "";
								if(this.odataId !=null){
									oid = ((JSONObject)(instanciaOnt.get("_id"))).get("$oid").toString();
									odataIdComplete = "\"@odata.id\":\""+this.odataId+"('"+oid+"')\",";								
								}
								if (this.odataEtag!=null){
									odataIdComplete += "\"@odata.etag\":\""+this.odataEtag+"\",";		
								}
								if (this.odataEditLink!=null && this.odataId !=null){
									odataIdComplete += "\"@odata.editLink\":\""+this.odataEditLink+"\",";
								}	
								if (this.projection){//Hay un select, se debe eliminar el campo oid
									instanciaOnt.remove("_id");
								}
								if (odataIdComplete.length()>0 && instanciaOnt.size()>0){
									numInstances++;
									instances += "{"+odataIdComplete+instanciaOnt.toString().substring(1,instanciaOnt.toString().length()-1)+"},";
								}

							}
							if (instances.length()>0){
								if (numInstances==1){
									result = "{"+contextData+instances.substring(1, instances.length()-2)+"}";
								}else{
									result = "{"+contextData+"\"value\":["+instances.substring(0, instances.length()-1)+"]}";
								}
								
							}else{
								if (((JSONObject) data.get(0)).values().iterator().next() instanceof String){
									result = "{"+contextData+"\"value\":"+((JSONObject) data.get(0)).values().iterator().next().toString()+"}";	
								}else{
									result = "{"+contextData+"\"value\":"+((JSONObject) data.get(0)).values().iterator().next()+"}";	
								}
															
							}							
	
						}else{//Es un unico valor
							result =((JSONObject) data.get(0)).values().iterator().next().toString();						
						}
						
					}
				}catch(Exception e){
					if (this.queryMongo.contains("count")){
						result = json.get("data").toString();
					}
				}
			}
		}
		return result;
	}
	
	

	public String getQueryMongo() {
		return queryMongo;
	}

	public String getOdataContext() {
		return odataContext;
	}

	public String getOdataId() {
		return odataId;
	}

	public String getOdataEtag() {
		return odataEtag;
	}

	public String getOdataEditLink() {
		return odataEditLink;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getOdataNextLink() {
		return odataNextLink;
	}

	
}
