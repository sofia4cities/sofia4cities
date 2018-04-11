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
package com.indracompany.sofia2.controlpanel.controller.audit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.config.services.utils.ServiceUtils;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;
import com.indracompany.sofia2.persistence.services.QueryToolService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/audit")
public class AuditController {

	@Autowired
	private QueryToolService queryToolService;

	@Autowired
	private AppWebUtils utils;

	@GetMapping("show")
	public String show(Model model) {
		List<OperationType> operations = new ArrayList<>();
		operations.add(OperationType.LOGIN);
		operations.add(OperationType.LOGOUT);
		operations.add(OperationType.INSERT);
		model.addAttribute("operations", operations);
		return "audit/show";
	}

	@PostMapping("executeQuery")
	public String query(Model model, @RequestParam String offset, @RequestParam String operation) {

		String result = null;

		try {

			String collection = ServiceUtils.getAuditCollectionName(utils.getUserId());

			String query = "select message, type, timeStamp, user, ontology, "
					+ "	operationType, module, extraData, otherType from " + collection;

			if (!operation.equalsIgnoreCase("all")) {
				query += " where operationType = \"" + operation + "\"";
			}

			query += " order by timestamp desc limit " + Integer.parseInt(offset);

			String queryResult = queryToolService.querySQLAsJson(utils.getUserId(), collection, query, 0);
			model.addAttribute("queryResult", queryResult);
			result = "audit/show :: query";

		} catch (Exception e) {
			model.addAttribute("queryResult",
					utils.getMessage("querytool.query.native.error", "Error malformed query"));
		}
		return result;
	}

}
