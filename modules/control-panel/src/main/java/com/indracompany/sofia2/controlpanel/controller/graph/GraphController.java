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
package com.indracompany.sofia2.controlpanel.controller.graph;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@Controller
public class GraphController {

	private String genericUserName = "USER";
	@Autowired
	private GraphUtil graphUtil;

	@Autowired
	private AppWebUtils utils;

	@GetMapping("/getgraph")
	public @ResponseBody String getGraph(Model model) {
		List<GraphDTO> arrayLinks = new LinkedList<GraphDTO>();

		arrayLinks.add(GraphDTO.constructSingleNode(genericUserName, null, genericUserName, utils.getUserId(), null,
				null, null));
		arrayLinks.addAll(graphUtil.constructGraphWithOntologies());
		arrayLinks.addAll(graphUtil.constructGraphWithClientPlatforms());
		arrayLinks.addAll(graphUtil.constructGraphWithVisualization());
		return arrayLinks.toString();
	}

}
