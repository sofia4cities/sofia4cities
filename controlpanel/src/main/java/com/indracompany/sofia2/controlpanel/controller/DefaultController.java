package com.indracompany.sofia2.controlpanel.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Controller
public class DefaultController {

    
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
