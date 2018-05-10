package com.indracompany.sofia2.controlpanel.controller.management.device;

import static com.indracompany.sofia2.controlpanel.controller.management.device.DeviceManagementUrl.OP_DEVICES;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.controlpanel.config.BaseRestServices;
import com.indracompany.sofia2.controlpanel.controller.management.model.ErrorServiceResponse;
import com.indracompany.sofia2.controlpanel.controller.management.model.ResponseGeneric;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(value="Device Management")
@RestController
@Slf4j
public class DeviceManagementController extends BaseRestServices {
	
	
	@ApiOperation(value = "Devices Management")
	@GetMapping(OP_DEVICES)
	
	public ResponseEntity<ResponseGeneric> devices() {

		ResponseGeneric response = new ResponseGeneric();
		try {
			log.info(OP_DEVICES + " Request: Sin Entrada");
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			log.info(authentication.toString());
			
		

		} catch (Exception e) {
			response = new ResponseGeneric();
			e.printStackTrace();
			ErrorServiceResponse errorResponseService = processError(e);
			response.setErrorResponse(errorResponseService);

			response.setErrorCode("500");
			response.setErrorDescription(errorResponseService.getDescription());
			log.error(OP_DEVICES + " Error: " + e.getMessage(),e);
			log.error(OP_DEVICES + " Error: " + e.getStackTrace());
			return new ResponseEntity<ResponseGeneric>(response, HttpStatus.OK);

		}
		return new ResponseEntity<ResponseGeneric> (response, HttpStatus.OK);

	}

}
