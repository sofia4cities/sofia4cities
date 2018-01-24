package com.indracompany.sofia2.api.rule.rules;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;

@Component
@Rule
public class InitialFactsRule {

   
    @Priority
    public int getPriority() {
    	return 1;
    }
    
    @Condition
    public boolean existsRequest(Facts facts) {
    	HttpServletRequest request = (HttpServletRequest)facts.get(RuleManager.REQUEST);
    	if (request!=null) return true;
    	else return false;
    }
    
    @Action
    public void setFirstDerivedData(Facts facts) {
    	HttpServletRequest request = (HttpServletRequest)facts.get(RuleManager.REQUEST);
    	Map<String,Object> data = (Map<String,Object>)facts.get(RuleManager.FACTS);
    	
    	String query=request.getParameter(ApiServiceInterface.QUERY);
		String queryType=request.getParameter(ApiServiceInterface.QUERY_TYPE);
		
		String headerToken=request.getHeader(ApiServiceInterface.AUTHENTICATION_HEADER);
		if (headerToken==null){
			headerToken=request.getParameter(ApiServiceInterface.AUTHENTICATION_HEADER);
		}
		
		String pathInfo=request.getPathInfo();
		
		String queryDb=request.getParameter(ApiServiceInterface.FILTER_PARAM).trim();
		String targetDb=request.getParameter(ApiServiceInterface.TARGET_DB_PARAM).trim();
		String formatResult = request.getParameter(ApiServiceInterface.FORMAT_RESULT);
		
		data.put(ApiServiceInterface.QUERY, query);
		data.put(ApiServiceInterface.QUERY_TYPE, queryType);
		data.put(ApiServiceInterface.AUTHENTICATION_HEADER, headerToken);
		data.put(ApiServiceInterface.PATH_INFO, pathInfo);
		data.put(ApiServiceInterface.FILTER_PARAM, queryDb);
		data.put(ApiServiceInterface.TARGET_DB_PARAM, targetDb);
		data.put(ApiServiceInterface.FORMAT_RESULT, formatResult);
    	
    	
    }
}