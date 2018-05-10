package com.indracompany.sofia2.controlpanel.controller.management.login.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class RequestLogin implements Serializable {

	private String username;
	private String password;
	
}