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
package com.indracompany.sofia2.controlpanel.controller.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/ontologies")
@Controller
@Slf4j
public class OntologyController {

	@Autowired
	private OntologyRepository ontologyRepository;

	@Autowired
	private AppWebUtils utils;

	@RequestMapping(value = "/list", produces = "text/html")
	public String list(Model uiModel, HttpServletRequest request) {

		String identification = request.getParameter("identification");
		String description = request.getParameter("description");
		List<Ontology> ontologies;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userRole = utils.getRole();
		if (userRole.equals("ROLE_ADMINISTRATOR")) {
			if (description != null && identification != null) {
				ontologies = this.ontologyRepository.findByIdentificationLikeAndDescriptionLike(identification,
						description);
			} else {
				ontologies = this.ontologyRepository.findAll();
			}
		} else {
			if (description != null && identification != null) {
				ontologies = this.ontologyRepository.findByUserIdAndIdentificationLikeAndDescriptionLike(
						authentication.getName(), identification, description);
			} else {
				ontologies = this.ontologyRepository.findByUserId(authentication.getName());

			}
		}
		uiModel.addAttribute("ontologies", ontologies);
		return "/ontologies/list";
	}

}
