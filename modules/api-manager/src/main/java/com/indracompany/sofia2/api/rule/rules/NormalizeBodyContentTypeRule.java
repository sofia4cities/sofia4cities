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

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rule.DefaultRuleBase;
import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Component
@Rule
public class NormalizeBodyContentTypeRule extends DefaultRuleBase {

	@Priority
	public int getPriority() {
		return 1;
	}

	@Condition
	public boolean existsRequest(Facts facts) {
		HttpServletRequest request = (HttpServletRequest) facts.get(RuleManager.REQUEST);
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);
		Object body = data.get(ApiServiceInterface.BODY);
		if ( body!=null)
			return true;
		else
			return false;
	}

	@Action
	public void setFirstDerivedData(Facts facts) throws IOException, ParseException {
		HttpServletRequest request = (HttpServletRequest) facts.get(RuleManager.REQUEST);
		Map<String, Object> data = (Map<String, Object>) facts.get(RuleManager.FACTS);

		String body = (String)data.get(ApiServiceInterface.BODY);
		String contentTypeInput = (String)data.get(ApiServiceInterface.CONTENT_TYPE_INPUT);
		
		if (!"".equals(body)) {
			
			if (contentTypeInput!=null && contentTypeInput.equals(MediaType.APPLICATION_ATOM_XML))
			{
				try {
					JSONObject xmlJSONObj = XML.toJSONObject(body);
					data.put(ApiServiceInterface.BODY, xmlJSONObj.toString());
				} catch (Exception e) {
					
					stopAllNextRules(facts, "BODY IS NOT JSON PARSEABLE "+body+ ": "+e.getMessage(),DefaultRuleBase.ReasonType.GENERAL);
				}
	
			}
			
		}

	}
	
	public boolean isValidJSON(String toTestStr) {
		JSONObject jsonObj = toJSONObject(toTestStr);
		JSONArray jsonArray = toJSONArray(toTestStr);
		
		if (jsonObj!=null || jsonArray!=null) return true;
		else return false;
	}
	
	private JSONObject toJSONObject(String input) {
		JSONObject jsonObj =null;
		try {
			jsonObj = new JSONObject(input);
		} catch (JSONException e) {
			return null;
		}
		return jsonObj;
	}
	
	private JSONArray toJSONArray(String input) {
		JSONArray jsonObj =null;
		try {
			jsonObj = new JSONArray(input);
		} catch (JSONException e) {
			return null;
		}
		return jsonObj;
	}
	
	public boolean isValidJSONtoMongo(String body) {
		try {
			DBObject dbObject = (DBObject) JSON.parse(body);
			
			
			
			if (dbObject!=null) return true;
			else return false;
		}
		catch (Exception e) {return false;}
	
	}
	
	public String depureJSON(String body) {
		
		DBObject dbObject =null;
		try {
			dbObject = (DBObject) JSON.parse(body);
			if (dbObject==null) return null;
			else {
				return dbObject.toString();
			}
		}
		catch (Exception e) {return null;}
	
	}
}