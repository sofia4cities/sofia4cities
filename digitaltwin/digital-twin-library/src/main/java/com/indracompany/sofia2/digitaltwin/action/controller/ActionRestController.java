/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.digitaltwin.action.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.digitaltwin.action.execute.ActionExecutor;
import com.indracompany.sofia2.digitaltwin.transaction.TransactionManager;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(value = "/actions")
public class ActionRestController {
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ActionExecutor actionExecutor;
	
	@RequestMapping(method = RequestMethod.POST)
	public  void setProperty(@RequestBody String action, HttpServletRequest request) {
		try {
			JSONObject actionJSON = new JSONObject(action);
			String idTransaction = request.getHeader("Transaction-Id");
			String actionName = actionJSON.getString("name");
			
			if(idTransaction!=null && idTransaction!="") {//Action Finish transaction
				//First executing set properties
				transactionManager.completeTransaction(idTransaction, actionName);
				
			}else {//Action without transactcion, executes it inmediately
				actionExecutor.executeAction(actionName);
			}
			
		} catch (JSONException e) {
			log.error("Invalid JSON action: " + action, e);
		}
	}

}
