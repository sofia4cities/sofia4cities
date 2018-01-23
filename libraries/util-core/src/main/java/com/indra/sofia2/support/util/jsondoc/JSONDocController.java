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
package com.indra.sofia2.support.util.jsondoc;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.util.JSONDocUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;

@Controller("documentationController")
@RequestMapping(value = "/jsondoc")
public class JSONDocController {
	
	private static final String BASE_PACKAGE = "com.indra.sofia2";
	private static final Logger logger = LoggerFactory.getLogger(JSONDocController.class);
	
	@Autowired
	private ServletContext servletContext;
	
	private String version;
	
	@Value("${docrest.baseUrl:/api}")
	private String baseUrl;
	
	@Value("${docrest.https.hostnames:#{null}}")
	private List<String> httpsHostnames;
	
	@PostConstruct
	public void init() throws UnknownHostException {
		version = ArqSpringContext.getVersionModulo();
		if (version == null || version.contains("${"))
			version = "1.0";
		if (httpsHostnames == null)
			httpsHostnames = new ArrayList<String>(0);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JSONDoc getApi(HttpServletRequest request) {
		String apiUrl = request.getRequestURL().toString().replace("/jsondoc", "");
		if (containsHttpsHostname(apiUrl)) {
			apiUrl = apiUrl.replace("http://", "https://");
		}
		logger.info("Configuring JSONDoc instance. BaseUrl = {}{}.", apiUrl, baseUrl);
		return JSONDocUtils.getApiDoc(servletContext, version, apiUrl + baseUrl, BASE_PACKAGE);
	}
	
	private boolean containsHttpsHostname(String requestUrl) {
		for (String hostname : httpsHostnames) {
			if (requestUrl.contains(hostname))
				return true;
		}
		return false;
	}
	
	
}
