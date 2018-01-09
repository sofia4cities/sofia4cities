package com.indracompany.sofia2.controlpanel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String home1() {
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
