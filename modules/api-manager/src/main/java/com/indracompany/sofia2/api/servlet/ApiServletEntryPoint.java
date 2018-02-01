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
package com.indracompany.sofia2.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.persistence.PersistenceException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.indracompany.sofia2.api.rule.RuleManager;
import com.indracompany.sofia2.api.service.ApiServiceInterface;
import com.indracompany.sofia2.api.service.exception.ApiLimitException;
import com.indracompany.sofia2.api.service.exception.BadRequestException;
import com.indracompany.sofia2.api.service.exception.ForbiddenException;
import com.indracompany.sofia2.api.service.exception.TargetDatabaseNotValid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiServletEntryPoint extends HttpServlet {

	
	@Autowired
	ApiServiceInterface apiService;
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			if (request.getMethod().equalsIgnoreCase("GET")){
				log.info("com.indra.sofia2.api.servlet.handleRequest.getRequest", request.getRequestURL());
				apiService.doGet(request, response);	
			} else if(request.getMethod().equalsIgnoreCase("POST")){
			   	log.info("com.indra.sofia2.api.servlet.handleRequest.postRequest", request.getRequestURL());
				apiService.doPost(request, response);
			} else if(request.getMethod().equalsIgnoreCase("PUT")){
				log.info("com.indra.sofia2.api.servlet.handleRequest.putRequest", request.getRequestURL());
				apiService.doPut(request, response);
			} else if(request.getMethod().equalsIgnoreCase("DELETE")){
				log.info("com.indra.sofia2.api.servlet.handleRequest.deleteRequest", request.getRequestURL());
				apiService.doDelete(request, response);
			}
			else if(request.getMethod().equalsIgnoreCase("OPTIONS")){
				log.info("com.indra.sofia2.api.servlet.handleRequest.optionsRequest", request.getRequestURL());
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Access-Control-Allow-Headers", "X-SOFIA2-APIKey,auth-token,Content-Type");
				response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,PUT,OPTIONS");
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
			}
		} catch (ApiLimitException e){
			log.error(e.getMessage());
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		}catch(BadRequestException e) {
			log.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		} catch(ForbiddenException e) {
			log.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		} catch(PersistenceException e) {
			log.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		} catch(TargetDatabaseNotValid e) {
			log.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		} catch(IllegalArgumentException e) {
			log.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		}
		catch(RuntimeException e) {
			log.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		} catch(Exception e) {
			log.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if(e.getMessage()!=null){
				response.getWriter().write(e.getMessage());
			}
			return;
		}
	}

	
	public ApiServiceInterface getApiService() {
		return apiService;
	}

	public void setApiService(ApiServiceInterface apiService) {
		this.apiService = apiService;
	}

	public void init(ServletConfig config) {
		try {
			super.init(config);
			SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		} catch (ServletException e) {}
	}

}