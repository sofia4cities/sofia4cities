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
package com.indracompany.sofia2.iotbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.indracompany.sofia2.config.converters.JPACryptoConverterCustom;
import com.indracompany.sofia2.config.converters.JPAHAS256ConverterCustom;



@SpringBootApplication(scanBasePackages="com.indracompany.sofia2.iotbroker")

@ComponentScan(basePackages = {
		"com.indracompany.sofia2.config.services",
		"com.indracompany.sofia2.router.config.repository",
		"com.indracompany.sofia2.router.service.app.service.crud"
},
excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = {JPACryptoConverterCustom.class, JPAHAS256ConverterCustom.class})}
		)

@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class})

public class IotbrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotbrokerApplication.class, args);
	}
}
