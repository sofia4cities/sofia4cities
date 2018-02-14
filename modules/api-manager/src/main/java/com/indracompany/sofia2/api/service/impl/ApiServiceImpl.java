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
package com.indracompany.sofia2.api.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.api.service.api.ApiManagerService;
import com.indracompany.sofia2.api.service.exception.ForbiddenException;
import com.indracompany.sofia2.api.service.exporter.ExportToCsv;
import com.indracompany.sofia2.api.service.exporter.ExportToExcel;
import com.indracompany.sofia2.api.service.exporter.ExportToXml;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.User;

import io.prometheus.client.spring.web.PrometheusTimeMethod;

@Service
public class ApiServiceImpl extends ApiManagerService implements ApiServiceInterface, Processor {
	
	@Autowired
	RuleManager ruleManager;
	
	@Autowired
	private ExportToExcel varExcel;
	
	@Autowired
	private ExportToXml varXml;
	
	@Autowired
	private ExportToCsv varCsv;
	
	@Override
	@PrometheusTimeMethod(name = "ApiServiceImplEntryPointCamel", help = "ApiServiceImpl doGET")
	@Timed
	public void process(Exchange exchange) throws Exception {
		
		HttpServletRequest request = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);
		HttpServletResponse response = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_RESPONSE, HttpServletResponse.class);

		Facts facts = new Facts();
		facts.put(RuleManager.REQUEST, request);
		
		Map<String,Object> dataFact=new HashMap<String,Object>();
		dataFact.put(ApiServiceInterface.BODY, exchange.getIn().getBody());
		
		facts.put(RuleManager.FACTS, dataFact);
		ruleManager.fire(facts);

		Map<String,Object> data = (Map<String,Object>)facts.get(RuleManager.FACTS);
		Boolean stopped = (Boolean)facts.get(RuleManager.STOP_STATE);
		String REASON="";
		Object REASON_TYPE;
		if (stopped!=null && stopped==true) {
			REASON=((String)facts.get(RuleManager.REASON));
			REASON_TYPE=((Object)facts.get(RuleManager.REASON_TYPE));
			

			exchange.getIn().setBody(REASON+" \n"+REASON_TYPE);
			exchange.getIn().setHeader("content-type", "text/plain");
		}
		else {
			System.out.println(hashPP(data));
			
			User user = (User) data.get(ApiServiceInterface.USER);
			Api api = (Api) data.get(ApiServiceInterface.API);
			String PATH_INFO = (String) data.get(ApiServiceInterface.PATH_INFO);
			String METHOD = (String) data.get(ApiServiceInterface.METHOD);
			String BODY = (String) data.get(ApiServiceInterface.BODY);
			String QUERY_TYPE = (String) data.get(ApiServiceInterface.QUERY_TYPE);
			String QUERY = (String) data.get(ApiServiceInterface.QUERY);
			String TARGET_DB_PARAM = (String) data.get(ApiServiceInterface.TARGET_DB_PARAM);
			String FORMAT_RESULT = (String) data.get(ApiServiceInterface.FORMAT_RESULT);
			String OBJECT_ID = (String) data.get(ApiServiceInterface.OBJECT_ID);
			
			String str = hashPP(data);
			
			exchange.getIn().setBody(str+" \n"+REASON);
			exchange.getIn().setHeader("content-type", "text/plain");
		}
		
	}

	@Override
	@PrometheusTimeMethod(name = "ApiServiceImplEntryPoint", help = "ApiServiceImpl doGET")
	@Timed
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Facts facts = new Facts();
		facts.put(RuleManager.REQUEST, request);
		facts.put(RuleManager.ACTION, "GET");
		Map<String,Object> dataFact=new HashMap<String,Object>();
		facts.put(RuleManager.FACTS, dataFact);
		ruleManager.fire(facts);

		Map<String,Object> data = (Map<String,Object>)facts.get(RuleManager.FACTS);
		Boolean stopped = (Boolean)facts.get(RuleManager.STOP_STATE);
		String REASON="";
		String  REASON_TYPE="";
		if (stopped!=null && stopped==true) {
			REASON=((String)facts.get(RuleManager.REASON));
			REASON_TYPE=((String)facts.get(RuleManager.REASON_TYPE));
		}
		System.out.println(hashPP(data));
		
		User user = (User) data.get(ApiServiceInterface.USER);
		Api api = (Api) data.get(ApiServiceInterface.API);
		String PATH_INFO = (String) data.get(ApiServiceInterface.PATH_INFO);
		String METHOD = (String) data.get(ApiServiceInterface.METHOD);
		String BODY = (String) data.get(ApiServiceInterface.BODY);
		String QUERY_TYPE = (String) data.get(ApiServiceInterface.QUERY_TYPE);
		String QUERY = (String) data.get(ApiServiceInterface.QUERY);
		String TARGET_DB_PARAM = (String) data.get(ApiServiceInterface.TARGET_DB_PARAM);
		String FORMAT_RESULT = (String) data.get(ApiServiceInterface.FORMAT_RESULT);
		String OBJECT_ID = (String) data.get(ApiServiceInterface.OBJECT_ID);
		
		
		
		sendResponse(response, HttpServletResponse.SC_OK, hashPP(data)+"\n"+REASON,null,null);

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request,response);

	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request,response);

	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request,response);

	}
	
	private static String hashPP(final Map<String,Object> m, String... offset) {
	    String retval = "";
	    String delta = offset.length == 0 ? "" : offset[0];
	    for( Map.Entry<String, Object> e : m.entrySet() ) {
	        retval += delta + "["+e.getKey() + "] -> ";
	        Object value = e.getValue();
	        if( value instanceof Map ) {
	            retval += "(Hash)\n" + hashPP((Map<String,Object>)value, delta + "  ");
	        } else if( value instanceof List ) {
	            retval += "{";
	            for( Object element : (List)value ) {
	                retval += element+", ";
	            }
	            retval += "}\n";
	        } else {
	            retval += "["+value.toString()+"]\n";
	        }
	    }
	    return retval+"\n";
	}
	
