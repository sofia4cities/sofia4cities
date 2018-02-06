/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.api.rest.api.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


public class ApiSuscripcionDTO implements Cloneable, Serializable{

	@ApiModelProperty(value = "APISubscription apiIdentification")
	@Getter
	@Setter
	private String apiIdentification;
	
	@ApiModelProperty(value = "APISubscription userId")
	@Getter
	@Setter
	private String userId;
	
	@ApiModelProperty(value = "APISubscription initDate")
	@Getter
	@Setter
	private String initDate;
	
	@ApiModelProperty(value = "APISubscription endDate")
	@Getter
	@Setter
	private String endDate;
	
	@ApiModelProperty(value = "APISubscription active")
	@Getter
	@Setter
	private Boolean active;

	
	
}
