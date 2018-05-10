package com.indracompany.sofia2.controlpanel.controller.management.login;

import static com.indracompany.sofia2.controlpanel.controller.management.login.LoginPostUrl.OP_LOGIN;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.controlpanel.config.BaseRestServices;
import com.indracompany.sofia2.controlpanel.controller.management.login.model.RequestLogin;
import com.indracompany.sofia2.controlpanel.controller.management.model.ErrorServiceResponse;
import com.indracompany.sofia2.controlpanel.controller.management.model.ResponseGeneric;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(value="Post Login")
@RestController
@Slf4j
public class LoginManagementController extends BaseRestServices {
	
	
	@ApiOperation(value = "Login Management")
	@GetMapping(OP_LOGIN)
	
	public ResponseEntity<ResponseGeneric> loadAccountsList(@Valid @RequestBody RequestLogin request) {

		ResponseGeneric response = new ResponseGeneric();
		try {
			log.info(OP_LOGIN + " Request: Sin Entrada");
			
			
		

		} catch (Exception e) {
			response = new ResponseGeneric();
			e.printStackTrace();
			ErrorServiceResponse errorResponseService = processError(e);
			response.setErrorResponse(errorResponseService);

			response.setErrorCode("500");
			response.setErrorDescription(errorResponseService.getDescription());
			log.error(OP_LOGIN + " Error: " + e.getMessage(),e);
			log.error(OP_LOGIN + " Error: " + e.getStackTrace());
			return new ResponseEntity<ResponseGeneric>(response, HttpStatus.OK);

		}
		return new ResponseEntity<ResponseGeneric> (response, HttpStatus.OK);

	}

}
