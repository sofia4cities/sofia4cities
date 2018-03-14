package com.indracompany.sofia2.controlpanel.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;

public class DefaultErrorController implements ErrorController{
	
	@Override
    public String getErrorPath() {
        return "/error";
    }
}
