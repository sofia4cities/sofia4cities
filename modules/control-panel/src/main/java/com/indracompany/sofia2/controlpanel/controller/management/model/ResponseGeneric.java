package com.indracompany.sofia2.controlpanel.controller.management.model;

import lombok.Data;

@Data
public class ResponseGeneric  {


	private String errorCode= "0";
	private String errorDescription="";
	
	private ErrorServiceResponse errorResponse= new ErrorServiceResponse();
	
	

	

	
}