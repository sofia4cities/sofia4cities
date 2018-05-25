/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.controlpanel.config;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final String INFO_VERSION = "";
	private static final String INFO_TITLE = "onesait Platform";
	private static final String INFO_DESCRIPTION = "onesait Platform Control Panel Management";

	private static final String LICENSE_NAME = "Apache2 License";
	private static final String LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0.html";

	private static final String CONTACT_NAME = "onesait Platform Team";
	private static final String CONTACT_URL = "https://www.sofia4cities.com";
	private static final String CONTACT_EMAIL = "select4citiesminsait@gmail.com";

	@Bean
	public ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(INFO_TITLE).description(INFO_DESCRIPTION).termsOfServiceUrl(CONTACT_URL)
				.contact(new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL)).license(INFO_VERSION)
				.licenseUrl(LICENSE_URL).version(LICENSE_NAME).build();
	}

	List<Parameter> addRestParameters(ParameterBuilder aParameterBuilder, List<Parameter> aParameters) {
		return aParameters;
	}

	@Bean
	public Docket ManagementAPI() {

		// Adding Header
		ParameterBuilder aParameterBuilder = new ParameterBuilder();
		List<Parameter> aParameters = new ArrayList<Parameter>();

		aParameterBuilder.name("Authorization").modelRef(new ModelRef("string")).parameterType("header").required(false)
				.build();
		aParameters.add(aParameterBuilder.build());

		return new Docket(DocumentationType.SWAGGER_2).groupName("management").select()
				.apis(RequestHandlerSelectors.any()).paths(buildPathSelectorManagement()).build()
				.globalOperationParameters(addRestParameters(aParameterBuilder, aParameters));
	}

	@SuppressWarnings("unchecked")
	private Predicate<String> buildPathSelectorManagement() {
		return or(regex("/management.*"));
	}

	@Bean
	public Docket ApiOpsAPI() {

		// Adding Header
		ParameterBuilder aParameterBuilder = new ParameterBuilder();
		List<Parameter> aParameters = new ArrayList<Parameter>();

		return new Docket(DocumentationType.SWAGGER_2).groupName("api-ops").select().apis(RequestHandlerSelectors.any())
				.paths(buildPathSelectorApiOps()).build()
				.globalOperationParameters(addRestParameters(aParameterBuilder, aParameters));
	}

	@SuppressWarnings("unchecked")
	private Predicate<String> buildPathSelectorApiOps() {
		return or(regex("/api-ops.*"));
	}

	@Bean
	public Docket LoginOpsAPI() {

		// Adding Header
		ParameterBuilder aParameterBuilder = new ParameterBuilder();
		List<Parameter> aParameters = new ArrayList<Parameter>();

		return new Docket(DocumentationType.SWAGGER_2).groupName("login").select().apis(RequestHandlerSelectors.any())
				.paths(buildPathSelectorApiOpsLogin()).build()
				.globalOperationParameters(addRestParameters(aParameterBuilder, aParameters));
	}

	@SuppressWarnings("unchecked")
	private Predicate<String> buildPathSelectorApiOpsLogin() {
		return or(regex("/api-ops/login.*"));
	}

}