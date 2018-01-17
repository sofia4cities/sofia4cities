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
package com.indracompany.sofia2.controlpanel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String home1(Model model) {
		return null;
		/*
    	JSONObject navItem = new JSONObject();
    	JSONArray nav= new JSONArray();
    	navItem.put("title", "Ontologies");
    	navItem.put("icon", "");
    	navItem.put("url", "#");
    	navItem.put("rol", "ROLE_OPERATIONS");
    	JSONObject navItem11 = new JSONObject();
    	navItem11.put("title", "Create Ontology");
    	navItem11.put("icon", "");
    	navItem11.put("url", "#");
    	navItem11.put("rol", "ROLE_OPERATIONS");
    	navItem.put("nav-item", navItem11);
    	nav.add(navItem);
    	JSONObject navItem2 = new JSONObject();
    	navItem2.put("title", "Visualizations");
    	navItem2.put("icon", "");
    	navItem2.put("url", "#");
    	navItem2.put("rol", "ROLE_OPERATIONS");
    	nav.add(navItem2);
    	model.addAttribute("menu", nav);
    	System.out.println(nav);
    	return "/main";
		*/
    }

    @GetMapping("/home")
    public String home() {
        return "/home";
    }

    @GetMapping("/main")
    public String user() {
        return "/main";
    }

    @GetMapping("/admin")
    public String admin() {
        return "/admin";
    }
    
    @GetMapping("/about")
    public String about() {
        return "/about";
    }

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

}
