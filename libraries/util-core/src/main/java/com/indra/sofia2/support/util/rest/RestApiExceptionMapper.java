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
package com.indra.sofia2.support.util.rest;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.util.apis.exceptions.RestApiException;

@Component("v2RestApiExceptionMapper")
public class RestApiExceptionMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {
		/**
		 * Convention: 
		 * - every exception thrown from a service must have a meaningful error message.
		 * - use the Spring security exceptions for all the authentication issues.
		 * - use IllegalArgumentExceptions to signal bad requests.
		 * - use ElementNotFoundExceptions to signal bad requests that have missing elements.
		 * - use the remaining subclasses of RestApiException other HTTP status codes (i.e. 404, 429, ...).
		 * - 
		 * 
		 */
		Status statusCode = Status.INTERNAL_SERVER_ERROR;
		String message = exception.getMessage();
		if (exception instanceof AuthorizationServiceException) {
			statusCode = Status.UNAUTHORIZED;
		} else if (exception instanceof AccessDeniedException) {
			statusCode = Status.FORBIDDEN;
		} else if (exception instanceof IllegalArgumentException || exception instanceof IOException) {
			statusCode = Status.BAD_REQUEST;
		} else if (exception instanceof RestApiException) {
			statusCode = ((RestApiException) exception).getStatusCode();
		} else if (exception instanceof WebApplicationException && exception.getCause() != null
				&& exception.getCause() instanceof IllegalArgumentException) {
			statusCode = Status.BAD_REQUEST;
			message = exception.getCause().getMessage();
		}
		return ResponseBuilder.buildResponse(statusCode, message);
	}
}
