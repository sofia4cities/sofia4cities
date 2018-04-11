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
package com.indracompany.sofia2.digitaltwin.broker.plugable.impl.gateway.reference.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@ConditionalOnProperty(
		prefix="sofia2.digitaltwin.broker.rest",
		name="enable",
		havingValue="true"
		)
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Value("${sofia2.digitaltwin.broker.rest.swaggerhost:localhost}")
	private String host;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.indracompany.sofia2.digitaltwin.broker.plugable.impl.gateway.reference.rest"))
				.paths(PathSelectors.any())
				.build()
				.host(host)
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		final ApiInfo apiInfo = new ApiInfo("Sofia4Cities IoT Digital Twin Rest Gateway",
				"Sofia4Cities IoT Digital Twin Rest Gateway",
				"v1.0.0",
				"",
				"select4citiesminsait@gmail.com",
				"Apache License 2.0",
				"https://github.com/sofia4cities/sofia4cities/blob/master/LICENSE");

		return apiInfo;
	}
}
