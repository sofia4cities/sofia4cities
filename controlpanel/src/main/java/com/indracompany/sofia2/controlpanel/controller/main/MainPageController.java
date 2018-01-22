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
package com.indracompany.sofia2.controlpanel.controller.main;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indracompany.sofia2.config.model.UserCDB;
import com.indracompany.sofia2.config.repository.ConsoleMenuRepository;
import com.indracompany.sofia2.config.repository.UserCDBRepository;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainPageController {

	private String genericUserName="USER";
	private UserCDB user;
	@Autowired
	private ConsoleMenuRepository consoleMenuRepository;
	@Autowired
	private UserCDBRepository userCDBRepository;
	@Autowired
	private GraphUtil graphUtil;
	@Autowired 
	private AppWebUtils utils;

	@Value("${sofia2.urls.iotbroker}")
	String url;

	@GetMapping("/main")
	private String home1(Model model, HttpServletRequest request) {

		user=userCDBRepository.findByUserId(utils.getUserId());
		utils.setSessionAttribute(request, "menu", this.loadMenu(user));
		return "/main";
	}
	
	@GetMapping("/getgraph")
	public @ResponseBody String getGraph(Model model)
	{
		List<GraphDTO> arrayLinks=new LinkedList<GraphDTO>();

		arrayLinks.add(GraphDTO.constructSingleNode(genericUserName,null,genericUserName,user.getUserId()));
		arrayLinks.addAll(graphUtil.constructGraphWithOntologies());
		arrayLinks.addAll(graphUtil.constructGraphWithClientPlatforms());
		arrayLinks.addAll(graphUtil.constructGraphWithVisualization());		
		System.out.println(arrayLinks.toString());
		return arrayLinks.toString();
	}
	
	private String loadMenu(UserCDB user)
	{
		String menu=this.consoleMenuRepository.findByRoleTypeId(user.getRoleTypeId()).getJsonSchema();
	
		return menu;	
		
	}

}