private void sendResponse(HttpServletResponse response, int status, String message, String formatResult,String query) throws IOException{
		
		String infoJSON=null;
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setStatus(status);
		response.setCharacterEncoding("UTF-8");
		
		ByteArrayOutputStream byteFichero=null;
		Locale locale = LocaleContextHolder.getLocale();
		
		if (formatResult!=null ){
		
			if (formatResult.toUpperCase().equals("JSON") || formatResult.toUpperCase().equals("JSON_DEPRECATED")) {
				response.setContentType("application/json");

			} else {	
				try {
					
					Map<String, Object> obj = new ObjectMapper().readValue(message,	new TypeReference<Map<String, Object>>() {});
					
					infoJSON=obj.get("data").toString();
				} catch (JsonParseException e) {
					e.printStackTrace();
					throw new ForbiddenException("com.indra.sofia2.api.service.noJSON");
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				if (formatResult.toUpperCase().equals("EXCEL")){
					byteFichero=varExcel.extractJSONtoFile(infoJSON);
					//response.setContentType("application/ms-excel");
					//response.setContentLength(byteFichero.toByteArray().length);
					//response.getOutputStream().write(byteFichero.toByteArray());
					throw new ForbiddenException("com.indra.sofia2.api.service.excelnotsupported");
					
				}else if (formatResult.toUpperCase().equals("XML")){
					
					
					byteFichero=varXml.extractJSONtoXML(null, infoJSON);
					response.setContentType("application/atom+xml");
					try {
						message=byteFichero.toString("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}else if (formatResult.toUpperCase().equals("CSV")){
					byteFichero=varCsv.extractJSONtoCSV(infoJSON);
					response.setContentType("text/plain");
					try {
						message=byteFichero.toString("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				else 
					throw new ForbiddenException("com.indra.sofia2.api.service.formatnotvalid");
			}
			
		}
		try {
			response.setContentType("text/plain");
			//message=message.replace("\n", " ");
			response.getWriter().write(message);
		} catch(IOException e) {
			throw new IOException(e);
		}
		return;
	}

public ExportToExcel getVarExcel() {
	return varExcel;
}

public void setVarExcel(ExportToExcel varExcel) {
	this.varExcel = varExcel;
}

public ExportToXml getVarXml() {
	return varXml;
}

public void setVarXml(ExportToXml varXml) {
	this.varXml = varXml;
}

public ExportToCsv getVarCsv() {
	return varCsv;
}

public void setVarCsv(ExportToCsv varCsv) {
	this.varCsv = varCsv;
}



	

}
