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

import java.net.URI;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;

public class ResponseBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ResponseBuilder.class);

	private static final String BASE_PATH_PROPERTY = "controlpanel.services.baseUrl";
	private static String basePath;

	private static String getBasePath() {
		if (basePath == null) {
			synchronized (BASE_PATH_PROPERTY) {
				if (basePath != null) {
					try {
						basePath = ArqSpringContext.getPropiedad(BASE_PATH_PROPERTY);
					} catch (Exception e) {
						logger.error("Unable to retrieve property {}. Throwing RuntimeException.", BASE_PATH_PROPERTY);
						throw new RuntimeException(e);
					}
					try {
						URI.create(basePath);
					} catch (Exception e) {
						logger.error(
								"The value of the property {} is not a valid URL. Throwing RuntimeException. URL = {}, cause = {}, errorMessage = {}.",
								basePath, e.getCause(), e.getMessage());
						throw new RuntimeException("The base URL is not valid");
					}
					if (basePath.endsWith("/"))
						basePath = basePath.substring(0, basePath.length() - 1);
				}
			}

		}
		return basePath;
	}

	public static Response buildCreatedResponse(Object entity, String relativeUrl) {
		return Response.created(URI.create(getBasePath() + relativeUrl)).entity(entity).type(MediaType.APPLICATION_JSON)
				.build();
	}

	public static Response buildResponse(final Status status) {
		return Response.status(status).type(MediaType.APPLICATION_JSON).build();
	}

	public static Response buildResponse(Object entity) {
		return buildResponse(Status.OK, entity);
	}

	public static Response buildResponse(final Status status, Object entity) {
		return Response.status(status).entity(entity).type(MediaType.APPLICATION_JSON)
				.build();
	}
	
	public static Response buildResponse(final Status status, Object entity, final CacheControl cacheControl) {
		return Response.status(status).entity(entity).cacheControl(cacheControl).type(MediaType.APPLICATION_JSON)
				.build();
	}
	
	public static CacheControl buildCachingDisabledHeader() {
		CacheControl result = new CacheControl();
		result.setNoCache(true);
		return result;
	}
}