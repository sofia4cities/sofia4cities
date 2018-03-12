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
package com.indracompany.sofia2.api.rule.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rest.api.dto.ODataDTO;
import com.indracompany.sofia2.api.rule.DefaultRuleBase;
import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.api.service.api.ApiManagerService;
import com.indracompany.sofia2.api.service.api.ApiSecurityService;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.ApiQueryParameter;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;

@Component
@Rule
public class APIQueryOntologyRule extends DefaultRuleBase {
	
	@Autowired
	private ApiSecurityService apiSecurityService;
	@Autowired
	private ApiManagerService apiManagerService;

	@Priority
	public int getPriority() {
		return 4;
	}

	@Condition
	public boolean existsRequest(Facts facts) {
		HttpServletRequest request = (HttpServletRequest) facts.get(RuleManager.REQUEST);
		if ((request != null) && canExecuteRule(facts))
			return true;
		else
			return false;
	}

	@Action
	public void setFirstDerivedData(Facts facts) {
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);
		HttpServletRequest request = (HttpServletRequest) facts.get(RuleManager.REQUEST);

		User user = (User) data.get(ApiServiceInterface.USER);
		Api api = (Api) data.get(ApiServiceInterface.API);
		String pathInfo = (String) data.get(ApiServiceInterface.PATH_INFO);
		String method = (String) data.get(ApiServiceInterface.METHOD);
		String body = (String) data.get(ApiServiceInterface.BODY);
		String queryType = (String) data.get(ApiServiceInterface.QUERY_TYPE);
		
		
		Ontology ontology = api.getOntology();
		if (ontology!=null) {
			data.put(ApiServiceInterface.IS_EXTERNAL_API, false);
			
			String queryDb = "";
			String targetDb = "";
			String formatResult="";
			ODataDTO odata=null;
			
			ApiOperation customSQL = apiManagerService.getCustomSQL(pathInfo, api,method);
			
			
			String objectId=apiManagerService.getObjectidFromPathQuery(pathInfo);
			if (!objectId.equals("") && (queryType.equals("") || queryType.equals("NONE") )) {
				queryDb = "db."+ontology.getIdentification()+".find({\"_id\":ObjectId('"+objectId+"')})";
				data.put(ApiServiceInterface.QUERY,queryDb);
			}
				
						
			HashSet<ApiQueryParameter> queryParametersCustomQuery = new HashSet<ApiQueryParameter>();
			HashMap<String, String> queryParametersValues = new HashMap<String, String>();
			if (customSQL!=null) {
				
				data.put(ApiServiceInterface.API_OPERATION, customSQL);
				
				for (ApiQueryParameter queryparameter : customSQL.getApiqueryparameters()) {
					String name = queryparameter.getName();
					String value = queryparameter.getValue();
					
					if (matchParameter(name,ApiServiceInterface.QUERY)) queryDb=value;
					else if (matchParameter(name,ApiServiceInterface.QUERY_TYPE)) queryType=value;
					else if (matchParameter(name,ApiServiceInterface.TARGET_DB_PARAM)) targetDb=value;
					//else if (matchParameter(name,ApiServiceInterface.FORMAT_RESULT)) formatResult=value;
					else queryParametersCustomQuery.add(queryparameter);
					
				}
				
				queryParametersValues = apiManagerService.getCustomParametersValues(request, body,queryParametersCustomQuery);
				
				if (body.equals("")) {
					queryDb = apiManagerService.buildQuery (queryDb, queryParametersValues);	
				}
				else queryDb = body;
			}
			
			data.put(ApiServiceInterface.QUERY_TYPE, queryType);
			data.put(ApiServiceInterface.QUERY, queryDb);
			data.put(ApiServiceInterface.TARGET_DB_PARAM, targetDb);
			//data.put(ApiServiceInterface.FORMAT_RESULT, formatResult);
			
			data.put(ApiServiceInterface.OBJECT_ID, objectId);
			data.put(ApiServiceInterface.ONTOLOGY, ontology);
			
			
			//Guess type of operation!!!
			
		}
		else {
			data.put(ApiServiceInterface.IS_EXTERNAL_API, true);
			
		}
	}

	private static boolean matchParameter(String name, String match) {
		String variable = match.replace("$", "");
		
		if (name.equalsIgnoreCase(match) || name.equalsIgnoreCase(variable)) return true;
		else return false;
	}
	
	private ODataDTO parseOperationByQueryString(String objectId, String pathInfo, Api api, HttpServletRequest request) {
		
		int index = pathInfo.lastIndexOf(api.getIdentification());
		String queryPath = api.getEndpoint()+"/"+pathInfo.substring(index+api.getIdentification().length()+1);
		
		ODataDTO odata = null;
		try{
			odata= new ODataDTO(api.getIdentification(),objectId,queryPath, request.getParameterMap());
		}catch(Exception e){
			System.out.println(e);
		}
		return odata;
	}
}