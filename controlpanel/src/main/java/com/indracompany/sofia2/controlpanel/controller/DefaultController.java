package com.indracompany.sofia2.controlpanel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String home1(Model model) {
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
